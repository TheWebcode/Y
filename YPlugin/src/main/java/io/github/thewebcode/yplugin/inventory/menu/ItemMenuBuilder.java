package io.github.thewebcode.yplugin.inventory.menu;

import io.github.thewebcode.yplugin.inventory.menu.Menus;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.lang.Validate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ItemMenuBuilder {

    private String title = null;

    private Map<Integer, MenuItem> items = Maps.newHashMap();

    private boolean exitOnClickOutside = false;

    private Map<MenuAction, ArrayList<MenuBehaviour>> menuActions = new HashMap<>();

    public ItemMenuBuilder() {
        menuActions.put(MenuAction.OPEN, Lists.newArrayList());
        menuActions.put(MenuAction.CLOSE, Lists.newArrayList());
    }

    public ItemMenuBuilder title(String title) {
        this.title = title;
        return this;
    }

    public ItemMenuBuilder exitOnClickOutside(boolean val) {
        exitOnClickOutside = val;
        return this;
    }

    public ItemMenuBuilder item(MenuItem item) {
        int freeSlot = Menus.getFirstFreeSlot(items);
        items.put(freeSlot, item);
        return this;
    }

    public ItemMenuBuilder item(int index, MenuItem item) {
        items.put(index, item);
        return this;
    }

    public ItemMenuBuilder addBehaviour(MenuAction type, MenuBehaviour action) {
        menuActions.get(type).add(action);
        return this;
    }

    public ItemMenuBuilder onMenuOpen(MenuBehaviour action) {
        addBehaviour(MenuAction.OPEN, action);
        return this;
    }

    public ItemMenuBuilder onMenuClose(MenuBehaviour action) {
        addBehaviour(MenuAction.CLOSE, action);
        return this;
    }

    public ItemMenu getMenu() {
        Validate.notNull(title);
        Validate.notEmpty(items);
        int rows = Menus.getRowsForIndex(Menus.getHighestIndex(items));

        ItemMenu menu = new ItemMenu(title, rows);
        menu.setBehaviours(menuActions);
        menu.setMenuItems(items);
        return menu;
    }

}
