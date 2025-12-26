# MSGuis API Reference

This document describes **all public types and methods** in the `com.github.echolightmc.msguis` package.

## Overview

MSGuis is centered around three concepts:

- **`GUIManager`**: hooks Minestom inventory click events and dispatches them to registered GUIs.
- **`ChestGUI`**: an abstract base class representing a “formatted” inventory + slot-to-`GUIItem` mapping.
- **`GUIItem`**: an item (slot behavior) that can render an `ItemStack` and handle click events.

Concrete GUI types:

- **`NormalGUI`**: a non-scrolling chest GUI.
- **`ScrollGUI`**: a chest GUI with a scrollable “content region”.

## Common usage patterns

### Creating a manager (required)

```java
GUIManager guiManager = new GUIManager(MinecraftServer.getGlobalEventHandler());
```

### Building a GUI (builder pattern)

All GUI types are constructed via a builder:

```java
NormalGUI gui = NormalGUI.builder()
        .manager(guiManager)
        .titled("<yellow>Title")          // MiniMessage string -> Component
        .format("#########.........")      // must be divisible by 9; newlines allowed
        .item('#', new StaticGUIItem(...)) // map a character to a GUIItem
        .build();                          // registers GUI with manager
```

### The format string

- **Every character maps to one slot**.
- **Same character** = multiple slots share the same `GUIItem`.
- Builders call `format.replace("\n", "")`, so you can use multi-line strings for readability.

Constraints enforced by `ChestGUI.GUIBuilder#format(String)`:

- length divisible by **9**
- row count **<= 6**

## Public API by type

## `GUIManager`

Responsible for registering GUIs and routing `InventoryPreClickEvent` to the appropriate `ChestGUI`.

### Constructor

- **`GUIManager(GlobalEventHandler globalEventHandler)`**
  - Hooks an inventory event node into the provided `GlobalEventHandler`.

### Methods

- **`boolean unregisterGUI(ChestGUI chestGUI)`**
  - Removes the GUI from the manager so its inventory clicks are no longer handled.

## `ChestGUI` (abstract)

Base class for all “formatted” GUIs. It owns:

- a Minestom `Inventory`
- a `format` string
- a mapping from **characters** to **`GUIItem`**
- an `items[]` cache indexed by slot for efficient click dispatch

### Methods

- **`void setItem(char character, GUIItem item)`**
  - Updates the character → item mapping and reapplies the format.

- **`void setFormat(String format)`**
  - Replaces the format (newlines removed), rebuilds internal slot mappings, and reapplies the format.

- **`void refreshDynamicItems()`**
  - Re-renders slots backed by `DynamicGUIItem` (and, for `ScrollGUI`, also refreshes visible content slots).
  - Use this if your dynamic items’ visual state changes over time.

- **`Inventory getInventory()`**
  - Returns the underlying Minestom inventory.

- **`ChestType getChestType()`**
  - Returns the chest size (`ROWS_1..ROWS_6`) inferred by the builder or constructor.

- **`GUIManager getGuiManager()`**
  - Returns the manager that registered this GUI.

- **`Map<Indicator, Character> getIndicators()`**
  - Returns indicator → character mappings (used primarily by `ScrollGUI`).

- **`void openTo(Player... players)`**
  - Opens this inventory for each provided player.

### Builder base: `ChestGUI.GUIBuilder<T,B>`

The builder base class used by `NormalGUI` and `ScrollGUI`.

#### Methods

- **`B format(String format)`**
  - Validates format size; sets `ChestType` based on row count.

- **`B titled(String title, MiniMessage... miniMessage)`**
  - Sets the inventory title by deserializing the provided MiniMessage string.
  - If a `MiniMessage` instance is not provided, the default `MiniMessage.miniMessage()` is used.

- **`B item(char character, GUIItem item)`**
  - Assigns a `GUIItem` to all slots matching the given character.

- **`B item(char character, Indicator indicator)`**
  - Assigns an indicator to a character. Indicators are used by some GUI types (notably `ScrollGUI`).

- **`B manager(GUIManager manager)`**
  - Sets the manager that the GUI will register with on `build()`.

