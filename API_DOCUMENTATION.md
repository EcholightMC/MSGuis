# MSGUIs API Documentation

## Table of Contents

1. [Overview](#overview)
2. [Getting Started](#getting-started)
3. [Core Components](#core-components)
4. [API Reference](#api-reference)
   - [GUIManager](#guimanager)
   - [ChestGUI](#chestgui)
   - [NormalGUI](#normalgui)
   - [ScrollGUI](#scrollgui)
   - [GUIItem](#guiitem)
   - [StaticGUIItem](#staticguiitem)
   - [DynamicGUIItem](#dynamicguiitem)
   - [ScrollGUIItem](#scrollguiitem)
   - [ChestType](#chesttype)
   - [Indicator](#indicator)
5. [Examples](#examples)
6. [Best Practices](#best-practices)

---

## Overview

MSGUIs is a powerful and easy-to-use GUI library for Minestom servers. It provides a flexible system for creating chest-based GUIs with support for static items, dynamic items, and scrollable content areas.

### Key Features

- **Format-based GUI Layout**: Define GUI layouts using simple character-based format strings
- **Multiple GUI Types**: Support for normal GUIs and scrollable GUIs
- **Dynamic Items**: Items that can be updated at runtime
- **Scrollable Content**: Built-in support for scrollable item lists
- **Event Handling**: Automatic click event handling and cancellation
- **MiniMessage Support**: Rich text formatting using MiniMessage

---

## Getting Started

### Initialization

First, create a `GUIManager` instance and register it with your Minestom server's global event handler:

```java
import com.github.echolightmc.msguis.GUIManager;
import net.minestom.server.MinecraftServer;

GUIManager guiManager = new GUIManager(MinecraftServer.getGlobalEventHandler());
```

### Basic GUI Creation

Create a simple GUI using the builder pattern:

```java
import com.github.echolightmc.msguis.NormalGUI;
import com.github.echolightmc.msguis.StaticGUIItem;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;

NormalGUI gui = NormalGUI.builder()
    .manager(guiManager)
    .titled("My GUI")
    .format("#########" +
            "#       #" +
            "#       #" +
            "#########")
    .item('#', new StaticGUIItem(ItemStack.of(Material.GRAY_STAINED_GLASS_PANE)))
    .build();

// Open the GUI to a player
gui.openTo(player);
```

---

## Core Components

### Architecture Overview

```
GUIManager
    └── Manages multiple ChestGUI instances
        ├── NormalGUI (standard GUI)
        └── ScrollGUI (scrollable content GUI)
            └── Contains GUIItem instances
                ├── StaticGUIItem (immutable)
                ├── DynamicGUIItem (mutable)
                └── ScrollGUIItem (triggers scrolling)
```

---

## API Reference

### GUIManager

The `GUIManager` class manages GUI registration and handles click events for all registered GUIs.

#### Constructor

```java
public GUIManager(GlobalEventHandler globalEventHandler)
```

Creates a new `GUIManager` instance and registers it with the provided global event handler.

**Parameters:**
- `globalEventHandler` - The Minestom global event handler to register GUI events with

**Example:**
```java
GUIManager guiManager = new GUIManager(MinecraftServer.getGlobalEventHandler());
```

#### Methods

##### `void registerGUI(Inventory inventory, ChestGUI chestGUI)`
*Package-private method - called automatically by builders*

Registers a GUI with the manager. This is automatically called when building a GUI.

##### `public boolean unregisterGUI(ChestGUI chestGUI)`

Unregisters a GUI from the manager.

**Parameters:**
- `chestGUI` - The GUI to unregister

**Returns:**
- `true` if the GUI was successfully unregistered, `false` otherwise

**Example:**
```java
boolean removed = guiManager.unregisterGUI(myGUI);
```

---

### ChestGUI

Abstract base class for all GUI implementations. Provides core functionality for format-based GUI layouts.

#### Format String

The format string defines the layout of your GUI. Each character represents a slot, and characters are mapped to `GUIItem` instances.

**Rules:**
- Format must be divisible by 9 (9 slots per row)
- Maximum of 6 rows (54 slots total)
- Newlines are automatically removed
- Each unique character can be mapped to a `GUIItem`

**Example Format:**
```
"#########" +  // Row 1: 9 border slots
"#       #" +  // Row 2: border, 7 empty, border
"#       #" +  // Row 3: border, 7 empty, border
"#########"   // Row 4: 9 border slots
```

#### Methods

##### `public void setItem(char character, GUIItem item)`

Sets or updates the item associated with a character in the format string.

**Parameters:**
- `character` - The character in the format string to map
- `item` - The `GUIItem` to associate with the character

**Example:**
```java
gui.setItem('X', new StaticGUIItem(ItemStack.of(Material.DIAMOND)));
```

##### `public void setFormat(String format)`

Updates the GUI format string and regenerates the layout.

**Parameters:**
- `format` - The new format string (must follow format rules)

**Example:**
```java
gui.setFormat("#########" +
              "#   X   #" +
              "#########");
```

##### `public void refreshDynamicItems()`

Refreshes all dynamic items in the GUI. Useful when dynamic items have been updated externally.

**Example:**
```java
dynamicItem.setItem(newItemStack);
gui.refreshDynamicItems(); // Update the GUI display
```

##### `public Inventory getInventory()`

Returns the underlying Minestom `Inventory` instance.

**Returns:**
- The `Inventory` instance for this GUI

##### `public ChestType getChestType()`

Returns the chest type (size) of this GUI.

**Returns:**
- The `ChestType` enum value

##### `public GUIManager getGuiManager()`

Returns the `GUIManager` that manages this GUI.

**Returns:**
- The associated `GUIManager` instance

##### `public Map<Indicator, Character> getIndicators()`

Returns the map of indicators to characters used in this GUI.

**Returns:**
- Map of `Indicator` to `Character` mappings

##### `public void openTo(Player... players)`

Opens the GUI to one or more players.

**Parameters:**
- `players` - Variable number of players to open the GUI for

**Example:**
```java
gui.openTo(player1, player2, player3);
```

#### Builder Pattern

All `ChestGUI` subclasses use a builder pattern through the abstract `GUIBuilder` class.

##### `public B format(String format)`

Sets the format string for the GUI. Automatically determines the `ChestType` based on row count.

**Parameters:**
- `format` - The format string

**Returns:**
- The builder instance for method chaining

**Throws:**
- `IllegalArgumentException` if format is not divisible by 9 or has more than 6 rows

**Example:**
```java
.builder()
.format("#########" +
        "#       #" +
        "#########")
```

##### `public B titled(String title, MiniMessage... miniMessage)`

Sets the GUI title using MiniMessage format.

**Parameters:**
- `title` - The title in MiniMessage format (e.g., `<red>My GUI`)
- `miniMessage` - Optional MiniMessage instance (uses default if not provided)

**Returns:**
- The builder instance for method chaining

**Example:**
```java
.titled("<green>Welcome!")
.titled("<red>Error", customMiniMessage)
```

##### `public final B item(char character, GUIItem item)`

Maps a character to a `GUIItem`.

**Parameters:**
- `character` - The character in the format string
- `item` - The `GUIItem` to map

**Returns:**
- The builder instance for method chaining

**Example:**
```java
.item('#', new StaticGUIItem(ItemStack.of(Material.GRAY_STAINED_GLASS_PANE)))
```

##### `public B item(char character, Indicator indicator)`

Maps a character to an indicator (for special slot types like content slots in scroll GUIs).

**Parameters:**
- `character` - The character in the format string
- `indicator` - The `Indicator` to map

**Returns:**
- The builder instance for method chaining

**Throws:**
- `IllegalArgumentException` if the indicator is already set

**Example:**
```java
.item(' ', Indicator.CONTENT_SLOT)
```

##### `public B manager(GUIManager manager)`

Sets the `GUIManager` for the GUI.

**Parameters:**
- `manager` - The `GUIManager` instance

**Returns:**
- The builder instance for method chaining

**Example:**
```java
.manager(guiManager)
```

##### `public T build()`

Builds and registers the GUI with the manager.

**Returns:**
- The constructed GUI instance

---

### NormalGUI

A standard GUI implementation without scrollable content.

#### Static Methods

##### `public static NormalGUIBuilder builder()`

Creates a new builder for a `NormalGUI`.

**Returns:**
- A new `NormalGUIBuilder` instance

**Example:**
```java
NormalGUI gui = NormalGUI.builder()
    .manager(guiManager)
    .titled("Menu")
    .format("#########" +
            "#       #" +
            "#########")
    .item('#', borderItem)
    .build();
```

---

### ScrollGUI

A GUI with scrollable content areas. Content items are displayed in slots marked with the `CONTENT_SLOT` indicator.

#### Methods

##### `public void scroll(int amount)`

Scrolls the content by the specified amount.

**Parameters:**
- `amount` - Positive values scroll down (show later items), negative values scroll up (show earlier items)

**Example:**
```java
scrollGUI.scroll(7);  // Scroll down 7 items
scrollGUI.scroll(-7); // Scroll up 7 items
```

##### `public void setContent(List<? extends GUIItem> content)`

Sets the scrollable content list.

**Parameters:**
- `content` - List of `GUIItem` instances to display

**Example:**
```java
List<GUIItem> items = Arrays.asList(
    new StaticGUIItem(ItemStack.of(Material.DIAMOND)),
    new StaticGUIItem(ItemStack.of(Material.EMERALD))
);
scrollGUI.setContent(items);
```

##### `public List<? extends GUIItem> getContent()`

Returns the current content list.

**Returns:**
- The list of content items

##### `public int getCurrentScrollPos()`

Returns the current scroll position.

**Returns:**
- The current scroll position index

#### Static Methods

##### `public static ScrollGUIBuilder builder()`

Creates a new builder for a `ScrollGUI`.

**Returns:**
- A new `ScrollGUIBuilder` instance

#### Builder Methods

##### `public ScrollGUIBuilder scrollContent(List<? extends GUIItem> content)`

Sets the scrollable content for the GUI.

**Parameters:**
- `content` - List of `GUIItem` instances

**Returns:**
- The builder instance for method chaining

**Example:**
```java
ScrollGUI gui = ScrollGUI.builder()
    .manager(guiManager)
    .titled("<blue>Scrollable Items")
    .format("#########" +
            "#       #" +
            "#       #" +
            "#       #" +
            "###<#>###")
    .item('#', borderItem)
    .item(' ', Indicator.CONTENT_SLOT)
    .item('<', new ScrollGUIItem(-7, scrollBackItem))
    .item('>', new ScrollGUIItem(7, scrollForwardItem))
    .scrollContent(itemList)
    .build();
```

---

### GUIItem

Abstract base class for all GUI items. Defines the contract for items that can be placed in GUIs.

#### Methods

##### `public abstract ItemStack getItem()`

Returns the `ItemStack` to display for this item.

**Returns:**
- The `ItemStack` instance

##### `public abstract void handleClick(InventoryPreClickEvent event)`

Handles click events on this item.

**Parameters:**
- `event` - The inventory pre-click event

**Note:** Override this method to implement custom click behavior. Remember to cancel the event if you don't want items to be taken.

##### `public ChestGUI getOwningGUI()`

Returns the GUI that owns this item.

**Returns:**
- The `ChestGUI` instance, or `null` if not yet assigned

---

### StaticGUIItem

A static, immutable GUI item. The item stack cannot be changed after creation.

#### Constructor

```java
public StaticGUIItem(ItemStack item)
```

Creates a new static GUI item.

**Parameters:**
- `item` - The `ItemStack` to display

**Example:**
```java
StaticGUIItem border = new StaticGUIItem(
    ItemStack.of(Material.GRAY_STAINED_GLASS_PANE)
        .withCustomName(Component.empty())
);
```

#### Behavior

- Click events are automatically cancelled
- Item stack cannot be changed after creation
- Use for borders, decorative elements, and static menu items

---

### DynamicGUIItem

A dynamic GUI item that can be updated at runtime. Changes are automatically reflected in the GUI.

#### Constructor

```java
public DynamicGUIItem(ItemStack item, Consumer<InventoryPreClickEvent> clickConsumer)
```

Creates a new dynamic GUI item.

**Parameters:**
- `item` - The initial `ItemStack` to display
- `clickConsumer` - Consumer function to handle click events

**Example:**
```java
DynamicGUIItem item = new DynamicGUIItem(
    ItemStack.of(Material.DIAMOND),
    event -> {
        event.setCancelled(true);
        Player player = (Player) event.getPlayer();
        player.sendMessage("You clicked the diamond!");
    }
);
```

#### Methods

##### `public void setItem(ItemStack item)`

Updates the item stack. The GUI is automatically refreshed.

**Parameters:**
- `item` - The new `ItemStack` to display

**Example:**
```java
dynamicItem.setItem(ItemStack.of(Material.EMERALD));
// GUI automatically updates
```

##### `public Consumer<InventoryPreClickEvent> getClickConsumer()`

Returns the click consumer function.

**Returns:**
- The click consumer

##### `public void setClickConsumer(Consumer<InventoryPreClickEvent> clickConsumer)`

Updates the click consumer function.

**Parameters:**
- `clickConsumer` - The new click consumer

**Example:**
```java
dynamicItem.setClickConsumer(event -> {
    event.setCancelled(true);
    // New click behavior
});
```

---

### ScrollGUIItem

A special GUI item that triggers scrolling when clicked. Used in `ScrollGUI` instances.

#### Constructor

```java
public ScrollGUIItem(int scroll, ItemStack itemStack)
```

Creates a new scroll GUI item.

**Parameters:**
- `scroll` - The scroll amount (positive = scroll down, negative = scroll up, cannot be zero)
- `itemStack` - The `ItemStack` to display

**Throws:**
- `IllegalArgumentException` if scroll amount is zero

**Example:**
```java
ScrollGUIItem scrollForward = new ScrollGUIItem(
    7, 
    ItemStack.of(Material.ARROW)
        .withCustomName(Component.text("Next Page"))
);

ScrollGUIItem scrollBack = new ScrollGUIItem(
    -7,
    ItemStack.of(Material.ARROW)
        .withCustomName(Component.text("Previous Page"))
);
```

#### Methods

##### `public int getScroll()`

Returns the scroll amount.

**Returns:**
- The scroll amount (positive or negative integer)

#### Behavior

- Automatically cancels click events
- Scrolls the owning `ScrollGUI` by the specified amount
- Only works when used in a `ScrollGUI` instance

---

### ChestType

Enumeration of available chest sizes.

#### Values

- `ROWS_1` - Single row (9 slots)
- `ROWS_2` - Two rows (18 slots)
- `ROWS_3` - Three rows (27 slots) - **Default**
- `ROWS_4` - Four rows (36 slots)
- `ROWS_5` - Five rows (45 slots)
- `ROWS_6` - Six rows (54 slots)

#### Methods

##### `public InventoryType getMinestomInventoryType()`

Returns the corresponding Minestom `InventoryType`.

**Returns:**
- The `InventoryType` instance

##### `public int getRowCount()`

Returns the number of rows.

**Returns:**
- The row count (1-6)

**Note:** The `ChestType` is automatically determined from the format string when using builders.

---

### Indicator

Enumeration of special slot indicators for advanced GUI features.

#### Values

- `INPUT_SLOT` - Reserved for input slots (planned feature)
- `OUTPUT_SLOT` - Reserved for output slots (planned feature)
- `CONTENT_SLOT` - Used in `ScrollGUI` to mark scrollable content areas

**Example:**
```java
.item(' ', Indicator.CONTENT_SLOT)
```

---

## Examples

### Example 1: Simple Menu GUI

```java
import com.github.echolightmc.msguis.*;
import net.kyori.adventure.text.Component;
import net.minestom.server.entity.Player;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;

// Initialize manager
GUIManager guiManager = new GUIManager(MinecraftServer.getGlobalEventHandler());

// Create border item
StaticGUIItem border = new StaticGUIItem(
    ItemStack.of(Material.GRAY_STAINED_GLASS_PANE)
        .withCustomName(Component.empty())
);

// Create menu items
StaticGUIItem shopItem = new StaticGUIItem(
    ItemStack.of(Material.EMERALD)
        .withCustomName(Component.text("Shop"))
);

StaticGUIItem settingsItem = new StaticGUIItem(
    ItemStack.of(Material.REDSTONE)
        .withCustomName(Component.text("Settings"))
);

// Build GUI
NormalGUI menu = NormalGUI.builder()
    .manager(guiManager)
    .titled("<green>Main Menu")
    .format("#########" +
            "#   S   #" +
            "#   T   #" +
            "#########")
    .item('#', border)
    .item('S', shopItem)
    .item('T', settingsItem)
    .build();

// Open to player
menu.openTo(player);
```

### Example 2: GUI with Click Handlers

```java
import java.util.function.Consumer;
import net.minestom.server.event.inventory.InventoryPreClickEvent;

// Create items with click handlers
DynamicGUIItem shopItem = new DynamicGUIItem(
    ItemStack.of(Material.EMERALD)
        .withCustomName(Component.text("Shop")),
    event -> {
        event.setCancelled(true);
        Player player = (Player) event.getPlayer();
        player.sendMessage("Opening shop...");
        // Open shop GUI
    }
);

DynamicGUIItem settingsItem = new DynamicGUIItem(
    ItemStack.of(Material.REDSTONE)
        .withCustomName(Component.text("Settings")),
    event -> {
        event.setCancelled(true);
        Player player = (Player) event.getPlayer();
        player.sendMessage("Opening settings...");
        // Open settings GUI
    }
);

NormalGUI menu = NormalGUI.builder()
    .manager(guiManager)
    .titled("<green>Main Menu")
    .format("#########" +
            "#   S   #" +
            "#   T   #" +
            "#########")
    .item('#', border)
    .item('S', shopItem)
    .item('T', settingsItem)
    .build();
```

### Example 3: Scrollable Item List

```java
import java.util.List;
import java.util.stream.Collectors;

// Create scroll buttons
ScrollGUIItem scrollBack = new ScrollGUIItem(
    -7,
    ItemStack.of(Material.ARROW)
        .withCustomName(Component.text("Previous Page"))
);

ScrollGUIItem scrollForward = new ScrollGUIItem(
    7,
    ItemStack.of(Material.ARROW)
        .withCustomName(Component.text("Next Page"))
);

// Create content items
List<GUIItem> items = List.of(
    Material.DIAMOND,
    Material.EMERALD,
    Material.GOLD_INGOT,
    Material.IRON_INGOT
).stream()
    .map(material -> new StaticGUIItem(ItemStack.of(material)))
    .collect(Collectors.toList());

// Build scroll GUI
ScrollGUI scrollGUI = ScrollGUI.builder()
    .manager(guiManager)
    .titled("<blue>Item List")
    .format("#########" +
            "#       #" +
            "#       #" +
            "#       #" +
            "#       #" +
            "#       #" +
            "#       #" +
            "###<#>###")
    .item('#', border)
    .item(' ', Indicator.CONTENT_SLOT)  // 7 content slots per row
    .item('<', scrollBack)
    .item('>', scrollForward)
    .scrollContent(items)
    .build();

scrollGUI.openTo(player);
```

### Example 4: Dynamic Item Updates

```java
// Create a dynamic item
DynamicGUIItem counterItem = new DynamicGUIItem(
    ItemStack.of(Material.DIAMOND)
        .withCustomName(Component.text("Clicks: 0")),
    event -> {
        event.setCancelled(true);
        // Update the item
        int clicks = getClickCount(event.getPlayer());
        clicks++;
        setClickCount(event.getPlayer(), clicks);
        
        counterItem.setItem(
            ItemStack.of(Material.DIAMOND)
                .withCustomName(Component.text("Clicks: " + clicks))
        );
        // GUI automatically updates
    }
);

NormalGUI gui = NormalGUI.builder()
    .manager(guiManager)
    .titled("<yellow>Click Counter")
    .format("#########" +
            "#   C   #" +
            "#########")
    .item('#', border)
    .item('C', counterItem)
    .build();
```

### Example 5: Multi-Player GUI

```java
NormalGUI gui = NormalGUI.builder()
    .manager(guiManager)
    .titled("<green>Welcome")
    .format("#########" +
            "#       #" +
            "#########")
    .item('#', border)
    .build();

// Open to multiple players
gui.openTo(player1, player2, player3);
```

### Example 6: Format Updates

```java
NormalGUI gui = NormalGUI.builder()
    .manager(guiManager)
    .titled("Dynamic Layout")
    .format("#########" +
            "#       #" +
            "#########")
    .item('#', border)
    .build();

// Later, update the format
gui.setFormat("#########" +
              "#   X   #" +
              "#   Y   #" +
              "#########");

// Add new items
gui.setItem('X', new StaticGUIItem(ItemStack.of(Material.DIAMOND)));
gui.setItem('Y', new StaticGUIItem(ItemStack.of(Material.EMERALD)));
```

---

## Best Practices

### 1. Reuse GUIManager Instance

Create a single `GUIManager` instance and reuse it for all GUIs:

```java
// Good
public static final GUIManager GUI_MANAGER = 
    new GUIManager(MinecraftServer.getGlobalEventHandler());

// Bad - creates unnecessary event listeners
GUIManager manager1 = new GUIManager(globalEventHandler);
GUIManager manager2 = new GUIManager(globalEventHandler);
```

### 2. Always Cancel Click Events

When handling clicks, always cancel the event to prevent item removal:

```java
DynamicGUIItem item = new DynamicGUIItem(
    itemStack,
    event -> {
        event.setCancelled(true); // Important!
        // Your logic here
    }
);
```

### 3. Use StaticGUIItem for Immutable Items

For items that never change (borders, decorative elements), use `StaticGUIItem`:

```java
// Good - uses less memory
StaticGUIItem border = new StaticGUIItem(borderItemStack);

// Less efficient - unnecessary overhead
DynamicGUIItem border = new DynamicGUIItem(borderItemStack, event -> {
    event.setCancelled(true);
});
```

### 4. Plan Your Format Strings

Design your format strings carefully to ensure they're readable and maintainable:

```java
// Good - clear and readable
String format = "#########" +  // Top border
                "#       #" +  // Empty row
                "#   X   #" +  // Content row
                "#########";   // Bottom border

// Harder to read
String format = "#########" + "#       #" + "#   X   #" + "#########";
```

### 5. Handle Null Content in ScrollGUI

When creating scroll GUIs, handle empty or null content gracefully:

```java
List<GUIItem> content = getItems();
if (content == null || content.isEmpty()) {
    content = List.of(new StaticGUIItem(
        ItemStack.of(Material.BARRIER)
            .withCustomName(Component.text("No items available"))
    ));
}
scrollGUI.setContent(content);
```

### 6. Use MiniMessage for Rich Text

Take advantage of MiniMessage for colorful and formatted titles:

```java
.titled("<green>Success!")
.titled("<red><bold>Error!</bold>")
.titled("<gradient:red:blue>Rainbow Title")
```

### 7. Store GUI References

Keep references to GUIs if you need to update them later:

```java
// Store reference
NormalGUI myGUI = NormalGUI.builder()
    .manager(guiManager)
    // ... build
    .build();

// Later, update items
myGUI.setItem('X', newItem);
myGUI.refreshDynamicItems();
```

### 8. Validate Format Strings

Ensure your format strings follow the rules:
- Must be divisible by 9
- Maximum 6 rows (54 slots)
- All characters used must be mapped to items or indicators

### 9. Use ScrollGUIItem Correctly

When creating scroll GUIs, ensure scroll amounts match your content slot count:

```java
// If you have 7 content slots per row
.item('<', new ScrollGUIItem(-7, scrollBackItem))  // Scroll up 7 (one row)
.item('>', new ScrollGUIItem(7, scrollForwardItem)) // Scroll down 7 (one row)
```

### 10. Clean Up Unused GUIs

Unregister GUIs when they're no longer needed to free resources:

```java
guiManager.unregisterGUI(oldGUI);
```

---

## Troubleshooting

### GUI Not Opening

- Ensure `GUIManager` is initialized with the global event handler
- Check that the GUI was built successfully (no exceptions)
- Verify the player instance is valid

### Items Not Displaying

- Ensure all characters in the format string are mapped to items
- Check that `ItemStack` instances are valid
- Verify the format string is correct (divisible by 9, max 6 rows)

### Clicks Not Working

- Ensure `GUIManager` is properly initialized
- Check that click events are being handled (not cancelled elsewhere)
- Verify the GUI is registered with the manager

### ScrollGUI Not Scrolling

- Ensure `CONTENT_SLOT` indicator is properly set
- Verify `ScrollGUIItem` instances are used (not regular items)
- Check that content list is not empty
- Verify scroll amounts are non-zero

### Dynamic Items Not Updating

- Call `setItem()` on the `DynamicGUIItem` instance
- The GUI updates automatically - no need to call `refreshDynamicItems()` for single item updates
- Use `refreshDynamicItems()` to refresh all dynamic items at once

---

## License

This library is provided as-is. Refer to the LICENSE file for details.
