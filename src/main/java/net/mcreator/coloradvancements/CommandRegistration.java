package net.mcreator.coloradvancements;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.item.crafting.SmithingRecipe;
import net.minecraft.core.RegistryAccess;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.mcreator.coloradvancements.ItemColorExtractor;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.HashMap;
import java.util.Map;

@Mod.EventBusSubscriber(modid = "color_advancements")
public class CommandRegistration {

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
        dispatcher.register(
            Commands.literal("dumpitemcolors")
                .executes(CommandRegistration::executeDumpItemColorsCommand)
        );
        dispatcher.register(
            Commands.literal("material_dump")
                .executes(CommandRegistration::executeMaterialDumpCommand)
        );
    }

    private static int executeDumpItemColorsCommand(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        StringBuilder message = new StringBuilder("Item Colors:\n");

        try {
            Map<Item, String[]> itemColors = ItemColorExtractor.getItemColors();
            for (Map.Entry<Item, String[]> entry : itemColors.entrySet()) {
                Item item = entry.getKey();
                String[] colorInfo = entry.getValue();
                String hexColor = colorInfo[0];
                String colorName = colorInfo[1];

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

    private static int executeMaterialDumpCommand(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        StringBuilder message = new StringBuilder("Material Dump:\n");

        try {
            RecipeManager recipeManager = source.getServer().getRecipeManager();
            RegistryAccess registryAccess = source.getServer().registryAccess();
            Map<Item, String[]> itemColors = ItemColorExtractor.getItemColors();
            Map<Item, MaterialStats> materialStatsMap = new HashMap<>();

            for (Item item : ForgeRegistries.ITEMS) {
                if (isTargetItem(item)) {
                    MaterialStats stats = getMaterialStats(item, recipeManager, registryAccess);
                    if (stats != null) {
                        materialStatsMap.put(item, stats);
                    }
                }
            }

            for (Map.Entry<Item, MaterialStats> entry : materialStatsMap.entrySet()) {
                Item item = entry.getKey();
                MaterialStats stats = entry.getValue();
                String[] colors = itemColors.get(item);
                String hexColor = colors != null ? colors[0] : "unknown";

                // Build the message
                message.append(item.getDescriptionId()).append(": ").append(hexColor)
                        .append(" (Durability: ").append(stats.durability)
                        .append(", Harvest Tier: ").append(stats.harvestTier)
                        .append(", Attack: ").append(stats.attack)
                        .append(", Enchantability: ").append(stats.enchantability)
                        .append(")\n");
            }

            source.sendSuccess(() -> Component.literal(message.toString()), false);
        } catch (Exception e) {
            source.sendFailure(Component.literal("An error occurred: " + e.getMessage()));
            return Command.SINGLE_SUCCESS;
        }

        return Command.SINGLE_SUCCESS;
    }

    private static boolean isTargetItem(Item item) {
        String itemName = item.getDescriptionId();
        return itemName.contains("sword") || itemName.contains("pickaxe") ||
               itemName.contains("chestplate") || itemName.contains("boots");
    }

    private static MaterialStats getMaterialStats(Item item, RecipeManager recipeManager, RegistryAccess registryAccess) {
        for (Recipe<?> recipe : recipeManager.getRecipes()) {
            if (recipe.getResultItem(registryAccess).getItem() == item) {
                if (recipe instanceof ShapedRecipe) {
                    return calculateStatsFromShapedRecipe((ShapedRecipe) recipe);
                } else if (recipe instanceof SmithingRecipe) {
                    return calculateStatsFromSmithingRecipe((SmithingRecipe) recipe);
                }
            }
        }
        return null;
    }

    private static MaterialStats calculateStatsFromShapedRecipe(ShapedRecipe recipe) {
        // Implement logic to calculate stats from shaped recipe
        return new MaterialStats(0, 0, 0, 0); // Placeholder
    }

    private static MaterialStats calculateStatsFromSmithingRecipe(SmithingRecipe recipe) {
        // Implement logic to calculate stats from smithing recipe
        return new MaterialStats(0, 0, 0, 0); // Placeholder
    }

    private static class MaterialStats {
        int durability;
        int harvestTier;
        int attack;
        int enchantability;

        MaterialStats(int durability, int harvestTier, int attack, int enchantability) {
            this.durability = durability;
            this.harvestTier = harvestTier;
            this.attack = attack;
            this.enchantability = enchantability;
        }
    }
}
