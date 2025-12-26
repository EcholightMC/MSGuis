package com.github.echolightmc.msguis;

import net.kyori.adventure.text.Component;

import java.util.Map;

/**
 * A standard chest GUI with fixed item positions.
 * <p>
 * NormalGUI is the simplest type of GUI where items are placed at fixed positions
 * defined by the format string. This is ideal for menus, shops, settings screens,
 * and any other GUI that doesn't require scrolling.
 * <p>
 * <b>Example:</b>
 * <pre>{@code
 * NormalGUI menu = NormalGUI.builder()
 *     .manager(guiManager)
 *     .titled("<green>Main Menu</green>")
 *     .format("""
 *         #########
 *         # A B C #
 *         #########""")
 *     .item('#', new StaticGUIItem(borderItem))
 *     .item('A', new DynamicGUIItem(option1, clickHandler))
 *     .item('B', new DynamicGUIItem(option2, clickHandler))
 *     .item('C', new DynamicGUIItem(option3, clickHandler))
 *     .build();
 * 
 * menu.openTo(player);
 * }</pre>
 *
 * @see ChestGUI
 * @see ScrollGUI
 * @see StaticGUIItem
 * @see DynamicGUIItem
 */
public class NormalGUI extends ChestGUI {

	protected NormalGUI(ChestType chestType, Component title, GUIManager guiManager, String format, Map<Character, GUIItem> itemMap, Map<Indicator, Character> indicators) {
		super(chestType, title, guiManager, format, itemMap, indicators);
	}

	/**
	 * Creates a new builder for constructing a NormalGUI.
	 * <p>
	 * This is the recommended way to create NormalGUI instances.
	 * <p>
	 * <b>Example:</b>
	 * <pre>{@code
	 * NormalGUI gui = NormalGUI.builder()
	 *     .manager(guiManager)
	 *     .titled("<red>Shop</red>")
	 *     .format("###\n# #\n###")
	 *     .item('#', borderItem)
	 *     .build();
	 * }</pre>
	 *
	 * @return a new NormalGUIBuilder instance
	 */
	public static NormalGUIBuilder builder() {
		return new NormalGUIBuilder();
	}

	/**
	 * Builder class for constructing NormalGUI instances.
	 * <p>
	 * Provides a fluent API for configuring and building NormalGUIs.
	 * Use {@link NormalGUI#builder()} to obtain an instance.
	 *
	 * @see NormalGUI#builder()
	 */
	public static class NormalGUIBuilder extends GUIBuilder<NormalGUI, NormalGUIBuilder> {

		protected NormalGUIBuilder() {}

		@Override
		protected NormalGUI provideGUI() {
			return new NormalGUI(chestType, title, this.guiManager, this.format, this.itemMap, this.indicators);
		}

	}

}
