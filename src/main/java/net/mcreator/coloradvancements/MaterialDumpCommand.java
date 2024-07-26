package net.mcreator.coloradvancements;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component; // Correct import for 1.20.1
import net.minecraft.world.item.Item;
import net.minecraft.core.Registry;

import java.util.HashMap;
import java.util.Map;

public class MaterialDumpCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
            Commands.literal("dump_materials")
                .executes(context -> execute(context.getSource()))
        );
    }

    private static int execute(CommandSourceStack source) {
        Map<String, MaterialStatsExtractor.MaterialStats> materialStatsMap = new HashMap<>();

        // Iterate over all items
        for (Item item : Registry.ITEM) {
            String itemId = Registry.ITEM.getKey(item).toString();
            String itemName = item.getDescription().getString();
            String color = getItemColor(item);

            MaterialStatsExtractor.MaterialStats stats = MaterialStatsExtractor.getMaterialStats(item, itemId, itemName, color);

            String materialName = getMaterialName(item);
            MaterialStatsExtractor.MaterialStats existingStats = materialStatsMap.getOrDefault(materialName, 
                new MaterialStatsExtractor.MaterialStats(0, 0, 0, 0, 0, 0, itemId, materialName, color));

            existingStats = new MaterialStatsExtractor.MaterialStats(
                existingStats.durability + stats.durability,
                existingStats.attack + stats.attack,
                existingStats.defense + stats.defense,
                existingStats.harvestTier + stats.harvestTier,
                existingStats.enchantability + stats.enchantability,
                existingStats.efficiency + stats.efficiency,
                existingStats.id,
                existingStats.name,
                existingStats.color
            );
            materialStatsMap.put(materialName, existingStats);
        }

        // Normalize and print the stats
        for (Map.Entry<String, MaterialStatsExtractor.MaterialStats> entry : materialStatsMap.entrySet()) {
            String materialName = entry.getKey();
            MaterialStatsExtractor.MaterialStats stats = entry.getValue();
            
            // Assuming you have a way to get item counts; here we use a fixed number for demonstration
            int itemCount = 1;  // Update this based on how you aggregate stats
            
            MaterialStatsExtractor.MaterialStats normalizedStats = new MaterialStatsExtractor.MaterialStats(
                stats.durability / itemCount,
                stats.attack / itemCount,
                stats.defense / itemCount,
                stats.harvestTier / itemCount,
                stats.enchantability / itemCount,
                stats.efficiency / itemCount,
                stats.id,
                stats.name,
                stats.color
            );

            String message = String.format(
                "Material: %s, Durability: %d, Attack: %.2f, Defense: %d, Harvest Tier: %d, Enchantability: %d, Efficiency: %.2f, Color: %s",
                materialName,
                normalizedStats.durability,
                normalizedStats.attack,
                normalizedStats.defense,
                normalizedStats.harvestTier,
                normalizedStats.enchantability,
                normalizedStats.efficiency,
                normalizedStats.color
            );
            
            source.sendSuccess(Component.literal(message), false); // Use Component.literal()
        }

        return Command.SINGLE_SUCCESS;
    }

    private static String getItemColor(Item item) {
        // Placeholder for item color retrieval logic
        return "#FFFFFF";  // Default to white
    }

    private static String getMaterialName(Item item) {
        // Placeholder for material name retrieval logic
        if (item instanceof SwordItem) {
            return "Sword";
        } else if (item instanceof PickaxeItem) {
            return "Pickaxe";
        } else if (item instanceof ArmorItem) {
            return "Armor";
        }
        return "Unknown";
    }
}
