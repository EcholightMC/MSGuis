package com.github.hapily04.msguis;

import net.kyori.adventure.text.Component;
import net.minestom.server.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ScrollGUI extends ChestGUI {

	private final List<? extends GUIItem> content ;

	private int currentScrollPos = 0;

	protected ScrollGUI(ChestType chestType, Component title, GUIManager guiManager, String format,
						Map<Character, GUIItem> itemMap, Map<Indicator, Character> indicators, List<? extends GUIItem> content) {
		super(chestType, title, guiManager, format, itemMap, indicators);
		this.content = content == null ? new ArrayList<>() : content;
		fillScrollContent();
	}

	public void scroll(int amount) {
		int proposedScrollPos = currentScrollPos + amount;
		if (proposedScrollPos < 0) return; // low bound check
		Integer[] scrollSlots = getScrollSlots();
		if (proposedScrollPos > content.size()-scrollSlots.length) return; // top bound check
		currentScrollPos = proposedScrollPos;
		fillScrollContent();
	}

	@SuppressWarnings("DuplicatedCode")
	@Override
	protected void applyFormat() {
		for (Map.Entry<Character, Integer[]> entry : charSlotMap.entrySet()) {
			if (entry.getKey() == indicators.get(Indicator.CONTENT_SLOT)) continue;
			for (Integer slot : entry.getValue()) {
				GUIItem item = itemMap.get(entry.getKey());
				items[slot] = item;
				ItemStack itemStack = item.getItem();
				if (inventory.getItemStack(slot) == itemStack) continue;
				inventory.setItemStack(slot, itemStack);
			}
		}
		//fillScrollContent(); moved to this constructor for now revert and come up with another solution if a problem arises
	}

	@Override
	public void setFormat(String format) {
		super.setFormat(format);
		fillScrollContent();
	}

	private void fillScrollContent() {
		Integer[] contentSlots = getScrollSlots();
		int i = currentScrollPos;
		for (int slot : contentSlots) {
			ItemStack contentItem;
			if (i >= content.size()) break;
			GUIItem item = content.get(i++);
			if (item == null) contentItem = ItemStack.AIR;
			else {
				contentItem = item.getItem();
				items[slot] = item;
			}
			if (inventory.getItemStack(slot) == contentItem) continue;
			inventory.setItemStack(slot, contentItem);
		}
	}

	private Integer[] getScrollSlots() {
		char scrollChar = indicators.get(Indicator.CONTENT_SLOT);
		return charSlotMap.get(scrollChar);
	}

	public List<? extends GUIItem> getContent() {
		return content;
	}

	public int getCurrentScrollPos() {
		return currentScrollPos;
	}

	public static class ScrollGUIBuilder extends GUIBuilder<ScrollGUI, ScrollGUIBuilder> {

		private List<? extends GUIItem> content;

		protected ScrollGUIBuilder() {}

		public ScrollGUIBuilder scrollContent(List<? extends GUIItem> content) {
			this.content = content;
			return this;
		}

		@Override
		protected ScrollGUI provideGUI() {
			return new ScrollGUI(chestType, title, guiManager, format, itemMap, indicators, content);
		}

	}

}
