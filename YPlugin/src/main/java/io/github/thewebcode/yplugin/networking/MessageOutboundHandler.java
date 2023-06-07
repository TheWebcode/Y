package io.github.thewebcode.yplugin.networking;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.MessageToMessageDecoder;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.protocol.game.PacketPlayInCustomPayload;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.server.network.PlayerConnection;
import org.bukkit.craftbukkit.v1_19_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

public class MessageOutboundHandler extends ChannelOutboundHandlerAdapter {
    public static final String NAME = "io.github.thewebcode.y:outbound_handler";
    private final Player player;
    private final UUID playerUUID;

    public MessageOutboundHandler(Player player) {
        this.player = player;
        this.playerUUID = player.getUniqueId();
    }

    private void detach(Player player){
        try{
            EntityPlayer entityPlayer = ((CraftPlayer) player).getHandle();
            PlayerConnection connection = entityPlayer.b;

            Field field = connection.getClass().getField("h");
            NetworkManager networkManager = (NetworkManager) field.get(connection);
            ChannelPipeline pipeline = networkManager.m.pipeline();
            pipeline.remove(NAME);
        } catch (NoSuchFieldException | IllegalAccessException e){
            e.printStackTrace();
        } catch (NoSuchElementException ignored) {}
    }

    private void attach(Player player) {
        try {
            EntityPlayer entityPlayer = ((CraftPlayer) player).getHandle();
            PlayerConnection connection = entityPlayer.b;

            Field field = connection.getClass().getField("h");
            NetworkManager networkManager = (NetworkManager) field.get(connection);
            ChannelPipeline pipeline = networkManager.m.pipeline();
            detach(player);
            pipeline.addAfter("decoder", NAME, new MessageToMessageDecoder<PacketPlayInCustomPayload>() {
                @Override
                protected void decode(ChannelHandlerContext channelHandlerContext, PacketPlayInCustomPayload packetPlayInCustomPayload, List<Object> list) throws Exception {
                    list.add(packetPlayInCustomPayload);
                    read(packetPlayInCustomPayload);
                }
            });
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private void read(PacketPlayInCustomPayload payload){
        String packet = payload.c.toString();
        if(packet.contains("yfabric")){
            String packetName = packet.replace("yfabric:", "");
            System.out.println("Got packet: " + packetName);
        }
    }

    public static class Builder{
        private Player player;

        public Builder(Player player){
            this.player = player;
        }

        public MessageOutboundHandler build(){
            return new MessageOutboundHandler(player);
        }
    }
}
