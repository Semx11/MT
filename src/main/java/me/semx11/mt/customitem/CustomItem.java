package me.semx11.mt.customitem;

import static java.util.Arrays.asList;
import static org.bukkit.Bukkit.getServer;
import static org.bukkit.ChatColor.AQUA;
import static org.bukkit.ChatColor.DARK_AQUA;
import static org.bukkit.ChatColor.GOLD;
import static org.bukkit.ChatColor.WHITE;
import static org.bukkit.entity.EntityType.CHICKEN;
import static org.bukkit.entity.EntityType.COW;
import static org.bukkit.entity.EntityType.PIG;
import static org.bukkit.entity.EntityType.SHEEP;

import com.google.common.collect.Ordering;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;
import me.dpohvar.powernbt.api.NBTCompound;
import me.semx11.mt.MinetopiaTools;
import me.semx11.mt.customitem.action.BlockBreakAction;
import me.semx11.mt.customitem.action.LeftClickEntityAction;
import me.semx11.mt.customitem.action.RightClickEntityAction;
import me.semx11.mt.customitem.action.RightClickItemAction;
import me.semx11.mt.util.Wrapper;
import org.bukkit.CropState;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Skull;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.Crops;

/**
 * The enum CustomItem contains all CustomItems available in the game.
 *
 * CustomItems that can be placed use the CanDestroy and CanPlaceOn NBT tags for it to work in
 * adventure mode. The server that uses this plugin is on Adventure mode to prevent players from
 * stealing items from chests by breaking glass panes (or any block) and this way being able to
 * access that chest in a split second.
 */
public enum CustomItem {

