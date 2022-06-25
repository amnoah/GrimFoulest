package ac.grim.grimac.checks.impl.pingspoof;

import ac.grim.grimac.checks.CheckData;
import ac.grim.grimac.checks.type.PacketCheck;
import ac.grim.grimac.player.GrimPlayer;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;

// Detects receiving more KeepAlive packets than sent
@CheckData(name = "PingSpoof K")
public class PingSpoofK extends PacketCheck {

    private int packetsSent;
    private int packetsReceived;

    public PingSpoofK(GrimPlayer player) {
        super(player);
    }

    @Override
    public void onPacketSend(PacketSendEvent event) {
        if (event.getPacketType() == PacketType.Play.Server.KEEP_ALIVE) {
            packetsSent++;
        }
    }

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        if (event.getPacketType() == PacketType.Play.Client.KEEP_ALIVE) {
            packetsReceived++;

            if (packetsReceived > packetsSent) {
                event.setCancelled(true);
                player.kick(getCheckName(), "SENT=" + packetsSent + " RECEIVED=" + packetsReceived, "You are sending too many packets!");
            }
        }
    }
}
