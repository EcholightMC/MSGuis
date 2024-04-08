package com.github.hapily04.msguis;

import net.minestom.server.event.inventory.InventoryPreClickEvent;
import net.minestom.server.item.ItemStack;

import java.util.function.Consumer;

public class DynamicGUIItem extends GUIItem {

	private ItemStack item;
	private Consumer<InventoryPreClickEvent> clickConsumer;

	public DynamicGUIItem(ItemStack item, Consumer<InventoryPreClickEvent> clickConsumer) {
		this.item = item;
		this.clickConsumer = clickConsumer;
	}

	@Override
	public ItemStack getItem() {
		return item;
	}

	public void setItem(ItemStack item) {
		this.item = item;
		getOwningGUI().notifyItemChange(this);
	}

	public Consumer<InventoryPreClickEvent> getClickConsumer() {
		return clickConsumer;
	}

	public void setClickConsumer(Consumer<InventoryPreClickEvent> clickConsumer) {
		this.clickConsumer = clickConsumer;
	}

	@Override
	public void handleClick(InventoryPreClickEvent event) {
		clickConsumer.accept(event);
	}

}
