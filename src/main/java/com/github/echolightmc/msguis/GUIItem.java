package com.github.echolightmc.msguis;

import net.minestom.server.event.inventory.InventoryPreClickEvent;
import net.minestom.server.item.ItemStack;

/**
 * Abstract base class for all GUI items.
 * <p>
 * GUIItem represents an item that can be placed in a GUI. Each item has:
 * <ul>
 *   <li>A visual representation (ItemStack)</li>
 *   <li>Click behavior (how it responds to player clicks)</li>
 *   <li>A reference to its owning GUI</li>
 * </ul>
 * <p>
 * Do not extend this class directly. Use one of the concrete implementations:
 * <ul>
 *   <li>{@link StaticGUIItem} - For non-interactive decorative items</li>
 *   <li>{@link DynamicGUIItem} - For interactive items with custom behavior</li>
 *   <li>{@link ScrollGUIItem} - For scroll control buttons</li>
 * </ul>
 *
 * @see StaticGUIItem
 * @see DynamicGUIItem
 * @see ScrollGUIItem
 */
public abstract class GUIItem {

	private ChestGUI owningGUI;

	/**
	 * Returns the visual representation of this item.
	 * <p>
	 * This method is called whenever the item needs to be displayed or updated.
	 * For dynamic items, this can return different ItemStacks based on state.
	 *
	 * @return the ItemStack to display
	 */
	public abstract ItemStack getItem();

	/**
	 * Returns the GUI that contains this item.
	 * <p>
	 * This reference is automatically set when the GUI is built and should not
	 * be null after the GUI is constructed.
	 *
	 * @return the owning GUI
	 */
	public ChestGUI getOwningGUI() {
		return owningGUI;
	}

	/**
	 * Handles click events for this item.
	 * <p>
	 * This method is called when a player clicks on this item in the GUI.
	 * Implementations should:
	 * <ul>
	 *   <li>Cancel the event if the item shouldn't be moveable</li>
	 *   <li>Execute any custom behavior</li>
	 *   <li>Update the item's state if needed</li>
	 * </ul>
	 *
	 * @param event the inventory click event
	 */
	public abstract void handleClick(InventoryPreClickEvent event);

	/**
	 * Sets the GUI that owns this item.
	 * <p>
	 * This is called internally by the GUI builder and should not be called manually.
	 *
	 * @param owningGUI the GUI that owns this item
	 * @throws IllegalStateException if the owning GUI is already set
	 */
	void setOwningGUI(ChestGUI owningGUI) {
		if (this.owningGUI != null) throw new IllegalStateException("OwningGUI is already set!");
		this.owningGUI = owningGUI;
	}

}
