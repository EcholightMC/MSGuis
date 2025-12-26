package com.github.echolightmc.msguis;

import net.minestom.server.event.inventory.InventoryPreClickEvent;
import net.minestom.server.item.ItemStack;

/**
 * A specialized GUI item for scrolling within a {@link ScrollGUI}.
 * <p>
 * ScrollGUIItem acts as a scroll button that moves the visible content area
 * when clicked. This is the primary way users navigate through large lists
 * in a ScrollGUI.
 * <p>
 * The scroll amount determines both direction and distance:
 * <ul>
 *   <li><b>Positive values</b> - Scroll down (forward in the list)</li>
 *   <li><b>Negative values</b> - Scroll up (backward in the list)</li>
 * </ul>
 * <p>
 * <b>Common Scroll Amounts:</b>
 * <ul>
 *   <li>{@code 1} - Scroll down by 1 slot</li>
 *   <li>{@code 7} - Scroll down by 1 row (7 content slots)</li>
 *   <li>{@code 21} - Scroll down by 3 rows (one "page")</li>
 *   <li>{@code -7} - Scroll up by 1 row</li>
 * </ul>
 * <p>
 * <b>Example - Next/Previous Buttons:</b>
 * <pre>{@code
 * // "Next Page" button (scroll down)
 * ScrollGUIItem nextButton = new ScrollGUIItem(
 *     7,  // Scroll down by one row
 *     ItemStack.of(Material.ARROW)
 *         .withCustomName(Component.text("⬇ Next Page"))
 * );
 * 
 * // "Previous Page" button (scroll up)
 * ScrollGUIItem prevButton = new ScrollGUIItem(
 *     -7,  // Scroll up by one row
 *     ItemStack.of(Material.ARROW)
 *         .withCustomName(Component.text("⬆ Previous Page"))
 * );
 * 
 * // Use in ScrollGUI
 * ScrollGUI.builder()
 *     .format("###U#D###")
 *     .item('U', prevButton)
 *     .item('D', nextButton)
 *     .build();
 * }</pre>
 * <p>
 * <b>Example - Fine and Coarse Scrolling:</b>
 * <pre>{@code
 * // Small scroll buttons
 * ScrollGUIItem scrollOne = new ScrollGUIItem(1,
 *     ItemStack.of(Material.ARROW).withCustomName(Component.text("▼")));
 * 
 * // Large scroll buttons
 * ScrollGUIItem scrollPage = new ScrollGUIItem(21,
 *     ItemStack.of(Material.SPECTRAL_ARROW).withCustomName(Component.text("▼▼")));
 * }</pre>
 * <p>
 * <b>Note:</b> This item should only be used in {@link ScrollGUI}. Using it in
 * a {@link NormalGUI} will cause a ClassCastException when clicked.
 *
 * @see ScrollGUI
 * @see GUIItem
 */
public class ScrollGUIItem extends GUIItem {

	private final int scroll;
	protected ItemStack item;

	/**
	 * Creates a new scroll GUI item.
	 * <p>
	 * The scroll amount determines how far and in which direction to scroll:
	 * <ul>
	 *   <li>Positive values scroll down (forward)</li>
	 *   <li>Negative values scroll up (backward)</li>
	 *   <li>Cannot be zero</li>
	 * </ul>
	 * <p>
	 * Common values:
	 * <ul>
	 *   <li>{@code 7} or {@code -7} for one row</li>
	 *   <li>{@code 21} or {@code -21} for three rows (one "page")</li>
	 * </ul>
	 *
	 * @param scroll the amount to scroll (positive = down, negative = up)
	 * @param itemStack the visual representation of the scroll button
	 * @throws IllegalArgumentException if scroll is zero
	 * @throws NullPointerException if itemStack is null
	 */
	public ScrollGUIItem(int scroll, ItemStack itemStack) {
		if (scroll == 0) throw new IllegalArgumentException("scroll amount cannot be zero");
		this.scroll = scroll;
		item = itemStack;
	}

	/**
	 * Returns the scroll amount for this item.
	 * <p>
	 * Positive values indicate scrolling down (forward in the list),
	 * negative values indicate scrolling up (backward in the list).
	 *
	 * @return the scroll amount
	 */
	public int getScroll() {
		return scroll;
	}

	@Override
	public ItemStack getItem() {
		return item;
	}

	/**
	 * Handles click events by scrolling the owning GUI.
	 * <p>
	 * This method automatically cancels the click event and triggers scrolling
	 * by the configured amount.
	 * <p>
	 * <b>Note:</b> The owning GUI must be a {@link ScrollGUI}, otherwise this
	 * will throw a ClassCastException.
	 *
	 * @param event the inventory click event (will be cancelled)
	 * @throws ClassCastException if the owning GUI is not a ScrollGUI
	 */
	@Override
	public void handleClick(InventoryPreClickEvent event) {
		event.setCancelled(true);
		((ScrollGUI) getOwningGUI()).scroll(scroll);
	}

}
