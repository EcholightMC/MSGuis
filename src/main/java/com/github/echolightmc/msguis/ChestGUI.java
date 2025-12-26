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
 * Abstract base class for all chest-based GUIs.
 * <p>
 * This class provides the core functionality for creating custom chest GUIs using
 * a format string system. The format string defines the layout using ASCII art,
 * where each character represents a specific item or slot type.
 * <p>
 * <b>Format String Rules:</b>
 * <ul>
 *   <li>Must be divisible by 9 (chest row width)</li>
 *   <li>Maximum of 6 rows (54 slots)</li>
 *   <li>Each character must be mapped to a GUIItem or Indicator</li>
 *   <li>Newlines in format strings are automatically removed</li>
 * </ul>
 * <p>
 * <b>Example Format:</b>
 * <pre>{@code
 * String format = """
 *     #########
 *     # X Y Z #
 *     #########""";
 * }</pre>
 * <p>
 * Do not instantiate this class directly. Use {@link NormalGUI} or {@link ScrollGUI} instead.
 *
 * @see NormalGUI
 * @see ScrollGUI
 * @see GUIItem
 * @see Indicator
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
	 * Updates the item associated with a character in the format string.
	 * <p>
	 * This method will update all slots in the GUI that use the specified character.
	 * The GUI is automatically refreshed after the change.
	 *
	 * @param character the character in the format string to update
	 * @param item the new GUIItem to associate with this character
	 */
	public void setItem(char character, GUIItem item) {
		itemMap.put(character, item);
		applyFormat();
	}

	/**
	 * Changes the GUI's format string, effectively changing the entire layout.
	 * <p>
	 * The new format must follow the same rules as the initial format:
	 * <ul>
	 *   <li>Divisible by 9</li>
	 *   <li>Maximum 6 rows</li>
	 *   <li>All characters must be mapped to items</li>
	 * </ul>
	 * <p>
	 * The GUI is automatically refreshed after the format change.
	 *
	 * @param format the new format string (newlines will be automatically removed)
	 * @throws IllegalArgumentException if the format is invalid
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
	 * Refreshes all dynamic items in the GUI.
	 * <p>
	 * This method updates the visual representation of all {@link DynamicGUIItem}s
	 * in the inventory. Use this when you've made changes to dynamic items and want
	 * to update the GUI without recreating it.
	 * <p>
	 * Static items are not affected by this method.
	 *
	 * @see DynamicGUIItem
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
	 * Returns the underlying Minestom inventory.
	 * <p>
	 * This can be used for advanced manipulation or integration with other systems.
	 *
	 * @return the Minestom inventory instance
	 */
	public Inventory getInventory() {
		return inventory;
	}

	/**
	 * Returns the chest type (size) of this GUI.
	 *
	 * @return the chest type
	 * @see ChestType
	 */
	public ChestType getChestType() {
		return chestType;
	}

	/**
	 * Returns the GUI manager that handles this GUI.
	 *
	 * @return the GUI manager
	 */
	public GUIManager getGuiManager() {
		return guiManager;
	}

	/**
	 * Returns the map of indicators used in this GUI.
	 * <p>
	 * Indicators are special markers used in ScrollGUI for content slots.
	 *
	 * @return map of indicators to their format characters
	 * @see Indicator
	 */
	public Map<Indicator, Character> getIndicators() {
		return indicators;
	}

	/**
	 * Opens this GUI for one or more players.
	 * <p>
	 * This is the primary method for displaying the GUI to players.
	 * Each player will see their own instance of the inventory.
	 *
	 * @param players the player(s) to open the GUI for
	 */
	public void openTo(Player... players) {
		for (Player player : players) {
			player.openInventory(inventory);
		}
	}

	/**
	 * Abstract builder class for constructing ChestGUI instances.
	 * <p>
	 * This class provides a fluent API for building GUIs. Use the concrete
	 * implementations {@link NormalGUI.NormalGUIBuilder} or {@link ScrollGUI.ScrollGUIBuilder}.
	 * <p>
	 * <b>Example:</b>
	 * <pre>{@code
	 * NormalGUI gui = NormalGUI.builder()
	 *     .manager(guiManager)
	 *     .titled("<green>My GUI</green>")
	 *     .format("#########\n# X Y Z #\n#########")
	 *     .item('#', borderItem)
	 *     .item('X', button1)
	 *     .build();
	 * }</pre>
	 *
	 * @param <T> the type of GUI being built
	 * @param <B> the type of builder (for method chaining)
	 */
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
		 * Sets the format string that defines the GUI layout.
		 * <p>
		 * The format string uses ASCII art to define the GUI layout, where each character
		 * represents an item slot. The chest type (number of rows) is automatically determined
		 * from the format string length.
		 * <p>
		 * <b>Rules:</b>
		 * <ul>
		 *   <li>Must be divisible by 9 (chest row width)</li>
		 *   <li>Maximum 6 rows (54 characters)</li>
		 *   <li>Newlines are automatically removed</li>
		 * </ul>
		 * <p>
		 * <b>Example:</b>
		 * <pre>{@code
		 * .format("""
		 *     #########
		 *     # X Y Z #
		 *     #########""")
		 * }</pre>
		 *
		 * @param format the format string defining the layout
		 * @return this builder for chaining
		 * @throws IllegalArgumentException if format is not divisible by 9 or has more than 6 rows
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
		 * Sets the title of the GUI with MiniMessage formatting support.
		 * <p>
		 * The title can include MiniMessage formatting tags for colors and styles.
		 * If no custom MiniMessage provider is specified, the default one is used.
		 * <p>
		 * <b>Examples:</b>
		 * <pre>{@code
		 * .titled("<green>Shop Menu</green>")
		 * .titled("<gradient:red:blue>Epic GUI</gradient>")
		 * .titled("<bold><red>WARNING!</red></bold>")
		 * }</pre>
		 *
		 * @param title the title in MiniMessage format to be converted to a component
		 * @param miniMessage optional custom MiniMessage provider (defaults to standard if not provided)
		 * @return this builder for chaining
		 */
		public B titled(String title, MiniMessage... miniMessage) {
			if (miniMessage.length == 1) miniMessageProvider = miniMessage[0];
			this.title = miniMessageProvider.deserialize(title);
			return (B) this;
		}

		/**
		 * Maps a character in the format string to a GUI item.
		 * <p>
		 * All slots in the format string that use this character will display this item.
		 * The same character can be used multiple times in the format for repeated items.
		 * <p>
		 * <b>Example:</b>
		 * <pre>{@code
		 * .format("#########")
		 * .item('#', new StaticGUIItem(borderItem))
		 * }</pre>
		 *
		 * @param character the character in the format string
		 * @param item the GUI item to associate with this character
		 * @return this builder for chaining
		 */
		public final B item(char character, GUIItem item) {
			itemMap.put(character, item);
			return (B) this;
		}

		/**
		 * Maps a character in the format string to a special indicator.
		 * <p>
		 * Indicators are used for special slot types, particularly in {@link ScrollGUI}
		 * where {@link Indicator#CONTENT_SLOT} marks the scrollable content area.
		 * <p>
		 * <b>Example:</b>
		 * <pre>{@code
		 * .format("#       #")  // Spaces will be content slots
		 * .item(' ', Indicator.CONTENT_SLOT)
		 * }</pre>
		 *
		 * @param character the character in the format string
		 * @param indicator the indicator type to associate with this character
		 * @return this builder for chaining
		 * @throws IllegalArgumentException if the indicator is already set to a different character
		 */
		public B item(char character, Indicator indicator) {
			if (indicators.containsKey(indicator))
				throw new IllegalArgumentException("The " + indicator.toString() + " indicator is already set!");
			indicators.put(indicator, character);
			return (B) this;
		}

		/**
		 * Sets the GUI manager that will handle this GUI's events.
		 * <p>
		 * This is required and must be called before building the GUI.
		 *
		 * @param manager the GUI manager
		 * @return this builder for chaining
		 */
		public B manager(GUIManager manager) {
			this.guiManager = manager;
			return (B) this;
		}

		/**
		 * Provides the concrete GUI instance to be built.
		 * <p>
		 * This method is implemented by subclasses to create the specific GUI type.
		 *
		 * @return the GUI instance
		 */
		protected abstract T provideGUI();

		/**
		 * Builds and registers the GUI.
		 * <p>
		 * This method constructs the GUI, associates all items with it, and registers
		 * it with the GUI manager for event handling.
		 * <p>
		 * After calling this method, the GUI is ready to be opened with {@link #openTo(Player...)}.
		 *
		 * @return the constructed GUI instance
		 * @throws NullPointerException if required fields (manager, format) are not set
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
