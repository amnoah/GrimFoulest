package ac.grim.grimac.checks.impl.pingspoof;

import ac.grim.grimac.checks.CheckData;
import ac.grim.grimac.checks.type.PacketCheck;
import ac.grim.grimac.player.GrimPlayer;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientKeepAlive;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerKeepAlive;

// Detects modifying KeepAlive packets
@CheckData(name = "PingSpoof A")
public class PingSpoofA extends PacketCheck {

    public long lastSentID;
    public long lastReceivedID;
    public long sentKeepAliveTime;
    public long streak;

    public PingSpoofA(GrimPlayer player) {
        super(player);
    }

    @Override
    public void onPacketSend(PacketSendEvent event) {
        if (event.getPacketType() == PacketType.Play.Server.KEEP_ALIVE) {
            WrapperPlayServerKeepAlive packet = new WrapperPlayServerKeepAlive(event);
            long sentID = packet.getId();

            // For calculating player ping
            sentKeepAliveTime = System.nanoTime();

            if (lastSentID != lastReceivedID && lastReceivedID != -1) {
                event.setCancelled(true);
                player.kick(getCheckName(), "SENT (S=" + lastSentID + ", R=" + lastReceivedID + ")");
                return;
            }

            lastSentID = sentID;
        }
    }

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        if (event.getPacketType() == PacketType.Play.Client.KEEP_ALIVE) {
            WrapperPlayClientKeepAlive packet = new WrapperPlayClientKeepAlive(event);
            long receivedID = packet.getId();

            // For calculating player ping
            player.keepAlivePing = (int) (System.nanoTime() - sentKeepAliveTime) / 1000000;

            if (receivedID != lastSentID && receivedID != -1) {
                event.setCancelled(true);
                player.kick(getCheckName(), "RECEIVED (S=" + lastSentID + ", R=" + receivedID + ")");
                return;
            }

            lastReceivedID = receivedID;
        }
    }
}
