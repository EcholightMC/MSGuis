# MSGuis

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

A powerful, format-based GUI library for [Minestom](https://minestom.net/) servers. Create beautiful chest GUIs using intuitive string-based layouts with character mapping.

## Features

- 📝 **Format-Based Layout** - Design GUIs using simple string patterns
- 🔄 **Scrollable Content** - Built-in support for scrollable lists
- 🎨 **MiniMessage Support** - Full Adventure MiniMessage formatting for titles
- ⚡ **Dynamic Items** - Update items on-the-fly with automatic GUI refresh
- 🎯 **Click Handlers** - Easy-to-use functional click event handling
- 🔧 **Builder Pattern** - Fluent API for GUI construction

## Quick Start

### Installation

Add MSGuis to your `build.gradle`:

```gradle
dependencies {
    implementation 'com.github.echolightmc:msguis:VERSION'
}
```

### Basic Usage

```java
import com.github.echolightmc.msguis.*;
import net.minestom.server.MinecraftServer;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;

// 1. Initialize GUIManager (once at startup)
GUIManager guiManager = new GUIManager(MinecraftServer.getGlobalEventHandler());

// 2. Create a GUI using the builder
NormalGUI gui = NormalGUI.builder()
    .manager(guiManager)
    .titled("<gradient:red:gold>My Awesome GUI")
    .format("""
        #########
        #   B   #
        #########""")
    .item('#', new StaticGUIItem(ItemStack.of(Material.GRAY_STAINED_GLASS_PANE)))
    .item('B', new DynamicGUIItem(
        ItemStack.of(Material.DIAMOND),
        event -> {
            event.setCancelled(true);
            event.getPlayer().sendMessage("You clicked the diamond!");
        }
    ))
    .build();

// 3. Open for a player
gui.openTo(player);
```

### Scrollable GUI

```java
ScrollGUI scrollGui = ScrollGUI.builder()
    .manager(guiManager)
    .titled("<aqua>Item Browser")
    .format("""
        #########
        #       #
        #       #
        #       #
        ###<#>###""")
    .item('#', new StaticGUIItem(borderItem))
    .item(' ', Indicator.CONTENT_SLOT)  // Define scroll area
    .item('<', new ScrollGUIItem(-7, backArrow))
    .item('>', new ScrollGUIItem(7, forwardArrow))
    .scrollContent(itemList)
    .build();
```

## Format System

The format system uses a string where each character maps to a GUI item:

```
#########     <- Row 1: 9 characters
#   B   #     <- Row 2: 9 characters  
#########     <- Row 3: 9 characters
```

- Each row **must have exactly 9 characters**
- Maximum of **6 rows** (54 slots total)
- Each unique character is mapped to a `GUIItem`

### Character Mapping Examples

| Character | Common Usage |
|-----------|--------------|
| `#` | Border/frame items |
| ` ` (space) | Empty slots or content area |
| `<` `>` | Navigation arrows |
| `A-Z` | Action buttons |

## GUI Item Types

| Type | Description | Click Behavior |
|------|-------------|----------------|
| `StaticGUIItem` | Fixed item display | Cancels click (no interaction) |
| `DynamicGUIItem` | Mutable item with custom handler | Custom consumer function |
| `ScrollGUIItem` | Scroll navigation button | Scrolls the parent ScrollGUI |

## GUI Types

### NormalGUI

Standard chest GUI for menus, dialogs, and static content.

```java
NormalGUI.builder()
    .manager(guiManager)
    .titled("<green>Normal GUI")
    .format("...")
    .item('X', guiItem)
    .build();
```

### ScrollGUI

GUI with a scrollable content area, perfect for lists and browsers.

```java
ScrollGUI.builder()
    .manager(guiManager)
    .titled("<blue>Scroll GUI")
    .format("...")
    .item(' ', Indicator.CONTENT_SLOT)
    .scrollContent(contentList)
    .build();
```

## API Components

| Component | Description |
|-----------|-------------|
| `GUIManager` | Central manager for GUI registration and event handling |
| `ChestGUI` | Abstract base class for all chest GUIs |
| `NormalGUI` | Standard static GUI implementation |
| `ScrollGUI` | Scrollable content GUI implementation |
| `GUIItem` | Abstract base class for all GUI items |
| `StaticGUIItem` | Immutable display item |
| `DynamicGUIItem` | Mutable item with click handler |
| `ScrollGUIItem` | Scroll navigation item |
| `ChestType` | Enum for inventory sizes (1-6 rows) |
| `Indicator` | Enum for special slot types |

## Documentation

📖 **[Full API Documentation](docs/API.md)** - Complete reference with all methods, parameters, and examples.

## Example Project

Check out the test files for a working example:

- [`DemoMain.java`](src/test/java/DemoMain.java) - Server setup with GUIManager
- [`ScrollCommand.java`](src/test/java/ScrollCommand.java) - ScrollGUI implementation

## Requirements

- Java 17+
- Minestom
- Adventure API (MiniMessage)

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.
