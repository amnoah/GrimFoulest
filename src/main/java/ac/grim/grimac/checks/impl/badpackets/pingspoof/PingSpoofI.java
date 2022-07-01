package ac.grim.grimac.checks.impl.badpackets.pingspoof;

import ac.grim.grimac.checks.CheckData;
import ac.grim.grimac.checks.type.PacketCheck;
import ac.grim.grimac.player.GrimPlayer;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;

// Detects receiving more Transaction packets than sent
@CheckData(name = "PingSpoof I")
public class PingSpoofI extends PacketCheck {

    public int packetsSent;
    public int packetsReceived;

    public PingSpoofI(GrimPlayer player) {
        super(player);
    }

    @Override
    public void onPacketSend(PacketSendEvent event) {
        if (event.getPacketType() == PacketType.Play.Server.WINDOW_CONFIRMATION) {
            ++packetsSent;
        }
    }

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        if (event.getPacketType() == PacketType.Play.Client.WINDOW_CONFIRMATION) {
            if (++packetsReceived > packetsSent) {
                player.kick(getCheckName(), event, "SENT=" + packetsSent + " RECEIVED=" + packetsReceived);
            }
        }
    }
}
