package com.github.hapily04.msguis;

import net.minestom.server.event.inventory.InventoryPreClickEvent;
import net.minestom.server.item.ItemStack;

public class ScrollGUIItem extends GUIItem {

	private final int scroll;
	protected ItemStack item;

	/**
	 * @param scroll the amount you want it to scroll. If it's positive, it will scroll up that amount of slots.
	 *                  If negative, it will scroll down that amount of slots. Cannot be zero
	 */
	public ScrollGUIItem(int scroll, ItemStack itemStack) {
		if (scroll == 0) throw new IllegalArgumentException("scroll amount cannot be zero");
		this.scroll = scroll;
		item = itemStack;
	}

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
