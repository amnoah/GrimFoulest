package ac.grim.grimac.checks.impl.pingspoof;

import ac.grim.grimac.checks.CheckData;
import ac.grim.grimac.checks.type.PacketCheck;
import ac.grim.grimac.player.GrimPlayer;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientKeepAlive;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerKeepAlive;

// Detects delaying & modifying KeepAlive packets
@CheckData(name = "PingSpoof A")
public class PingSpoofA extends PacketCheck {

    public long lastSentID;
    public long lastSentTime;
    public long lastReceivedID;
    public long lastReceivedTime;
    public long streak;

    public PingSpoofA(GrimPlayer player) {
        super(player);
    }

    @Override
    public void onPacketSend(PacketSendEvent event) {
        if (event.getPacketType() == PacketType.Play.Server.KEEP_ALIVE) {
            WrapperPlayServerKeepAlive packet = new WrapperPlayServerKeepAlive(event);
            long sentID = packet.getId();
            long sentTime = System.nanoTime();

//            System.out.println("(Send)"
//                    + " sentID=" + sentID
//                    + " sentTime=" + sentTime
//                    + " lastSentID=" + lastSentID
//                    + " lastSentTime=" + lastSentTime
//                    + " lastReceivedID=" + lastReceivedID
//                    + " lastReceivedTime=" + lastReceivedTime
//                    + " timeDiff=" + (sentTime - lastReceivedTime));

            if (lastSentID != lastReceivedID) {
                event.setCancelled(true);
                player.kick(getCheckName(), "SENT (S=" + lastSentID + ", R=" + lastReceivedID + ")");
                return;
            }

            lastSentID = sentID;
            lastSentTime = sentTime;
        }
    }

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        if (event.getPacketType() == PacketType.Play.Client.KEEP_ALIVE) {
            WrapperPlayClientKeepAlive packet = new WrapperPlayClientKeepAlive(event);
            long receivedID = packet.getId();
            long receivedTime = System.nanoTime();
            player.ping = (double) (receivedTime - lastSentTime) / 1000000; // Calculates player ping

//            System.out.println("(Received)"
//                    + " receivedID=" + receivedID
//                    + " receivedTime=" + receivedTime
//                    + " lastReceivedID=" + lastReceivedID
//                    + " lastReceivedTime=" + lastReceivedTime
//                    + " lastSentID=" + lastSentID
//                    + " lastSentTime=" + lastSentTime
//                    + " timeDiff=" + (receivedTime - lastSentTime));

            if (receivedID != lastSentID && receivedID != -1) {
                event.setCancelled(true);
                player.kick(getCheckName(), "RECEIVED (S=" + lastSentID + ", R=" + receivedID + ")");
                return;
            }

            lastReceivedID = receivedID;
            lastReceivedTime = receivedTime;
        }
    }
}