    CAKE(new CustomItemBuilder()
            .itemStack(Material.CAKE)
            .canPlaceOn(MinetopiaTools.minecraftBlocks)
    ),
    WHEAT_SEEDS(new CustomItemBuilder()
            .itemStack(Material.SEEDS)
            .canPlaceOn("minecraft:farmland")
            .canDestroy(Constants.farmBlocks)
            .blockBreakAction((e, b) -> {
                Crops wheatCrop = (Crops) b.getState().getData();
                if (wheatCrop.getState().equals(CropState.RIPE)) {
                    dropItem(b.getLocation(), null, Material.SEEDS);
                } else {
                    dropItem(b.getLocation(), CustomItem.valueOf("WHEAT_SEEDS").getItemStack(),
                            Material.SEEDS);
                }
            }, Material.CROPS)
    ),
    MELON_SEEDS(new CustomItemBuilder()
            .itemStack(Material.MELON_SEEDS)
            .canPlaceOn("minecraft:farmland")
            .canDestroy(Constants.farmBlocks)
            .blockBreakAction(
                    (e, b) -> dropItem(b.getLocation(), null, Material.MELON_SEEDS),
                    Material.MELON_STEM)
    ),
    PUMPKIN_SEEDS(new CustomItemBuilder()
            .itemStack(Material.PUMPKIN_SEEDS)
            .canPlaceOn("minecraft:farmland")
            .canDestroy(Constants.farmBlocks)
            .blockBreakAction(
                    (e, b) -> dropItem(b.getLocation(), null, Material.PUMPKIN_SEEDS),
                    Material.PUMPKIN_STEM)
    ),
    SUGAR_CANES(new CustomItemBuilder()
            .itemStack(Material.SUGAR_CANE)
            .canPlaceOn("minecraft:sand", "minecraft:grass", "minecraft:reeds")
            .canDestroy(Constants.farmBlocks)
            .blockBreakAction((e, b) -> {
                ItemStack customCanes = CustomItem.valueOf("SUGAR_CANES").getItemStack();
                e.setCancelled(true);
                Location caneLoc = e.getBlock().getLocation();
                while (caneLoc.getBlock().getType().equals(Material.SUGAR_CANE_BLOCK)) {
                    caneLoc.getBlock()
                            .getWorld()
                            .dropItemNaturally(caneLoc.getBlock().getLocation().add(0.5, 0.5, 0.5),
                                    customCanes);
                    caneLoc.add(0, 1, 0);
                }
                caneLoc.subtract(0, 1, 0);
                do {
                    caneLoc.getBlock().setType(Material.AIR);
                    caneLoc.subtract(0, 1, 0);
                }
                while ((caneLoc.getBlock()
                        .getType()
                        .equals(Material.SUGAR_CANE_BLOCK)) && (caneLoc.getBlockY() >= e.getBlock()
                        .getLocation()
                        .getBlockY()));
            }, Material.SUGAR_CANE_BLOCK)
    ),
    STONE_HOE(new CustomItemBuilder()
            .itemStack(Material.STONE_HOE)
            .canPlaceOn("minecraft:grass", "minecraft:dirt")
            .canDestroy(Constants.farmBlocks)
    ),
    IRON_HOE(new CustomItemBuilder()
            .itemStack(Material.IRON_HOE)
            .canPlaceOn("minecraft:grass", "minecraft:dirt")
            .canDestroy(Constants.farmBlocks)
    ),
    DIAMOND_HOE(new CustomItemBuilder()
            .itemStack(Material.DIAMOND_HOE)
            .canPlaceOn("minecraft:grass", "minecraft:dirt")
            .canDestroy(Constants.farmBlocks)
    ),
    SHEPHERDS_CROOK(new CustomItemBuilder()
            .itemStack(Material.IRON_SWORD)
            .displayName(GOLD + "Shepherd's Crook")
            .leftClickEntityAction(e -> {
                List<EntityType> animals = Arrays.asList(COW, SHEEP, PIG, CHICKEN);
                if (!animals.contains(e.getEntityType())) {
                    e.setCancelled(true);
                }
            })
    ),
    SIGN(new CustomItemBuilder()
            .itemStack(Material.SIGN)
            .canPlaceOn(MinetopiaTools.minecraftBlocks)
            .canDestroy("minecraft:standing_sign", "minecraft:wall_sign")
            .blockBreakAction(
                    (e, b) -> dropItem(b.getLocation(), CustomItem.valueOf("SIGN").getItemStack(),
                            Material.SIGN), Material.WALL_SIGN, Material.SIGN_POST)
    ),
    SECURITY_CAMERA(new CustomItemBuilder()
            .itemStack(Material.SKULL_ITEM, (short) 3)
            .canPlaceOn(MinetopiaTools.minecraftBlocks)
            .canDestroy("minecraft:skull")
            .displayName(WHITE + "Security Camera")
            .mojangson("{SkullOwner:{Id:d32b720b-1914-4209-bf9a-8662f1bb7b32,Name:MHF_Cam}}")
            .blockBreakAction((e, b) -> {
                Skull skull = (Skull) b.getState();
                if (skull.getOwningPlayer() != null) {
                    if (skull.getOwningPlayer().getUniqueId()
                            .equals(UUID.fromString("d32b720b-1914-4209-bf9a-8662f1bb7b32"))) {
                        dropItem(b.getLocation(),
                                CustomItem.valueOf("SECURITY_CAMERA").getItemStack(),
                                Material.SKULL_ITEM);
                        return;
                    }
                }
                e.setCancelled(true);
            }, Material.SKULL)
    ),
    ELYTRA(new CustomItemBuilder()
            .itemStack(Material.ELYTRA)
            .enchant(Enchantment.DURABILITY, 10)
    ),
    ELYTRA_UNBREAKABLE(new CustomItemBuilder()
            .itemStack(Material.ELYTRA)
            .unbreakable()
    ),
    BATON(new CustomItemBuilder()
            .itemStack(Material.STICK)
            .displayName(GOLD + "Baton")
            .enchant(Enchantment.KNOCKBACK, 1)
            .leftClickEntityAction(e -> e.setDamage(0))
    ),
    HANDCUFFS(new CustomItemBuilder()
            .itemStack(Material.LEASH)
            .displayName(GOLD + "Handcuffs")
            .rightClickEntityAction(e -> {
                if (e.getRightClicked() instanceof Player) {
                    Handcuffs.toggleHandcuffs(e.getPlayer(), (Player) e.getRightClicked());
                }
            })
    ),
    PETTRACKER(new CustomItemBuilder()
            .itemStack(Material.COMPASS)
            .displayName(DARK_AQUA + "[" + AQUA + "PetTracker" + DARK_AQUA + "]")
            .lore(AQUA + "Find your pets with a click!")
            .rightClickItemAction(e -> {
                e.setCancelled(true);
                Player p = e.getPlayer();
                PetTracker.trackPets(
                        MinetopiaTools.getInstance().getServer().getOfflinePlayer(p.getUniqueId()),
                        p);
            })
            .leftClickEntityAction(e -> {
                e.setCancelled(true);
                PetTracker.printOwner(e.getEntity(), e.getDamager());
            })
    );

    // Cached list of all CustomItems, sorted alphabetically by name.
    private static List<CustomItem> sortedValues;

    private final ItemStack item;
    private String itemTag;
    private BlockBreakAction breakAction;
    private List<Material> breakMaterials;
    private LeftClickEntityAction leftClickEntityAction;
    private RightClickEntityAction rightClickEntityAction;
    private RightClickItemAction rightClickItemAction;

