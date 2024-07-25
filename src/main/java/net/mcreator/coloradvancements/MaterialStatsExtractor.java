package net.mcreator.coloradvancements;

import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TieredItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import com.google.common.collect.Multimap;
import net.minecraft.world.entity.ai.attributes.Attribute;

public class MaterialStatsExtractor {

    public static class MaterialStats {
        public final int durability;
        public final float attack;
        public final int defense;
        public final int harvestTier;
        public final int enchantability;
        public final float efficiency;
        public final String id;
        public final String name;
        public final String color;

        public MaterialStats(int durability, float attack, int defense, int harvestTier, int enchantability, float efficiency, String id, String name, String color) {
            this.durability = durability;
            this.attack = attack;
            this.defense = defense;
            this.harvestTier = harvestTier;
            this.enchantability = enchantability;
            this.efficiency = efficiency;
            this.id = id;
            this.name = name;
            this.color = color;
        }
    }

    public static MaterialStats getMaterialStats(Item item, String itemId, String itemName, String color) {
        int durability = 0;
        float attack = 0.0f;
        int defense = 0;
        int harvestTier = 0;
        int enchantability = 0;
        float efficiency = 0.0f;

        System.out.println("Processing Item: " + item);

        if (item instanceof SwordItem swordItem) {
            Tier tier = swordItem.getTier();
            if (tier != null) {
                durability = tier.getUses();
                harvestTier = tier.getLevel();
                enchantability = tier.getEnchantmentValue();
                efficiency = tier.getSpeed();
                attack = (float) getAttributeModifierValue(new ItemStack(swordItem), Attributes.ATTACK_DAMAGE, EquipmentSlot.MAINHAND);
                System.out.println("SwordItem - Attack: " + attack);
            }
        } else if (item instanceof ArmorItem armorItem) {
            ArmorMaterial armorMaterial = armorItem.getMaterial();
            if (armorMaterial != null) {
                durability = armorMaterial.getDurabilityForType(armorItem.getType());
                defense = armorMaterial.getDefenseForType(armorItem.getType());
                enchantability = armorMaterial.getEnchantmentValue();
                System.out.println("ArmorItem - Defense: " + defense);
            }
        } else if (item instanceof TieredItem tieredItem) {
            Tier tier = tieredItem.getTier();
            if (tier != null) {
                durability = tier.getUses();
                harvestTier = tier.getLevel();
                enchantability = tier.getEnchantmentValue();
                efficiency = tier.getSpeed();
                attack = (float) getAttributeModifierValue(new ItemStack(tieredItem), Attributes.ATTACK_DAMAGE, EquipmentSlot.MAINHAND);
                System.out.println("TieredItem - Attack: " + attack);
            }
        }

        return new MaterialStats(durability, attack, defense, harvestTier, enchantability, efficiency, itemId, itemName, color);
    }

    private static double getAttributeModifierValue(ItemStack itemStack, Attribute attribute, EquipmentSlot slot) {
        double value = 0.0;
        if (itemStack != null && attribute != null) {
            Multimap<Attribute, AttributeModifier> modifiers = itemStack.getAttributeModifiers(slot);
            if (modifiers != null) {
                for (AttributeModifier modifier : modifiers.get(attribute)) {
                    value += modifier.getAmount();
                }
            }
        }
        return value;
    }
}
