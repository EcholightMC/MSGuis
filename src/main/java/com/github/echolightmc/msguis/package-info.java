/**
 * MSGuis - A flexible and powerful GUI library for Minestom.
 * <p>
 * This library provides a format-based system for creating chest GUIs in Minestom.
 * Instead of manually placing items in specific slots, you define the layout using
 * ASCII art format strings, making GUI creation intuitive and maintainable.
 *
 * <h2>Quick Start</h2>
 * 
 * <h3>1. Initialize the GUI Manager</h3>
 * <pre>{@code
 * GUIManager manager = new GUIManager(MinecraftServer.getGlobalEventHandler());
 * }</pre>
 *
 * <h3>2. Create a Simple GUI</h3>
 * <pre>{@code
 * NormalGUI gui = NormalGUI.builder()
 *     .manager(manager)
 *     .titled("<green>My GUI</green>")
 *     .format("""
 *         #########
 *         # X Y Z #
 *         #########""")
 *     .item('#', new StaticGUIItem(borderItem))
 *     .item('X', new DynamicGUIItem(button1, clickHandler))
 *     .build();
 * }</pre>
 *
 * <h3>3. Open the GUI</h3>
 * <pre>{@code
 * gui.openTo(player);
 * }</pre>
 *
 * <h2>Core Concepts</h2>
 *
 * <h3>GUI Types</h3>
 * <ul>
 *   <li>{@link com.github.echolightmc.msguis.NormalGUI NormalGUI} - Standard fixed-position GUI</li>
 *   <li>{@link com.github.echolightmc.msguis.ScrollGUI ScrollGUI} - GUI with scrollable content areas</li>
 * </ul>
 *
 * <h3>Item Types</h3>
 * <ul>
 *   <li>{@link com.github.echolightmc.msguis.StaticGUIItem StaticGUIItem} - Immutable decorative items</li>
 *   <li>{@link com.github.echolightmc.msguis.DynamicGUIItem DynamicGUIItem} - Interactive items that can change</li>
 *   <li>{@link com.github.echolightmc.msguis.ScrollGUIItem ScrollGUIItem} - Scroll control buttons</li>
 * </ul>
 *
 * <h3>Format Strings</h3>
 * <p>
 * Format strings define GUI layouts using ASCII art. Each character represents a slot:
 * <pre>{@code
 * // Example: 3-row GUI with border (#) and buttons (A, B, C)
 * String format = """
 *     #########
 *     #A#B#C###
 *     #########""";
 * }</pre>
 * <p>
 * Rules:
 * <ul>
 *   <li>Must be divisible by 9 (chest row width)</li>
 *   <li>Maximum 6 rows (54 slots)</li>
 *   <li>Each character must be mapped to an item or indicator</li>
 * </ul>
 *
 * <h3>Builders</h3>
 * <p>
 * All GUIs are created using the builder pattern for a fluent API:
 * <pre>{@code
 * NormalGUI gui = NormalGUI.builder()
 *     .manager(guiManager)              // Required: GUI manager
 *     .titled("<red>Title</red>")       // Optional: MiniMessage title
 *     .format("###\n# #\n###")          // Required: Layout format
 *     .item('#', borderItem)            // Map characters to items
 *     .item('X', buttonItem)
 *     .build();                         // Build and register
 * }</pre>
 *
 * <h2>Advanced Features</h2>
 *
 * <h3>Scrollable GUIs</h3>
 * <p>
 * ScrollGUI supports displaying large lists with automatic scrolling:
 * <pre>{@code
 * ScrollGUI gui = ScrollGUI.builder()
 *     .manager(guiManager)
 *     .format("""
 *         #########
 *         #       #    <- Content area
 *         ###<#>###""")
 *     .item('#', border)
 *     .item(' ', Indicator.CONTENT_SLOT)  // Mark scrollable area
 *     .item('<', new ScrollGUIItem(-7, scrollUpItem))
 *     .item('>', new ScrollGUIItem(7, scrollDownItem))
 *     .scrollContent(itemList)
 *     .build();
 * }</pre>
 *
 * <h3>Dynamic Updates</h3>
 * <p>
 * Dynamic items can be updated at runtime:
 * <pre>{@code
 * DynamicGUIItem counter = new DynamicGUIItem(
 *     ItemStack.of(Material.EMERALD).withCustomName(Component.text("Count: 0")),
 *     event -> {
 *         event.setCancelled(true);
 *         count++;
 *         counter.setItem(ItemStack.of(Material.EMERALD)
 *             .withCustomName(Component.text("Count: " + count)));
 *     }
 * );
 * }</pre>
 *
 * <h3>MiniMessage Support</h3>
 * <p>
 * All GUI titles support Adventure MiniMessage formatting:
 * <pre>{@code
 * .titled("<gradient:red:blue>Epic Title</gradient>")
 * .titled("<rainbow>Rainbow Text</rainbow>")
 * .titled("<bold><green>Bold Green</green></bold>")
 * }</pre>
 *
 * <h2>Examples</h2>
 *
 * <h3>Simple Menu</h3>
 * <pre>{@code
 * NormalGUI menu = NormalGUI.builder()
 *     .manager(guiManager)
 *     .titled("<green>Main Menu</green>")
 *     .format("#########\n# A B C #\n#########")
 *     .item('#', new StaticGUIItem(border))
 *     .item('A', new DynamicGUIItem(option1, handler1))
 *     .item('B', new DynamicGUIItem(option2, handler2))
 *     .item('C', new DynamicGUIItem(option3, handler3))
 *     .build();
 * menu.openTo(player);
 * }</pre>
 *
 * <h3>Confirmation Dialog</h3>
 * <pre>{@code
 * NormalGUI confirm = NormalGUI.builder()
 *     .manager(guiManager)
 *     .titled("<yellow>Are you sure?</yellow>")
 *     .format("#########\n# Y # N #\n#########")
 *     .item('#', border)
 *     .item('Y', new DynamicGUIItem(confirmButton, event -> {
 *         event.setCancelled(true);
 *         event.getPlayer().closeInventory();
 *         onConfirm.run();
 *     }))
 *     .item('N', new DynamicGUIItem(cancelButton, event -> {
 *         event.setCancelled(true);
 *         event.getPlayer().closeInventory();
 *     }))
 *     .build();
 * }</pre>
 *
 * <h3>Player Selector</h3>
 * <pre>{@code
 * List<GUIItem> players = MinecraftServer.getConnectionManager()
 *     .getOnlinePlayers()
 *     .stream()
 *     .map(p -> new DynamicGUIItem(
 *         ItemStack.of(Material.PLAYER_HEAD)
 *             .withCustomName(Component.text(p.getUsername())),
 *         event -> {
 *             event.setCancelled(true);
 *             onPlayerSelected(p);
 *         }
 *     ))
 *     .collect(Collectors.toList());
 *
 * ScrollGUI selector = ScrollGUI.builder()
 *     .manager(guiManager)
 *     .titled("<aqua>Select Player</aqua>")
 *     .format("#########\n#       #\n###<#>###")
 *     .item('#', border)
 *     .item(' ', Indicator.CONTENT_SLOT)
 *     .item('<', new ScrollGUIItem(-7, scrollBackArrow))
 *     .item('>', new ScrollGUIItem(7, scrollForwardArrow))
 *     .scrollContent(players)
 *     .build();
 * }</pre>
 *
 * <h2>Best Practices</h2>
 *
 * <h3>Always Cancel Events</h3>
 * <p>
 * Unless you want items to be moveable, always cancel the click event:
 * <pre>{@code
 * new DynamicGUIItem(item, event -> {
 *     event.setCancelled(true);  // Prevents item from being moved
 *     // Your logic here
 * });
 * }</pre>
 *
 * <h3>Reuse Static Items</h3>
 * <p>
 * Static items can be reused across multiple GUIs:
 * <pre>{@code
 * public static final StaticGUIItem BORDER = new StaticGUIItem(
 *     ItemStack.of(Material.GRAY_STAINED_GLASS_PANE)
 *         .withCustomName(Component.empty())
 * );
 * }</pre>
 *
 * <h3>Use Format Constants</h3>
 * <p>
 * Define reusable format strings:
 * <pre>{@code
 * public static final String SMALL_MENU = """
 *     #########
 *     # X Y Z #
 *     #########""";
 * }</pre>
 *
 * <h3>Clean Up Resources</h3>
 * <p>
 * Unregister GUIs when they're no longer needed:
 * <pre>{@code
 * manager.unregisterGUI(gui);
 * }</pre>
 *
 * <h2>Thread Safety</h2>
 * <p>
 * GUI operations should be performed on the Minestom server thread.
 * The library uses WeakHashMap internally, allowing GUIs to be garbage collected
 * when no longer referenced.
 *
 * <h2>Dependencies</h2>
 * <ul>
 *   <li>Minestom - Core server implementation</li>
 *   <li>Adventure API - Text components and formatting</li>
 * </ul>
 *
 * <h2>Further Reading</h2>
 * <ul>
 *   <li>README.md - Comprehensive documentation and examples</li>
 *   <li>API.md - Complete API reference</li>
 *   <li>USAGE_EXAMPLES.md - Real-world usage examples</li>
 *   <li>CONTRIBUTING.md - Contributing guidelines</li>
 * </ul>
 *
 * @see com.github.echolightmc.msguis.GUIManager
 * @see com.github.echolightmc.msguis.NormalGUI
 * @see com.github.echolightmc.msguis.ScrollGUI
 * @see com.github.echolightmc.msguis.GUIItem
 * @see com.github.echolightmc.msguis.StaticGUIItem
 * @see com.github.echolightmc.msguis.DynamicGUIItem
 * @see com.github.echolightmc.msguis.ScrollGUIItem
 * 
 * @since 1.0.0
 * @version 1.0.0
 */
package com.github.echolightmc.msguis;
