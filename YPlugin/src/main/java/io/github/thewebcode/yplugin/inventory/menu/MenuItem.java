package io.github.thewebcode.yplugin.inventory.menu;

import io.github.thewebcode.yplugin.item.Items;
import io.github.thewebcode.yplugin.utilities.StringUtil;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.MaterialData;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class MenuItem {
    private static final MaterialData DEFAULT_ICON = new MaterialData(Material.PAPER);
    private ItemMenu menu;
    private int number;
    private MaterialData icon;
    private String text;
    private List<String> descriptions = new ArrayList<>();

    private ItemStack item = null;

    public MenuItem() {
        this("", DEFAULT_ICON);
    }

    public MenuItem(String text) {
        this(text, DEFAULT_ICON);
    }

    public MenuItem(String text, MaterialData icon) {
        this(text, icon, 1);
    }

    public MenuItem(String text, ItemStack item) {
        this.text = text;
        this.item = item.clone();

        if (this.text != null) {
			Items.setName(this.item, StringUtil.formatColorCodes(text));
		}
    }

    public MenuItem(ItemStack item) {
        this.item = item.clone();
    }

    public MenuItem(String text, MaterialData icon, int number) {
        this.text = StringUtil.formatColorCodes(text);
        this.icon = icon;
        this.number = number;
    }

    protected void addToMenu(ItemMenu menu) {
        this.menu = menu;
    }

    protected void removeFromMenu(ItemMenu menu) {
        if (this.menu == menu) {
            this.menu = null;
        }
    }

    public ItemMenu getMenu() {
        return menu;
    }

    public int getNumber() {
        return number;
    }

    public MaterialData getIcon() {
        return icon;
    }

    public void setIcon(MaterialData data) {
        icon = data;
    }

    public void setIcon(ItemStack item) {
        this.item = item;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = StringUtil.formatColorCodes(text);
    }

    public void setDescriptions(List<String> lines) {
        descriptions = StringUtil.formatColorCodes(lines);
    }

    public void setDescriptions(String... lines) {
        descriptions = StringUtil.formatColorCodes(Arrays.asList(lines));
    }

    public void addDescriptions(String... line) {
        for (String s : line) {
            descriptions.add(StringUtil.formatColorCodes(s));
        }
    }

    public void setIconNumber(int num) {
        this.number = num;
    }

    public int getIconNumber() {
        return number;
    }

    protected ItemStack getItemStack() {
        if (item != null) {
            return item;
        }

        ItemStack itemStack = new ItemStack(getIcon().getItemType(), getNumber(), getIcon().getData());
        Items.setLore(itemStack, descriptions);
        Items.setName(itemStack, getText());
        return itemStack;
    }

    public void close(Player player) {
        getMenu().closeMenu(player);
    }

    public MenuItem text(String text) {
        setText(text);
        return this;
    }

    public MenuItem description(String... lines) {
        setDescriptions(lines);
        return this;
    }

    public MenuItem addDescription(String line) {
        addDescriptions(line);
        return this;
    }

    public MenuItem icon(MaterialData data) {
        setIcon(data);
        return this;
    }

    public MenuItem number(int num) {
        setIconNumber(num);
        return this;
    }

    public abstract void onClick(Player player, ClickType type);
}
