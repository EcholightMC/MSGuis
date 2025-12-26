# MSGuis

Easy-to-use **Chest GUI** library for **Minestom**.

- **Core idea**: define an inventory layout with a *format string* (characters map to slots), then attach `GUIItem`s to those characters.
- **Supported GUIs**: `NormalGUI` (static layout) and `ScrollGUI` (layout + scrollable content region).
- **Runtime**: Java 21+ (see `build.gradle`).

## Installation

### Gradle (recommended)

```gradle
repositories {
    mavenCentral()
    maven { url = "https://jitpack.io" }
}

dependencies {
    implementation "com.github.echolightmc:MSGuis:1.4-SNAPSHOT"
}
```

> Note: the library depends on Minestom snapshots and Adventure MiniMessage. Align your Minestom version accordingly.

## Quickstart

### 1) Create a `GUIManager`

Create one `GUIManager` during server startup and keep it around. It hooks inventory click events on the provided `GlobalEventHandler`.

```java
import com.github.echolightmc.msguis.GUIManager;
import net.minestom.server.MinecraftServer;

GUIManager guiManager = new GUIManager(MinecraftServer.getGlobalEventHandler());
```

### 2) Build and open a `NormalGUI`

```java
import com.github.echolightmc.msguis.NormalGUI;
import com.github.echolightmc.msguis.StaticGUIItem;
import net.kyori.adventure.text.Component;
import net.minestom.server.entity.Player;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;

NormalGUI gui = NormalGUI.builder()
        .manager(guiManager)
        .titled("<gold>Example GUI") // MiniMessage string -> Component
        .format("""
                #########
                #   X   #
                #########
                """)
        .item('#', new StaticGUIItem(ItemStack.of(Material.GRAY_STAINED_GLASS_PANE).withCustomName(Component.empty())))
        .item('X', new StaticGUIItem(ItemStack.of(Material.DIAMOND)))
        .build();

gui.openTo(player);
```

## Concepts

### Format strings

- **A format string is a grid of characters** (9 columns wide per row).
- Newlines are allowed and automatically removed.
- Each character represents a slot (same character = repeated across all matching slots).

Example (3 rows):

```text
#########
#  X    #
#########
```

Rules:
- total character count must be divisible by **9**
- row count must be **1..6**

### `GUIItem`s

All clickable slots are backed by a `GUIItem`:

- `StaticGUIItem`: fixed `ItemStack`, cancels clicks by default.
- `DynamicGUIItem`: mutable `ItemStack` + click handler; calling `setItem(...)` updates the GUI slot(s) immediately.
- `ScrollGUIItem`: a convenience `GUIItem` that scrolls its owning `ScrollGUI` when clicked.

### Dynamic items: refreshing

If a slot uses a `DynamicGUIItem`, changes propagate automatically when you call `DynamicGUIItem#setItem(...)`.

If your dynamic item’s `getItem()` depends on external state (time, player state, etc.), call:

```java
gui.refreshDynamicItems();
```

This refreshes only slots backed by `DynamicGUIItem` (and also refreshes visible scroll content in a `ScrollGUI`).

## Scroll GUIs

`ScrollGUI` supports a “content region” defined via an `Indicator`.

Minimal example (from `src/test/java/ScrollCommand.java`):

```java
import com.github.echolightmc.msguis.Indicator;
import com.github.echolightmc.msguis.ScrollGUI;
import com.github.echolightmc.msguis.ScrollGUIItem;
import com.github.echolightmc.msguis.StaticGUIItem;
import net.kyori.adventure.text.Component;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;

ScrollGUI gui = ScrollGUI.builder()
        .manager(guiManager)
        .titled("<red>Scroll GUI")
        .format("""
                #########
                #       #
                #       #
                #       #
                #       #
                ###<#>###""")
        .item('#', new StaticGUIItem(ItemStack.of(Material.GRAY_STAINED_GLASS_PANE).withCustomName(Component.empty())))
        .item(' ', Indicator.CONTENT_SLOT) // marks all ' ' slots as the scrollable content region
        .item('<', new ScrollGUIItem(-7, ItemStack.of(Material.ARROW).withCustomName(Component.text("Scroll Back"))))
        .item('>', new ScrollGUIItem(7, ItemStack.of(Material.ARROW).withCustomName(Component.text("Scroll Forward"))))
        .scrollContent(java.util.List.of(
                new StaticGUIItem(ItemStack.of(Material.BOW))
        ))
        .build();
```

## API docs

- **Full API reference**: see `docs/API.md`
- **In-IDE docs**: every public type/method is documented via Javadoc in `src/main/java/...`

