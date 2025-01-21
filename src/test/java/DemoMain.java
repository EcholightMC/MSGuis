import com.github.echolightmc.msguis.GUIManager;
import net.minestom.server.MinecraftServer;
import net.minestom.server.command.CommandManager;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Player;
import net.minestom.server.event.GlobalEventHandler;
import net.minestom.server.event.player.AsyncPlayerConfigurationEvent;
import net.minestom.server.event.player.PlayerSpawnEvent;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.instance.InstanceManager;
import net.minestom.server.instance.block.Block;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;

public class DemoMain {

	public static GUIManager GUI_MANAGER;

	public static void main(String[] args) {
		MinecraftServer minecraftServer = MinecraftServer.init();

		GUI_MANAGER = new GUIManager(MinecraftServer.getGlobalEventHandler());

		CommandManager commandManager = MinecraftServer.getCommandManager();
		commandManager.register(new ScrollCommand());

		InstanceManager instanceManager = MinecraftServer.getInstanceManager();
		InstanceContainer instanceContainer = instanceManager.createInstanceContainer();
		instanceContainer.setGenerator(unit -> unit.modifier().fillHeight(0, 40, Block.GRASS_BLOCK));

		GlobalEventHandler globalEventHandler = MinecraftServer.getGlobalEventHandler();
		globalEventHandler.addListener(AsyncPlayerConfigurationEvent.class, event -> {
			Player player = event.getPlayer();
			event.setSpawningInstance(instanceContainer);
			player.setRespawnPoint(new Pos(0, 42, 0));
		});
		globalEventHandler.addListener(PlayerSpawnEvent.class, event -> {
			Player player = event.getPlayer();
			player.getInventory().addItemStack(ItemStack.of(Material.BEDROCK).withAmount(64));
		});

		// Start the server on port 25565
		minecraftServer.start("0.0.0.0", 25565);
	}

}
