# MSGuis Usage Examples

This document contains comprehensive, real-world examples for using MSGuis in your Minecraft server.

## Table of Contents

- [Basic Examples](#basic-examples)
  - [Simple Menu](#simple-menu)
  - [Confirmation Dialog](#confirmation-dialog)
  - [Settings Menu](#settings-menu)
- [Intermediate Examples](#intermediate-examples)
  - [Shop System](#shop-system)
  - [Player Selector](#player-selector)
  - [Item Browser](#item-browser)
- [Advanced Examples](#advanced-examples)
  - [Paginated Shop](#paginated-shop)
  - [Dynamic Crafting GUI](#dynamic-crafting-gui)
  - [Multi-Page Settings](#multi-page-settings)
- [Special Cases](#special-cases)
  - [Search System](#search-system)
  - [Animated Items](#animated-items)
  - [Permission-Based GUIs](#permission-based-guis)

---

## Basic Examples

### Simple Menu

A basic menu with a few options.

```java
public class SimpleMenu {
    
    private final GUIManager guiManager;
    
    public SimpleMenu(GUIManager manager) {
        this.guiManager = manager;
    }
    
    public NormalGUI create(Player player) {
        // Create border
        StaticGUIItem border = new StaticGUIItem(
            ItemStack.of(Material.BLACK_STAINED_GLASS_PANE)
                .withCustomName(Component.empty())
        );
        
        // Home button
        DynamicGUIItem homeButton = new DynamicGUIItem(
            ItemStack.of(Material.RED_BED)
                .withCustomName(Component.text("Teleport Home", NamedTextColor.GREEN))
                .withLore(
                    Component.text("Click to teleport to your home", NamedTextColor.GRAY)
                ),
            event -> {
                event.setCancelled(true);
                player.teleport(getPlayerHome(player));
                player.closeInventory();
                player.sendMessage(Component.text("Welcome home!", NamedTextColor.GREEN));
            }
        );
        
        // Spawn button
        DynamicGUIItem spawnButton = new DynamicGUIItem(
            ItemStack.of(Material.COMPASS)
                .withCustomName(Component.text("Teleport to Spawn", NamedTextColor.AQUA))
                .withLore(
                    Component.text("Click to teleport to spawn", NamedTextColor.GRAY)
                ),
            event -> {
                event.setCancelled(true);
                player.teleport(getSpawnLocation());
                player.closeInventory();
                player.sendMessage(Component.text("Teleported to spawn!", NamedTextColor.AQUA));
            }
        );
        
        // Close button
        DynamicGUIItem closeButton = new DynamicGUIItem(
            ItemStack.of(Material.BARRIER)
                .withCustomName(Component.text("Close", NamedTextColor.RED)),
            event -> {
                event.setCancelled(true);
                player.closeInventory();
            }
        );
        
        return NormalGUI.builder()
            .manager(guiManager)
            .titled("<gradient:green:blue>Main Menu</gradient>")
            .format("""
                #########
                #H#S###C#
                #########""")
            .item('#', border)
            .item('H', homeButton)
            .item('S', spawnButton)
            .item('C', closeButton)
            .build();
    }
    
    private Pos getPlayerHome(Player player) {
        // Your implementation
        return new Pos(0, 64, 0);
    }
    
    private Pos getSpawnLocation() {
        // Your implementation
        return new Pos(0, 64, 0);
    }
}
```

---

### Confirmation Dialog

A reusable confirmation dialog for dangerous actions.

```java
public class ConfirmationDialog {
    
    public static NormalGUI create(
        GUIManager manager,
        String title,
        String message,
        Material iconMaterial,
        Runnable onConfirm,
        Runnable onCancel
    ) {
        // Background
        StaticGUIItem background = new StaticGUIItem(
            ItemStack.of(Material.GRAY_STAINED_GLASS_PANE)
                .withCustomName(Component.empty())
        );
        
        // Message/info display
        StaticGUIItem messageItem = new StaticGUIItem(
            ItemStack.of(iconMaterial)
                .withCustomName(Component.text(message, NamedTextColor.YELLOW))
                .withLore(
                    Component.empty(),
                    Component.text("Are you sure?", NamedTextColor.GRAY)
                )
        );
        
        // Confirm button
        DynamicGUIItem confirmButton = new DynamicGUIItem(
            ItemStack.of(Material.LIME_WOOL)
                .withCustomName(Component.text("✓ Confirm", NamedTextColor.GREEN))
                .withLore(
                    Component.text("Click to confirm", NamedTextColor.GRAY)
                ),
            event -> {
                event.setCancelled(true);
                event.getPlayer().closeInventory();
                onConfirm.run();
            }
        );
        
        // Cancel button
        DynamicGUIItem cancelButton = new DynamicGUIItem(
            ItemStack.of(Material.RED_WOOL)
                .withCustomName(Component.text("✗ Cancel", NamedTextColor.RED))
                .withLore(
                    Component.text("Click to cancel", NamedTextColor.GRAY)
                ),
            event -> {
                event.setCancelled(true);
                event.getPlayer().closeInventory();
                onCancel.run();
            }
        );
        
        return NormalGUI.builder()
            .manager(manager)
            .titled("<yellow>" + title + "</yellow>")
            .format("""
                #########
                ####M####
                #Y#####N#
                #########""")
            .item('#', background)
            .item('M', messageItem)
            .item('Y', confirmButton)
            .item('N', cancelButton)
            .build();
    }
}

// Usage:
NormalGUI confirmDialog = ConfirmationDialog.create(
    guiManager,
    "Delete Home",
    "Delete your home location?",
    Material.RED_BED,
    () -> {
        deletePlayerHome(player);
        player.sendMessage("Home deleted!");
    },
    () -> player.sendMessage("Cancelled.")
);
confirmDialog.openTo(player);
```

---

### Settings Menu

A settings menu with toggleable options.

```java
public class SettingsMenu {
    
    private final GUIManager guiManager;
    private final Map<Player, PlayerSettings> playerSettings = new HashMap<>();
    
    public SettingsMenu(GUIManager manager) {
        this.guiManager = manager;
    }
    
    public NormalGUI create(Player player) {
        PlayerSettings settings = getSettings(player);
        
        // Border
        StaticGUIItem border = new StaticGUIItem(
            ItemStack.of(Material.GRAY_STAINED_GLASS_PANE)
                .withCustomName(Component.empty())
        );
        
        // PvP toggle
        DynamicGUIItem pvpToggle = createToggle(
            "PvP",
            Material.DIAMOND_SWORD,
            settings.pvpEnabled,
            enabled -> {
                settings.pvpEnabled = enabled;
                player.sendMessage(Component.text(
                    "PvP " + (enabled ? "enabled" : "disabled"),
                    enabled ? NamedTextColor.GREEN : NamedTextColor.RED
                ));
            }
        );
        
        // Flight toggle
        DynamicGUIItem flightToggle = createToggle(
            "Flight",
            Material.ELYTRA,
            settings.flightEnabled,
            enabled -> {
                settings.flightEnabled = enabled;
                player.setAllowFlying(enabled);
                player.sendMessage(Component.text(
                    "Flight " + (enabled ? "enabled" : "disabled"),
                    enabled ? NamedTextColor.GREEN : NamedTextColor.RED
                ));
            }
        );
        
        // Chat toggle
        DynamicGUIItem chatToggle = createToggle(
            "Chat",
            Material.WRITABLE_BOOK,
            settings.chatEnabled,
            enabled -> {
                settings.chatEnabled = enabled;
                player.sendMessage(Component.text(
                    "Chat " + (enabled ? "enabled" : "disabled"),
                    enabled ? NamedTextColor.GREEN : NamedTextColor.RED
                ));
            }
        );
        
        return NormalGUI.builder()
            .manager(guiManager)
            .titled("<gold>Settings</gold>")
            .format("""
                #########
                #P#F#C###
                #########""")
            .item('#', border)
            .item('P', pvpToggle)
            .item('F', flightToggle)
            .item('C', chatToggle)
            .build();
    }
    
    private DynamicGUIItem createToggle(
        String name,
        Material material,
        boolean initialState,
        Consumer<Boolean> onChange
    ) {
        AtomicBoolean state = new AtomicBoolean(initialState);
        
        DynamicGUIItem toggle = new DynamicGUIItem(
            createToggleItem(name, material, state.get()),
            event -> {
                event.setCancelled(true);
                boolean newState = !state.get();
                state.set(newState);
                
                // Update visual
                toggle.setItem(createToggleItem(name, material, newState));
                
                // Trigger callback
                onChange.accept(newState);
            }
        );
        
        return toggle;
    }
    
    private ItemStack createToggleItem(String name, Material material, boolean enabled) {
        return ItemStack.of(material)
            .withCustomName(Component.text(
                name + ": " + (enabled ? "ON" : "OFF"),
                enabled ? NamedTextColor.GREEN : NamedTextColor.RED
            ))
            .withLore(
                Component.text("Click to toggle", NamedTextColor.GRAY),
                Component.empty(),
                Component.text("Status: " + (enabled ? "Enabled" : "Disabled"),
                    enabled ? NamedTextColor.GREEN : NamedTextColor.RED)
            );
    }
    
    private PlayerSettings getSettings(Player player) {
        return playerSettings.computeIfAbsent(player, p -> new PlayerSettings());
    }
    
    static class PlayerSettings {
        boolean pvpEnabled = true;
        boolean flightEnabled = false;
        boolean chatEnabled = true;
    }
}
```

---

## Intermediate Examples

### Shop System

A shop with buyable items and dynamic money display.

```java
public class ShopGUI {
    
    private final GUIManager guiManager;
    private final Economy economy;
    
    public ShopGUI(GUIManager manager, Economy economy) {
        this.guiManager = manager;
        this.economy = economy;
    }
    
    public NormalGUI create(Player player) {
        int playerMoney = economy.getMoney(player);
        
        // Border
        StaticGUIItem border = new StaticGUIItem(
            ItemStack.of(Material.BLACK_STAINED_GLASS_PANE)
                .withCustomName(Component.empty())
        );
        
        // Money display (updates dynamically)
        DynamicGUIItem moneyDisplay = new DynamicGUIItem(
            createMoneyItem(playerMoney),
            event -> event.setCancelled(true)
        );
        
        // Shop items
        List<ShopItem> shopItems = List.of(
            new ShopItem("Diamond Sword", Material.DIAMOND_SWORD, 100, 1),
            new ShopItem("Diamond Pickaxe", Material.DIAMOND_PICKAXE, 150, 1),
            new ShopItem("Enchanted Golden Apple", Material.ENCHANTED_GOLDEN_APPLE, 50, 1),
            new ShopItem("Diamond", Material.DIAMOND, 10, 64),
            new ShopItem("Emerald", Material.EMERALD, 20, 64)
        );
        
        // Create shop item buttons
        char[] itemChars = {'A', 'B', 'C', 'D', 'E'};
        NormalGUI.NormalGUIBuilder builder = NormalGUI.builder()
            .manager(guiManager)
            .titled("<gold><bold>Shop</bold></gold>")
            .format("""
                #########
                #ABCDE#M#
                #########""")
            .item('#', border)
            .item('M', moneyDisplay);
        
        for (int i = 0; i < shopItems.size(); i++) {
            ShopItem shopItem = shopItems.get(i);
            DynamicGUIItem button = createShopItemButton(
                player, shopItem, moneyDisplay
            );
            builder.item(itemChars[i], button);
        }
        
        return builder.build();
    }
    
    private DynamicGUIItem createShopItemButton(
        Player player,
        ShopItem shopItem,
        DynamicGUIItem moneyDisplay
    ) {
        return new DynamicGUIItem(
            ItemStack.of(shopItem.material)
                .withAmount(shopItem.amount)
                .withCustomName(Component.text(shopItem.name, NamedTextColor.GREEN))
                .withLore(
                    Component.text("Price: $" + shopItem.price, NamedTextColor.GOLD),
                    Component.text("Amount: " + shopItem.amount, NamedTextColor.GRAY),
                    Component.empty(),
                    Component.text("Click to purchase", NamedTextColor.YELLOW)
                ),
            event -> {
                event.setCancelled(true);
                
                int playerMoney = economy.getMoney(player);
                
                if (playerMoney >= shopItem.price) {
                    // Process purchase
                    economy.removeMoney(player, shopItem.price);
                    player.getInventory().addItemStack(
                        ItemStack.of(shopItem.material).withAmount(shopItem.amount)
                    );
                    
                    player.sendMessage(Component.text(
                        "Purchased " + shopItem.name + " for $" + shopItem.price,
                        NamedTextColor.GREEN
                    ));
                    
                    // Update money display
                    int newMoney = economy.getMoney(player);
                    moneyDisplay.setItem(createMoneyItem(newMoney));
                    
                    // Play sound
                    player.playSound(
                        Sound.sound(SoundEvent.BLOCK_NOTE_BLOCK_PLING, Sound.Source.MASTER, 1f, 2f)
                    );
                } else {
                    player.sendMessage(Component.text(
                        "You need $" + shopItem.price + " (you have $" + playerMoney + ")",
                        NamedTextColor.RED
                    ));
                    player.playSound(
                        Sound.sound(SoundEvent.ENTITY_VILLAGER_NO, Sound.Source.MASTER, 1f, 1f)
                    );
                }
            }
        );
    }
    
    private ItemStack createMoneyItem(int money) {
        return ItemStack.of(Material.GOLD_INGOT)
            .withCustomName(Component.text("Your Money", NamedTextColor.GOLD))
            .withLore(
                Component.empty(),
                Component.text("Balance: $" + money, NamedTextColor.YELLOW)
            );
    }
    
    record ShopItem(String name, Material material, int price, int amount) {}
    
    // Simple economy interface
    interface Economy {
        int getMoney(Player player);
        void removeMoney(Player player, int amount);
        void addMoney(Player player, int amount);
    }
}
```

---

### Player Selector

A scrollable GUI for selecting online players.

```java
public class PlayerSelectorGUI {
    
    private final GUIManager guiManager;
    
    public PlayerSelectorGUI(GUIManager manager) {
        this.guiManager = manager;
    }
    
    public ScrollGUI create(Player viewer, Consumer<Player> onSelect) {
        // Border
        StaticGUIItem border = new StaticGUIItem(
            ItemStack.of(Material.CYAN_STAINED_GLASS_PANE)
                .withCustomName(Component.empty())
        );
        
        // Scroll buttons
        ScrollGUIItem scrollUp = new ScrollGUIItem(-7,
            ItemStack.of(Material.ARROW)
                .withCustomName(Component.text("⬆ Previous Page", NamedTextColor.YELLOW))
        );
        
        ScrollGUIItem scrollDown = new ScrollGUIItem(7,
            ItemStack.of(Material.ARROW)
                .withCustomName(Component.text("⬇ Next Page", NamedTextColor.YELLOW))
        );
        
        // Create player items
        List<GUIItem> playerItems = MinecraftServer.getConnectionManager()
            .getOnlinePlayers()
            .stream()
            .filter(p -> !p.equals(viewer)) // Exclude the viewer
            .map(player -> createPlayerItem(player, viewer, onSelect))
            .collect(Collectors.toList());
        
        if (playerItems.isEmpty()) {
            playerItems.add(new StaticGUIItem(
                ItemStack.of(Material.BARRIER)
                    .withCustomName(Component.text("No players online", NamedTextColor.RED))
            ));
        }
        
        return ScrollGUI.builder()
            .manager(guiManager)
            .titled("<aqua>Select a Player</aqua>")
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
            .scrollContent(playerItems)
            .build();
    }
    
    private DynamicGUIItem createPlayerItem(
        Player target,
        Player viewer,
        Consumer<Player> onSelect
    ) {
        return new DynamicGUIItem(
            ItemStack.of(Material.PLAYER_HEAD)
                .withCustomName(Component.text(target.getUsername(), NamedTextColor.GREEN))
                .withLore(
                    Component.text("Health: " + (int)target.getHealth() + "/20", NamedTextColor.RED),
                    Component.text("Level: " + target.getLevel(), NamedTextColor.GOLD),
                    Component.empty(),
                    Component.text("Click to select", NamedTextColor.GRAY)
                ),
            event -> {
                event.setCancelled(true);
                viewer.closeInventory();
                onSelect.accept(target);
            }
        );
    }
}

// Usage:
PlayerSelectorGUI selector = new PlayerSelectorGUI(guiManager);
ScrollGUI gui = selector.create(player, selectedPlayer -> {
    player.sendMessage(Component.text("You selected: " + selectedPlayer.getUsername()));
    // Do something with the selected player
});
gui.openTo(player);
```

---

### Item Browser

Browse all materials in the game.

```java
public class ItemBrowserGUI {
    
    private final GUIManager guiManager;
    
    public ItemBrowserGUI(GUIManager manager) {
        this.guiManager = manager;
    }
    
    public ScrollGUI create(Player player) {
        // Border
        StaticGUIItem border = new StaticGUIItem(
            ItemStack.of(Material.PURPLE_STAINED_GLASS_PANE)
                .withCustomName(Component.empty())
        );
        
        // Scroll buttons
        ScrollGUIItem scrollUp = new ScrollGUIItem(-21,
            ItemStack.of(Material.SPECTRAL_ARROW)
                .withCustomName(Component.text("⬆⬆ Page Up", NamedTextColor.LIGHT_PURPLE))
        );
        
        ScrollGUIItem scrollDown = new ScrollGUIItem(21,
            ItemStack.of(Material.SPECTRAL_ARROW)
                .withCustomName(Component.text("⬇⬇ Page Down", NamedTextColor.LIGHT_PURPLE))
        );
        
        // Info item
        StaticGUIItem infoItem = new StaticGUIItem(
            ItemStack.of(Material.BOOK)
                .withCustomName(Component.text("Material Browser", NamedTextColor.GOLD))
                .withLore(
                    Component.text("Browse all materials", NamedTextColor.GRAY),
                    Component.text("Click an item to receive it", NamedTextColor.YELLOW)
                )
        );
        
        // Create items for all materials
        List<GUIItem> materialItems = Arrays.stream(Material.values())
            .filter(Material::isItem) // Only items, not blocks
            .map(material -> new DynamicGUIItem(
                ItemStack.of(material)
                    .withCustomName(Component.text(
                        formatMaterialName(material),
                        NamedTextColor.AQUA
                    ))
                    .withLore(
                        Component.text("ID: " + material.name(), NamedTextColor.DARK_GRAY),
                        Component.empty(),
                        Component.text("Click to receive", NamedTextColor.GRAY)
                    ),
                event -> {
                    event.setCancelled(true);
                    player.getInventory().addItemStack(ItemStack.of(material));
                    player.sendMessage(Component.text(
                        "Received " + formatMaterialName(material),
                        NamedTextColor.GREEN
                    ));
                }
            ))
            .collect(Collectors.toList());
        
        return ScrollGUI.builder()
            .manager(guiManager)
            .titled("<gradient:red:gold>Material Browser</gradient>")
            .format("""
                ###I#####
                #       #
                #       #
                #       #
                #       #
                ###U#D###""")
            .item('#', border)
            .item('I', infoItem)
            .item(' ', Indicator.CONTENT_SLOT)
            .item('U', scrollUp)
            .item('D', scrollDown)
            .scrollContent(materialItems)
            .build();
    }
    
    private String formatMaterialName(Material material) {
        String name = material.name().toLowerCase().replace('_', ' ');
        return Arrays.stream(name.split(" "))
            .map(word -> word.substring(0, 1).toUpperCase() + word.substring(1))
            .collect(Collectors.joining(" "));
    }
}
```

---

## Advanced Examples

### Paginated Shop

A shop with multiple pages of categories.

```java
public class PaginatedShopGUI {
    
    private final GUIManager guiManager;
    private final Map<ShopCategory, List<ShopItem>> categories;
    
    public PaginatedShopGUI(GUIManager manager) {
        this.guiManager = manager;
        this.categories = initializeCategories();
    }
    
    public NormalGUI createMainMenu(Player player) {
        StaticGUIItem border = new StaticGUIItem(
            ItemStack.of(Material.BLACK_STAINED_GLASS_PANE)
                .withCustomName(Component.empty())
        );
        
        // Create category buttons
        DynamicGUIItem weaponsButton = createCategoryButton(
            player, ShopCategory.WEAPONS, Material.DIAMOND_SWORD
        );
        DynamicGUIItem toolsButton = createCategoryButton(
            player, ShopCategory.TOOLS, Material.DIAMOND_PICKAXE
        );
        DynamicGUIItem armorButton = createCategoryButton(
            player, ShopCategory.ARMOR, Material.DIAMOND_CHESTPLATE
        );
        DynamicGUIItem foodButton = createCategoryButton(
            player, ShopCategory.FOOD, Material.COOKED_BEEF
        );
        DynamicGUIItem blocksButton = createCategoryButton(
            player, ShopCategory.BLOCKS, Material.STONE_BRICKS
        );
        
        return NormalGUI.builder()
            .manager(guiManager)
            .titled("<gold><bold>Shop Categories</bold></gold>")
            .format("""
                #########
                #W#T#A###
                #F#B#####
                #########""")
            .item('#', border)
            .item('W', weaponsButton)
            .item('T', toolsButton)
            .item('A', armorButton)
            .item('F', foodButton)
            .item('B', blocksButton)
            .build();
    }
    
    private DynamicGUIItem createCategoryButton(
        Player player,
        ShopCategory category,
        Material icon
    ) {
        return new DynamicGUIItem(
            ItemStack.of(icon)
                .withCustomName(Component.text(category.displayName, NamedTextColor.YELLOW))
                .withLore(
                    Component.text(
                        categories.get(category).size() + " items available",
                        NamedTextColor.GRAY
                    ),
                    Component.empty(),
                    Component.text("Click to browse", NamedTextColor.GREEN)
                ),
            event -> {
                event.setCancelled(true);
                ScrollGUI categoryGUI = createCategoryGUI(player, category);
                categoryGUI.openTo(player);
            }
        );
    }
    
    private ScrollGUI createCategoryGUI(Player player, ShopCategory category) {
        StaticGUIItem border = new StaticGUIItem(
            ItemStack.of(Material.GRAY_STAINED_GLASS_PANE)
                .withCustomName(Component.empty())
        );
        
        // Back button
        DynamicGUIItem backButton = new DynamicGUIItem(
            ItemStack.of(Material.ARROW)
                .withCustomName(Component.text("« Back to Categories", NamedTextColor.YELLOW)),
            event -> {
                event.setCancelled(true);
                createMainMenu(player).openTo(player);
            }
        );
        
        // Scroll buttons
        ScrollGUIItem scrollUp = new ScrollGUIItem(-7,
            ItemStack.of(Material.ARROW)
                .withCustomName(Component.text("⬆ Previous", NamedTextColor.AQUA))
        );
        
        ScrollGUIItem scrollDown = new ScrollGUIItem(7,
            ItemStack.of(Material.ARROW)
                .withCustomName(Component.text("⬇ Next", NamedTextColor.AQUA))
        );
        
        // Create shop item buttons
        List<GUIItem> items = categories.get(category).stream()
            .map(shopItem -> createShopItemButton(player, shopItem))
            .collect(Collectors.toList());
        
        return ScrollGUI.builder()
            .manager(guiManager)
            .titled("<gold>" + category.displayName + "</gold>")
            .format("""
                ###B#####
                #       #
                #       #
                #       #
                ###U#D###""")
            .item('#', border)
            .item('B', backButton)
            .item(' ', Indicator.CONTENT_SLOT)
            .item('U', scrollUp)
            .item('D', scrollDown)
            .scrollContent(items)
            .build();
    }
    
    private DynamicGUIItem createShopItemButton(Player player, ShopItem item) {
        return new DynamicGUIItem(
            ItemStack.of(item.material)
                .withAmount(item.amount)
                .withCustomName(Component.text(item.name, NamedTextColor.GREEN))
                .withLore(
                    Component.text("Price: $" + item.price, NamedTextColor.GOLD),
                    Component.empty(),
                    Component.text("Click to purchase", NamedTextColor.YELLOW)
                ),
            event -> {
                event.setCancelled(true);
                // Purchase logic here
                player.sendMessage(Component.text("Purchased " + item.name));
            }
        );
    }
    
    private Map<ShopCategory, List<ShopItem>> initializeCategories() {
        Map<ShopCategory, List<ShopItem>> map = new HashMap<>();
        
        // Weapons
        map.put(ShopCategory.WEAPONS, List.of(
            new ShopItem("Wooden Sword", Material.WOODEN_SWORD, 10, 1),
            new ShopItem("Stone Sword", Material.STONE_SWORD, 25, 1),
            new ShopItem("Iron Sword", Material.IRON_SWORD, 50, 1),
            new ShopItem("Diamond Sword", Material.DIAMOND_SWORD, 100, 1)
        ));
        
        // Tools
        map.put(ShopCategory.TOOLS, List.of(
            new ShopItem("Iron Pickaxe", Material.IRON_PICKAXE, 40, 1),
            new ShopItem("Diamond Pickaxe", Material.DIAMOND_PICKAXE, 80, 1),
            new ShopItem("Iron Axe", Material.IRON_AXE, 35, 1),
            new ShopItem("Diamond Axe", Material.DIAMOND_AXE, 70, 1)
        ));
        
        // Add more categories...
        map.put(ShopCategory.ARMOR, new ArrayList<>());
        map.put(ShopCategory.FOOD, new ArrayList<>());
        map.put(ShopCategory.BLOCKS, new ArrayList<>());
        
        return map;
    }
    
    enum ShopCategory {
        WEAPONS("Weapons"),
        TOOLS("Tools"),
        ARMOR("Armor"),
        FOOD("Food"),
        BLOCKS("Blocks");
        
        final String displayName;
        
        ShopCategory(String displayName) {
            this.displayName = displayName;
        }
    }
    
    record ShopItem(String name, Material material, int price, int amount) {}
}
```

---

## Special Cases

### Search System

A search system that filters content dynamically.

```java
public class SearchableGUI {
    
    private final GUIManager guiManager;
    private final List<SearchableItem> allItems;
    private ScrollGUI currentGUI;
    
    public SearchableGUI(GUIManager manager) {
        this.guiManager = manager;
        this.allItems = initializeItems();
    }
    
    public ScrollGUI create(Player player, String searchTerm) {
        // Filter items based on search
        List<GUIItem> filteredItems = allItems.stream()
            .filter(item -> searchTerm.isEmpty() || 
                item.name.toLowerCase().contains(searchTerm.toLowerCase()))
            .map(item -> createItemButton(player, item))
            .collect(Collectors.toList());
        
        if (filteredItems.isEmpty()) {
            filteredItems.add(new StaticGUIItem(
                ItemStack.of(Material.BARRIER)
                    .withCustomName(Component.text("No results found", NamedTextColor.RED))
            ));
        }
        
        // Border
        StaticGUIItem border = new StaticGUIItem(
            ItemStack.of(Material.BLUE_STAINED_GLASS_PANE)
                .withCustomName(Component.empty())
        );
        
        // Search info
        StaticGUIItem searchInfo = new StaticGUIItem(
            ItemStack.of(Material.COMPASS)
                .withCustomName(Component.text("Search", NamedTextColor.GOLD))
                .withLore(
                    Component.text("Current: " + (searchTerm.isEmpty() ? "All" : searchTerm),
                        NamedTextColor.YELLOW),
                    Component.empty(),
                    Component.text("Type in chat to search", NamedTextColor.GRAY),
                    Component.text("Type 'clear' to reset", NamedTextColor.GRAY)
                )
        );
        
        // Scroll buttons
        ScrollGUIItem scrollUp = new ScrollGUIItem(-7,
            ItemStack.of(Material.ARROW)
                .withCustomName(Component.text("⬆ Up", NamedTextColor.YELLOW))
        );
        
        ScrollGUIItem scrollDown = new ScrollGUIItem(7,
            ItemStack.of(Material.ARROW)
                .withCustomName(Component.text("⬇ Down", NamedTextColor.YELLOW))
        );
        
        currentGUI = ScrollGUI.builder()
            .manager(guiManager)
            .titled("<aqua>Browse Items " + 
                (searchTerm.isEmpty() ? "" : "- '" + searchTerm + "'") + "</aqua>")
            .format("""
                ###S#####
                #       #
                #       #
                #       #
                ###U#D###""")
            .item('#', border)
            .item('S', searchInfo)
            .item(' ', Indicator.CONTENT_SLOT)
            .item('U', scrollUp)
            .item('D', scrollDown)
            .scrollContent(filteredItems)
            .build();
        
        return currentGUI;
    }
    
    private DynamicGUIItem createItemButton(Player player, SearchableItem item) {
        return new DynamicGUIItem(
            ItemStack.of(item.material)
                .withCustomName(Component.text(item.name, NamedTextColor.GREEN))
                .withLore(
                    Component.text(item.description, NamedTextColor.GRAY),
                    Component.empty(),
                    Component.text("Click for info", NamedTextColor.YELLOW)
                ),
            event -> {
                event.setCancelled(true);
                player.sendMessage(Component.text("Selected: " + item.name));
            }
        );
    }
    
    private List<SearchableItem> initializeItems() {
        return Arrays.stream(Material.values())
            .filter(Material::isItem)
            .map(m -> new SearchableItem(
                formatName(m),
                "A " + formatName(m).toLowerCase(),
                m
            ))
            .collect(Collectors.toList());
    }
    
    private String formatName(Material material) {
        return Arrays.stream(material.name().split("_"))
            .map(word -> word.substring(0, 1) + word.substring(1).toLowerCase())
            .collect(Collectors.joining(" "));
    }
    
    record SearchableItem(String name, String description, Material material) {}
}
```

---

## Summary

These examples cover a wide range of use cases for MSGuis:

- **Basic menus** - Simple navigation and actions
- **Confirmation dialogs** - Safe handling of dangerous actions
- **Settings menus** - Toggleable options with state
- **Shops** - Purchase systems with economy integration
- **Player selectors** - Browsing online players
- **Item browsers** - Scrolling through large lists
- **Paginated systems** - Multi-level navigation
- **Search systems** - Dynamic content filtering

All examples follow best practices:
- Proper event cancellation
- Clear user feedback
- Appropriate use of colors and formatting
- Clean code structure
- Reusable components

For more information, see [README.md](README.md) and [API.md](API.md).
