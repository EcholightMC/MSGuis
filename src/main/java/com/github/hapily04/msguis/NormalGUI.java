package com.github.hapily04.msguis;

import net.kyori.adventure.text.Component;

import java.util.Map;

public class NormalGUI extends ChestGUI {

	protected NormalGUI(ChestType chestType, Component title, GUIManager guiManager, String format, Map<Character, GUIItem> itemMap) {
		super(chestType, title, guiManager, format, itemMap);
	}


	public static class NormalGUIBuilder extends GUIBuilder<NormalGUI, NormalGUIBuilder> {

		@Override
		protected NormalGUI provideGUI() {
			return new NormalGUI(chestType, title, this.guiManager, this.format, this.itemMap);
		}

	}

}
