package com.github.hapily04.msguis;

import net.minestom.server.event.inventory.InventoryPreClickEvent;
import net.minestom.server.item.ItemStack;

public abstract class GUIItem {

	private ChestGUI owningGUI;

	public abstract ItemStack getItem();

	public ChestGUI getOwningGUI() {
		return owningGUI;
	}

	public abstract void handleClick(InventoryPreClickEvent event);

	void setOwningGUI(ChestGUI owningGUI) {
		if (this.owningGUI != null) throw new IllegalStateException("OwningGUI is already set!");
		this.owningGUI = owningGUI;
	}

}
