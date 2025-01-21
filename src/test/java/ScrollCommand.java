import com.github.echolightmc.msguis.Indicator;
import com.github.echolightmc.msguis.ScrollGUI;
import com.github.echolightmc.msguis.ScrollGUIItem;
import com.github.echolightmc.msguis.StaticGUIItem;
import net.kyori.adventure.text.Component;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.condition.Conditions;
import net.minestom.server.entity.Player;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;

import java.util.List;

public class ScrollCommand extends Command {

	private static final ItemStack BORDER = ItemStack.of(Material.GRAY_STAINED_GLASS_PANE).withCustomName(Component.empty());
	private static final ItemStack SCROLL_BACK = ItemStack.of(Material.ARROW).withCustomName(Component.text("Scroll Back"));
	private static final ItemStack SCROLL = ItemStack.of(Material.ARROW).withCustomName(Component.text("Scroll Forward"));
	private static final ScrollGUI GUI = ScrollGUI.builder()
												 .manager(DemoMain.GUI_MANAGER)
												 .titled("<red>Scroll GUI")
												 .format("""
															#########
															#       #
															#       #
															#       #
															#       #
															###<#>###""")
												 .item('#', new StaticGUIItem(BORDER))
												 .item(' ', Indicator.CONTENT_SLOT)
												 .item('<', new ScrollGUIItem(-7, SCROLL_BACK))
												 .item('>', new ScrollGUIItem(7, SCROLL))
												 .scrollContent(List.of(new StaticGUIItem(ItemStack.of(Material.BOW))))
												 /*.scrollContent(Material.values().stream().map(
														 material -> new StaticGUIItem(ItemStack.of(material))).toList())*/
												 .build();

	public ScrollCommand() {
		super("scroll");
		setCondition(Conditions::playerOnly);
		setDefaultExecutor((sender, context) -> GUI.openTo((Player) sender));
	}

}
