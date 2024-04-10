package com.github.hapily04.msguis;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.minestom.server.entity.Player;
import net.minestom.server.event.inventory.InventoryPreClickEvent;
import net.minestom.server.inventory.Inventory;
import net.minestom.server.item.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class ChestGUI {

	protected final Inventory inventory;
	protected final ChestType chestType;
	protected final GUIManager guiManager;

	protected String format;
	protected final Map<Character, GUIItem> itemMap;
	protected Map<Character, Integer[]> charSlotMap;
	protected final Map<Indicator, Character> indicators;
	protected final GUIItem[] items;

	protected ChestGUI(GUIManager guiManager, String format, Map<Character, GUIItem> itemMap,
					   Map<Indicator, Character> indicators) {
		this(ChestType.ROWS_3, guiManager, format, itemMap, indicators);
	}

	protected ChestGUI(ChestType chestType, GUIManager guiManager, String format, Map<Character, GUIItem> itemMap,
					   Map<Indicator, Character> indicators) {
		this(chestType, Component.text("GUI"), guiManager, format, itemMap, indicators);
	}

	protected ChestGUI(ChestType chestType, Component title, GUIManager guiManager, String format,
					   Map<Character, GUIItem> itemMap, Map<Indicator, Character> indicators) {
		inventory = new Inventory(chestType.getMinestomInventoryType(), title);
		this.chestType = chestType;
		this.guiManager = guiManager;
		this.format = format;
		items = new GUIItem[format.toCharArray().length];
		charSlotMap = createCharSlotMap();
		this.itemMap = itemMap;
		this.indicators = indicators;
		applyFormat();
	}

	public void setItem(char character, GUIItem item) {
		itemMap.put(character, item);
		applyFormat();
	}

	public void setFormat(String format) {
		this.format = format.replace("\n", "");
		charSlotMap = createCharSlotMap();
		applyFormat();
	}

	@SuppressWarnings("DuplicatedCode")
	protected void applyFormat() {
		for (Map.Entry<Character, Integer[]> entry : charSlotMap.entrySet()) {
			for (Integer slot : entry.getValue()) {
				GUIItem item = itemMap.get(entry.getKey());
				items[slot] = item;
				ItemStack itemStack = item.getItem();
				if (inventory.getItemStack(slot) == itemStack) continue;
				inventory.setItemStack(slot, itemStack);
			}
		}
	}

	private Map<Character, Integer[]> createCharSlotMap() {
		Map<Character, List<Integer>> tempMap = new HashMap<>();
		char[] chars = format.toCharArray();
		for (int i = 0; i < chars.length; i++) {
			char c = chars[i];
			if (!tempMap.containsKey(c)) {
				List<Integer> tempList = new ArrayList<>();
				tempList.add(i);
				tempMap.put(c, tempList);
			} else tempMap.get(c).add(i);
		}
		Map<Character, Integer[]> cSlotMap = new HashMap<>();
		for (Map.Entry<Character, List<Integer>> entry : tempMap.entrySet()) {
			cSlotMap.put(entry.getKey(), entry.getValue().toArray(new Integer[0]));
		}
		return cSlotMap;
	}

	void notifyItemChange(GUIItem item) {
		for (Map.Entry<Character, GUIItem> guiItemEntry : itemMap.entrySet()) {
			if (guiItemEntry.getValue() != item) return;
			for (Map.Entry<Character, Integer[]> entry : charSlotMap.entrySet()) {
				for (Integer slot : entry.getValue()) {
					inventory.setItemStack(slot, item.getItem());
				}
			}
		}
	}

	void handleClick(InventoryPreClickEvent event) {
		int slot = event.getSlot();
		if (slot > items.length || slot < 0) return;
		GUIItem item = items[slot];
		if (item != null) item.handleClick(event);
	}

	public Inventory getInventory() {
		return inventory;
	}

	public ChestType getChestType() {
		return chestType;
	}

	public GUIManager getGuiManager() {
		return guiManager;
	}

	public Map<Indicator, Character> getIndicators() {
		return indicators;
	}

	public void openTo(Player... players) {
		for (Player player : players) {
			player.openInventory(inventory);
		}
	}

	public static NormalGUI.NormalGUIBuilder normalBuilder() {
		return new NormalGUI.NormalGUIBuilder();
	}

	public static ScrollGUI.ScrollGUIBuilder scrollBuilder() {
		return new ScrollGUI.ScrollGUIBuilder();
	}

	@SuppressWarnings("unchecked")
	static abstract class GUIBuilder<T extends ChestGUI, B extends GUIBuilder<T, B>> {

		protected String format;
		protected ChestType chestType;
		protected MiniMessage miniMessageProvider = MiniMessage.miniMessage();
		protected Component title;
		protected GUIManager guiManager;
		protected final Map<Character, GUIItem> itemMap = new HashMap<>();
		protected final Map<Indicator, Character> indicators = new HashMap<>();

		protected GUIBuilder() {}

		public B format(String format) {
			format = format.replace("\n", "");
			int characterCount = format.toCharArray().length;
			if (characterCount % 9 != 0) throw new IllegalArgumentException("The format is not divisible by 9 (slot count per row).");
			int rowCount = characterCount/9;
			if (rowCount > 6) throw new IllegalArgumentException("Too many rows provided in the format!");
			for (ChestType type : ChestType.values()) {
				if (type.getRowCount() == rowCount) {
					chestType = type;
					break;
				}
			}
			this.format = format;
			return (B) this;
		}

		/**
		 * If no minimessage provider is provided, the default one will be used.
		 *
		 * @param title the title of the GUI in minimessage format to be converted to a component
		 * @param miniMessage the provider to be used on the title
		 * @return {@link B}
		 */
		public B titled(String title, MiniMessage... miniMessage) {
			if (miniMessage.length == 1) miniMessageProvider = miniMessage[0];
			this.title = miniMessageProvider.deserialize(title);
			return (B) this;
		}

		public final B item(char c, GUIItem item) {
			itemMap.put(c, item);
			return (B) this;
		}

		public B item(char character, Indicator indicator) {
			if (indicators.containsKey(indicator))
				throw new IllegalArgumentException("The " + indicator.toString() + " indicator is already set!");
			indicators.put(indicator, character);
			return (B) this;
		}

		public B manager(GUIManager manager) {
			this.guiManager = manager;
			return (B) this;
		}

		protected abstract T provideGUI();

		public T build() {
			T gui = provideGUI();
			for (Map.Entry<Character, GUIItem> entry : itemMap.entrySet()) {
				entry.getValue().setOwningGUI(gui);
			}
			guiManager.registerGUI(gui.inventory, gui);
			return gui;
		}

	}

}
