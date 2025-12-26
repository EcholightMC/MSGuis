package com.github.echolightmc.msguis;

import net.kyori.adventure.text.Component;

import java.util.Map;

/**
 * A non-scrolling chest GUI.
 * <p>
 * Create instances via {@link #builder()}.
 */
public class NormalGUI extends ChestGUI {

	protected NormalGUI(ChestType chestType, Component title, GUIManager guiManager, String format, Map<Character, GUIItem> itemMap, Map<Indicator, Character> indicators) {
		super(chestType, title, guiManager, format, itemMap, indicators);
	}

	/**
	 * Creates a builder for {@link NormalGUI}.
	 *
	 * @return a new builder
	 */
	public static NormalGUIBuilder builder() {
		return new NormalGUIBuilder();
	}

	/**
	 * Builder for {@link NormalGUI}.
	 * <p>
	 * Inherits all builder methods from {@link ChestGUI.GUIBuilder}.
	 */
	public static class NormalGUIBuilder extends GUIBuilder<NormalGUI, NormalGUIBuilder> {

		protected NormalGUIBuilder() {}

		@Override
		protected NormalGUI provideGUI() {
			return new NormalGUI(chestType, title, this.guiManager, this.format, this.itemMap, this.indicators);
		}

	}

}
