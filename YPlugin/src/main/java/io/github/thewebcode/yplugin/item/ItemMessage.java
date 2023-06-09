package io.github.thewebcode.yplugin.item;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import io.github.thewebcode.yplugin.chat.Chat;
import io.github.thewebcode.yplugin.plugin.Plugins;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import java.util.List;
import java.util.PriorityQueue;

public class ItemMessage {
    private int interval = 20;
    private static final int DEFAULT_DURATION = 2;
    private static final int DEFAULT_PRIORITY = 0;
    private static final String DEF_FORMAT_1 = "%s";
    private static final String DEF_FORMAT_2 = " %s ";
    private static final String METADATA_Q_KEY = "item-message:msg-queue";
    private static final String METADATA_ID_KEY = "item-message:id";
    private final Plugin plugin;

    private String[] formats = new String[]{DEF_FORMAT_1, DEF_FORMAT_2};
    private Material emptyHandReplacement = Material.SNOW;

    public ItemMessage(Plugin plugin) {
        Plugin p = Plugins.getPlugin("ProtocolLib");
        if (p == null || !p.isEnabled()) {
            throw new IllegalStateException("ItemMessage can not be used without ProtocolLib");
        }
        this.plugin = plugin;
    }

    public void setInterval(int interval) {
        Validate.isTrue(interval > 0, "Interval can't be below 1!");
        this.interval = interval;
    }

    public void setEmptyHandReplacement(Material material) {
        Validate.notNull(material, "There must be a replacement for an empty hand!");
        this.emptyHandReplacement = material;
    }

    public void sendMessage(Player player, String message) {
        sendMessage(player, message, DEFAULT_DURATION, DEFAULT_PRIORITY);
    }

    public void sendMessage(Player player, String message, int duration) {
        sendMessage(player, message, duration, DEFAULT_PRIORITY);
    }

    public void sendMessage(Player player, String message, int duration, int priority) {
        if (player.getGameMode() == GameMode.CREATIVE) {
            Chat.actionMessage(player, message);
        } else {
            PriorityQueue<MessageRecord> msgQueue = getMessageQueue(player);
            msgQueue.add(new MessageRecord(message, duration, priority, getNextId(player)));
            if (msgQueue.size() == 1) {
                new NamerTask(player, msgQueue.peek()).runTaskTimer(plugin, 1L, interval);
            }
        }
    }

    public void setFormats(String... formats) {
        Validate.isTrue(formats.length > 1, "Two formats are minimum!");
        for (String format : formats) {
            Validate.isTrue(format.contains("%s"), "format string \"" + format + "\" must contain a %s");
        }
        this.formats = formats;
    }

    private long getNextId(Player player) {
        long id;
        if (player.hasMetadata(METADATA_ID_KEY)) {
            List<MetadataValue> l = player.getMetadata(METADATA_ID_KEY);
            id = l.size() >= 1 ? l.get(0).asLong() : 1L;
        } else {
            id = 1L;
        }
        player.setMetadata(METADATA_ID_KEY, new FixedMetadataValue(plugin, id + 1));
        return id;
    }

    @SuppressWarnings("unchecked")
    private PriorityQueue<MessageRecord> getMessageQueue(Player player) {
        if (!player.hasMetadata(METADATA_Q_KEY)) {
            player.setMetadata(METADATA_Q_KEY, new FixedMetadataValue(plugin, new PriorityQueue<MessageRecord>()));
        }
        for (MetadataValue v : player.getMetadata(METADATA_Q_KEY)) {
            if (v.value() instanceof PriorityQueue<?>) {
                return (PriorityQueue<MessageRecord>) v.value();
            }
        }
        return null;
    }

    private void notifyDone(Player player) {
        PriorityQueue<MessageRecord> msgQueue = getMessageQueue(player);
        msgQueue.poll();
        if (!msgQueue.isEmpty()) {
            MessageRecord rec = importOtherMessageRecord(msgQueue.peek());
            new NamerTask(player, rec).runTaskTimer(plugin, 1L, interval);
        }
    }

