package ac.grim.grimac.checks.impl.badpackets.pingspoof;

import ac.grim.grimac.checks.CheckData;
import ac.grim.grimac.checks.type.PacketCheck;
import ac.grim.grimac.player.GrimPlayer;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientResourcePackStatus;

// Detects receiving more ResourcePackStatus packets than sent
@CheckData(name = "PingSpoof J")
public class PingSpoofJ extends PacketCheck {

    private int packetsSent;
    private int packetsReceived;

    public PingSpoofJ(GrimPlayer player) {
        super(player);
    }

    @Override
    public void onPacketSend(PacketSendEvent event) {
        if (event.getPacketType() == PacketType.Play.Server.RESOURCE_PACK_SEND) {
            packetsSent++;
        }
    }

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        if (event.getPacketType() == PacketType.Play.Client.RESOURCE_PACK_STATUS) {
            WrapperPlayClientResourcePackStatus packet = new WrapperPlayClientResourcePackStatus(event);

            if (packet.getResult() != WrapperPlayClientResourcePackStatus.Result.ACCEPTED) {
                packetsReceived++;
            }

            if (packetsReceived > packetsSent) {
                event.setCancelled(true);
                player.kick(getCheckName(), "SENT=" + packetsSent + " RECEIVED=" + packetsReceived, "You are sending too many packets!");
            }
        }
    }
}
