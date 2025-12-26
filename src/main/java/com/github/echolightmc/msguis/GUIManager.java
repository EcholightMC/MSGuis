package com.github.echolightmc.msguis;

import net.minestom.server.event.EventFilter;
import net.minestom.server.event.EventListener;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.GlobalEventHandler;
import net.minestom.server.event.inventory.InventoryPreClickEvent;
import net.minestom.server.event.trait.InventoryEvent;
import net.minestom.server.inventory.AbstractInventory;
import net.minestom.server.inventory.Inventory;

import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * Central manager for handling GUI events and registration.
 * <p>
 * This class is responsible for:
 * <ul>
 *   <li>Registering GUIs and their associated inventories</li>
 *   <li>Handling click events for all managed GUIs</li>
 *   <li>Managing the lifecycle of GUI instances</li>
 * </ul>
 * <p>
 * Each plugin/module should have exactly one GUIManager instance.
 * The manager uses WeakHashMap for GUI storage, allowing garbage collection
 * of GUIs that are no longer referenced.
 * <p>
 * <b>Example Usage:</b>
 * <pre>{@code
 * // Initialize the manager (typically in your main class)
 * GUIManager manager = new GUIManager(MinecraftServer.getGlobalEventHandler());
 * 
 * // GUIs are automatically registered when built
 * NormalGUI gui = NormalGUI.builder()
 *     .manager(manager)
 *     .format("...")
 *     .build();
 * 
 * // Optionally unregister when done
 * manager.unregisterGUI(gui);
 * }</pre>
 *
 * @see ChestGUI
 * @see NormalGUI
 * @see ScrollGUI
 */
public class GUIManager {

	private static int idCounter = 0;

	private final int ID;
	private final Map<Inventory, ChestGUI> guiMap = new WeakHashMap<>();

	/**
	 * Creates a new GUI manager and registers it with the global event handler.
	 * <p>
	 * This constructor automatically sets up event listeners for handling GUI clicks.
	 * You should create exactly one GUIManager instance per plugin/module.
	 *
	 * @param globalEventHandler the Minestom global event handler to register with
	 * @throws NullPointerException if globalEventHandler is null
	 */
	public GUIManager(GlobalEventHandler globalEventHandler) {
		ID = idCounter++;
		hookEvents(globalEventHandler);
	}

	void registerGUI(Inventory inventory, ChestGUI chestGUI) {
		guiMap.put(inventory, chestGUI);
	}

	/**
	 * Unregisters a GUI from this manager.
	 * <p>
	 * After unregistering, click events will no longer be handled for this GUI.
	 * This is useful for cleaning up GUIs that are no longer needed.
	 *
	 * @param chestGUI the GUI to unregister
	 * @return true if the GUI was registered and successfully removed, false otherwise
	 */
	public boolean unregisterGUI(ChestGUI chestGUI) {
		return guiMap.remove(chestGUI.inventory, chestGUI);
	}

	private void hookEvents(GlobalEventHandler globalEventHandler) {
		EventNode<InventoryEvent> node = EventNode.type("MSGuis-Manager-" + ID, EventFilter.INVENTORY)
														  .addListener(getClickListener());
		globalEventHandler.addChild(node);
	}

	private EventListener<InventoryPreClickEvent> getClickListener() {
		return EventListener.of(InventoryPreClickEvent.class, event -> {
			AbstractInventory abstractInventory = event.getInventory();
			if (!(abstractInventory instanceof Inventory inventory)) return;
			if (!guiMap.containsKey(inventory)) return;
			guiMap.get(inventory).handleClick(event);
		});
	}

}
