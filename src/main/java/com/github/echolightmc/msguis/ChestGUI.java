package com.github.echolightmc.msguis;

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

/**
 * Base class for all formatted chest GUIs in MSGuis.
 * <p>
 * A {@code ChestGUI} is primarily defined by:
 * <ul>
 *     <li>a {@link #format} string (characters map to slots, 9 columns per row)</li>
 *     <li>a character-to-{@link GUIItem} mapping ({@link #itemMap})</li>
 *     <li>an underlying Minestom {@link Inventory}</li>
 * </ul>
 * Characters in the format that map to the same value will share the same {@link GUIItem} instance.
 * <p>
 * Instances are typically built via the builder exposed by concrete types such as {@link NormalGUI} and
 * {@link ScrollGUI}. Builders also register the GUI with a {@link GUIManager} for click handling.
 */
public abstract class ChestGUI {

	protected final Inventory inventory;
	protected final ChestType chestType;
	protected final GUIManager guiManager;

	protected String format;
	protected final Map<Character, GUIItem> itemMap;
	protected Map<Character, Integer[]> charSlotMap;
	protected final Map<Indicator, Character> indicators;
	protected final GUIItem[] items;

	// TODO actually implement input/output slots and maybe allow those slots to be se

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

	/**
	 * Sets (or replaces) the {@link GUIItem} mapped to a character in the format.
	 * <p>
	 * This updates all slots whose format character matches {@code character}.
	 *
	 * @param character the format character to target
	 * @param item the item to display/handle clicks for those slots
	 */
	public void setItem(char character, GUIItem item) {
		itemMap.put(character, item);
		applyFormat();
	}

	/**
	 * Replaces the current format string.
	 * <p>
	 * Newlines are stripped ({@code "\n"}) before applying. The resulting string length should match
	 * the inventory size implied by the {@link ChestType}.
	 *
	 * @param format the new format string
	 */
	public void setFormat(String format) {
		this.format = format.replace("\n", "");
		charSlotMap = createCharSlotMap();
		applyFormat();
	}

	@SuppressWarnings("DuplicatedCode")
	protected void applyFormat() {
		for (Map.Entry<Character, Integer[]> entry : charSlotMap.entrySet()) {
			GUIItem item = itemMap.get(entry.getKey());
			for (Integer slot : entry.getValue()) {
				items[slot] = item;
				ItemStack itemStack = item.getItem();
				if (inventory.getItemStack(slot).equals(itemStack)) continue;
				inventory.setItemStack(slot, itemStack);
			}
		}
	}

	/**
	 * Re-renders all slots backed by {@link DynamicGUIItem}.
	 * <p>
	 * Use this if a dynamic item's displayed {@link ItemStack} is derived from external state and can change
	 * without calling {@link DynamicGUIItem#setItem(ItemStack)}.
	 */
	public void refreshDynamicItems() {
		for (Map.Entry<Character, Integer[]> entry : charSlotMap.entrySet()) {
			GUIItem item = itemMap.get(entry.getKey());
			if (!(item instanceof DynamicGUIItem)) continue;
			for (Integer slot : entry.getValue()) {
				items[slot] = item;
				ItemStack itemStack = item.getItem();
				if (inventory.getItemStack(slot).equals(itemStack)) continue;
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

	/**
	 * Returns the underlying Minestom inventory backing this GUI.
	 *
	 * @return the inventory
	 */
	public Inventory getInventory() {
		return inventory;
	}

	/**
	 * Returns the configured chest type (size).
	 *
	 * @return the chest type
	 */
	public ChestType getChestType() {
		return chestType;
	}

	/**
	 * Returns the manager that registered this GUI.
	 *
	 * @return the gui manager
	 */
	public GUIManager getGuiManager() {
		return guiManager;
	}

	/**
	 * Returns the indicator mappings for this GUI.
	 * <p>
	 * Indicators allow a GUI implementation to treat certain format characters specially (e.g. {@link ScrollGUI}
	 * uses {@link Indicator#CONTENT_SLOT} to identify its scrollable region).
	 *
	 * @return indicator mappings (indicator -&gt; character)
	 */
	public Map<Indicator, Character> getIndicators() {
		return indicators;
	}

	/**
	 * Opens this GUI inventory for the provided players.
	 *
	 * @param players players to open the inventory for
	 */
	public void openTo(Player... players) {
		for (Player player : players) {
			player.openInventory(inventory);
		}
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

		/**
		 * Sets the format string for the GUI and infers the {@link ChestType} from its row count.
		 * <p>
		 * Newlines are removed before validation. The resulting character count must be divisible by 9,
		 * and the number of rows must be {@code 1..6}.
		 *
		 * @param format the format string
		 * @return this builder
		 * @throws IllegalArgumentException if the format length is not divisible by 9 or exceeds 6 rows
		 */
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

		/**
		 * Maps the given character in the format to a {@link GUIItem}.
		 * <p>
		 * All slots containing {@code character} will display this item and dispatch clicks to it.
		 *
		 * @param character the character to map
		 * @param item the item to use for those slots
		 * @return this builder
		 */
		public final B item(char character, GUIItem item) {
			itemMap.put(character, item);
			return (B) this;
		}

		/**
		 * Maps an {@link Indicator} to a character in the format.
		 * <p>
		 * Some GUI implementations interpret specific indicators (for example, {@link ScrollGUI} uses
		 * {@link Indicator#CONTENT_SLOT} to locate the scrollable content region).
		 *
		 * @param character the format character that represents the indicator
		 * @param indicator the indicator to associate with that character
		 * @return this builder
		 * @throws IllegalArgumentException if the indicator is already mapped
		 */
		public B item(char character, Indicator indicator) {
			if (indicators.containsKey(indicator))
				throw new IllegalArgumentException("The " + indicator.toString() + " indicator is already set!");
			indicators.put(indicator, character);
			return (B) this;
		}

		/**
		 * Sets the {@link GUIManager} that will register the created GUI.
		 *
		 * @param manager the manager to register with
		 * @return this builder
		 */
		public B manager(GUIManager manager) {
			this.guiManager = manager;
			return (B) this;
		}

		protected abstract T provideGUI();

		/**
		 * Builds the GUI, assigns each configured {@link GUIItem} its owning GUI, and registers it with the manager.
		 *
		 * @return the created GUI instance
		 */
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
