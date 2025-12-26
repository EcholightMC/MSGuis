package com.github.echolightmc.msguis;

import net.minestom.server.event.inventory.InventoryPreClickEvent;
import net.minestom.server.item.ItemStack;

public class StaticGUIItem extends GUIItem {

	private final ItemStack item;

	public StaticGUIItem(ItemStack item) {
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
