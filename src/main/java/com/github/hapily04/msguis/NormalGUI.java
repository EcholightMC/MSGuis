package com.github.hapily04.msguis;

import net.kyori.adventure.text.Component;

import java.util.Map;

public class NormalGUI extends ChestGUI {

	public NormalGUI(ChestType chestType, Component title, GUIManager guiManager, String format, Map<Character, GUIItem> itemMap) {
		super(chestType, title, guiManager, format, itemMap);
	}

	@Override
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


	public static class NormalGUIBuilder extends GUIBuilder<NormalGUI, NormalGUIBuilder> {

		@Override
		protected NormalGUI provideGUI() {
			return new NormalGUI(chestType, title, this.guiManager, this.format, this.itemMap);
		}

	}

}