    private MessageRecord importOtherMessageRecord(Object other) {
        if (other instanceof MessageRecord) {
            return (MessageRecord) other;
        } else if (other.getClass().getName().endsWith(".ItemMessage$MessageRecord")) {
            try {
                Method m1 = other.getClass().getMethod("getId");
                Method m2 = other.getClass().getMethod("getPriority");
                Method m3 = other.getClass().getMethod("getMessage");
                Method m4 = other.getClass().getMethod("getDuration");
                long otherId = (Long) m1.invoke(other);
                int otherPriority = (Integer) m2.invoke(other);
                String otherMessage = (String) m3.invoke(other);
                int otherDuration = (Integer) m4.invoke(other);
                return new MessageRecord(otherMessage, otherDuration, otherPriority, otherId);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        } else {
            return null;
        }
    }

    private class NamerTask extends BukkitRunnable implements Listener {
        private final WeakReference<Player> playerRef;
        private final String message;
        private int slot;
        private int iterations;

        public NamerTask(Player player, MessageRecord rec) {
            this.playerRef = new WeakReference<>(player);
            this.iterations = Math.max(1, (rec.getDuration() * 20) / interval);
            this.slot = player.getInventory().getHeldItemSlot();
            this.message = rec.getMessage();
            Plugins.registerListener(plugin, this);
        }

        @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
        public void onItemHeldChange(PlayerItemHeldEvent event) {
            Player player = event.getPlayer();
            if (player.equals(playerRef.get())) {
                sendItemSlotChange(player, event.getPreviousSlot(), player.getInventory().getItem(event.getPreviousSlot()));
                slot = event.getNewSlot();
                refresh(event.getPlayer());
            }
        }

        @EventHandler
        public void onPluginDisable(PluginDisableEvent event) {
            Player player = playerRef.get();
            if (event.getPlugin() == plugin && player != null) {
                getMessageQueue(player).clear();
                finish(playerRef.get());
            }
        }

        @Override
        public void run() {
            Player player = playerRef.get();
            if (player != null) {
                if (iterations-- <= 0) {
                    finish(player);
                } else {
                    refresh(player);
                }
            } else {
                cleanup();
            }
        }

        private void refresh(Player player) {
            sendItemSlotChange(player, slot, makeStack(player));
        }

        private void finish(Player player) {
            sendItemSlotChange(player, slot, player.getInventory().getItem(slot));
            notifyDone(player);
            cleanup();
        }

        private void cleanup() {
            cancel();
            HandlerList.unregisterAll(this);
        }

        private ItemStack makeStack(Player player) {
            ItemStack stack0 = player.getInventory().getItem(slot);
            ItemStack stack;
            if (stack0 == null || stack0.getType() == Material.AIR) {
                stack = new ItemStack(emptyHandReplacement, 1);
            } else {
                stack = new ItemStack(stack0.getType(), stack0.getAmount(), stack0.getDurability());
            }
            ItemMeta meta = Bukkit.getItemFactory().getItemMeta(stack.getType());
            meta.setDisplayName(String.format(formats[iterations % formats.length], message));
            stack.setItemMeta(meta);
            return stack;
        }

        private void sendItemSlotChange(Player player, int slot, ItemStack stack) {
            PacketContainer setSlot = new PacketContainer(PacketType.Play.Server.SET_SLOT);
            setSlot.getIntegers().write(0, 0).write(1, slot + 36);
            setSlot.getItemModifier().write(0, stack);
            ProtocolLibrary.getProtocolManager().sendServerPacket(player, setSlot);
        }
    }

    public class MessageRecord implements Comparable<Object> {
        private final String message;
        private final int duration;
        private final int priority;
        private final long id;

        public MessageRecord(String message, int duration, int priority, long id) {
            this.message = message;
            this.duration = duration;
            this.priority = priority;
            this.id = id;
        }

        public String getMessage() {
            return message;
        }

        public int getDuration() {
            return duration;
        }

        public int getPriority() {
            return priority;
        }

        public long getId() {
            return id;
        }

        @Override
        public int compareTo(Object other) {
            MessageRecord rec = importOtherMessageRecord(other);
            if (rec != null) {
                if (this.priority == rec.getPriority()) {
                    return (Long.valueOf(this.id)).compareTo(rec.getId());
                } else {
                    return (Integer.valueOf(this.priority)).compareTo(rec.getPriority());
                }
            } else {
                return 0;
            }
        }
    }
}