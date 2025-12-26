# MSGuis API Documentation

A powerful, format-based GUI library for Minestom servers. MSGuis allows you to create chest GUIs using intuitive string-based layouts with character mapping.

## Table of Contents

- [Installation](#installation)
- [Quick Start](#quick-start)
- [Core Concepts](#core-concepts)
- [API Reference](#api-reference)
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
- [Examples](#examples)

---

## Installation

Add MSGuis to your Gradle project:

```gradle
dependencies {
    implementation 'com.github.echolightmc:msguis:VERSION'
}
```

**Dependencies:**
- Minestom
- Adventure API (MiniMessage)

---

## Quick Start

### 1. Initialize the GUIManager

The `GUIManager` is required for all GUIs to function. Initialize it once during server startup:

```java
import com.github.echolightmc.msguis.GUIManager;
import net.minestom.server.MinecraftServer;

public class MyServer {
    public static GUIManager GUI_MANAGER;

    public static void main(String[] args) {
        MinecraftServer minecraftServer = MinecraftServer.init();
        
        // Initialize GUIManager with the global event handler
        GUI_MANAGER = new GUIManager(MinecraftServer.getGlobalEventHandler());
        
        minecraftServer.start("0.0.0.0", 25565);
    }
}
```

### 2. Create a Simple GUI

```java
import com.github.echolightmc.msguis.NormalGUI;
import com.github.echolightmc.msguis.StaticGUIItem;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;

// Define items
ItemStack borderItem = ItemStack.of(Material.GRAY_STAINED_GLASS_PANE);
ItemStack buttonItem = ItemStack.of(Material.EMERALD);

// Build the GUI
NormalGUI myGUI = NormalGUI.builder()
    .manager(GUI_MANAGER)
    .titled("<green>My First GUI")
    .format("""
        #########
        #   B   #
        #########""")
    .item('#', new StaticGUIItem(borderItem))
    .item('B', new DynamicGUIItem(buttonItem, event -> {
        event.setCancelled(true);
        event.getPlayer().sendMessage("Button clicked!");
    }))
    .build();

// Open for a player
myGUI.openTo(player);
```

---

## Core Concepts

### Format-Based Layout

MSGuis uses a string-based format system where each character represents a slot in the inventory. The format must:
- Be divisible by 9 (slots per row)
- Have a maximum of 6 rows (54 slots)
- Map each character to a `GUIItem`

Example 3-row format:
```
#########
#       #
#########
```

### Character Mapping

Each unique character in the format is mapped to a `GUIItem`:
- `#` → Border item
- ` ` (space) → Empty/content slot
- Custom characters → Your custom items

### GUI Types

| Type | Description |
|------|-------------|
| `NormalGUI` | Standard static chest GUI |
| `ScrollGUI` | GUI with scrollable content area |

---

## API Reference

### GUIManager

The central manager that handles GUI registration and click events.

#### Constructor

```java
public GUIManager(GlobalEventHandler globalEventHandler)
```

Creates a new GUIManager and hooks into the provided event handler.

**Parameters:**
- `globalEventHandler` - The Minestom GlobalEventHandler to register events with

**Example:**
```java
GUIManager guiManager = new GUIManager(MinecraftServer.getGlobalEventHandler());
```

#### Methods

##### `unregisterGUI(ChestGUI chestGUI)`

Unregisters a GUI from the manager.

```java
public boolean unregisterGUI(ChestGUI chestGUI)
```

**Parameters:**
- `chestGUI` - The GUI to unregister

**Returns:** `true` if the GUI was successfully unregistered, `false` otherwise

**Example:**
```java
boolean success = guiManager.unregisterGUI(myGUI);
```

---

### ChestGUI

Abstract base class for all chest-based GUIs.

#### Methods

##### `setItem(char character, GUIItem item)`

Sets or replaces an item mapping in the GUI.

```java
public void setItem(char character, GUIItem item)
```

**Parameters:**
- `character` - The character in the format to map
- `item` - The GUIItem to display

**Example:**
```java
gui.setItem('B', new StaticGUIItem(ItemStack.of(Material.DIAMOND)));
```

##### `setFormat(String format)`

Updates the GUI's layout format.

```java
public void setFormat(String format)
```

**Parameters:**
- `format` - New format string (newlines are automatically removed)

**Example:**
```java
gui.setFormat("""
    #########
    #BBBBBBB#
    #########""");
```

##### `refreshDynamicItems()`

Refreshes all `DynamicGUIItem` instances in the GUI.

```java
public void refreshDynamicItems()
```

**Example:**
```java
// After updating a DynamicGUIItem's underlying data
gui.refreshDynamicItems();
```

##### `getInventory()`

Gets the underlying Minestom Inventory.

```java
public Inventory getInventory()
```

**Returns:** The Minestom `Inventory` instance

##### `getChestType()`

Gets the chest type (number of rows).

```java
public ChestType getChestType()
```

**Returns:** The `ChestType` enum value

##### `getGuiManager()`

Gets the associated GUIManager.

```java
public GUIManager getGuiManager()
```

**Returns:** The `GUIManager` instance

##### `getIndicators()`

Gets the indicator mappings.

```java
public Map<Indicator, Character> getIndicators()
```

**Returns:** Map of `Indicator` to their assigned characters

##### `openTo(Player... players)`

Opens the GUI for one or more players.

```java
public void openTo(Player... players)
```

**Parameters:**
- `players` - Variable number of players to open the GUI for

**Example:**
```java
// Single player
gui.openTo(player);

// Multiple players
gui.openTo(player1, player2, player3);
```

---

### NormalGUI

A standard chest GUI without scrolling functionality. Extends `ChestGUI`.

#### Static Methods

##### `builder()`

Creates a new NormalGUI builder.

```java
public static NormalGUIBuilder builder()
```

**Returns:** A new `NormalGUIBuilder` instance

#### NormalGUIBuilder

Builder class for creating NormalGUI instances.

##### `format(String format)`

Sets the GUI layout format.

```java
public NormalGUIBuilder format(String format)
```

**Parameters:**
- `format` - Layout string where each character represents a slot

**Throws:** `IllegalArgumentException` if format is not divisible by 9 or has more than 6 rows

##### `titled(String title, MiniMessage... miniMessage)`

Sets the GUI title using MiniMessage format.

```java
public NormalGUIBuilder titled(String title, MiniMessage... miniMessage)
```

**Parameters:**
- `title` - The title in MiniMessage format
- `miniMessage` - Optional custom MiniMessage provider (defaults to standard)

**Example:**
```java
.titled("<gradient:red:blue>My Cool GUI")
```

##### `item(char character, GUIItem item)`

Maps a character to a GUIItem.

```java
public NormalGUIBuilder item(char character, GUIItem item)
```

**Parameters:**
- `character` - Character to map
- `item` - GUIItem to display

##### `item(char character, Indicator indicator)`

Maps a character to a special indicator slot.

```java
public NormalGUIBuilder item(char character, Indicator indicator)
```

**Parameters:**
- `character` - Character to map
- `indicator` - The indicator type

**Throws:** `IllegalArgumentException` if indicator is already set

##### `manager(GUIManager manager)`

Sets the GUIManager for this GUI.

```java
public NormalGUIBuilder manager(GUIManager manager)
```

**Parameters:**
- `manager` - The GUIManager instance

##### `build()`

Builds and registers the NormalGUI.

```java
public NormalGUI build()
```

**Returns:** The constructed `NormalGUI` instance

**Complete Example:**
```java
NormalGUI gui = NormalGUI.builder()
    .manager(guiManager)
    .titled("<gold><bold>Settings Menu")
    .format("""
        #########
        # A B C #
        #########""")
    .item('#', new StaticGUIItem(border))
    .item('A', optionA)
    .item('B', optionB)
    .item('C', optionC)
    .build();
```

---

### ScrollGUI

A chest GUI with scrollable content area. Extends `ChestGUI`.

#### Static Methods

##### `builder()`

Creates a new ScrollGUI builder.

```java
public static ScrollGUIBuilder builder()
```

**Returns:** A new `ScrollGUIBuilder` instance

#### Methods

##### `scroll(int amount)`

Scrolls the content by the specified amount.

```java
public void scroll(int amount)
```

**Parameters:**
- `amount` - Number of slots to scroll (positive = forward, negative = backward)

**Example:**
```java
gui.scroll(7);  // Scroll forward by 7 slots
gui.scroll(-7); // Scroll backward by 7 slots
```

##### `setContent(List<? extends GUIItem> content)`

Sets the scrollable content items.

```java
public void setContent(List<? extends GUIItem> content)
```

**Parameters:**
- `content` - List of GUIItems to display in the scroll area

**Example:**
```java
List<GUIItem> items = materials.stream()
    .map(mat -> new StaticGUIItem(ItemStack.of(mat)))
    .toList();
gui.setContent(items);
```

##### `getContent()`

Gets the current content list.

```java
public List<? extends GUIItem> getContent()
```

**Returns:** The list of content items

##### `getCurrentScrollPos()`

Gets the current scroll position.

```java
public int getCurrentScrollPos()
```

**Returns:** The current scroll position index

##### `fillScrollContent()`

Manually fills/refreshes the scroll content area.

```java
@ApiStatus.Internal
public void fillScrollContent()
```

> **Note:** This method is marked as internal but can be used to force-refresh the scroll content.

#### ScrollGUIBuilder

Builder class for creating ScrollGUI instances. Inherits all methods from `GUIBuilder`.

##### `scrollContent(List<? extends GUIItem> content)`

Sets the initial scrollable content.

```java
public ScrollGUIBuilder scrollContent(List<? extends GUIItem> content)
```

**Parameters:**
- `content` - List of GUIItems for the scroll area

**Complete Example:**
```java
ScrollGUI scrollGui = ScrollGUI.builder()
    .manager(guiManager)
    .titled("<red>Item Browser")
    .format("""
        #########
        #       #
        #       #
        #       #
        #       #
        ###<#>###""")
    .item('#', new StaticGUIItem(border))
    .item(' ', Indicator.CONTENT_SLOT)  // Define scroll area
    .item('<', new ScrollGUIItem(-7, scrollBackArrow))
    .item('>', new ScrollGUIItem(7, scrollForwardArrow))
    .scrollContent(myItems)
    .build();
```

---

### GUIItem

Abstract base class for all GUI items.

#### Methods

##### `getItem()`

Gets the ItemStack to display.

```java
public abstract ItemStack getItem()
```

**Returns:** The `ItemStack` to render in the GUI

##### `handleClick(InventoryPreClickEvent event)`

Handles click events on this item.

```java
public abstract void handleClick(InventoryPreClickEvent event)
```

**Parameters:**
- `event` - The inventory click event

##### `getOwningGUI()`

Gets the GUI this item belongs to.

```java
public ChestGUI getOwningGUI()
```

**Returns:** The parent `ChestGUI` instance

---

### StaticGUIItem

A GUI item with a fixed ItemStack that cancels all click interactions.

#### Constructor

```java
public StaticGUIItem(ItemStack item)
```

**Parameters:**
- `item` - The ItemStack to display

**Example:**
```java
// Border item
StaticGUIItem border = new StaticGUIItem(
    ItemStack.of(Material.GRAY_STAINED_GLASS_PANE)
        .withCustomName(Component.empty())
);

// Decorative item
StaticGUIItem decoration = new StaticGUIItem(
    ItemStack.of(Material.SUNFLOWER)
        .withCustomName(Component.text("Decoration"))
);
```

#### Methods

##### `getItem()`

Returns the static ItemStack.

```java
@Override
public ItemStack getItem()
```

##### `handleClick(InventoryPreClickEvent event)`

Cancels the click event (prevents item pickup).

```java
@Override
public void handleClick(InventoryPreClickEvent event)
```

---

### DynamicGUIItem

A GUI item that can be updated and has customizable click behavior.

#### Constructor

```java
public DynamicGUIItem(ItemStack item, Consumer<InventoryPreClickEvent> clickConsumer)
```

**Parameters:**
- `item` - Initial ItemStack to display
- `clickConsumer` - Click handler function

**Example:**
```java
DynamicGUIItem toggleButton = new DynamicGUIItem(
    ItemStack.of(Material.LIME_DYE).withCustomName(Component.text("ON")),
    event -> {
        event.setCancelled(true);
        Player player = event.getPlayer();
        // Toggle logic here
        player.sendMessage("Toggled!");
    }
);
```

#### Methods

##### `getItem()`

Returns the current ItemStack.

```java
@Override
public ItemStack getItem()
```

##### `setItem(ItemStack item)`

Updates the displayed ItemStack and notifies the GUI.

```java
public void setItem(ItemStack item)
```

**Parameters:**
- `item` - The new ItemStack to display

**Example:**
```java
// Update button appearance
dynamicItem.setItem(ItemStack.of(Material.RED_DYE)
    .withCustomName(Component.text("OFF")));
```

##### `getClickConsumer()`

Gets the current click handler.

```java
public Consumer<InventoryPreClickEvent> getClickConsumer()
```

**Returns:** The click consumer function

##### `setClickConsumer(Consumer<InventoryPreClickEvent> clickConsumer)`

Updates the click handler.

```java
public void setClickConsumer(Consumer<InventoryPreClickEvent> clickConsumer)
```

**Parameters:**
- `clickConsumer` - New click handler function

##### `handleClick(InventoryPreClickEvent event)`

Invokes the click consumer.

```java
@Override
public void handleClick(InventoryPreClickEvent event)
```

---

### ScrollGUIItem

A specialized GUI item for scrolling in ScrollGUI.

#### Constructor

```java
public ScrollGUIItem(int scroll, ItemStack itemStack)
```

**Parameters:**
- `scroll` - Scroll amount (positive = forward/up, negative = backward/down). Cannot be zero.
- `itemStack` - The ItemStack to display

**Throws:** `IllegalArgumentException` if scroll amount is zero

**Example:**
```java
// Scroll back 7 slots (one row of content)
ScrollGUIItem scrollBack = new ScrollGUIItem(-7, 
    ItemStack.of(Material.ARROW)
        .withCustomName(Component.text("Previous Page"))
);

// Scroll forward 7 slots
ScrollGUIItem scrollForward = new ScrollGUIItem(7,
    ItemStack.of(Material.ARROW)
        .withCustomName(Component.text("Next Page"))
);
```

#### Methods

##### `getScroll()`

Gets the scroll amount.

```java
public int getScroll()
```

**Returns:** The scroll amount

##### `getItem()`

Returns the ItemStack.

```java
@Override
public ItemStack getItem()
```

##### `handleClick(InventoryPreClickEvent event)`

Cancels the event and scrolls the parent ScrollGUI.

```java
@Override
public void handleClick(InventoryPreClickEvent event)
```

---

### ChestType

Enum representing different chest inventory sizes.

#### Values

| Value | Rows | Slots |
|-------|------|-------|
| `ROWS_1` | 1 | 9 |
| `ROWS_2` | 2 | 18 |
| `ROWS_3` | 3 | 27 |
| `ROWS_4` | 4 | 36 |
| `ROWS_5` | 5 | 45 |
| `ROWS_6` | 6 | 54 |

#### Methods

##### `getMinestomInventoryType()`

Gets the corresponding Minestom InventoryType.

```java
public InventoryType getMinestomInventoryType()
```

**Returns:** The Minestom `InventoryType`

##### `getRowCount()`

Gets the number of rows.

```java
public int getRowCount()
```

**Returns:** Number of rows (1-6)

**Example:**
```java
ChestType type = gui.getChestType();
int rows = type.getRowCount(); // e.g., 3
```

---

### Indicator

Enum for special slot types in GUIs.

#### Values

| Value | Description |
|-------|-------------|
| `INPUT_SLOT` | Slot where players can place items (future feature) |
| `OUTPUT_SLOT` | Slot for item output (future feature) |
| `CONTENT_SLOT` | Defines the scrollable content area in ScrollGUI |

**Example:**
```java
ScrollGUI.builder()
    .item(' ', Indicator.CONTENT_SLOT)  // Spaces are scroll content
    .build();
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

public class MainMenuGUI {
    
    private static final ItemStack BORDER = ItemStack.of(Material.BLACK_STAINED_GLASS_PANE)
        .withCustomName(Component.empty());
    
    public static NormalGUI createMainMenu(GUIManager manager, Player player) {
        return NormalGUI.builder()
            .manager(manager)
            .titled("<gradient:#FF6B6B:#4ECDC4><bold>Main Menu")
            .format("""
                #########
                # S T P #
                #########""")
            .item('#', new StaticGUIItem(BORDER))
            .item('S', new DynamicGUIItem(
                ItemStack.of(Material.COMPASS).withCustomName(Component.text("Settings")),
                event -> {
                    event.setCancelled(true);
                    event.getPlayer().sendMessage("Opening settings...");
                }
            ))
            .item('T', new DynamicGUIItem(
                ItemStack.of(Material.CHEST).withCustomName(Component.text("Teleport")),
                event -> {
                    event.setCancelled(true);
                    event.getPlayer().sendMessage("Opening teleport menu...");
                }
            ))
            .item('P', new DynamicGUIItem(
                ItemStack.of(Material.PLAYER_HEAD).withCustomName(Component.text("Profile")),
                event -> {
                    event.setCancelled(true);
                    event.getPlayer().sendMessage("Opening profile...");
                }
            ))
            .build();
    }
}
```

### Example 2: Confirmation Dialog

```java
public class ConfirmationGUI {
    
    public static NormalGUI createConfirmation(GUIManager manager, 
                                               String message,
                                               Runnable onConfirm,
                                               Runnable onCancel) {
        ItemStack border = ItemStack.of(Material.GRAY_STAINED_GLASS_PANE)
            .withCustomName(Component.empty());
        
        ItemStack confirm = ItemStack.of(Material.LIME_CONCRETE)
            .withCustomName(Component.text("Confirm").color(NamedTextColor.GREEN));
        
        ItemStack cancel = ItemStack.of(Material.RED_CONCRETE)
            .withCustomName(Component.text("Cancel").color(NamedTextColor.RED));
        
        return NormalGUI.builder()
            .manager(manager)
            .titled("<yellow>Confirm Action")
            .format("""
                #########
                ## Y N ##
                #########""")
            .item('#', new StaticGUIItem(border))
            .item('Y', new DynamicGUIItem(confirm, event -> {
                event.setCancelled(true);
                event.getPlayer().closeInventory();
                onConfirm.run();
            }))
            .item('N', new DynamicGUIItem(cancel, event -> {
                event.setCancelled(true);
                event.getPlayer().closeInventory();
                onCancel.run();
            }))
            .build();
    }
}
```

### Example 3: Item Browser with Scrolling

```java
public class ItemBrowserGUI {
    
    public static ScrollGUI createItemBrowser(GUIManager manager) {
        ItemStack border = ItemStack.of(Material.BLUE_STAINED_GLASS_PANE)
            .withCustomName(Component.empty());
        
        ItemStack scrollBack = ItemStack.of(Material.ARROW)
            .withCustomName(Component.text("← Previous"));
        
        ItemStack scrollForward = ItemStack.of(Material.ARROW)
            .withCustomName(Component.text("Next →"));
        
        // Create content from all materials
        List<GUIItem> allItems = Arrays.stream(Material.values())
            .filter(Material::isItem)
            .limit(200) // Limit for performance
            .map(material -> new DynamicGUIItem(
                ItemStack.of(material),
                event -> {
                    event.setCancelled(true);
                    event.getPlayer().sendMessage("Selected: " + material.name());
                }
            ))
            .collect(Collectors.toList());
        
        return ScrollGUI.builder()
            .manager(manager)
            .titled("<aqua>Item Browser")
            .format("""
                #########
                #       #
                #       #
                #       #
                #       #
                #<#####>#""")
            .item('#', new StaticGUIItem(border))
            .item(' ', Indicator.CONTENT_SLOT)
            .item('<', new ScrollGUIItem(-7, scrollBack))
            .item('>', new ScrollGUIItem(7, scrollForward))
            .scrollContent(allItems)
            .build();
    }
}
```

### Example 4: Dynamic Counter GUI

```java
public class CounterGUI {
    
    private int count = 0;
    private DynamicGUIItem counterDisplay;
    
    public NormalGUI create(GUIManager manager) {
        ItemStack border = ItemStack.of(Material.WHITE_STAINED_GLASS_PANE)
            .withCustomName(Component.empty());
        
        counterDisplay = new DynamicGUIItem(
            createCounterItem(),
            event -> event.setCancelled(true)
        );
        
        DynamicGUIItem decreaseBtn = new DynamicGUIItem(
            ItemStack.of(Material.RED_WOOL)
                .withCustomName(Component.text("-1").color(NamedTextColor.RED)),
            event -> {
                event.setCancelled(true);
                count--;
                updateCounter();
            }
        );
        
        DynamicGUIItem increaseBtn = new DynamicGUIItem(
            ItemStack.of(Material.LIME_WOOL)
                .withCustomName(Component.text("+1").color(NamedTextColor.GREEN)),
            event -> {
                event.setCancelled(true);
                count++;
                updateCounter();
            }
        );
        
        return NormalGUI.builder()
            .manager(manager)
            .titled("<white>Counter")
            .format("""
                #########
                ## - C + ##
                #########""")
            .item('#', new StaticGUIItem(border))
            .item('-', decreaseBtn)
            .item('C', counterDisplay)
            .item('+', increaseBtn)
            .build();
    }
    
    private void updateCounter() {
        counterDisplay.setItem(createCounterItem());
    }
    
    private ItemStack createCounterItem() {
        return ItemStack.of(Material.PAPER)
            .withCustomName(Component.text("Count: " + count))
            .withAmount(Math.max(1, Math.min(64, Math.abs(count))));
    }
}
```

### Example 5: Player List GUI with Scroll

```java
public class PlayerListGUI {
    
    public static ScrollGUI create(GUIManager manager) {
        ItemStack border = ItemStack.of(Material.OAK_SIGN)
            .withCustomName(Component.text("Online Players").color(NamedTextColor.GOLD));
        
        return ScrollGUI.builder()
            .manager(manager)
            .titled("<gold>Online Players")
            .format("""
                ####B####
                #       #
                #       #
                #       #
                ###<#>###""")
            .item('#', new StaticGUIItem(ItemStack.of(Material.GRAY_STAINED_GLASS_PANE)
                .withCustomName(Component.empty())))
            .item('B', new StaticGUIItem(border))
            .item(' ', Indicator.CONTENT_SLOT)
            .item('<', new ScrollGUIItem(-7, ItemStack.of(Material.SPECTRAL_ARROW)
                .withCustomName(Component.text("Previous"))))
            .item('>', new ScrollGUIItem(7, ItemStack.of(Material.SPECTRAL_ARROW)
                .withCustomName(Component.text("Next"))))
            .scrollContent(new ArrayList<>()) // Will be populated dynamically
            .build();
    }
    
    public static void refreshPlayerList(ScrollGUI gui, Collection<Player> players) {
        List<GUIItem> playerItems = players.stream()
            .map(player -> new DynamicGUIItem(
                ItemStack.of(Material.PLAYER_HEAD)
                    .withCustomName(player.getName()),
                event -> {
                    event.setCancelled(true);
                    event.getPlayer().sendMessage("Viewing: " + player.getUsername());
                }
            ))
            .collect(Collectors.toList());
        
        gui.setContent(playerItems);
    }
}
```

---

## Best Practices

### 1. Always Cancel Events for Static Items

When using `DynamicGUIItem`, remember to call `event.setCancelled(true)` to prevent players from taking items:

```java
new DynamicGUIItem(item, event -> {
    event.setCancelled(true);  // Prevent item pickup
    // Your logic here
});
```

### 2. Use Meaningful Format Characters

Choose format characters that are easy to understand:
- `#` - Borders/frames
- ` ` (space) - Empty or content slots
- `<` `>` - Navigation arrows
- Letters for specific buttons (e.g., `S` for Settings)

### 3. Reuse GUIManager

Create a single `GUIManager` instance and share it across all GUIs:

```java
public class MyPlugin {
    public static final GUIManager GUI_MANAGER = 
        new GUIManager(MinecraftServer.getGlobalEventHandler());
}
```

### 4. Unregister Unused GUIs

If you create temporary GUIs, unregister them to prevent memory leaks:

```java
guiManager.unregisterGUI(temporaryGui);
```

### 5. Use ScrollGUI for Large Content

When displaying many items (like item lists, player lists), use `ScrollGUI` for better UX:

```java
ScrollGUI.builder()
    .item(' ', Indicator.CONTENT_SLOT)
    .scrollContent(largeItemList)
    .build();
```

---

## Troubleshooting

### GUI Not Opening

Ensure the `GUIManager` is initialized before building GUIs:
```java
GUI_MANAGER = new GUIManager(MinecraftServer.getGlobalEventHandler());
```

### Format Error: "Not divisible by 9"

Every row must have exactly 9 characters. Check your format string:
```java
// ✓ Correct (9 chars per row)
.format("""
    #########
    #       #
    #########""")

// ✗ Wrong (8 chars in row)
.format("""
    ########
    #      #
    ########""")
```

### Items Not Updating

For `DynamicGUIItem`, call `setItem()` which automatically notifies the GUI:
```java
dynamicItem.setItem(newItemStack);  // GUI updates automatically
```

For batch updates, use `refreshDynamicItems()`:
```java
gui.refreshDynamicItems();
```

---

## License

This project is licensed under the MIT License. See the [LICENSE](../LICENSE) file for details.
