package me.semx11.mt.customitem;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import me.semx11.mt.customitem.action.BlockBreakAction;
import me.semx11.mt.customitem.action.LeftClickEntityAction;
import me.semx11.mt.customitem.action.RightClickEntityAction;
import me.semx11.mt.customitem.action.RightClickItemAction;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

/**
 * This class is a builder pattern for CustomItems.
 */
class CustomItemBuilder {

    ItemStack item;
    String[] canPlaceOn = null;
    String[] canDestroy = null;
    String displayName = null;
    String[] lore = null;
    Map<Enchantment, Integer> enchantments = new HashMap<>();
    // CustomItems hide the CanDestroy & CanPlaceOn by default so you can actually see the item.
    ItemFlag[] itemFlags = {ItemFlag.HIDE_PLACED_ON, ItemFlag.HIDE_DESTROYS};
    String mojangson = null;
    boolean unbreakable = false;
    List<Material> breakMaterials = null;
    BlockBreakAction breakAction = null;
    LeftClickEntityAction leftClickEntityAction = null;
    RightClickEntityAction rightClickEntityAction = null;
    RightClickItemAction rightClickItemAction = null;

    CustomItemBuilder itemStack(Material material) {
        this.item = new ItemStack(material);
        return this;
    }

    CustomItemBuilder itemStack(Material material, short damage) {
        this.item = new ItemStack(material, 1, damage);
        return this;
    }

    CustomItemBuilder canPlaceOn(String... canPlaceOn) {
        this.canPlaceOn = canPlaceOn;
        return this;
    }

    CustomItemBuilder canDestroy(String... canDestroy) {
        this.canDestroy = canDestroy;
        return this;
    }

    CustomItemBuilder displayName(String displayName) {
        this.displayName = displayName;
        return this;
    }

    CustomItemBuilder lore(String... lore) {
        this.lore = lore;
        return this;
    }

    CustomItemBuilder enchant(Enchantment enchantment, int level) {
        enchantments.put(enchantment, level);
        return this;
    }

    CustomItemBuilder itemFlag(ItemFlag... itemFlags) {
        this.itemFlags = itemFlags;
        return this;
    }

    CustomItemBuilder mojangson(String mojangson) {
        this.mojangson = mojangson;
        return this;
    }

    CustomItemBuilder unbreakable() {
        this.unbreakable = true;
        return this;
    }

    CustomItemBuilder blockBreakAction(BlockBreakAction breakAction, Material... breakMaterials) {
        this.breakAction = breakAction;
        this.breakMaterials = Arrays.asList(breakMaterials);
        return this;
    }

    CustomItemBuilder leftClickEntityAction(LeftClickEntityAction leftClickEntityAction) {
        this.leftClickEntityAction = leftClickEntityAction;
        return this;
    }

    CustomItemBuilder rightClickEntityAction(RightClickEntityAction rightClickEntityAction) {
        this.rightClickEntityAction = rightClickEntityAction;
        return this;
    }

    CustomItemBuilder rightClickItemAction(RightClickItemAction rightClickItemAction) {
        this.rightClickItemAction = rightClickItemAction;
        return this;
    }
}
