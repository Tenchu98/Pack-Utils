// File: ItemColorExtractor.java

package net.mcreator.coloradvancements;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraftforge.registries.ForgeRegistries;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ItemColorExtractor {

    private static final Map<String, String> colorNames = createColorNamesMap();

    public static Map<Item, String[]> getItemColors() {
        Map<Item, String[]> itemColors = new HashMap<>();

        for (Item item : BuiltInRegistries.ITEM) {
            ResourceLocation itemKey = ForgeRegistries.ITEMS.getKey(item);
            if (itemKey == null) {
                System.out.println("Item key is null for item: " + item.getDescriptionId());
                continue;
            }

            try {
                InputStream textureStream = getItemTextureStream(itemKey);
                if (textureStream != null) {
                    BufferedImage texture = ImageIO.read(textureStream);
                    if (texture != null) {
                        Color dominantColor = getDominantColor(texture);
                        String hexColor = String.format("#%02x%02x%02x", dominantColor.getRed(), dominantColor.getGreen(), dominantColor.getBlue());
                        String colorName = getColorNameFromHex(dominantColor);
                        itemColors.put(item, new String[]{hexColor, colorName});
                        System.out.println("Dominant color for item " + item.getDescriptionId() + ": " + hexColor + " (" + colorName + ")");
                    } else {
                        System.out.println("Texture is null for item: " + item.getDescriptionId());
                    }
                } else {
                    System.out.println("Texture stream is null for item: " + item.getDescriptionId());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return itemColors;
    }

    private static InputStream getItemTextureStream(ResourceLocation itemKey) {
        ResourceLocation textureLocation = new ResourceLocation(itemKey.getNamespace(), "textures/item/" + itemKey.getPath() + ".png");
        System.out.println("Trying to load texture from: " + textureLocation);
        try {
            Optional<net.minecraft.server.packs.resources.Resource> resource = Minecraft.getInstance().getResourceManager().getResource(textureLocation);
            if (resource.isPresent()) {
                System.out.println("Resource found for: " + textureLocation);
                return resource.get().open();
            } else {
                System.out.println("Resource not found for texture location: " + textureLocation);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static Color getDominantColor(BufferedImage image) {
        Map<Color, Integer> colorCount = new HashMap<>();
        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                Color color = new Color(image.getRGB(x, y), true);
                if (color.getAlpha() > 0) { // Ignore fully transparent pixels
                    colorCount.put(color, colorCount.getOrDefault(color, 0) + 1);
                }
            }
        }

        return colorCount.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(Color.WHITE);
    }

    private static String getColorNameFromHex(Color color) {
        String hex = String.format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue());
        return colorNames.getOrDefault(hex, "Unknown");
    }

    private static Map<String, String> createColorNamesMap() {
        Map<String, String> colorNames = new HashMap<>();
        colorNames.put("#000000", "Black");
        colorNames.put("#0000ff", "Blue");
        colorNames.put("#00ffff", "Cyan");
        colorNames.put("#a9a9a9", "Dark Gray");
        colorNames.put("#808080", "Gray");
        colorNames.put("#008000", "Green");
        colorNames.put("#d3d3d3", "Light Gray");
        colorNames.put("#ff00ff", "Magenta");
        colorNames.put("#ffa500", "Orange");
        colorNames.put("#ffc0cb", "Pink");
        colorNames.put("#ff0000", "Red");
        colorNames.put("#ffffff", "White");
        colorNames.put("#ffff00", "Yellow");
        return colorNames;
    }
}
