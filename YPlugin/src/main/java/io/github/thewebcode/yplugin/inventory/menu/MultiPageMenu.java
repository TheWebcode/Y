package io.github.thewebcode.yplugin.inventory.menu;

import io.github.thewebcode.yplugin.chat.Chat;
import io.github.thewebcode.yplugin.item.Items;
import com.google.common.collect.Lists;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;

import java.util.*;

import static io.github.thewebcode.yplugin.inventory.menu.MenuAction.OPEN;

public class MultiPageMenu implements Menu {
    private List<ItemPage> pages;

    private String titleFormat = "{title} - Page {page}";
    private String title = "";

    private int pageActive = 0;

    private PageSettings settings = new PageSettings();

    private Map<MenuAction, List<MenuBehaviour>> behaviours = new HashMap<>();

    public MultiPageMenu(String title) {
        this.title = title;

        behaviours.put(OPEN, new ArrayList<>());
        behaviours.put(MenuAction.CLOSE, new ArrayList<>());
    }

    public void setTitleFormat(String titleFormat) {
        this.titleFormat = titleFormat;
    }

    public int getPageActive() {
        return pageActive;
    }

    public void setPageActive(int index) {
        this.pageActive = index;
    }

    public ItemPage getPage(int index) {
        try {
            return pages.get(index);
        } catch (IndexOutOfBoundsException ex) {
            return null;
        }
    }

    public ItemPage getNextPage() {
        int index = getNextPageIndex(pageActive);

        return pages.get(index);
    }

    public int getNextPageIndex(int index) {
        int nextPage = index + 1;

        try {
            ItemPage page = pages.get(nextPage);
            return page.pageIndex();
        } catch (IndexOutOfBoundsException ex) {
            Chat.debug("[MultiPageMenu] Handling indexOutOfBounds for next page (value = " + nextPage + ")");

            if (pages.size() > 1) {
                return 0;
            }
        }

        return 0;
    }

    public ItemPage getPreviousPage() {
        int index = getPreviousPageIndex(pageActive);

        return pages.get(index);
    }

    public int getPreviousPageIndex(int index) {
        int previousPage = index - 1;

        try {
            ItemPage page = pages.get(previousPage);
            return page.pageIndex();
        } catch (IndexOutOfBoundsException ex) {
            Chat.debug("[MultiPageMenu] Handling indexOutOfBounds for previous page (value = " + previousPage + ")");
            if (pages.size() > 1) {
                return pages.size() - 1;
            }
        }
        return 0;
    }

    public void populate(List<MenuItem> items) {
        this.pages = generatePages(items, settings);
        MenuBehaviour updatePageActiveIndex = new MenuBehaviour() {
            @Override
            public void doAction(Menu menu, Player player) {
                pageActive = ((ItemPage.Menu) menu).getPage();
            }
        };

        for (ItemPage page : pages) {
            page.addBehaviour(OPEN, updatePageActiveIndex);
        }
    }

