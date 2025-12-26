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
 * Central event router for MSGuis.
 * <p>
 * A {@link GUIManager} registers {@link ChestGUI} instances and hooks into Minestom's inventory click events
 * to dispatch {@link InventoryPreClickEvent}s to the correct GUI.
 * <p>
 * Typical usage is to create a single instance at server startup:
 *
 * <pre>
 * {@code
 * GUIManager guiManager = new GUIManager(MinecraftServer.getGlobalEventHandler());
 * }
 * </pre>
 */
public class GUIManager {

	private static int idCounter = 0;

	private final int ID;
	private final Map<Inventory, ChestGUI> guiMap = new WeakHashMap<>();

	/**
	 * Creates a manager and installs an inventory listener into the provided global event handler.
	 *
	 * @param globalEventHandler the handler to attach MSGuis' inventory listeners to
	 */
	public GUIManager(GlobalEventHandler globalEventHandler) {
		ID = idCounter++;
		hookEvents(globalEventHandler);
	}

	void registerGUI(Inventory inventory, ChestGUI chestGUI) {
		guiMap.put(inventory, chestGUI);
	}

	/**
	 * Unregisters a GUI previously created via a builder (which registers it automatically on {@code build()}).
	 * <p>
	 * Once unregistered, clicks in that GUI inventory will no longer be dispatched to the GUI's items.
	 *
	 * @param chestGUI the GUI to unregister
	 * @return {@code true} if the mapping existed and was removed; {@code false} otherwise
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
