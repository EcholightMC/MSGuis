package com.github.echolightmc.msguis;

import net.kyori.adventure.text.Component;
import net.minestom.server.item.ItemStack;
import org.jetbrains.annotations.ApiStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * A {@link ChestGUI} with a scrollable content region.
 * <p>
 * The scrollable region is defined by mapping {@link Indicator#CONTENT_SLOT} to a character in the format:
 *
 * <pre>
 * {@code
 * ScrollGUI gui = ScrollGUI.builder()
 *     .item(' ', Indicator.CONTENT_SLOT)
 *     .scrollContent(myItems)
 *     .build();
 * }
 * </pre>
 *
 * All slots containing that character become "content slots" and will be filled with the current window
 * of the configured content list.
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
	 * Scrolls the content region by the given amount.
	 * <p>
	 * The amount is applied to the internal content index ({@link #getCurrentScrollPos()}). If the proposed
	 * scroll position would exceed the bounds of the content list, nothing happens.
	 *
	 * @param amount the scroll amount (positive or negative)
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
	 * Replaces the scrollable content list and re-renders visible content slots.
	 *
	 * @param content the new content list
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
	 * Returns the current scroll content list.
	 *
	 * @return the content list
	 */
	public List<? extends GUIItem> getContent() {
		return content;
	}

	/**
	 * Returns the current scroll position (index into {@link #getContent()}).
	 *
	 * @return the current scroll position
	 */
	public int getCurrentScrollPos() {
		return currentScrollPos;
	}

	/**
	 * Creates a builder for {@link ScrollGUI}.
	 *
	 * @return a new builder
	 */
	public static ScrollGUIBuilder builder() {
		return new ScrollGUIBuilder();
	}

	/**
	 * Builder for {@link ScrollGUI}.
	 * <p>
	 * Inherits all builder methods from {@link ChestGUI.GUIBuilder} and adds {@link #scrollContent(List)}.
	 */
	public static class ScrollGUIBuilder extends GUIBuilder<ScrollGUI, ScrollGUIBuilder> {

		private List<? extends GUIItem> content;

		protected ScrollGUIBuilder() {}

		/**
		 * Sets the initial scroll content list.
		 *
		 * @param content the content list
		 * @return this builder
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
