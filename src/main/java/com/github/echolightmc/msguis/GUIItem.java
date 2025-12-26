package com.github.echolightmc.msguis;

import net.minestom.server.event.inventory.InventoryPreClickEvent;
import net.minestom.server.item.ItemStack;

/**
 * A GUI "component" representing an item stack + click behavior for one or more GUI slots.
 * <p>
 * {@link GUIItem}s are assigned to characters in a {@link ChestGUI} format string. During {@code build()},
 * each item is associated with its owning GUI and will receive click events for any slots it occupies.
 */
public abstract class GUIItem {

	private ChestGUI owningGUI;

	/**
	 * Returns the item stack to display for this GUI item.
	 *
	 * @return the current item stack
	 */
	public abstract ItemStack getItem();

	/**
	 * Returns the GUI that owns this item.
	 * <p>
	 * This is set automatically by {@link ChestGUI.GUIBuilder#build()}.
	 *
	 * @return the owning GUI, or {@code null} if the item has not been built into a GUI yet
	 */
	public ChestGUI getOwningGUI() {
		return owningGUI;
	}

	/**
	 * Handles a click on a slot using this item.
	 * <p>
	 * This is called from {@link GUIManager}'s inventory click listener.
	 * Typically, implementations will call {@link InventoryPreClickEvent#setCancelled(boolean)} to prevent
	 * players from taking items from the GUI.
	 *
	 * @param event the click event
	 */
	public abstract void handleClick(InventoryPreClickEvent event);

	void setOwningGUI(ChestGUI owningGUI) {
		if (this.owningGUI != null) throw new IllegalStateException("OwningGUI is already set!");
		this.owningGUI = owningGUI;
	}

}
