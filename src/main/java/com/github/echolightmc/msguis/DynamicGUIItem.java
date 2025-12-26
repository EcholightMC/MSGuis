package com.github.echolightmc.msguis;

import net.minestom.server.event.inventory.InventoryPreClickEvent;
import net.minestom.server.item.ItemStack;

import java.util.function.Consumer;

/**
 * A GUI item that can change its appearance and has custom click behavior.
 * <p>
 * DynamicGUIItem is the most flexible item type and is perfect for:
 * <ul>
 *   <li>Interactive buttons</li>
 *   <li>Toggleable options</li>
 *   <li>Counter displays</li>
 *   <li>Status indicators</li>
 *   <li>Any item that needs to update or respond to clicks</li>
 * </ul>
 * <p>
 * Unlike {@link StaticGUIItem}, dynamic items can:
 * <ul>
 *   <li>Change their visual appearance at runtime</li>
 *   <li>Execute custom logic when clicked</li>
 *   <li>Update their click behavior dynamically</li>
 * </ul>
 * <p>
 * <b>Example - Simple Button:</b>
 * <pre>{@code
 * DynamicGUIItem button = new DynamicGUIItem(
 *     ItemStack.of(Material.DIAMOND).withCustomName(Component.text("Click Me!")),
 *     event -> {
 *         event.setCancelled(true);
 *         event.getPlayer().sendMessage("Button clicked!");
 *     }
 * );
 * }</pre>
 * <p>
 * <b>Example - Toggle:</b>
 * <pre>{@code
 * AtomicBoolean enabled = new AtomicBoolean(false);
 * DynamicGUIItem toggle = new DynamicGUIItem(
 *     ItemStack.of(Material.REDSTONE).withCustomName(Component.text("Off")),
 *     event -> {
 *         event.setCancelled(true);
 *         boolean newState = !enabled.get();
 *         enabled.set(newState);
 *         
 *         // Update the item's appearance
 *         toggle.setItem(ItemStack.of(newState ? Material.EMERALD : Material.REDSTONE)
 *             .withCustomName(Component.text(newState ? "On" : "Off")));
 *     }
 * );
 * }</pre>
 * <p>
 * <b>Example - Counter:</b>
 * <pre>{@code
 * AtomicInteger count = new AtomicInteger(0);
 * DynamicGUIItem counter = new DynamicGUIItem(
 *     ItemStack.of(Material.GOLD_INGOT).withCustomName(Component.text("Count: 0")),
 *     event -> {
 *         event.setCancelled(true);
 *         int newCount = count.incrementAndGet();
 *         counter.setItem(ItemStack.of(Material.GOLD_INGOT)
 *             .withCustomName(Component.text("Count: " + newCount)));
 *     }
 * );
 * }</pre>
 *
 * @see GUIItem
 * @see StaticGUIItem
 */
public class DynamicGUIItem extends GUIItem {

	private ItemStack item;
	private Consumer<InventoryPreClickEvent> clickConsumer;

	/**
	 * Creates a new dynamic GUI item.
	 *
	 * @param item the initial ItemStack to display
	 * @param clickConsumer the function to execute when this item is clicked
	 * @throws NullPointerException if item or clickConsumer is null
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
	 * Updates the visual appearance of this item.
	 * <p>
	 * This method automatically notifies the owning GUI to refresh the display.
	 * The new item will be immediately visible to players viewing the GUI.
	 * <p>
	 * <b>Example:</b>
	 * <pre>{@code
	 * // Update item to show new status
	 * dynamicItem.setItem(
	 *     ItemStack.of(Material.EMERALD)
	 *         .withCustomName(Component.text("Active"))
	 *         .withLore(Component.text("Click to deactivate"))
	 * );
	 * }</pre>
	 *
	 * @param item the new ItemStack to display
	 * @throws NullPointerException if item is null
	 */
	public void setItem(ItemStack item) {
		this.item = item;
		getOwningGUI().notifyItemChange(this);
	}

	/**
	 * Returns the current click consumer.
	 *
	 * @return the function executed when this item is clicked
	 */
	public Consumer<InventoryPreClickEvent> getClickConsumer() {
		return clickConsumer;
	}

	/**
	 * Updates the click behavior of this item.
	 * <p>
	 * This allows you to change how the item responds to clicks without
	 * recreating the entire GUI.
	 * <p>
	 * <b>Example:</b>
	 * <pre>{@code
	 * // Change behavior based on game state
	 * if (gameStarted) {
	 *     item.setClickConsumer(event -> {
	 *         event.setCancelled(true);
	 *         player.sendMessage("Game already started!");
	 *     });
	 * }
	 * }</pre>
	 *
	 * @param clickConsumer the new function to execute when clicked
	 * @throws NullPointerException if clickConsumer is null
	 */
	public void setClickConsumer(Consumer<InventoryPreClickEvent> clickConsumer) {
		this.clickConsumer = clickConsumer;
	}

	/**
	 * Handles click events by executing the click consumer.
	 * <p>
	 * This method delegates to the configured click consumer function.
	 *
	 * @param event the inventory click event
	 */
	@Override
	public void handleClick(InventoryPreClickEvent event) {
		clickConsumer.accept(event);
	}

}