- **`T build()`**
  - Creates the GUI, assigns each `GUIItem` its owning GUI, and registers the GUI with the manager.

## `NormalGUI`

Concrete `ChestGUI` with no scrolling behavior.

### Factory

- **`static NormalGUI.NormalGUIBuilder builder()`**

### Builder: `NormalGUI.NormalGUIBuilder`

Inherits all builder methods from `ChestGUI.GUIBuilder`.

## `ScrollGUI`

A `ChestGUI` with a scrollable content region. The content region is defined by setting the
`Indicator.CONTENT_SLOT` indicator to a character in the format.

### Methods

- **`void scroll(int amount)`**
  - Moves the scroll position by `amount` “slots” within the content region.
  - Does nothing if the scroll would move beyond the bounds of the content.

- **`void setContent(List<? extends GUIItem> content)`**
  - Replaces the scrollable content list and re-renders visible content slots.

- **`List<? extends GUIItem> getContent()`**
  - Returns the current content list.

- **`int getCurrentScrollPos()`**
  - Returns the current scroll offset (index into `content`).

### Factory

- **`static ScrollGUI.ScrollGUIBuilder builder()`**

### Builder: `ScrollGUI.ScrollGUIBuilder`

Inherits all builder methods from `ChestGUI.GUIBuilder` and adds:

- **`ScrollGUIBuilder scrollContent(List<? extends GUIItem> content)`**
  - Sets the initial scrollable content list.

### Required indicator

To use scrolling, you must map `Indicator.CONTENT_SLOT` to some character in your format:

```java
.item(' ', Indicator.CONTENT_SLOT)
```

All slots containing that character become the scrollable region.

## `GUIItem` (abstract)

Represents a slot item + behavior.

### Methods

- **`ItemStack getItem()`**
  - Returns the current item stack to display in the GUI.

- **`ChestGUI getOwningGUI()`**
  - Returns the GUI this item belongs to (set automatically during `build()`).

- **`void handleClick(InventoryPreClickEvent event)`**
  - Called when the slot is clicked.
  - Typically you should call `event.setCancelled(true)` if you don’t want players taking items.

## `StaticGUIItem`

A simple `GUIItem` that always displays a fixed `ItemStack` and **cancels clicks**.

### Constructor

- **`StaticGUIItem(ItemStack item)`**

## `DynamicGUIItem`

A `GUIItem` that can change its displayed item over time and delegates clicks to a `Consumer`.

### Constructor

- **`DynamicGUIItem(ItemStack item, Consumer<InventoryPreClickEvent> clickConsumer)`**

### Methods

- **`void setItem(ItemStack item)`**
  - Updates the displayed item and notifies the owning GUI to re-render the slot(s).
  - Requires that the item has been built into a GUI (owning GUI must be set).

- **`Consumer<InventoryPreClickEvent> getClickConsumer()`**

- **`void setClickConsumer(Consumer<InventoryPreClickEvent> clickConsumer)`**

## `ScrollGUIItem`

A convenience `GUIItem` that scrolls its owning `ScrollGUI` when clicked.

### Constructor

- **`ScrollGUIItem(int scroll, ItemStack itemStack)`**
  - `scroll` must be non-zero.
  - Positive values scroll “forward”, negative values scroll “back”.

### Method

- **`int getScroll()`**
  - Returns the configured scroll amount.

## `ChestType` (enum)

Represents chest sizes from 1 to 6 rows.

### Values

- `ROWS_1`, `ROWS_2`, `ROWS_3`, `ROWS_4`, `ROWS_5`, `ROWS_6`

### Methods

- **`InventoryType getMinestomInventoryType()`**
- **`int getRowCount()`**

## `Indicator` (enum)

Special markers that GUI implementations can use to interpret a character in the format.

### Values

- `INPUT_SLOT` (reserved / not yet implemented by the current GUI types)
- `OUTPUT_SLOT` (reserved / not yet implemented by the current GUI types)
- `CONTENT_SLOT` (used by `ScrollGUI` to mark the scrollable region)

