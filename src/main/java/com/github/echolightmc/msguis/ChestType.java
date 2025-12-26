package com.github.echolightmc.msguis;

import net.minestom.server.inventory.InventoryType;

/**
 * Represents the different chest sizes available for GUIs.
 * <p>
 * Each chest type corresponds to a specific number of rows and total slots.
 * The chest type is automatically determined from the format string length
 * when building a GUI, so you typically don't need to work with this enum directly.
 * <p>
 * <b>Slot Calculation:</b>
 * <ul>
 *   <li>Each row has 9 slots</li>
 *   <li>Total slots = rows × 9</li>
 * </ul>
 * <p>
 * <b>Example:</b>
 * <pre>{@code
 * // This format string has 27 characters = 3 rows = ROWS_3
 * String format = """
 *     #########
 *     #       #
 *     #########""";
 * 
 * // The chest type is automatically set based on format
 * NormalGUI gui = NormalGUI.builder()
 *     .format(format)  // Automatically uses ROWS_3
 *     .build();
 * 
 * // You can check the type
 * ChestType type = gui.getChestType();  // Returns ROWS_3
 * int rows = type.getRowCount();         // Returns 3
 * }</pre>
 *
 * @see ChestGUI#getChestType()
 */
public enum ChestType {

	/** 1 row chest (9 slots) */
	ROWS_1(InventoryType.CHEST_1_ROW, 1),
	
	/** 2 row chest (18 slots) */
	ROWS_2(InventoryType.CHEST_2_ROW, 2),
	
	/** 3 row chest (27 slots) - Default size */
	ROWS_3(InventoryType.CHEST_3_ROW, 3),
	
	/** 4 row chest (36 slots) */
	ROWS_4(InventoryType.CHEST_4_ROW, 4),
	
	/** 5 row chest (45 slots) */
	ROWS_5(InventoryType.CHEST_5_ROW,5),
	
	/** 6 row chest (54 slots) - Maximum size */
	ROWS_6(InventoryType.CHEST_6_ROW, 6);


	private final InventoryType minestomInventoryType;

	private final int rowCount;

	ChestType(InventoryType minestomInventoryType, int rowCount) {
		this.minestomInventoryType = minestomInventoryType;
		this.rowCount = rowCount;
	}

	/**
	 * Returns the corresponding Minestom inventory type.
	 * <p>
	 * This is used internally to create the underlying Minestom inventory.
	 *
	 * @return the Minestom inventory type
	 */
	public InventoryType getMinestomInventoryType() {
		return minestomInventoryType;
	}

	/**
	 * Returns the number of rows in this chest type.
	 * <p>
	 * Each row contains 9 slots, so the total slot count is {@code rowCount * 9}.
	 *
	 * @return the number of rows (1-6)
	 */
	public int getRowCount() {
		return rowCount;
	}

}
