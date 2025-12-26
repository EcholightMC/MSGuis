package com.github.echolightmc.msguis;

/**
 * Special slot indicators used to mark slots with specific purposes in GUIs.
 * <p>
 * Indicators are used instead of regular GUI items to designate slots that have
 * special behavior or meaning. Currently, only {@link #CONTENT_SLOT} is fully
 * implemented and used in {@link ScrollGUI}.
 * <p>
 * <b>Example:</b>
 * <pre>{@code
 * ScrollGUI.builder()
 *     .format("""
 *         #########
 *         #       #
 *         #########""")
 *     .item('#', borderItem)
 *     .item(' ', Indicator.CONTENT_SLOT)  // Marks scrollable area
 *     .scrollContent(itemList)
 *     .build();
 * }</pre>
 *
 * @see ScrollGUI
 * @see ChestGUI.GUIBuilder#item(char, Indicator)
 */
public enum Indicator {

	/**
	 * Marks slots designated for input items.
	 * <p>
	 * <b>Note:</b> This indicator is reserved for future use and is not currently implemented.
	 * Using it will have no effect.
	 */
	INPUT_SLOT,

	/**
	 * Marks slots designated for output items.
	 * <p>
	 * <b>Note:</b> This indicator is reserved for future use and is not currently implemented.
	 * Using it will have no effect.
	 */
	OUTPUT_SLOT,

	/**
	 * Marks slots for scrollable content in a {@link ScrollGUI}.
	 * <p>
	 * In a ScrollGUI, slots marked with this indicator will display items from
	 * the scrollable content list. These slots automatically update when the user
	 * scrolls through the content.
	 * <p>
	 * <b>Usage:</b>
	 * <pre>{@code
	 * ScrollGUI.builder()
	 *     .format("#       #")  // Spaces will be content slots
	 *     .item(' ', Indicator.CONTENT_SLOT)
	 *     .scrollContent(itemList)
	 *     .build();
	 * }</pre>
	 * <p>
	 * <b>Important:</b> This indicator is required for {@link ScrollGUI} and should
	 * not be used with {@link NormalGUI}.
	 *
	 * @see ScrollGUI
	 * @see ScrollGUI.ScrollGUIBuilder#scrollContent(java.util.List)
	 */
	CONTENT_SLOT

}
