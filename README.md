# MSGuis - Minestom GUI Library

A flexible and powerful GUI library for [Minestom](https://github.com/Minestom/Minestom) that allows you to create custom chest-based GUIs using an intuitive format string system.

## Features

- 📐 **Format-based Layout**: Define GUI layouts using simple ASCII art strings
- 🔄 **Multiple GUI Types**: Support for normal and scrollable GUIs
- 🎨 **Dynamic Items**: Items that can update their appearance and behavior at runtime
- 📦 **Static Items**: Immutable decorative items for borders and backgrounds
- ↕️ **Scroll Support**: Built-in scrollable content areas with automatic pagination
- 🎯 **Event Handling**: Clean event handling for item clicks
- 🔧 **Builder Pattern**: Fluent API for easy GUI construction
- 🎭 **MiniMessage Support**: Built-in support for colored titles using Adventure MiniMessage

## Installation

### Gradle

Add the following to your `build.gradle`:

```gradle
repositories {
    maven { url 'https://jitpack.io' }
}

dependencies {
    implementation 'com.github.echolightmc:msguis:VERSION'
}
```

### Maven

Add the following to your `pom.xml`:

```xml
<repository>
    <id>jitpack.io</id>
    <url>https://jitpack.io</url>
</repository>

<dependency>
    <groupId>com.github.echolightmc</groupId>
    <artifactId>msguis</artifactId>
    <version>VERSION</version>
</dependency>
```

## Quick Start

### 1. Initialize the GUI Manager

The GUI Manager handles all GUI-related events and must be initialized once:

```java
import com.github.echolightmc.msguis.GUIManager;
import net.minestom.server.MinecraftServer;

public class MyPlugin {
    public static GUIManager guiManager;
    
    public void onEnable() {
        guiManager = new GUIManager(MinecraftServer.getGlobalEventHandler());
    }
}
```

### 2. Create a Simple GUI

```java
import com.github.echolightmc.msguis.*;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;

// Create items
StaticGUIItem border = new StaticGUIItem(ItemStack.of(Material.GRAY_STAINED_GLASS_PANE));
DynamicGUIItem button = new DynamicGUIItem(
    ItemStack.of(Material.DIAMOND).withCustomName(Component.text("Click Me!")),
    event -> {
        event.setCancelled(true);
        event.getPlayer().sendMessage("Button clicked!");
    }
);

// Build the GUI
NormalGUI gui = NormalGUI.builder()
    .manager(guiManager)
    .titled("<green>My First GUI")
    .format("""
        #########
        #   B   #
        #########""")
    .item('#', border)
    .item('B', button)
    .build();

// Open the GUI
gui.openTo(player);
```

## API Documentation

### Core Components

#### GUIManager

The central manager that handles all GUI events and registration.

**Constructor:**
```java
GUIManager(GlobalEventHandler globalEventHandler)
```

**Methods:**
- `void registerGUI(Inventory inventory, ChestGUI gui)` - Registers a GUI (called automatically by builders)
- `boolean unregisterGUI(ChestGUI gui)` - Unregisters a GUI and stops handling its events

**Example:**
```java
GUIManager manager = new GUIManager(MinecraftServer.getGlobalEventHandler());
```

---

### GUI Types

#### NormalGUI

A standard chest GUI with fixed item positions.

**Creation:**
```java
NormalGUI gui = NormalGUI.builder()
    .manager(guiManager)
    .titled("<red>Normal GUI")
    .format("###\n# #\n###")
    .item('#', borderItem)
    .build();
```

**Methods:**
- `void openTo(Player... players)` - Opens the GUI for one or more players
- `void setItem(char character, GUIItem item)` - Updates an item in the GUI
- `void setFormat(String format)` - Changes the GUI layout
- `void refreshDynamicItems()` - Updates all dynamic items in the GUI
- `Inventory getInventory()` - Returns the underlying Minestom inventory
- `ChestType getChestType()` - Returns the chest type (rows)
- `GUIManager getGuiManager()` - Returns the GUI manager

#### ScrollGUI

A GUI with scrollable content areas for displaying large lists.

**Creation:**
```java
ScrollGUI gui = ScrollGUI.builder()
    .manager(guiManager)
    .titled("<blue>Scrollable GUI")
    .format("""
        #########
        #       #
        #       #
        ###<#>###""")
    .item('#', borderItem)
    .item(' ', Indicator.CONTENT_SLOT)
    .item('<', new ScrollGUIItem(-7, scrollBackArrow))
    .item('>', new ScrollGUIItem(7, scrollForwardArrow))
    .scrollContent(contentList)
    .build();
```

**Methods:**
- `void scroll(int amount)` - Scrolls by the specified amount (positive = down, negative = up)
- `void setContent(List<? extends GUIItem> content)` - Updates the scrollable content
- `List<? extends GUIItem> getContent()` - Returns the current content
- `int getCurrentScrollPos()` - Returns the current scroll position

---

### Item Types

#### StaticGUIItem

A simple, non-interactive item that cancels all click events.

**Constructor:**
```java
StaticGUIItem(ItemStack item)
```

**Use Cases:**
- Borders and backgrounds
- Decorative elements
- Non-clickable information displays

**Example:**
```java
ItemStack borderItem = ItemStack.of(Material.GRAY_STAINED_GLASS_PANE)
    .withCustomName(Component.empty());
StaticGUIItem border = new StaticGUIItem(borderItem);
```

#### DynamicGUIItem

An interactive item that can change its appearance and has custom click behavior.

**Constructor:**
```java
DynamicGUIItem(ItemStack item, Consumer<InventoryPreClickEvent> clickConsumer)
```

**Methods:**
- `void setItem(ItemStack item)` - Updates the item's appearance
- `void setClickConsumer(Consumer<InventoryPreClickEvent> consumer)` - Updates the click handler
- `ItemStack getItem()` - Returns the current item
- `Consumer<InventoryPreClickEvent> getClickConsumer()` - Returns the click handler

**Example:**
```java
DynamicGUIItem counter = new DynamicGUIItem(
    ItemStack.of(Material.EMERALD).withCustomName(Component.text("Counter: 0")),
    event -> {
        event.setCancelled(true);
        int count = /* get count from somewhere */;
        count++;
        counter.setItem(ItemStack.of(Material.EMERALD)
            .withCustomName(Component.text("Counter: " + count)));
    }
);
```

#### ScrollGUIItem

A specialized item for scrolling within a ScrollGUI.

**Constructor:**
```java
ScrollGUIItem(int scroll, ItemStack itemStack)
```

**Parameters:**
- `scroll` - The scroll amount (positive = scroll down, negative = scroll up, cannot be 0)
- `itemStack` - The visual representation of the scroll button

**Example:**
```java
ScrollGUIItem scrollDown = new ScrollGUIItem(
    7,  // Scroll down 7 slots (one row)
    ItemStack.of(Material.ARROW).withCustomName(Component.text("Next Page"))
);

ScrollGUIItem scrollUp = new ScrollGUIItem(
    -7,  // Scroll up 7 slots (one row)
    ItemStack.of(Material.ARROW).withCustomName(Component.text("Previous Page"))
);
```

---

### Builder Pattern

All GUIs use a fluent builder pattern for construction.

#### Common Builder Methods

**format(String format)**

Defines the GUI layout using an ASCII art string. Each character represents a slot.
- Format must be divisible by 9 (chest row width)
- Maximum of 6 rows (54 slots)
- Newlines are automatically removed

```java
.format("""
    #########
    # X X X #
    #########""")
```

**titled(String title, MiniMessage... miniMessage)**

Sets the GUI title with MiniMessage formatting support.

```java
.titled("<gradient:red:blue>Epic GUI</gradient>")
.titled("<red><bold>Warning!</bold></red>")
```

**item(char character, GUIItem item)**

Maps a character in the format string to a GUI item.

```java
.item('#', new StaticGUIItem(borderItem))
.item('X', new DynamicGUIItem(buttonItem, clickHandler))
```

**item(char character, Indicator indicator)**

Maps a character to a special indicator (for ScrollGUI).

```java
.item(' ', Indicator.CONTENT_SLOT)
```

**manager(GUIManager manager)**

Sets the GUI manager (required).

```java
.manager(guiManager)
```

**build()**

Constructs and registers the GUI.

```java
NormalGUI gui = builder.build();
```

#### ScrollGUI-Specific Builder Methods

**scrollContent(List<? extends GUIItem> content)**

Sets the initial scrollable content.

```java
.scrollContent(List.of(
    new StaticGUIItem(ItemStack.of(Material.DIAMOND)),
    new StaticGUIItem(ItemStack.of(Material.EMERALD)),
    new StaticGUIItem(ItemStack.of(Material.GOLD_INGOT))
))
```

---

### Enums

#### ChestType

Represents different chest sizes.

**Values:**
- `ROWS_1` - 1 row (9 slots)
- `ROWS_2` - 2 rows (18 slots)
- `ROWS_3` - 3 rows (27 slots) - Default
- `ROWS_4` - 4 rows (36 slots)
- `ROWS_5` - 5 rows (45 slots)
- `ROWS_6` - 6 rows (54 slots)

**Methods:**
- `InventoryType getMinestomInventoryType()` - Returns the Minestom inventory type
- `int getRowCount()` - Returns the number of rows

**Note:** The chest type is automatically determined from your format string.

#### Indicator

Special slot indicators for ScrollGUI.

**Values:**
- `CONTENT_SLOT` - Marks slots for scrollable content
- `INPUT_SLOT` - Reserved for future use
- `OUTPUT_SLOT` - Reserved for future use

---

## Examples

### Example 1: Simple Menu

```java
public class SimpleMenu {
    
    public static NormalGUI createMenu(GUIManager manager) {
        // Create items
        StaticGUIItem border = new StaticGUIItem(
            ItemStack.of(Material.BLACK_STAINED_GLASS_PANE)
                .withCustomName(Component.empty())
        );
        
        DynamicGUIItem teleportButton = new DynamicGUIItem(
            ItemStack.of(Material.ENDER_PEARL)
                .withCustomName(Component.text("Teleport to Spawn")),
            event -> {
                event.setCancelled(true);
                Player player = event.getPlayer();
                player.teleport(new Pos(0, 64, 0));
                player.closeInventory();
                player.sendMessage("Teleported to spawn!");
            }
        );
        
        DynamicGUIItem giveItemButton = new DynamicGUIItem(
            ItemStack.of(Material.DIAMOND_SWORD)
                .withCustomName(Component.text("Receive Diamond Sword")),
            event -> {
                event.setCancelled(true);
                Player player = event.getPlayer();
                player.getInventory().addItemStack(ItemStack.of(Material.DIAMOND_SWORD));
                player.closeInventory();
                player.sendMessage("Received a diamond sword!");
            }
        );
        
        // Build GUI
        return NormalGUI.builder()
            .manager(manager)
            .titled("<gradient:green:blue>Main Menu</gradient>")
            .format("""
                #########
                # T # G #
                #########""")
            .item('#', border)
            .item('T', teleportButton)
            .item('G', giveItemButton)
            .build();
    }
}
```

### Example 2: Player Selector with Scroll

```java
public class PlayerSelector {
    
    public static ScrollGUI createSelector(GUIManager manager) {
        // Create border and scroll items
        StaticGUIItem border = new StaticGUIItem(
            ItemStack.of(Material.GRAY_STAINED_GLASS_PANE)
                .withCustomName(Component.empty())
        );
        
        ScrollGUIItem scrollUp = new ScrollGUIItem(-7,
            ItemStack.of(Material.ARROW)
                .withCustomName(Component.text("⬆ Previous Page"))
        );
        
        ScrollGUIItem scrollDown = new ScrollGUIItem(7,
            ItemStack.of(Material.ARROW)
                .withCustomName(Component.text("⬇ Next Page"))
        );
        
        // Create player items
        List<GUIItem> players = MinecraftServer.getConnectionManager()
            .getOnlinePlayers()
            .stream()
            .map(player -> new DynamicGUIItem(
                ItemStack.of(Material.PLAYER_HEAD)
                    .withCustomName(Component.text(player.getUsername())),
                event -> {
                    event.setCancelled(true);
                    event.getPlayer().sendMessage("Selected: " + player.getUsername());
                }
            ))
            .collect(Collectors.toList());
        
        // Build scrollable GUI
        return ScrollGUI.builder()
            .manager(manager)
            .titled("<yellow>Select a Player</yellow>")
            .format("""
                #########
                #       #
                #       #
                #       #
                ###U#D###""")
            .item('#', border)
            .item(' ', Indicator.CONTENT_SLOT)
            .item('U', scrollUp)
            .item('D', scrollDown)
            .scrollContent(players)
            .build();
    }
}
```

### Example 3: Shop with Dynamic Prices

```java
public class Shop {
    
    private static final Map<Player, Integer> playerMoney = new HashMap<>();
    
    public static NormalGUI createShop(GUIManager manager, Player player) {
        int money = playerMoney.getOrDefault(player, 100);
        
        // Border
        StaticGUIItem border = new StaticGUIItem(
            ItemStack.of(Material.BLACK_STAINED_GLASS_PANE)
                .withCustomName(Component.empty())
        );
        
        // Money display (updates dynamically)
        DynamicGUIItem moneyDisplay = new DynamicGUIItem(
            ItemStack.of(Material.GOLD_INGOT)
                .withCustomName(Component.text("Money: $" + money)),
            event -> event.setCancelled(true)
        );
        
        // Shop items
        DynamicGUIItem swordItem = createShopItem(
            Material.DIAMOND_SWORD,
            "Diamond Sword",
            50,
            player,
            moneyDisplay
        );
        
        DynamicGUIItem armorItem = createShopItem(
            Material.DIAMOND_CHESTPLATE,
            "Diamond Armor",
            100,
            player,
            moneyDisplay
        );
        
        return NormalGUI.builder()
            .manager(manager)
            .titled("<gold>Shop - Click to Buy</gold>")
            .format("""
                #########
                #S#A###M#
                #########""")
            .item('#', border)
            .item('M', moneyDisplay)
            .item('S', swordItem)
            .item('A', armorItem)
            .build();
    }
    
    private static DynamicGUIItem createShopItem(
        Material material,
        String name,
        int price,
        Player player,
        DynamicGUIItem moneyDisplay
    ) {
        return new DynamicGUIItem(
            ItemStack.of(material)
                .withCustomName(Component.text(name))
                .withLore(Component.text("Price: $" + price)),
            event -> {
                event.setCancelled(true);
                int currentMoney = playerMoney.getOrDefault(player, 100);
                
                if (currentMoney >= price) {
                    currentMoney -= price;
                    playerMoney.put(player, currentMoney);
                    player.getInventory().addItemStack(ItemStack.of(material));
                    player.sendMessage("Purchased " + name + "!");
                    
                    // Update money display
                    moneyDisplay.setItem(
                        ItemStack.of(Material.GOLD_INGOT)
                            .withCustomName(Component.text("Money: $" + currentMoney))
                    );
                } else {
                    player.sendMessage("Not enough money!");
                }
            }
        );
    }
}
```

### Example 4: Material Browser with Search

```java
public class MaterialBrowser {
    
    public static ScrollGUI createBrowser(GUIManager manager) {
        // Create all material items
        List<GUIItem> materials = Arrays.stream(Material.values())
            .map(material -> new DynamicGUIItem(
                ItemStack.of(material)
                    .withCustomName(Component.text(material.name())),
                event -> {
                    event.setCancelled(true);
                    Player player = event.getPlayer();
                    player.getInventory().addItemStack(ItemStack.of(material));
                    player.sendMessage("Received " + material.name());
                }
            ))
            .collect(Collectors.toList());
        
        // Create GUI components
        StaticGUIItem border = new StaticGUIItem(
            ItemStack.of(Material.CYAN_STAINED_GLASS_PANE)
                .withCustomName(Component.empty())
        );
        
        ScrollGUIItem scrollUp = new ScrollGUIItem(-21,
            ItemStack.of(Material.ARROW)
                .withCustomName(Component.text("⬆ Page Up"))
        );
        
        ScrollGUIItem scrollDown = new ScrollGUIItem(21,
            ItemStack.of(Material.ARROW)
                .withCustomName(Component.text("⬇ Page Down"))
        );
        
        // Build 6-row GUI with large content area
        return ScrollGUI.builder()
            .manager(manager)
            .titled("<gradient:red:gold>Material Browser</gradient>")
            .format("""
                #########
                #       #
                #       #
                #       #
                #       #
                ###U#D###""")
            .item('#', border)
            .item(' ', Indicator.CONTENT_SLOT)
            .item('U', scrollUp)
            .item('D', scrollDown)
            .scrollContent(materials)
            .build();
    }
}
```

### Example 5: Confirmation Dialog

```java
public class ConfirmationDialog {
    
    public static NormalGUI createDialog(
        GUIManager manager,
        String message,
        Runnable onConfirm,
        Runnable onCancel
    ) {
        // Background
        StaticGUIItem background = new StaticGUIItem(
            ItemStack.of(Material.GRAY_STAINED_GLASS_PANE)
                .withCustomName(Component.empty())
        );
        
        // Confirm button
        DynamicGUIItem confirmButton = new DynamicGUIItem(
            ItemStack.of(Material.LIME_WOOL)
                .withCustomName(Component.text("✓ Confirm", NamedTextColor.GREEN)),
            event -> {
                event.setCancelled(true);
                event.getPlayer().closeInventory();
                onConfirm.run();
            }
        );
        
        // Cancel button
        DynamicGUIItem cancelButton = new DynamicGUIItem(
            ItemStack.of(Material.RED_WOOL)
                .withCustomName(Component.text("✗ Cancel", NamedTextColor.RED)),
            event -> {
                event.setCancelled(true);
                event.getPlayer().closeInventory();
                onCancel.run();
            }
        );
        
        // Info item
        StaticGUIItem infoItem = new StaticGUIItem(
            ItemStack.of(Material.PAPER)
                .withCustomName(Component.text(message))
        );
        
        return NormalGUI.builder()
            .manager(manager)
            .titled("<yellow>Confirmation</yellow>")
            .format("""
                #########
                #   I   #
                # Y # N #
                #########""")
            .item('#', background)
            .item('I', infoItem)
            .item('Y', confirmButton)
            .item('N', cancelButton)
            .build();
    }
}
```

## Advanced Usage

### Updating Dynamic Items

Dynamic items can be updated after the GUI is created:

```java
DynamicGUIItem statusItem = new DynamicGUIItem(
    ItemStack.of(Material.REDSTONE).withCustomName(Component.text("Status: Offline")),
    event -> event.setCancelled(true)
);

// Later in your code...
statusItem.setItem(
    ItemStack.of(Material.EMERALD).withCustomName(Component.text("Status: Online"))
);
```

### Refreshing All Dynamic Items

If you have multiple dynamic items that need updating:

```java
// Update your data
updateGameState();

// Refresh all dynamic items in the GUI
gui.refreshDynamicItems();
```

### Changing GUI Format at Runtime

You can completely change the layout of a GUI:

```java
gui.setFormat("""
    #########
    #XXXXXXX#
    #########""");
```

### Managing Multiple GUIs

```java
public class GUIRegistry {
    private final GUIManager manager;
    private final Map<String, NormalGUI> guis = new HashMap<>();
    
    public GUIRegistry(GUIManager manager) {
        this.manager = manager;
    }
    
    public void registerGUI(String id, NormalGUI gui) {
        guis.put(id, gui);
    }
    
    public void openGUI(String id, Player player) {
        NormalGUI gui = guis.get(id);
        if (gui != null) {
            gui.openTo(player);
        }
    }
    
    public void unregisterGUI(String id) {
        NormalGUI gui = guis.remove(id);
        if (gui != null) {
            manager.unregisterGUI(gui);
        }
    }
}
```

### Linked GUIs (Navigation)

```java
public class LinkedGUIExample {
    
    public static NormalGUI createMainMenu(GUIManager manager) {
        NormalGUI settingsMenu = createSettingsMenu(manager);
        
        DynamicGUIItem settingsButton = new DynamicGUIItem(
            ItemStack.of(Material.COMPARATOR)
                .withCustomName(Component.text("Settings")),
            event -> {
                event.setCancelled(true);
                settingsMenu.openTo(event.getPlayer());
            }
        );
        
        return NormalGUI.builder()
            .manager(manager)
            .titled("<blue>Main Menu</blue>")
            .format("#########\n#   S   #\n#########")
            .item('#', border)
            .item('S', settingsButton)
            .build();
    }
    
    private static NormalGUI createSettingsMenu(GUIManager manager) {
        DynamicGUIItem backButton = new DynamicGUIItem(
            ItemStack.of(Material.ARROW)
                .withCustomName(Component.text("« Back")),
            event -> {
                event.setCancelled(true);
                // Go back to main menu
                createMainMenu(manager).openTo(event.getPlayer());
            }
        );
        
        return NormalGUI.builder()
            .manager(manager)
            .titled("<yellow>Settings</yellow>")
            .format("#########\n# B     #\n#########")
            .item('#', border)
            .item('B', backButton)
            .build();
    }
}
```

## Best Practices

### 1. Reuse Static Items

Static items that don't change can be reused across multiple GUIs:

```java
public class GUIComponents {
    public static final StaticGUIItem BORDER = new StaticGUIItem(
        ItemStack.of(Material.GRAY_STAINED_GLASS_PANE)
            .withCustomName(Component.empty())
    );
}
```

### 2. Cancel Events Appropriately

Always cancel the click event if you don't want items to be moveable:

```java
new DynamicGUIItem(item, event -> {
    event.setCancelled(true);  // Prevents item from being moved
    // Your logic here
});
```

### 3. Clean Up Resources

Unregister GUIs when they're no longer needed:

```java
manager.unregisterGUI(gui);
```

### 4. Use Format Constants

Define reusable format strings as constants:

```java
public class GUIFormats {
    public static final String SMALL_MENU = """
        #########
        # X X X #
        #########""";
    
    public static final String LARGE_SCROLL = """
        #########
        #       #
        #       #
        #       #
        #       #
        ###<#>###""";
}
```

### 5. Validate User Input

When using dynamic items that change based on user actions, always validate:

```java
new DynamicGUIItem(item, event -> {
    event.setCancelled(true);
    Player player = event.getPlayer();
    
    // Validate permissions
    if (!player.hasPermission("shop.buy")) {
        player.sendMessage("You don't have permission!");
        return;
    }
    
    // Validate resources
    if (getPlayerMoney(player) < price) {
        player.sendMessage("Insufficient funds!");
        return;
    }
    
    // Process action
    processPurchase(player);
});
```

## Troubleshooting

### GUI Not Opening

**Problem:** GUI doesn't open when calling `openTo(player)`

**Solutions:**
- Ensure `GUIManager` is initialized before creating GUIs
- Verify the GUI was built with `.build()`
- Check that the format string is valid (divisible by 9, max 6 rows)

### Items Not Clickable

**Problem:** Clicking items has no effect

**Solutions:**
- Ensure you're using `DynamicGUIItem` or `ScrollGUIItem` for interactive items
- Verify the click consumer is not null
- Check that events are being cancelled appropriately

### Format String Errors

**Problem:** `IllegalArgumentException` when building GUI

**Solutions:**
- Ensure format is divisible by 9: `format.length() % 9 == 0`
- Maximum 6 rows (54 characters after removing newlines)
- All characters in format must have corresponding items mapped

### Dynamic Items Not Updating

**Problem:** Changes to dynamic items don't appear in GUI

**Solutions:**
- Use `setItem()` method instead of directly modifying the ItemStack
- Call `gui.refreshDynamicItems()` after making changes
- Ensure you're modifying the correct item instance

### Scroll Not Working

**Problem:** ScrollGUI doesn't scroll when clicking scroll items

**Solutions:**
- Ensure you used `Indicator.CONTENT_SLOT` for content area
- Verify ScrollGUIItem scroll amount is not zero
- Check that content list has more items than visible slots

## Performance Tips

1. **Reuse GUIs**: Create GUI templates once and reuse them for multiple players
2. **Batch Updates**: Use `refreshDynamicItems()` instead of updating items individually
3. **Weak References**: The GUI manager uses WeakHashMap, so GUIs are automatically garbage collected
4. **Lazy Loading**: For large scroll lists, consider loading content on-demand

## Contributing

Contributions are welcome! Please feel free to submit pull requests or open issues.

## License

See [LICENSE](LICENSE) file for details.

## Support

For questions, issues, or feature requests, please open an issue on the GitHub repository.

---

**Made with ❤️ for the Minestom community**
