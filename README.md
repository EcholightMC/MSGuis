# MSGUIs - Minestom GUI Library

A powerful and easy-to-use GUI library for Minestom servers, providing format-based GUI layouts with support for static items, dynamic items, and scrollable content.

## Features

- 🎨 **Format-based Layouts**: Define GUI layouts using simple character-based format strings
- 📦 **Multiple GUI Types**: Support for normal GUIs and scrollable GUIs
- 🔄 **Dynamic Items**: Items that can be updated at runtime
- 📜 **Scrollable Content**: Built-in support for scrollable item lists
- 🎯 **Event Handling**: Automatic click event handling and cancellation
- 🎨 **Rich Text**: MiniMessage support for colorful titles

## Quick Start

### 1. Initialize GUIManager

```java
import com.github.echolightmc.msguis.GUIManager;
import net.minestom.server.MinecraftServer;

GUIManager guiManager = new GUIManager(MinecraftServer.getGlobalEventHandler());
```

### 2. Create a Simple GUI

```java
import com.github.echolightmc.msguis.NormalGUI;
import com.github.echolightmc.msguis.StaticGUIItem;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;

NormalGUI gui = NormalGUI.builder()
    .manager(guiManager)
    .titled("<green>My GUI")
    .format("#########" +
            "#       #" +
            "#   X   #" +
            "#########")
    .item('#', new StaticGUIItem(ItemStack.of(Material.GRAY_STAINED_GLASS_PANE)))
    .item('X', new StaticGUIItem(ItemStack.of(Material.DIAMOND)))
    .build();

gui.openTo(player);
```

### 3. Create a Scrollable GUI

```java
import com.github.echolightmc.msguis.ScrollGUI;
import com.github.echolightmc.msguis.ScrollGUIItem;
import com.github.echolightmc.msguis.Indicator;

ScrollGUI scrollGUI = ScrollGUI.builder()
    .manager(guiManager)
    .titled("<blue>Scrollable Items")
    .format("#########" +
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

## Documentation

For comprehensive API documentation, examples, and usage instructions, see [API_DOCUMENTATION.md](API_DOCUMENTATION.md).

## Core Components

- **GUIManager**: Manages GUI registration and click events
- **NormalGUI**: Standard GUI without scrollable content
- **ScrollGUI**: GUI with scrollable content areas
- **StaticGUIItem**: Immutable GUI items (borders, static menu items)
- **DynamicGUIItem**: Mutable GUI items that can be updated at runtime
- **ScrollGUIItem**: Special items that trigger scrolling

## Requirements

- Java 21+
- Minestom (latest snapshot)
- Adventure Text MiniMessage 4.16.0+

## Installation

Add to your `build.gradle`:

```gradle
repositories {
    maven {
        url = "https://jitpack.io"
    }
}

dependencies {
    implementation "com.github.echolightmc:msguis:1.4-SNAPSHOT"
}
```

## Examples

See the [API Documentation](API_DOCUMENTATION.md#examples) for comprehensive examples including:
- Simple menu GUIs
- GUIs with click handlers
- Scrollable item lists
- Dynamic item updates
- Multi-player GUIs

## License

See LICENSE file for details.
