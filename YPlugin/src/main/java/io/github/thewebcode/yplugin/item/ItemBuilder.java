package io.github.thewebcode.yplugin.item;

import com.google.common.collect.Lists;
import com.mysql.cj.util.StringUtils;
import io.github.thewebcode.yplugin.exceptions.ItemCreationException;
import io.github.thewebcode.yplugin.plugin.Plugins;
import io.github.thewebcode.yplugin.utilities.SneakyThrow;
import io.github.thewebcode.yplugin.utilities.StringUtil;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.MaterialData;

import java.util.*;

public class ItemBuilder {
    private Material material;
    private MaterialData materialData;

    private String name;

    private int amount = 1;
    private short durability = -101;

    private List<String> lore = new ArrayList<>();
    private List<ItemEnchantmentWrapper> enchantments = new ArrayList<>();

    private boolean forceEnchantments = false;

    private List<ItemFlag> flags = new ArrayList<>();

    private Attributes attributes;
    private List<Attributes.Attribute> attributeList = new ArrayList<>();

    private boolean unbreakable = false;

    public static ItemBuilder of(Material material) {
        return new ItemBuilder(material);
    }

    public static ItemBuilder of(ItemStack item) {
        return new ItemBuilder(item);
    }

    public static ItemBuilder of(MaterialData data) {
        return of(data.toItemStack(1));
    }

    public ItemBuilder(Material material) {
        this.material = material;
    }

    public ItemBuilder(Material material, int amount) {
        this.material = material;
        this.amount = amount;
    }

    public ItemBuilder(ItemStack base) {
        this.material = base.getType();
        this.materialData = base.getData();
        this.durability = (short) Items.getDataValue(base);

        if (Items.hasLore(base)) {
            this.lore = Items.getLore(base);
        }

        this.enchantments = Lists.newArrayList(Items.getEnchantments(base));
        this.amount = base.getAmount();
        this.name = Items.getName(base);
    }

    public ItemBuilder() {
    }

    public ItemBuilder addAttribute(String name, Attributes.AttributeType attributeType, Attributes.Operation operation, int amount) {
        attributeList.add(Attributes.Attribute.newBuilder().name(name).type(attributeType).operation(operation).amount(amount).build());
        return this;
    }

    public ItemBuilder addAttribute(Attributes.Attribute attr) {
        attributeList.add(attr);
        return this;
    }

    public ItemBuilder addAttribute(Optional<Attributes.Attribute> attr) {
        if (attr.isPresent()) {
            return addAttribute(attr.get());
        }
        return this;
    }

    public ItemBuilder addFlags(ItemFlag... flags) {
        for (ItemFlag flag : flags) {
            this.flags.add(flag);
        }

        return this;
    }

    public ItemBuilder amount(int amount) {
        this.amount = amount;
        return this;
    }

    public ItemBuilder name(String name) {
        this.name = StringUtil.formatColorCodes(name);
        return this;
    }

    public ItemBuilder lore(String... lore) {
        Collections.addAll(this.lore, lore);
        return this;
    }

    public ItemBuilder lore(List<String> lore) {
        this.lore.addAll(lore);
        return this;
    }

    public ItemBuilder durability(short durability) {
        this.durability = durability;
        return this;
    }

    public ItemBuilder enchantment(Enchantment enchantment, int level) {
        enchantments.add(new ItemEnchantmentWrapper(enchantment, level, false,enchantment.isTreasure()));
        return this;
    }

    public ItemBuilder forceEnchantments(boolean val) {
        this.forceEnchantments = val;
        return this;
    }

    public ItemBuilder enchantments(Map<Enchantment, Integer> enchantments) {
        for (Map.Entry<Enchantment, Integer> enchant : enchantments.entrySet()) {
            enchantment(enchant.getKey(),enchant.getValue());
        }

        return this;
    }

    public ItemBuilder materialData(MaterialData materialData) {
        this.materialData = materialData;
        return this;
    }

    public ItemBuilder unbreakable() {
        unbreakable = true;
        return this;
    }

    public ItemBuilder breakable() {
        unbreakable = false;
        return this;
    }

    public ItemStack item() {
        if (material == null || material == Material.AIR) {
            SneakyThrow.sneaky(new ItemCreationException("Unable to create an item with air (or null) materials"));
        }

        ItemStack itemStack = new ItemStack(material, amount);

        ItemMeta itemMeta = itemStack.getItemMeta();
        if (!StringUtils.isNullOrEmpty(name)) {
            itemMeta.setDisplayName(name);
        }
        if (lore != null && !lore.isEmpty()) {
            itemMeta.setLore(StringUtil.formatColorCodes(lore));
        }
        if (!enchantments.isEmpty()) {
            for (ItemEnchantmentWrapper enchantWrapper : enchantments) {
                itemMeta.addEnchant(enchantWrapper.getEnchantment(), enchantWrapper.getLevel(), enchantWrapper.isItemGlow());
            }
        }
        itemStack.setItemMeta(itemMeta);
        if (durability != -101) {
            itemStack.setDurability(durability);
        }
        if (materialData != null) {
            itemStack.setData(materialData);
        }
        itemStack.getItemMeta().setUnbreakable(unbreakable);

        if (flags.size() > 0) {
            Items.addFlags(itemStack, flags);
        }
        if (attributeList.size() > 0) {
            if (!Plugins.hasProtocolLib()) {
                SneakyThrow.sneaky(new ItemCreationException("Unable to apply attributes to the items as it required ProtocolLib as a dependancy!"));
            }

            attributes = new Attributes(itemStack);

            for (Attributes.Attribute itemAttribute : attributeList) {
                attributes.add(itemAttribute);
            }

            itemStack = attributes.getStack();
        }
        return itemStack;
    }
}
