package io.github.thewebcode.yplugin.game.event;

import io.github.thewebcode.yplugin.game.MiniGame;
import io.github.thewebcode.yplugin.game.world.Arena;
import org.bukkit.World;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class ArenaCycleEvent extends Event implements Cancellable {
    private static HandlerList handlers = new HandlerList();

    private boolean cancelled = false;

    private Arena from;

    private Arena to;

    private MiniGame game;

    public ArenaCycleEvent(MiniGame game, Arena from, Arena to) {
        this.game = game;
        this.from = from;
        this.to = to;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean b) {
        this.cancelled = b;
    }

    public Arena getFrom() {
        return from;
    }

    public Arena getTo() {
        return to;
    }

    public void setTo(World world) {
        to = getGame().getArenaManager().getArena(world);
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public MiniGame getGame() {
        return game;
    }
}
