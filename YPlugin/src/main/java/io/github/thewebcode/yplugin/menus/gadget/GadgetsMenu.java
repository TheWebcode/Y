package io.github.thewebcode.yplugin.menus.gadget;

import io.github.thewebcode.yplugin.chat.Chat;
import io.github.thewebcode.yplugin.game.gadget.Gadget;
import io.github.thewebcode.yplugin.game.gadget.Gadgets;
import io.github.thewebcode.yplugin.item.Items;
import io.github.thewebcode.yplugin.inventory.menu.ItemMenu;
import io.github.thewebcode.yplugin.inventory.menu.MenuItem;
import io.github.thewebcode.yplugin.inventory.menu.Menus;
import io.github.thewebcode.yplugin.player.Players;
import com.google.common.collect.Lists;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GadgetsMenu {
    private static Map<Integer, List<Gadget>> gadgetPages = new HashMap<>();

    private static void initPages() {
        gadgetPages.clear();

        List<List<Gadget>> pages = Lists.partition(Lists.newArrayList(Gadgets.getAllGadgets()), 52);

        int i = 0;
        for (List<Gadget> gadgetList : pages) {
            gadgetPages.put(i, gadgetList);
            i++;
        }
    }

    public static GadgetMenu getMenu(int page) throws IndexOutOfBoundsException {
        initPages();

        if (!gadgetPages.containsKey(page)) {
            throw new IndexOutOfBoundsException("The page " + page + " doesn't exist for the gadgets menus");
        }

        List<Gadget> gadgets = gadgetPages.get(page);
        GadgetMenu menu = new GadgetMenu(page, gadgets);
        return menu;
    }

    public static class GadgetMenu extends ItemMenu {
        public GadgetMenu(int page, List<Gadget> gadgets) {
            super(Chat.format("Gadgets Menu Page[%s]", page), Menus.getRows(53));

            setExitOnClickOutside(false);
            addMenuItem(new GadgetMenuSwitchPageItem(page, 1), 45);
            addMenuItem(new GadgetMenuSwitchPageItem(page, 0), 53);

            for (int i = 0; i < gadgets.size(); i++) {
                addMenuItem(new GadgetMenuItem(gadgets.get(i)), i >= 45 ? i + 1 : i);
            }
        }
    }

    private static class GadgetMenuItem extends MenuItem {
        private int gadgetId;

        public GadgetMenuItem(Gadget gadget) {
            super(Items.getName(gadget.getItem()), gadget.getItem().getData());
            this.gadgetId = gadget.id();

            setDescriptions(Chat.format("&aGadget Id:&e%s", gadgetId));
        }

        @Override
        public void onClick(Player player, ClickType type) {
            if (!Gadgets.isGadget(gadgetId)) {
                Chat.actionMessage(player, "&cGadget ID " + gadgetId + " isn't a valid gadget.");
                return;
            }

            ItemStack gadget = Gadgets.getGadget(gadgetId).getItem();
            Players.giveItem(player, gadget);
        }
    }

    private static class GadgetMenuSwitchPageItem extends MenuItem {
        private int direction;
        private int currentPage;

        public GadgetMenuSwitchPageItem(int currentPage, int direction) {
            super(direction == 0 ? "&aNext" : "&ePrevious");
            this.direction = direction;
            this.currentPage = currentPage;
        }

        @Override
        public void onClick(Player player, ClickType type) {
            try {
                switch (direction) {
                    case 0:
                        if (currentPage >= gadgetPages.size()) {
                            Chat.message(player, "&7This is the final page, please use the &oprevious&r&7 button");
                            return;
                        }

                        getMenu().switchMenu(player, GadgetsMenu.getMenu(currentPage + 1));
                        break;
                    case 1:
                        if (currentPage <= gadgetPages.size()) {
                            Chat.message(player, "&7This is the first page, please use the &onext&r&7 button");
                            return;
                        }

                        getMenu().switchMenu(player, GadgetsMenu.getMenu(currentPage - 1));
                        break;
                    default:
                        break;
                }
            } catch (IndexOutOfBoundsException e) {
                getMenu().switchMenu(player, GadgetsMenu.getMenu(0));
            }
        }

    }
}
