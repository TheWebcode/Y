package io.github.thewebcode.yplugin.debug.gadget;

import io.github.thewebcode.yplugin.chat.Chat;
import io.github.thewebcode.yplugin.entity.Entities;
import io.github.thewebcode.yplugin.game.gadget.Gadgets;
import io.github.thewebcode.yplugin.game.item.BaseWeapon;
import io.github.thewebcode.yplugin.inventory.Inventories;
import io.github.thewebcode.yplugin.item.ItemBuilder;
import io.github.thewebcode.yplugin.permission.Perms;
import io.github.thewebcode.yplugin.player.Players;
import io.github.thewebcode.yplugin.utilities.NumberUtil;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class KickStick extends BaseWeapon {
    private static KickStick instance = null;

    public static KickStick getInstance() {
        if (instance == null) {
            instance = new KickStick();
            Gadgets.registerGadget(instance);
        }
        return instance;
    }

    protected KickStick() {
        super(ItemBuilder.of(Material.STICK).name("&cKick Stick").lore("&eGive em a &agood ol' &7&o*whacking*&r&e!"));
        properties().droppable(false);
    }


    @Override
    public void perform(Player holder) {

    }

    @Override
    public void onSwing(Player p) {

    }

    @Override
    public void onActivate(Player p) {

    }

    @Override
    public void onAttack(Player p, LivingEntity e) {
        int kickStickSlot = Inventories.getSlotOf(p.getInventory(),getItem());
        if (!p.hasPermission(Perms.KICK_STICK)) {
            Inventories.clearSlot(p.getInventory(),kickStickSlot);
            return;
        }

        if (!(e instanceof Player)) {
            Chat.message(p, "&cYou can't kick a(n) " + Entities.getDefaultName(e.getType()) + ", only players!");
            return;
        }

        Player target = (Player) e;

        if (Players.hasPermission(target, Perms.KICK_DENY)) {
            Chat.message(p, String.format("&a%s&e is unable to be kicked.", target));
            return;
        }

        int embursment = NumberUtil.getRandomInRange(100, 200);
        Players.kick(target, String.format("&c%s&e whacked you with the kick stick! Here's &a&o%s XP&e (for being a good sport &c<3&e)!", p.getName(), embursment));
        Chat.message(p, String.format("&eKicked &c%s&e and gave them &a%s", target.getName(), embursment));
    }

    @Override
    public void onBreak(Player p) {

    }

    @Override
    public void onDrop(Player p, Item item) {

    }
}
