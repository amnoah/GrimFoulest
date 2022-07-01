package ac.grim.grimac.checks.impl.badpackets;

import ac.grim.grimac.checks.CheckData;
import ac.grim.grimac.checks.type.PacketCheck;
import ac.grim.grimac.player.GrimPlayer;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientResourcePackStatus;

// Detects sending invalid RESOURCE_PACK_STATUS packets
@CheckData(name = "BadPackets O")
public class BadPacketsO extends PacketCheck {

    public boolean accepted;
    public int packetsSent;
    public int packetsReceived;

    public BadPacketsO(GrimPlayer player) {
        super(player);
    }

    @Override
    public void onPacketSend(PacketSendEvent event) {
        // Keeps track of packets sent.
        if (event.getPacketType() == PacketType.Play.Server.RESOURCE_PACK_SEND) {
            ++packetsSent;
        }
    }

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        if (event.getPacketType() == PacketType.Play.Client.RESOURCE_PACK_STATUS) {
            WrapperPlayClientResourcePackStatus packet = new WrapperPlayClientResourcePackStatus(event);

            // Keeps track of packets received.
            if (packet.getResult() != WrapperPlayClientResourcePackStatus.Result.ACCEPTED) {
                ++packetsReceived;
            }

            // Detects receiving more packets than sent.
            if (packetsReceived > packetsSent) {
                player.kick(getCheckName(), event, "SENT=" + packetsSent + " RECEIVED=" + packetsReceived);
            }

            // Detects sending two ACCEPTED packets in a row.
            switch (packet.getResult()) {
                case DECLINED:
                case SUCCESSFULLY_LOADED:
                case FAILED_DOWNLOAD:
                    accepted = false;
                    break;

                case ACCEPTED:
                    if (accepted) {
                        player.kick(getCheckName(), event, "Accepted");
                        return;
                    }

                    accepted = true;
            }
        }
    }
}
