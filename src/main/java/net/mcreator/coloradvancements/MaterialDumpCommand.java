package net.mcreator.coloradvancements;

import com.google.gson.Gson;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.EquipmentSlot;
import net.minecraft.world.item.Attributes;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.server.level.ServerLevel;

import java.util.HashMap;
import java.util.Map;

public class MaterialDumpCommand {

    public static int execute(CommandSourceStack context) {
        ServerLevel level = context.getLevel();
        RecipeManager recipeManager = level.getRecipeManager();
        Gson gson = new Gson();
        Map<Item, MaterialStats> materialStatsMap = new HashMap<>();

        // Iterate over all items in the registry
        for (Item item : Registry.ITEM) {
            if (item instanceof SwordItem || item instanceof PickaxeItem || item instanceof ArmorItem) {
                ItemStack itemStack = new ItemStack(item);
                int maxDamage = itemStack.getMaxDamage();
                int damage = itemStack.getDamageValue();
                double attackDamage = itemStack.getAttributeModifiers(EquipmentSlot.MAINHAND)
                    .get(Attributes.ATTACK_DAMAGE).stream()
                    .mapToDouble(AttributeModifier::getAmount)
                    .sum();
                String color = "#FFFFFF"; // TODO: Replace with actual method to get color
                MaterialStats stats = new MaterialStats(maxDamage, damage, attackDamage, color);
                materialStatsMap.put(item, stats);
            }
        }

        // Iterate over recipes to calculate material usage
        for (Recipe<?> recipe : recipeManager.getRecipes()) {
            if (recipe instanceof ShapedRecipe || recipe instanceof ShapelessRecipe) {
                Map<Item, Integer> itemUsage = new HashMap<>();
                recipe.getIngredients().forEach(ingredient -> {
                    for (ItemStack stack : ingredient.getItems()) {
                        Item item = stack.getItem();
                        itemUsage.put(item, itemUsage.getOrDefault(item, 0) + stack.getCount());
                    }
                });

                // Calculate stats per material based on usage in recipes
                for (Map.Entry<Item, Integer> entry : itemUsage.entrySet()) {
                    Item item = entry.getKey();
                    int count = entry.getValue();
                    MaterialStats stats = materialStatsMap.get(item);
                    if (stats != null) {
                        // Update stats based on material usage
                        stats = new MaterialStats(
                                stats.getMaxDamage(),
                                stats.getDamage(),
                                stats.getAttackDamage(),
                                stats.getColor()
                        );
                        materialStatsMap.put(item, stats);
                    }
                }
            }
        }

        // Send results to the player
        StringBuilder message = new StringBuilder("Material Dump:\n");
        for (Map.Entry<Item, MaterialStats> entry : materialStatsMap.entrySet()) {
            Item item = entry.getKey();
            MaterialStats stats = entry.getValue();
            message.append(String.format(
                    "%s: Color: %s (Durability: %d, Attack: %.2f)\n",
                    Registry.ITEM.getKey(item),
                    stats.getColor(),
                    stats.getMaxDamage(),
                    stats.getAttackDamage()
            ));
        }

        context.sendSuccess(Component.literal(message.toString()), false);
        return 1; // Indicates success
    }

    // Define your MaterialStats class
    public static class MaterialStats {
        private final int maxDamage;
        private final int damage;
        private final double attackDamage;
        private final String color;

        public MaterialStats(int maxDamage, int damage, double attackDamage, String color) {
            this.maxDamage = maxDamage;
            this.damage = damage;
            this.attackDamage = attackDamage;
            this.color = color;
        }

        public int getMaxDamage() {
            return maxDamage;
        }

        public int getDamage() {
            return damage;
        }

        public double getAttackDamage() {
            return attackDamage;
        }

        public String getColor() {
            return color;
        }
    }
}
