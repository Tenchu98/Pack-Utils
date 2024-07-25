// File: ItemColorCommand.java

package net.mcreator.coloradvancements;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import java.util.Map;

@Mod("color_advancements")
public class ItemColorCommand {

    public ItemColorCommand() {
        // Register the server starting event listener
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(this::onServerStarting);
    }

    private void onServerStarting(ServerStartingEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getServer().getCommands().getDispatcher();
        dispatcher.register(
            Commands.literal("dumpitemcolors")
                .executes(this::executeCommand)
        );
    }

    private int executeCommand(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        StringBuilder message = new StringBuilder("Item Colors:\n");

        try {
            Map<Item, String[]> itemColors = ItemColorExtractor.getItemColors();
            for (Map.Entry<Item, String[]> entry : itemColors.entrySet()) {
                Item item = entry.getKey();
                String[] colors = entry.getValue();
                String hexColor = colors[0];
                String colorName = colors[1];

                // Build the message
                message.append(item.getDescriptionId()).append(": ").append(hexColor).append(" (").append(colorName).append(")\n");
            }

            source.sendSuccess(() -> Component.literal(message.toString()), false);
        } catch (Exception e) {
            source.sendFailure(Component.literal("An error occurred: " + e.getMessage()));
            return Command.SINGLE_SUCCESS;
        }

        return Command.SINGLE_SUCCESS;
    }
}
