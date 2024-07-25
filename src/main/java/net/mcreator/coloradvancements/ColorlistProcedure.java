package net.mcreator.coloradvancements.procedures;

import net.minecraft.world.item.Item;
import java.awt.Color;
import java.util.HashMap;
import java.util.Map;
import net.mcreator.coloradvancements.ItemColorExtractor;


public class ColorlistProcedure {
    private static final Map<Color, String> colorNames = new HashMap<>();

    static {
        // Initialize color names
        colorNames.put(Color.BLACK, "Black");
        colorNames.put(Color.BLUE, "Blue");
        colorNames.put(Color.CYAN, "Cyan");
        colorNames.put(Color.DARK_GRAY, "Dark Gray");
        colorNames.put(Color.GRAY, "Gray");
        colorNames.put(Color.GREEN, "Green");
        colorNames.put(Color.LIGHT_GRAY, "Light Gray");
        colorNames.put(Color.MAGENTA, "Magenta");
        colorNames.put(Color.ORANGE, "Orange");
        colorNames.put(Color.PINK, "Pink");
        colorNames.put(Color.RED, "Red");
        colorNames.put(Color.WHITE, "White");
        colorNames.put(Color.YELLOW, "Yellow");
    }

    public static void execute() {
        executeProcedure(null);
    }

    @SuppressWarnings("unchecked")
    public static void executeProcedure(Map<String, Object> dependencies) {
        // Assuming ItemColorExtractor.getItemColors() returns Map<?, ?>
        Map<?, ?> rawMap = ItemColorExtractor.getItemColors();
        Map<Item, Color> itemColors = (Map<Item, Color>) rawMap;

        // Use StringBuilder to construct the output
        StringBuilder output = new StringBuilder();
        String lineSeparator = System.lineSeparator();
        itemColors.forEach((item, color) -> {
            String colorName = colorNames.getOrDefault(color, "Unknown");
            output.append(item.getDescriptionId()).append(": ").append(colorName).append(lineSeparator);
        });

        // Print the constructed output
        System.out.print(output.toString());
    }
}
