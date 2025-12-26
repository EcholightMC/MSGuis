package com.github.echolightmc.msguis;

import net.minestom.server.event.inventory.InventoryPreClickEvent;
import net.minestom.server.item.ItemStack;

/**
 * A {@link GUIItem} that scrolls its owning {@link ScrollGUI} when clicked.
 * <p>
 * This is intended to be used as a navigation button inside a {@link ScrollGUI}.
 */
public class ScrollGUIItem extends GUIItem {

	private final int scroll;
	protected ItemStack item;

	/**
	 * @param scroll the amount you want it to scroll. If it's positive, it will scroll up that amount of slots.
	 *                  If negative, it will scroll down that amount of slots. Cannot be zero
	 * @param itemStack the displayed item (for example, an arrow)
	 */
	public ScrollGUIItem(int scroll, ItemStack itemStack) {
		if (scroll == 0) throw new IllegalArgumentException("scroll amount cannot be zero");
		this.scroll = scroll;
		item = itemStack;
	}

	/**
	 * Returns the configured scroll amount.
	 *
	 * @return the scroll amount (non-zero)
	 */
	public int getScroll() {
		return scroll;
	}

	@Override
	public ItemStack getItem() {
		return item;
	}

	@Override
	public void handleClick(InventoryPreClickEvent event) {
		event.setCancelled(true);
		((ScrollGUI) getOwningGUI()).scroll(scroll);
	}

}
