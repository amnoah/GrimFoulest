package ac.grim.grimac.checks.impl.badpackets.pingspoof;

import ac.grim.grimac.checks.CheckData;
import ac.grim.grimac.checks.type.PacketCheck;
import ac.grim.grimac.player.GrimPlayer;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;

// Detects receiving more KeepAlive packets than sent
@CheckData(name = "PingSpoof A")
public class PingSpoofA extends PacketCheck {

    private int packetsSent;
    private int packetsReceived;

    public PingSpoofA(GrimPlayer player) {
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
                player.kick(getCheckName(), event, "SENT=" + packetsSent + " RECEIVED=" + packetsReceived);
            }
        }
    }
}
