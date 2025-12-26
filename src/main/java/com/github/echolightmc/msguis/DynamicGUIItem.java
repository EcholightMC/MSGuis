package com.github.echolightmc.msguis;

import net.minestom.server.event.inventory.InventoryPreClickEvent;
import net.minestom.server.item.ItemStack;

import java.util.function.Consumer;

/**
 * A {@link GUIItem} whose displayed {@link ItemStack} and click handler can be changed at runtime.
 * <p>
 * Calling {@link #setItem(ItemStack)} will update the owning GUI's slot(s) immediately.
 */
public class DynamicGUIItem extends GUIItem {

	private ItemStack item;
	private Consumer<InventoryPreClickEvent> clickConsumer;

	/**
	 * Creates a dynamic item.
	 *
	 * @param item the initial displayed item
	 * @param clickConsumer click handler invoked from {@link #handleClick(InventoryPreClickEvent)}
	 */
	public DynamicGUIItem(ItemStack item, Consumer<InventoryPreClickEvent> clickConsumer) {
		this.item = item;
		this.clickConsumer = clickConsumer;
	}

	@Override
	public ItemStack getItem() {
		return item;
	}

	/**
	 * Updates the displayed item and notifies the owning GUI to re-render the slot(s).
	 * <p>
	 * This requires that the item has already been built into a GUI (i.e. {@link #getOwningGUI()} is non-null).
	 *
	 * @param item the new item stack to display
	 */
	public void setItem(ItemStack item) {
		this.item = item;
		getOwningGUI().notifyItemChange(this);
	}

	/**
	 * Returns the consumer used to handle click events.
	 *
	 * @return the click handler
	 */
	public Consumer<InventoryPreClickEvent> getClickConsumer() {
		return clickConsumer;
	}

	/**
	 * Replaces the click handler used by {@link #handleClick(InventoryPreClickEvent)}.
	 *
	 * @param clickConsumer the new click handler
	 */
	public void setClickConsumer(Consumer<InventoryPreClickEvent> clickConsumer) {
		this.clickConsumer = clickConsumer;
	}

	@Override
	public void handleClick(InventoryPreClickEvent event) {
		clickConsumer.accept(event);
	}

}
