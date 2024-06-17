package com.github.echolightmc.msguis;

import net.minestom.server.event.EventFilter;
import net.minestom.server.event.EventListener;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.GlobalEventHandler;
import net.minestom.server.event.inventory.InventoryPreClickEvent;
import net.minestom.server.event.trait.InventoryEvent;
import net.minestom.server.inventory.Inventory;

import java.util.HashMap;
import java.util.Map;

public class GUIManager {

	private static int idCounter = 0;

	private final int ID;
	private final Map<Inventory, ChestGUI> guiMap;

	public GUIManager(GlobalEventHandler globalEventHandler) {
		ID = idCounter++;
		guiMap = new HashMap<>();
		hookEvents(globalEventHandler);
	}
	void registerGUI(Inventory inventory, ChestGUI chestGUI) {
		guiMap.put(inventory, chestGUI);
	}

	private void hookEvents(GlobalEventHandler globalEventHandler) {
		EventNode<InventoryEvent> node = EventNode.type("MSGuis-Manager-" + ID, EventFilter.INVENTORY)
														  .addListener(getClickListener());
		globalEventHandler.addChild(node);
	}

	private EventListener<InventoryPreClickEvent> getClickListener() {
		return EventListener.of(InventoryPreClickEvent.class, event -> {
			Inventory inventory = event.getInventory();
			if (!guiMap.containsKey(inventory)) return;
			guiMap.get(inventory).handleClick(event);
		});
	}

}
