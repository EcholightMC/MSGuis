# MSGuis API Reference

This document provides a quick reference for all public APIs in the MSGuis library.

## Table of Contents

- [Core Classes](#core-classes)
  - [GUIManager](#guimanager)
  - [ChestGUI](#chestgui)
  - [NormalGUI](#normalgui)
  - [ScrollGUI](#scrollgui)
- [Item Types](#item-types)
  - [GUIItem](#guiitem)
  - [StaticGUIItem](#staticguiitem)
  - [DynamicGUIItem](#dynamicguiitem)
  - [ScrollGUIItem](#scrollguiitem)
- [Builders](#builders)
  - [GUIBuilder](#guibuilder)
  - [NormalGUIBuilder](#normalguibuilder)
  - [ScrollGUIBuilder](#scrollguibuilder)
- [Enums](#enums)
  - [ChestType](#chesttype)
  - [Indicator](#indicator)

---

## Core Classes

### GUIManager

**Package:** `com.github.echolightmc.msguis`

Central manager for handling GUI events and registration.

#### Constructor

```java
GUIManager(GlobalEventHandler globalEventHandler)
```

Creates a new GUI manager and registers it with the global event handler.

**Parameters:**
- `globalEventHandler` - The Minestom global event handler

**Example:**
```java
GUIManager manager = new GUIManager(MinecraftServer.getGlobalEventHandler());
```

#### Methods

| Method | Return Type | Description |
|--------|-------------|-------------|
| `unregisterGUI(ChestGUI gui)` | `boolean` | Unregisters a GUI from this manager |

---

### ChestGUI

**Package:** `com.github.echolightmc.msguis`

Abstract base class for all chest-based GUIs. Do not instantiate directly.

#### Methods

| Method | Return Type | Description |
|--------|-------------|-------------|
| `setItem(char character, GUIItem item)` | `void` | Updates the item associated with a character |
| `setFormat(String format)` | `void` | Changes the GUI's format string |
| `refreshDynamicItems()` | `void` | Refreshes all dynamic items in the GUI |
| `getInventory()` | `Inventory` | Returns the underlying Minestom inventory |
| `getChestType()` | `ChestType` | Returns the chest type (size) |
| `getGuiManager()` | `GUIManager` | Returns the GUI manager |
| `getIndicators()` | `Map<Indicator, Character>` | Returns the indicator map |
| `openTo(Player... players)` | `void` | Opens the GUI for one or more players |

**Format String Rules:**
- Must be divisible by 9 (chest row width)
- Maximum of 6 rows (54 slots)
- Each character must be mapped to a GUIItem or Indicator

---

### NormalGUI

**Package:** `com.github.echolightmc.msguis`

**Extends:** `ChestGUI`

A standard chest GUI with fixed item positions.

#### Static Methods

| Method | Return Type | Description |
|--------|-------------|-------------|
| `builder()` | `NormalGUIBuilder` | Creates a new builder for constructing a NormalGUI |

#### Inherited Methods

All methods from [ChestGUI](#chestgui)

**Example:**
```java
NormalGUI gui = NormalGUI.builder()
    .manager(guiManager)
    .titled("<green>Menu</green>")
    .format("#########\n# X Y Z #\n#########")
    .item('#', borderItem)
    .item('X', button1)
    .item('Y', button2)
    .item('Z', button3)
    .build();
```

---

### ScrollGUI

**Package:** `com.github.echolightmc.msguis`

**Extends:** `ChestGUI`

A chest GUI with scrollable content areas for displaying large lists.

#### Static Methods

| Method | Return Type | Description |
|--------|-------------|-------------|
| `builder()` | `ScrollGUIBuilder` | Creates a new builder for constructing a ScrollGUI |

#### Methods

| Method | Return Type | Description |
|--------|-------------|-------------|
| `scroll(int amount)` | `void` | Scrolls by the specified amount (positive = down, negative = up) |
| `setContent(List<? extends GUIItem> content)` | `void` | Updates the scrollable content list |
| `getContent()` | `List<? extends GUIItem>` | Returns the current content list |
| `getCurrentScrollPos()` | `int` | Returns the current scroll position (0-based) |

#### Inherited Methods

All methods from [ChestGUI](#chestgui)

**Example:**
```java
ScrollGUI gui = ScrollGUI.builder()
    .manager(guiManager)
    .titled("<blue>Browser</blue>")
    .format("#########\n#       #\n###<#>###")
    .item('#', borderItem)
    .item(' ', Indicator.CONTENT_SLOT)
    .item('<', new ScrollGUIItem(-7, scrollBack))
    .item('>', new ScrollGUIItem(7, scrollForward))
    .scrollContent(itemList)
    .build();
```

---

## Item Types

### GUIItem

**Package:** `com.github.echolightmc.msguis`

Abstract base class for all GUI items. Do not extend directly.

#### Abstract Methods

| Method | Return Type | Description |
|--------|-------------|-------------|
| `getItem()` | `ItemStack` | Returns the visual representation of this item |
| `handleClick(InventoryPreClickEvent event)` | `void` | Handles click events for this item |

#### Methods

| Method | Return Type | Description |
|--------|-------------|-------------|
| `getOwningGUI()` | `ChestGUI` | Returns the GUI that contains this item |

---

### StaticGUIItem

**Package:** `com.github.echolightmc.msguis`

**Extends:** `GUIItem`

A simple, immutable GUI item that cannot be interacted with.

#### Constructor

```java
StaticGUIItem(ItemStack item)
```

**Parameters:**
- `item` - The ItemStack to display

**Use Cases:**
- Borders and backgrounds
- Decorative elements
- Information displays

**Example:**
```java
ItemStack borderStack = ItemStack.of(Material.GRAY_STAINED_GLASS_PANE)
    .withCustomName(Component.empty());
StaticGUIItem border = new StaticGUIItem(borderStack);
```

---

### DynamicGUIItem

**Package:** `com.github.echolightmc.msguis`

**Extends:** `GUIItem`

A GUI item that can change its appearance and has custom click behavior.

#### Constructor

```java
DynamicGUIItem(ItemStack item, Consumer<InventoryPreClickEvent> clickConsumer)
```

**Parameters:**
- `item` - The initial ItemStack to display
- `clickConsumer` - The function to execute when clicked

#### Methods

| Method | Return Type | Description |
|--------|-------------|-------------|
| `setItem(ItemStack item)` | `void` | Updates the visual appearance |
| `getClickConsumer()` | `Consumer<InventoryPreClickEvent>` | Returns the click consumer |
| `setClickConsumer(Consumer<InventoryPreClickEvent> consumer)` | `void` | Updates the click behavior |

**Use Cases:**
- Interactive buttons
- Toggleable options
- Counter displays
- Status indicators

**Example:**
```java
DynamicGUIItem button = new DynamicGUIItem(
    ItemStack.of(Material.DIAMOND).withCustomName(Component.text("Click Me!")),
    event -> {
        event.setCancelled(true);
        event.getPlayer().sendMessage("Clicked!");
    }
);
```

---

### ScrollGUIItem

**Package:** `com.github.echolightmc.msguis`

**Extends:** `GUIItem`

A specialized GUI item for scrolling within a ScrollGUI.

#### Constructor

```java
ScrollGUIItem(int scroll, ItemStack itemStack)
```

**Parameters:**
- `scroll` - The scroll amount (positive = down, negative = up, cannot be 0)
- `itemStack` - The visual representation

**Throws:**
- `IllegalArgumentException` - If scroll is zero

#### Methods

| Method | Return Type | Description |
|--------|-------------|-------------|
| `getScroll()` | `int` | Returns the scroll amount |

**Common Scroll Amounts:**
- `1` or `-1` - One slot
- `7` or `-7` - One row
- `21` or `-21` - Three rows (one "page")

**Example:**
```java
ScrollGUIItem nextPage = new ScrollGUIItem(
    7,  // Scroll down by one row
    ItemStack.of(Material.ARROW).withCustomName(Component.text("Next Page"))
);
```

---

## Builders

### GUIBuilder

**Package:** `com.github.echolightmc.msguis`

**Type Parameters:**
- `T` - The type of GUI being built
- `B` - The type of builder (for method chaining)

Abstract builder class for constructing ChestGUI instances.

#### Methods

| Method | Return Type | Description |
|--------|-------------|-------------|
| `format(String format)` | `B` | Sets the format string |
| `titled(String title, MiniMessage... miniMessage)` | `B` | Sets the title with MiniMessage formatting |
| `item(char character, GUIItem item)` | `B` | Maps a character to a GUI item |
| `item(char character, Indicator indicator)` | `B` | Maps a character to an indicator |
| `manager(GUIManager manager)` | `B` | Sets the GUI manager (required) |
| `build()` | `T` | Builds and registers the GUI |

**Throws:**
- `IllegalArgumentException` - If format is invalid

---

### NormalGUIBuilder

**Package:** `com.github.echolightmc.msguis`

**Extends:** `GUIBuilder<NormalGUI, NormalGUIBuilder>`

Builder for constructing NormalGUI instances.

#### Inherited Methods

All methods from [GUIBuilder](#guibuilder)

**Example:**
```java
NormalGUI gui = NormalGUI.builder()
    .manager(guiManager)
    .titled("<red>Shop</red>")
    .format("###\n# #\n###")
    .item('#', borderItem)
    .build();
```

---

### ScrollGUIBuilder

**Package:** `com.github.echolightmc.msguis`

**Extends:** `GUIBuilder<ScrollGUI, ScrollGUIBuilder>`

Builder for constructing ScrollGUI instances.

#### Methods

| Method | Return Type | Description |
|--------|-------------|-------------|
| `scrollContent(List<? extends GUIItem> content)` | `ScrollGUIBuilder` | Sets the initial scrollable content |

#### Inherited Methods

All methods from [GUIBuilder](#guibuilder)

**Example:**
```java
ScrollGUI gui = ScrollGUI.builder()
    .manager(guiManager)
    .titled("<blue>Browser</blue>")
    .format("#       #")
    .item(' ', Indicator.CONTENT_SLOT)
    .scrollContent(itemList)
    .build();
```

---

## Enums

### ChestType

**Package:** `com.github.echolightmc.msguis`

Represents the different chest sizes available for GUIs.

#### Values

| Value | Rows | Slots | Description |
|-------|------|-------|-------------|
| `ROWS_1` | 1 | 9 | Smallest chest |
| `ROWS_2` | 2 | 18 | |
| `ROWS_3` | 3 | 27 | Default size |
| `ROWS_4` | 4 | 36 | |
| `ROWS_5` | 5 | 45 | |
| `ROWS_6` | 6 | 54 | Maximum size |

#### Methods

| Method | Return Type | Description |
|--------|-------------|-------------|
| `getMinestomInventoryType()` | `InventoryType` | Returns the Minestom inventory type |
| `getRowCount()` | `int` | Returns the number of rows (1-6) |

**Note:** The chest type is automatically determined from the format string length.

---

### Indicator

**Package:** `com.github.echolightmc.msguis`

Special slot indicators used to mark slots with specific purposes.

#### Values

| Value | Status | Description |
|-------|--------|-------------|
| `INPUT_SLOT` | Reserved | For future use - input item slots |
| `OUTPUT_SLOT` | Reserved | For future use - output item slots |
| `CONTENT_SLOT` | **Active** | Marks scrollable content area in ScrollGUI |

**Example:**
```java
ScrollGUI.builder()
    .format("#       #")
    .item(' ', Indicator.CONTENT_SLOT)  // Marks scrollable area
    .scrollContent(itemList)
    .build();
```

---

## Quick Reference Tables

### Common Patterns

| Pattern | Implementation |
|---------|----------------|
| Border | `StaticGUIItem(ItemStack.of(Material.GRAY_STAINED_GLASS_PANE))` |
| Button | `DynamicGUIItem(item, event -> { ... })` |
| Scroll Down | `ScrollGUIItem(7, arrowItem)` |
| Scroll Up | `ScrollGUIItem(-7, arrowItem)` |

### Format String Sizes

| Characters | Rows | ChestType | Common Use |
|------------|------|-----------|------------|
| 9 | 1 | `ROWS_1` | Small menu |
| 18 | 2 | `ROWS_2` | Compact menu |
| 27 | 3 | `ROWS_3` | Standard menu |
| 36 | 4 | `ROWS_4` | Large menu |
| 45 | 5 | `ROWS_5` | Very large menu |
| 54 | 6 | `ROWS_6` | Maximum size |

### MiniMessage Examples

| Format | Result |
|--------|--------|
| `<red>Text</red>` | Red text |
| `<green><bold>Text</bold></green>` | Bold green text |
| `<gradient:red:blue>Text</gradient>` | Red to blue gradient |
| `<rainbow>Text</rainbow>` | Rainbow effect |
| `<hover:show_text:'Tooltip'>Text</hover>` | Text with tooltip |

---

## Exception Reference

| Exception | Thrown By | Reason |
|-----------|-----------|--------|
| `IllegalArgumentException` | `GUIBuilder.format()` | Format not divisible by 9 or > 6 rows |
| `IllegalArgumentException` | `GUIBuilder.item(char, Indicator)` | Indicator already set |
| `IllegalArgumentException` | `ScrollGUIItem` constructor | Scroll amount is zero |
| `IllegalStateException` | `GUIItem.setOwningGUI()` | Owning GUI already set |
| `NullPointerException` | Various | Required parameter is null |
| `ClassCastException` | `ScrollGUIItem.handleClick()` | Used in non-ScrollGUI |

---

## Version Information

This API reference is for **MSGuis v1.0.0**

For more detailed information and examples, see [README.md](README.md)

For contributing guidelines, see [CONTRIBUTING.md](CONTRIBUTING.md)
