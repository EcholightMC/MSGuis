package com.github.echolightmc.msguis;

/**
 * Special markers that GUI implementations can use to interpret a character in a GUI format string.
 * <p>
 * Indicators are configured via {@link ChestGUI.GUIBuilder#item(char, Indicator)}.
 */
public enum Indicator {

	/**
	 * Reserved for future support of input slots.
	 */
	INPUT_SLOT,
	/**
	 * Reserved for future support of output slots.
	 */
	OUTPUT_SLOT,
	/**
	 * Marks the scrollable content region in a {@link ScrollGUI}.
	 */
	CONTENT_SLOT

}
