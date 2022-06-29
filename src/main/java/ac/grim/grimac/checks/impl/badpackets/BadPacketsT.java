package ac.grim.grimac.checks.impl.badpackets;

import ac.grim.grimac.checks.CheckData;
import ac.grim.grimac.checks.type.PacketCheck;
import ac.grim.grimac.player.GrimPlayer;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientResourcePackStatus;

// Detects sending invalid resource pack packets
@CheckData(name = "BadPackets T")
public class BadPacketsT extends PacketCheck {

    public boolean sent;
    public boolean accepted;

    public BadPacketsT(GrimPlayer player) {
        super(player);
    }

    @Override
    public void onPacketSend(PacketSendEvent event) {
        if (event.getPacketType() == PacketType.Play.Server.RESOURCE_PACK_SEND) {
            sent = true;
        }
    }

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        if (event.getPacketType() == PacketType.Play.Client.RESOURCE_PACK_STATUS) {
            WrapperPlayClientResourcePackStatus packet = new WrapperPlayClientResourcePackStatus(event);

            switch (packet.getResult()) {
                case DECLINED:
                    if (!sent) {
                        player.kick(getCheckName(), event, "DECLINED");
                    }

                    sent = false;
                    accepted = false;
                    break;

                case FAILED_DOWNLOAD:
                    if (!sent) {
                        player.kick(getCheckName(), event, "FAILED_DOWNLOAD");
                    }

                    sent = false;
                    accepted = false;
                    break;

                case SUCCESSFULLY_LOADED:
                    if (!sent) {
                        player.kick(getCheckName(), event, "SUCCESSFULLY_LOADED");
                    }

                    sent = false;
                    accepted = false;
                    break;

                case ACCEPTED:
                    if (!sent) {
                        player.kick(getCheckName(), event, "ACCEPTED (NOT SENT)");
                        return;
                    }

                    if (accepted) {
                        player.kick(getCheckName(), event, "ACCEPTED (SENT)");
                        return;
                    }

                    accepted = true;
            }
        }
    }
}
