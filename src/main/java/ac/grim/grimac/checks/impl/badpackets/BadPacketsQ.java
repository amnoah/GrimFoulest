package ac.grim.grimac.checks.impl.badpackets;

import ac.grim.grimac.checks.CheckData;
import ac.grim.grimac.checks.type.PacketCheck;
import ac.grim.grimac.player.GrimPlayer;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.item.ItemStack;
import com.github.retrooper.packetevents.protocol.item.type.ItemTypes;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPluginMessage;

// Detects sending invalid PLUGIN_MESSAGE packets
@CheckData(name = "BadPackets Q")
public class BadPacketsQ extends PacketCheck {

    public BadPacketsQ(GrimPlayer player) {
        super(player);
    }

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        if (event.getPacketType() == PacketType.Play.Client.PLUGIN_MESSAGE) {
            WrapperPlayClientPluginMessage packet = new WrapperPlayClientPluginMessage(event);
            String channel = packet.getChannelName().toString();

            // Detects sending packets with invalid sizes.
            if (packet.getData().length > 15000 || packet.getData().length <= 0) {
                player.kick(getCheckName(), event, "SIZE=" + packet.getData().length);
            }

            // Detects sending invalid book-related channels.
            if (channel.equals("MC|BOpen") || channel.equals("MC|BEdit") || channel.equals("MC|BSign")) {
                ItemStack itemStack = player.getInventory().getHeldItem();

                if (itemStack != null && itemStack.getType() != ItemTypes.BOOK
                        && itemStack.getType() != ItemTypes.WRITTEN_BOOK
                        && itemStack.getType() != ItemTypes.WRITABLE_BOOK
                        && itemStack.getType() != ItemTypes.ENCHANTED_BOOK) {
                    player.kick(getCheckName(), event, "CHANNEL=" + channel);
                }
            }
        }
    }
}