    /**
     * You create a CustomItem by giving it a CustomItemBuilder. It will then read the Builder and
     * compose the ItemStack from it.
     *
     * @param b A CustomItemBuilder.
     */
    CustomItem(CustomItemBuilder b) {

        ItemStack cItemStack = b.item;
        ItemMeta cItemMeta = cItemStack.getItemMeta();

        if (b.displayName != null) {
            cItemMeta.setDisplayName(b.displayName);
        }
        if (b.lore != null) {
            cItemMeta.setLore(asList(b.lore));
        }
        if (b.unbreakable) {
            cItemMeta.spigot().setUnbreakable(true);
        }

        cItemStack.setItemMeta(cItemMeta);

        if (b.enchantments.size() > 0) {
            cItemStack.addUnsafeEnchantments(b.enchantments);
        }

        NBTCompound comp = MinetopiaTools.nbtManager.read(cItemStack) != null
                ? MinetopiaTools.nbtManager.read(cItemStack)
                : new NBTCompound();

        if (b.mojangson != null) {
            NBTCompound compound = (NBTCompound) MinetopiaTools.nbtManager
                    .parseMojangson(b.mojangson);
            comp.merge(compound);
        }

        if (b.canPlaceOn != null) {
            comp.put("CanPlaceOn", b.canPlaceOn);
        }
        if (b.canDestroy != null) {
            comp.put("CanDestroy", b.canDestroy);
        }

        comp.put("CustomItem", this.name());
        MinetopiaTools.nbtManager.write(cItemStack, comp);

        if (b.itemFlags != null) {
            cItemMeta = cItemStack.getItemMeta();
            cItemMeta.addItemFlags(b.itemFlags);
            cItemStack.setItemMeta(cItemMeta);
        }

        if (b.breakMaterials != null && b.breakAction != null) {
            this.breakMaterials = b.breakMaterials;
            this.breakAction = b.breakAction;
        }

        if (b.leftClickEntityAction != null) {
            this.leftClickEntityAction = b.leftClickEntityAction;
        }
        if (b.rightClickEntityAction != null) {
            this.rightClickEntityAction = b.rightClickEntityAction;
        }
        if (b.rightClickItemAction != null) {
            this.rightClickItemAction = b.rightClickItemAction;
        }

        this.item = cItemStack; // This item contains all the NBT modifications.

        // The itemTag is used to display a preview of the item when hovering over it in chat.
        // This is basic usage of NMS and Reflection.
        try {
            Class<?> compoundClazz = Wrapper.getNMSClass("NBTTagCompound");
            Object compound = compoundClazz.newInstance();

            Class<?> craftClazz = Wrapper.getCraftClass("inventory.CraftItemStack");
            Object nmsStack = craftClazz.getMethod("asNMSCopy", ItemStack.class)
                    .invoke(null, this.getItemStack());

            this.itemTag = nmsStack.getClass().getMethod("save", compoundClazz)
                    .invoke(nmsStack, compound)
                    .toString();
        } catch (InstantiationException | ClassNotFoundException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            itemTag = "";
            e.printStackTrace();
        }
    }

    public static CustomItem valueOfIgnoreCase(String name) throws IllegalArgumentException {
        for (CustomItem cItem : CustomItem.sortedValues()) {
            if (cItem.name().equalsIgnoreCase(name)) {
                return cItem;
            }
        }
        throw new IllegalArgumentException(String.format(
                "There is no CustomItem with name '%s'", name
        ));
    }

    /**
     * @return List with all CustomItems sorted alphabetically.
     */
    public static List<CustomItem> sortedValues() {
        if (sortedValues == null) {
            (sortedValues = Arrays.asList(CustomItem.values())).sort(Ordering.usingToString());
        }
        return sortedValues;
    }

    /**
     * Checks if the given ItemStack is a CustomItem by checking the NBT data, and returns the
     * CustomItem if it exists.
     *
     * @param item ItemStack that should be checked.
     * @return Optional with a CustomItem if it exists, otherwise an empty Optional.
     */
    public static Optional<CustomItem> getCustomItem(ItemStack item) {
        if (item.getType().equals(Material.AIR)) {
            return Optional.empty();
        }
        NBTCompound compound = MinetopiaTools.nbtManager.read(item);
        if (compound == null) {
            return Optional.empty();
        }
        if (compound.containsKey("CustomItem")) {
            try {
                CustomItem cItem = CustomItem.valueOfIgnoreCase(compound.getString("CustomItem"));
                if (isSimilar(item, cItem.getItemStack())) {
                    return Optional.of(cItem);
                }
            } catch (IllegalArgumentException e) {
                return Optional.empty();
            }
        }
        return Optional.empty();
    }

