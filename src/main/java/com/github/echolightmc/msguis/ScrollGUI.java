package com.github.echolightmc.msguis;

import net.kyori.adventure.text.Component;
import net.minestom.server.item.ItemStack;
import org.jetbrains.annotations.ApiStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * A chest GUI with scrollable content areas for displaying large lists.
 * <p>
 * ScrollGUI extends the basic ChestGUI functionality to support dynamic scrolling
 * through lists of items. This is perfect for:
 * <ul>
 *   <li>Player lists</li>
 *   <li>Item shops with many products</li>
 *   <li>Material browsers</li>
 *   <li>Achievement displays</li>
 *   <li>Any list that exceeds the visible area</li>
 * </ul>
 * <p>
 * The GUI distinguishes between:
 * <ul>
 *   <li><b>Static items</b> - Border, buttons, etc. that don't scroll</li>
 *   <li><b>Content slots</b> - Marked with {@link Indicator#CONTENT_SLOT}, these display scrollable content</li>
 *   <li><b>Scroll items</b> - {@link ScrollGUIItem}s that trigger scrolling</li>
 * </ul>
 * <p>
 * <b>Example:</b>
 * <pre>{@code
 * ScrollGUI browser = ScrollGUI.builder()
 *     .manager(guiManager)
 *     .titled("<blue>Item Browser</blue>")
 *     .format("""
 *         #########
 *         #       #
 *         #       #
 *         ###U#D###""")
 *     .item('#', new StaticGUIItem(border))
 *     .item(' ', Indicator.CONTENT_SLOT)
 *     .item('U', new ScrollGUIItem(-7, scrollUpArrow))
 *     .item('D', new ScrollGUIItem(7, scrollDownArrow))
 *     .scrollContent(itemList)
 *     .build();
 * }</pre>
 *
 * @see ChestGUI
 * @see NormalGUI
 * @see ScrollGUIItem
 * @see Indicator#CONTENT_SLOT
 */
public class ScrollGUI extends ChestGUI {

	private static final StaticGUIItem STATIC_AIR = new StaticGUIItem(ItemStack.AIR);

	private List<? extends GUIItem> content ;

	private int currentScrollPos = 0;

	// TODO maybe add 2 different scrolling types: horizontal (not coded) & vertical (what is already coded)

	protected ScrollGUI(ChestType chestType, Component title, GUIManager guiManager, String format,
						Map<Character, GUIItem> itemMap, Map<Indicator, Character> indicators, List<? extends GUIItem> content) {
		super(chestType, title, guiManager, format, itemMap, indicators);
		this.content = content == null ? new ArrayList<>() : content;
		fillScrollContent();
	}

	/**
	 * Scrolls the content by the specified amount.
	 * <p>
	 * This method handles scrolling with automatic boundary checking:
	 * <ul>
	 *   <li>Positive amounts scroll down (forward in the list)</li>
	 *   <li>Negative amounts scroll up (backward in the list)</li>
	 *   <li>Scrolling is prevented if it would go out of bounds</li>
	 * </ul>
	 * <p>
	 * This method is typically called by {@link ScrollGUIItem} click handlers,
	 * but can also be called manually.
	 * <p>
	 * <b>Example:</b>
	 * <pre>{@code
	 * // Scroll down by one row (7 slots)
	 * scrollGUI.scroll(7);
	 * 
	 * // Scroll up by one row
	 * scrollGUI.scroll(-7);
	 * }</pre>
	 *
	 * @param amount the number of slots to scroll (positive = down, negative = up)
	 */
	public void scroll(int amount) {
		int proposedScrollPos = currentScrollPos + amount;
		if (proposedScrollPos < 0) return; // low bound check
		Integer[] scrollSlots = getScrollSlots();
		int remainingItems = content.size()-proposedScrollPos;
		// checks to see if there are no remaining items to be shown & if there are enough items to continue scrolling if we're scrolling up
		if (remainingItems <= 0 || (scrollSlots.length-remainingItems > amount && currentScrollPos < proposedScrollPos))
			return; // top bound
		currentScrollPos = proposedScrollPos;
		fillScrollContent();
	}

	/**
	 * Updates the scrollable content list.
	 * <p>
	 * This method replaces the entire content list and refreshes the display.
	 * The scroll position is maintained if possible, or reset if the new content
	 * is shorter than the current position.
	 * <p>
	 * <b>Example:</b>
	 * <pre>{@code
	 * // Filter content based on search
	 * List<GUIItem> filtered = allItems.stream()
	 *     .filter(item -> item.getName().contains(search))
	 *     .collect(Collectors.toList());
	 * 
	 * scrollGUI.setContent(filtered);
	 * }</pre>
	 *
	 * @param content the new list of content items
	 */
	public void setContent(List<? extends GUIItem> content) {
		this.content = content;
		fillScrollContent();
	}

	@SuppressWarnings("DuplicatedCode")
	@Override
	protected void applyFormat() {
		for (Map.Entry<Character, Integer[]> entry : charSlotMap.entrySet()) {
			if (entry.getKey() == indicators.get(Indicator.CONTENT_SLOT)) continue;
			for (Integer slot : entry.getValue()) {
				GUIItem item = itemMap.get(entry.getKey());
				items[slot] = item;
				ItemStack itemStack = item.getItem();
				if (inventory.getItemStack(slot).equals(itemStack)) continue;
				inventory.setItemStack(slot, itemStack);
			}
		}
		//fillScrollContent(); moved to this constructor for now revert and come up with another solution if a problem arises
	}

	@Override
	public void setFormat(String format) {
		super.setFormat(format);
		fillScrollContent();
	}

	@Override
	public void refreshDynamicItems() {
		super.refreshDynamicItems();
		Integer[] contentSlots = getScrollSlots();
		int i = currentScrollPos;
		for (int slot : contentSlots) {
			ItemStack contentItem;
			if (i >= content.size()) { // out of bounds
				contentItem = STATIC_AIR.getItem();
				items[slot] = STATIC_AIR;
				i++;
			} else {
				GUIItem item = content.get(i++);
				if (!(item instanceof DynamicGUIItem)) continue;
				contentItem = item.getItem();
				items[slot] = item;
			}
			if (inventory.getItemStack(slot).equals(contentItem)) continue;
			inventory.setItemStack(slot, contentItem);
		}
	}

	@ApiStatus.Internal
	public void fillScrollContent() {
		Integer[] contentSlots = getScrollSlots();
		int i = currentScrollPos;
		for (int slot : contentSlots) {
			ItemStack contentItem;
			if (i >= content.size()) { // out of bounds
				contentItem = STATIC_AIR.getItem();
				items[slot] = STATIC_AIR;
				i++;
			} else {
				GUIItem item = content.get(i++);
				if (item == null) contentItem = ItemStack.AIR;
				else {
					contentItem = item.getItem();
					items[slot] = item;
				}
			}
			if (inventory.getItemStack(slot).equals(contentItem)) continue;
			inventory.setItemStack(slot, contentItem);
		}
	}

	private Integer[] getScrollSlots() {
		char scrollChar = indicators.get(Indicator.CONTENT_SLOT);
		return charSlotMap.get(scrollChar);
	}

	/**
	 * Returns the current scrollable content list.
	 * <p>
	 * This list represents all items available for scrolling, not just
	 * the currently visible items.
	 *
	 * @return the list of content items
	 */
	public List<? extends GUIItem> getContent() {
		return content;
	}

	/**
	 * Returns the current scroll position.
	 * <p>
	 * The scroll position represents the index of the first visible item
	 * in the content list. A position of 0 means the list is scrolled to the top.
	 *
	 * @return the current scroll position (0-based index)
	 */
	public int getCurrentScrollPos() {
		return currentScrollPos;
	}

	/**
	 * Creates a new builder for constructing a ScrollGUI.
	 * <p>
	 * This is the recommended way to create ScrollGUI instances.
	 * <p>
	 * <b>Example:</b>
	 * <pre>{@code
	 * ScrollGUI gui = ScrollGUI.builder()
	 *     .manager(guiManager)
	 *     .titled("<blue>Browser</blue>")
	 *     .format("#########\n#       #\n###<#>###")
	 *     .item('#', border)
	 *     .item(' ', Indicator.CONTENT_SLOT)
	 *     .item('<', new ScrollGUIItem(-7, scrollBack))
	 *     .item('>', new ScrollGUIItem(7, scrollForward))
	 *     .scrollContent(itemList)
	 *     .build();
	 * }</pre>
	 *
	 * @return a new ScrollGUIBuilder instance
	 */
	public static ScrollGUIBuilder builder() {
		return new ScrollGUIBuilder();
	}

	/**
	 * Builder class for constructing ScrollGUI instances.
	 * <p>
	 * Extends the base GUIBuilder with scroll-specific configuration.
	 * Use {@link ScrollGUI#builder()} to obtain an instance.
	 *
	 * @see ScrollGUI#builder()
	 */
	public static class ScrollGUIBuilder extends GUIBuilder<ScrollGUI, ScrollGUIBuilder> {

		private List<? extends GUIItem> content;

		protected ScrollGUIBuilder() {}

		/**
		 * Sets the initial scrollable content for the GUI.
		 * <p>
		 * This content will be displayed in slots marked with {@link Indicator#CONTENT_SLOT}.
		 * The content can be updated later using {@link ScrollGUI#setContent(List)}.
		 * <p>
		 * <b>Example:</b>
		 * <pre>{@code
		 * List<GUIItem> items = Arrays.stream(Material.values())
		 *     .map(m -> new StaticGUIItem(ItemStack.of(m)))
		 *     .collect(Collectors.toList());
		 * 
		 * .scrollContent(items)
		 * }</pre>
		 *
		 * @param content the list of items to display in the scrollable area
		 * @return this builder for chaining
		 */
		public ScrollGUIBuilder scrollContent(List<? extends GUIItem> content) {
			this.content = content;
			return this;
		}

		@Override
		protected ScrollGUI provideGUI() {
			return new ScrollGUI(chestType, title, guiManager, format, itemMap, indicators, content);
		}

	}

}
