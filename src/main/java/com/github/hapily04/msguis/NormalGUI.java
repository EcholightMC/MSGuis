package com.github.hapily04.msguis;

import net.kyori.adventure.text.Component;

import java.util.Map;

public class NormalGUI extends ChestGUI {

	protected NormalGUI(ChestType chestType, Component title, GUIManager guiManager, String format, Map<Character, GUIItem> itemMap, Map<Indicator, Character> indicators) {
		super(chestType, title, guiManager, format, itemMap, indicators);
	}

	public static NormalGUIBuilder builder() {
		return new NormalGUIBuilder();
	}

	public static class NormalGUIBuilder extends GUIBuilder<NormalGUI, NormalGUIBuilder> {

		protected NormalGUIBuilder() {}

		@Override
		protected NormalGUI provideGUI() {
			return new NormalGUI(chestType, title, this.guiManager, this.format, this.itemMap, this.indicators);
		}

	}

}