    /**
     * Compares if two ItemStacks are similar, not accounting for damage values. Used to detect
     * if an ItemStack is a CustomItem.
     *
     * @param thisStack ItemStack to compare with.
     * @param thatStack The other ItemStack to compare with.
     * @return true or false depending if the given ItemStacks are similar.
     */
    private static boolean isSimilar(ItemStack thisStack, ItemStack thatStack) {
        if (!thisStack.getType().equals(thatStack.getType())) {
            return false;
        }
        if (!thisStack.hasItemMeta() && !thatStack.hasItemMeta()) {
            return true;
        }
        if (!thisStack.hasItemMeta() || !thatStack.hasItemMeta()) {
            return false;
        }
        ItemMeta thisMeta = thisStack.getItemMeta();
        ItemMeta thatMeta = thatStack.getItemMeta();
        return Objects.equals(thisMeta.getDisplayName(), thatMeta.getDisplayName())
                && Objects.equals(thisMeta.getLore(), thatMeta.getLore())
                && Objects.equals(thisMeta.getEnchants(), thatMeta.getEnchants())
                && Objects.equals(thisMeta.getItemFlags(), thatMeta.getItemFlags())
                && Objects
                .equals(thisMeta.spigot().isUnbreakable(), thatMeta.spigot().isUnbreakable());
    }

    /**
     * Replace or remove the drops from a block WITHOUT cancelling the BlockBreakEvent. It schedules
     * the task 0 ticks later to allow the server to spawn the drop. It will then check for any
     * items that match the given Materials in a 0.5 radius from the middle of the block, and not be
     * older than 1 game-tick.
     *
     * @param loc The location of the block that was broken.
     * @param is The ItemStack that should be dropped instead of the regular drop. Null if no items
     * should be dropped.
     * @param materials The Materials it should check for.
     */
    private static void dropItem(Location loc, ItemStack is, Material... materials) {
        getServer().getScheduler().runTaskLater(
                MinetopiaTools.getInstance(),
                () -> getNearbyEntities(loc.add(0.5, 0.5, 0.5), 0.5)
                        .filter(entity -> entity instanceof Item)
                        .forEach(entity -> {
                            Item itemEntity = (Item) entity;
                            if (itemEntity.getTicksLived() <= 1) {
                                if (Arrays.asList(materials)
                                        .contains(itemEntity.getItemStack().getType())) {
                                    if (is != null) {
                                        itemEntity.setItemStack(is);
                                    } else {
                                        entity.remove();
                                    }
                                }
                            }
                        }), 0L);
    }

    /**
     * This method was written before World#getNearbyEntities existed.
     *
     * @param l The center of the location.
     * @param radius The search radius.
     * @return Stream of entities in the specified area.
     */
    private static Stream<Entity> getNearbyEntities(Location l, double radius) {
        if (radius > 0.5) {
            // Currently not supported because it can go outside of a chunk, and it uses
            // l.getChunk() to improve performance.
            throw new UnsupportedOperationException("Radius cannot be bigger than 0.5!");
        }
        return Arrays.stream(l.getChunk().getEntities())
                .filter(e -> l.distance(e.getLocation()) <= radius);
    }

    public String toString() {
        return this.name();
    }

    public ItemStack getItemStack() {
        return item.clone();
    }

    public String getItemTag() {
        return itemTag;
    }

    public boolean hasBlockBreakAction() {
        return breakAction != null && breakMaterials != null;
    }

    public BlockBreakAction getBlockBreakAction() {
        return breakAction;
    }

    public List<Material> getBreakMaterials() {
        return breakMaterials;
    }

    public boolean hasLeftClickEntityAction() {
        return leftClickEntityAction != null;
    }

    public LeftClickEntityAction getLeftClickEntityAction() {
        return leftClickEntityAction;
    }

    public boolean hasRightClickItemAction() {
        return rightClickItemAction != null;
    }

    public RightClickItemAction getRightClickItemAction() {
        return rightClickItemAction;
    }

    public boolean hasRightClickEntityAction() {
        return rightClickEntityAction != null;
    }

    public RightClickEntityAction getRightClickEntityAction() {
        return rightClickEntityAction;
    }

    /**
     * Static nested class that can contain constants that are used frequently in the initialization
     * of CustomItems.
     * Only contains one variable right now.
     */
    private static final class Constants {

        private static final String[] farmBlocks = {"minecraft:wheat", "minecraft:melon_block",
                "minecraft:pumpkin", "minecraft:melon_stem", "minecraft:pumpkin_stem",
                "minecraft:reeds"};

    }

}
