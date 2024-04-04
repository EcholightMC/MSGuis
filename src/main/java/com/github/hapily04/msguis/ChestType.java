package com.github.hapily04.msguis;

import net.minestom.server.inventory.InventoryType;

public enum ChestType {

	ROWS_1(InventoryType.CHEST_1_ROW, 1),
	ROWS_2(InventoryType.CHEST_2_ROW, 2),
	ROWS_3(InventoryType.CHEST_3_ROW, 3),
	ROWS_4(InventoryType.CHEST_4_ROW, 4),
	ROWS_5(InventoryType.CHEST_5_ROW,5),
	ROWS_6(InventoryType.CHEST_6_ROW, 6);


	private final InventoryType minestomInventoryType;

	private final int rowCount;

	ChestType(InventoryType minestomInventoryType, int rowCount) {
		this.minestomInventoryType = minestomInventoryType;
		this.rowCount = rowCount;
	}

	public InventoryType getMinestomInventoryType() {
		return minestomInventoryType;
	}

	public int getRowCount() {
		return rowCount;
	}

}