    public List<ItemPage> generatePages(List<MenuItem> items, PageSettings settings) {

        int utilitySlot = 0;

        if (settings == null) {
            throw new NullPointerException("Settings Object is Null");
        }

        if (settings.pageFirstEnabled) {
            utilitySlot += 1;
        }

        if (settings.slotPageNext > 0) {
            utilitySlot += 1;
        }

        if (settings.slotPagePrevious > 0) {
            utilitySlot += 1;
        }

        List<ItemPage> generatedPages = new ArrayList<>();

        if (items.size() == 0) {
            generatedPages.add(new ItemPage(0, new ArrayList<>()));
        }

        List<List<MenuItem>> itemPages = Lists.partition(items, settings.getPageSlotCount() - utilitySlot);

        for (List<MenuItem> pageItems : itemPages) {

            List<MenuItem> processedPageItems = new ArrayList<>(pageItems);
            for (int i = 0; i < settings.getPageSlotCount(); i++) {
                if (i == settings.slotFirstPage) {
                    InlineMenuItem firstPageItem = new InlineMenuItem(Items.getName(settings.itemPageFirst));

                    firstPageItem.setClickHandler(new MenuItemClickHandler() {
                        @Override
                        public void onClick(MenuItem item, Player player, ClickType type) {
                            Menus.switchMenu(player, item.getMenu(), pages.get(0).getPageMenu());
                        }
                    });

                    processedPageItems.add(firstPageItem);
                    continue;
                }

                if (i == settings.slotPagePrevious) {
                    InlineMenuItem previousPageItem = new InlineMenuItem(Items.getName(settings.itemPagePrevious));

                    previousPageItem.setClickHandler(new MenuItemClickHandler() {
                        @Override
                        public void onClick(MenuItem item, Player player, ClickType type) {
                            Menus.switchMenu(player, item.getMenu(), getPreviousPage().getPageMenu());
                        }
                    });

                    processedPageItems.add(previousPageItem);
                    continue;
                }

                if (i == settings.slotPageNext) {
                    InlineMenuItem nextPageItem = new InlineMenuItem(Items.getName(settings.itemPageNext));

                    nextPageItem.setClickHandler(new MenuItemClickHandler() {
                        @Override
                        public void onClick(MenuItem item, Player player, ClickType type) {
                            Menus.switchMenu(player, item.getMenu(), getNextPage().getPageMenu());
                        }
                    });

                    processedPageItems.add(nextPageItem);
                    continue;
                }

                try {
                    MenuItem item = pageItems.get(i);
                    processedPageItems.add(item);
                } catch (IndexOutOfBoundsException ex) {
                    continue;
                }
            }

            int generatedIndex = 0;

            if (generatedPages.size() > 0) {
                generatedIndex = generatedPages.size();
            }

            ItemPage generatedPage = new ItemPage(generatedIndex, processedPageItems);
            generatedPages.add(generatedPage);

        }

        return generatedPages;
    }

    @Override
    public void addBehaviour(MenuAction action, MenuBehaviour behaviour) {
        behaviours.get(action).add(behaviour);
    }

    @Override
    public List<MenuBehaviour> getBehaviours(MenuAction action) {
        return behaviours.get(action);
    }

    @Override
    public boolean exitOnClickOutside() {
        return false;
    }

    @Override
    public Menu clone() {
        MultiPageMenu menu = new MultiPageMenu(title);
        menu.setPages(new ArrayList<>(pages));
        PageSettings newSettings = new PageSettings();
        newSettings.item(settings.pageFirstEnabled).firstPageItem(settings.itemPageFirst).firstPageSlot(settings.slotFirstPage)
                .nextPageItem(settings.itemPageNext.clone())
                .nextPageSlot(settings.slotPageNext)
                .previousPageItem(settings.itemPagePrevious.clone())
                .previousPageSlot(settings.slotPagePrevious)
                .rows(settings.rows);

        menu.settings = newSettings;

        menu.behaviours = new HashMap<>(behaviours);

        menu.titleFormat = titleFormat;

        return menu;
    }

    @Override
    public Inventory getInventory() {
        ItemPage activePage = getPage(getPageActive());

        activePage.setTitle(title);

        return activePage.getPageMenu().getInventory();
    }

    public void addPage(ItemPage page) {
        int index = pages.size() + 1;
        page.setPageIndex(index);
        MenuBehaviour updatePageActiveIndex = new MenuBehaviour() {
            @Override
            public void doAction(Menu menu, Player player) {
                pageActive = ((ItemPage.Menu) menu).getPage();
            }
        };
        page.addBehaviour(MenuAction.OPEN, updatePageActiveIndex);
        pages.add(page);
    }

    public void setPages(List<ItemPage> pages) {
        this.pages = pages;
    }

    public PageSettings settings() {
        return settings;
    }

    public void setSettings(PageSettings settings) {
        this.settings = settings;
    }
}
