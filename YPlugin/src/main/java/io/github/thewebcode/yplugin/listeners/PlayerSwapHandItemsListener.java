package io.github.thewebcode.yplugin.listeners;

import io.github.thewebcode.yplugin.Messages;
import io.github.thewebcode.yplugin.chat.Chat;
import io.github.thewebcode.yplugin.game.gadget.Gadget;
import io.github.thewebcode.yplugin.game.gadget.Gadgets;
import io.github.thewebcode.yplugin.inventory.HandSlot;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.ItemStack;

public class PlayerSwapHandItemsListener implements Listener {
    @EventHandler
    public void onPlayerSwapHandItem(PlayerSwapHandItemsEvent e) {
        if (e.isCancelled()) {
            return;
        }

        ItemStack offHandItem = e.getOffHandItem();
        Player player = e.getPlayer();
        if (Gadgets.isGadget(offHandItem)) {
            Gadget gadgetGoingOffhand = Gadgets.getGadget(offHandItem);
            if (!gadgetGoingOffhand.properties().isOffhandEquippable()) {
                Chat.actionMessage(player, Messages.gadgetEquipError(gadgetGoingOffhand, HandSlot.OFF_HAND));
                e.setCancelled(true);
            }
        }
    }
}
