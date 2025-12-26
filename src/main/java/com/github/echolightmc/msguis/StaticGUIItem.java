package com.github.echolightmc.msguis;

import net.minestom.server.event.inventory.InventoryPreClickEvent;
import net.minestom.server.item.ItemStack;

/**
 * A simple, immutable GUI item that cannot be interacted with.
 * <p>
 * StaticGUIItem is perfect for:
 * <ul>
 *   <li>Borders and backgrounds</li>
 *   <li>Decorative elements</li>
 *   <li>Information displays that don't change</li>
 *   <li>Spacers and separators</li>
 * </ul>
 * <p>
 * This item automatically cancels all click events, preventing players from
 * moving or interacting with it.
 * <p>
 * <b>Example:</b>
 * <pre>{@code
 * // Create a border item
 * ItemStack borderStack = ItemStack.of(Material.GRAY_STAINED_GLASS_PANE)
 *     .withCustomName(Component.empty());
 * StaticGUIItem border = new StaticGUIItem(borderStack);
 * 
 * // Use in GUI
 * NormalGUI.builder()
 *     .format("#########")
 *     .item('#', border)
 *     .build();
 * }</pre>
 *
 * @see GUIItem
 * @see DynamicGUIItem
 */
public class StaticGUIItem extends GUIItem {

	private final ItemStack item;

	/**
	 * Creates a new static GUI item.
	 * <p>
	 * The provided ItemStack will be displayed exactly as given and cannot be changed.
	 *
	 * @param item the ItemStack to display
	 * @throws NullPointerException if item is null
	 */
	public StaticGUIItem(ItemStack item) {
		this.item = item;
	}

	@Override
	public ItemStack getItem() {
		return item;
	}

	/**
	 * Handles click events by cancelling them.
	 * <p>
	 * This prevents the item from being moved or interacted with.
	 *
	 * @param event the inventory click event (will be cancelled)
	 */
	@Override
	public void handleClick(InventoryPreClickEvent event) {
		event.setCancelled(true);
	}

}
