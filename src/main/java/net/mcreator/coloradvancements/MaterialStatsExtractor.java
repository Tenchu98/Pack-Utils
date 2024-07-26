package net.mcreator.coloradvancements;

import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TieredItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import com.google.common.collect.Multimap;
import com.google.common.collect.HashMultimap;

public class MaterialStatsExtractor {

    public static class MaterialStats {
        public int durability;
        public float attack;
        public int defense;
        public int harvestTier;
        public int enchantability;
        public float efficiency;
        public final String id;
        public final String name;
        public final String color;
        public int itemCount;

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
            this.itemCount = 1; // Initial count of items contributing to this material
        }

        public void addStats(MaterialStats other) {
            this.durability += other.durability;
            this.attack += other.attack;
            this.defense += other.defense;
            this.harvestTier += other.harvestTier;
            this.enchantability += other.enchantability;
            this.efficiency += other.efficiency;
            this.itemCount += 1;
        }

        public void averageStats() {
            if (this.itemCount > 0) {
                this.durability /= this.itemCount;
                this.attack /= this.itemCount;
                this.defense /= this.itemCount;
                this.harvestTier /= this.itemCount;
                this.enchantability /= this.itemCount;
                this.efficiency /= this.itemCount;
            }
        }
    }

    public static MaterialStats getMaterialStats(Item item, String itemId, String itemName, String color) {
        int durability = 0;
        float attack = 0.0f;
        int defense = 0;
        int harvestTier = 0;
        int enchantability = 0;
        float efficiency = 0.0f;

        if (item instanceof SwordItem swordItem) {
            Tier tier = swordItem.getTier();
            if (tier != null) {
                durability = tier.getUses();
                harvestTier = tier.getLevel();
                enchantability = tier.getEnchantmentValue();
                efficiency = tier.getSpeed();
                attack = (float) getAttributeModifierValue(new ItemStack(swordItem), Attributes.ATTACK_DAMAGE, EquipmentSlot.MAINHAND);
            }
        } else if (item instanceof ArmorItem armorItem) {
            ArmorMaterial armorMaterial = armorItem.getMaterial();
            if (armorMaterial != null) {
                durability = armorMaterial.getDurabilityForType(armorItem.getType());
                defense = armorMaterial.getDefenseForType(armorItem.getType());
                enchantability = armorMaterial.getEnchantmentValue();
            }
        } else if (item instanceof TieredItem tieredItem) {
            Tier tier = tieredItem.getTier();
            if (tier != null) {
                durability = tier.getUses();
                harvestTier = tier.getLevel();
                enchantability = tier.getEnchantmentValue();
                efficiency = tier.getSpeed();
                attack = (float) getAttributeModifierValue(new ItemStack(tieredItem), Attributes.ATTACK_DAMAGE, EquipmentSlot.MAINHAND);
            }
        }

        return new MaterialStats(durability, attack, defense, harvestTier, enchantability, efficiency, itemId, itemName, color);
    }

    private static double getAttributeModifierValue(ItemStack itemStack, Attribute attribute, EquipmentSlot slot) {
        double value = 0.0;
        if (itemStack != null && attribute != null) {
            Multimap<Attribute, AttributeModifier> modifiers = HashMultimap.create();
            modifiers.putAll(itemStack.getAttributeModifiers(slot));
            if (modifiers != null) {
                for (AttributeModifier modifier : modifiers.get(attribute)) {
                    value += modifier.getAmount();
                }
            }
        }
        return value;
    }
}
