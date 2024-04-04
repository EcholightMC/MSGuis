package com.github.hapily04.msguis;

import net.minestom.server.event.inventory.InventoryPreClickEvent;
import net.minestom.server.item.ItemStack;

public class NormalGUIItem extends GUIItem {

	private final ItemStack item;

	public NormalGUIItem(ItemStack item) {
		this.item = item;
	}

	@Override
	public ItemStack getItem() {
		return item;
	}

	@Override
	public void handleClick(InventoryPreClickEvent event) {
		event.setCancelled(true);
	}

}
