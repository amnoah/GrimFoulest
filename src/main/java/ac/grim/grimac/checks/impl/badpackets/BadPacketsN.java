package ac.grim.grimac.checks.impl.badpackets;

import ac.grim.grimac.checks.CheckData;
import ac.grim.grimac.checks.type.PacketCheck;
import ac.grim.grimac.player.GrimPlayer;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientChatMessage;

// Detects sending invalid CHAT_MESSAGE packets
@CheckData(name = "BadPackets N")
public class BadPacketsN extends PacketCheck {

    public BadPacketsN(GrimPlayer player) {
        super(player);
    }

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        if (event.getPacketType() == PacketType.Play.Client.CHAT_MESSAGE) {
            WrapperPlayClientChatMessage packet = new WrapperPlayClientChatMessage(event);

            if (packet.getMessage().equals("")) {
                player.kick(getCheckName(), event, "Empty Message");
            }
        }
    }
}
