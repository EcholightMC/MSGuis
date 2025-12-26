# MSGUIs

MSGUIs is a lightweight GUI library for Minestom servers, designed to make creating chest-based interfaces simple and intuitive using a character-based layout system.

## Getting Started

To use MSGUIs, you first need to initialize the `GUIManager`. This should be done during your server startup.

```java
import com.github.echolightmc.msguis.GUIManager;
import net.minestom.server.MinecraftServer;

public class MyServer {
    public static GUIManager GUI_MANAGER;

    public static void main(String[] args) {
        // Initialize the server
        MinecraftServer minecraftServer = MinecraftServer.init();

        // Initialize GUIManager with the global event handler
        GUI_MANAGER = new GUIManager(MinecraftServer.getGlobalEventHandler());
        
        // ... rest of your server code
    }
}
```

## Creating GUIs

MSGUIs uses a builder pattern and a visual format string to define the layout of your GUI.

### Normal GUI

A `NormalGUI` is a standard chest interface.

```java
import com.github.echolightmc.msguis.NormalGUI;
import com.github.echolightmc.msguis.StaticGUIItem;
import com.github.echolightmc.msguis.DynamicGUIItem;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;

NormalGUI myGui = NormalGUI.builder()
    .manager(GUI_MANAGER)
    .titled("<red>My Awesome GUI")
    .format("""
        #########
        #       #
        #   X   #
        #       #
        #########""")
    .item('#', new StaticGUIItem(ItemStack.of(Material.GRAY_STAINED_GLASS_PANE)))
    .item('X', new DynamicGUIItem(ItemStack.of(Material.DIAMOND), event -> {
        event.getPlayer().sendMessage("You clicked the diamond!");
    }))
    .build();

// Open the GUI for a player
myGui.openTo(player);
```

### Scroll GUI

A `ScrollGUI` allows you to display a list of items that is larger than the inventory size, with scroll buttons.

```java
import com.github.echolightmc.msguis.ScrollGUI;
import com.github.echolightmc.msguis.Indicator;
import com.github.echolightmc.msguis.ScrollGUIItem;

ScrollGUI scrollGui = ScrollGUI.builder()
    .manager(GUI_MANAGER)
    .titled("<green>Scrollable Inventory")
    .format("""
        #########
        #       #
        #       #
        #       #
        #       #
        ###<#>###""")
    // Define the border
    .item('#', new StaticGUIItem(ItemStack.of(Material.BLACK_STAINED_GLASS_PANE)))
    // Define the scrollable area using the CONTENT_SLOT indicator
    .item(' ', Indicator.CONTENT_SLOT)
    // Define scroll buttons (negative for back, positive for forward)
    .item('<', new ScrollGUIItem(-7, ItemStack.of(Material.ARROW).withCustomName(Component.text("Up"))))
    .item('>', new ScrollGUIItem(7, ItemStack.of(Material.ARROW).withCustomName(Component.text("Down"))))
    // Set the content list
    .scrollContent(itemsList) 
    .build();
```

## GUI Items

There are three main types of items you can use in your GUIs:

### StaticGUIItem
Used for decorative items or borders. It cancels the click event automatically, preventing players from taking the item.

```java
new StaticGUIItem(ItemStack.of(Material.STONE));
```

### DynamicGUIItem
Used for interactive items. It takes an `ItemStack` and a `Consumer<InventoryPreClickEvent>`.

```java
new DynamicGUIItem(ItemStack.of(Material.APPLE), event -> {
    // Handle click logic here
    event.getPlayer().getInventory().addItemStack(ItemStack.of(Material.APPLE));
});
```

### ScrollGUIItem
Specific to `ScrollGUI`. It handles scrolling the content view. The first argument is the scroll amount (slots).

```java
// Scroll back 9 slots (1 row)
new ScrollGUIItem(-9, ItemStack.of(Material.ARROW));
```

## Indicators

Indicators are used to mark special slots in the format string.

*   `Indicator.CONTENT_SLOT`: Used in `ScrollGUI` to define where the scrollable content should appear.
*   `Indicator.INPUT_SLOT`: (Reserved for future use)
*   `Indicator.OUTPUT_SLOT`: (Reserved for future use)

## API Reference

### GUIManager
*   `new GUIManager(GlobalEventHandler)`: Creates a new manager and hooks into Minestom's event system.
*   `registerGUI(Inventory, ChestGUI)`: Registers a GUI (handled automatically by the builder).
*   `unregisterGUI(ChestGUI)`: Unregisters a GUI.

### ChestGUI (Abstract)
*   `openTo(Player...)`: Opens the GUI for the specified players.
*   `setItem(char, GUIItem)`: Updates an item in the GUI dynamically.
*   `setFormat(String)`: Updates the layout format dynamically.
*   `refreshDynamicItems()`: Refreshes items, useful if their underlying state has changed.

### ScrollGUI
*   `scroll(int amount)`: Manually scrolls the view.
*   `setContent(List<? extends GUIItem>)`: Updates the content list.
