package com.github.echolightmc.msguis;

import net.minestom.server.event.inventory.InventoryPreClickEvent;
import net.minestom.server.item.ItemStack;

/**
 * A simple {@link GUIItem} that always displays a fixed {@link ItemStack} and cancels clicks.
 */
public class StaticGUIItem extends GUIItem {

	private final ItemStack item;

	/**
	 * Creates a static item.
	 *
	 * @param item the item stack to display
	 */
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
