package ac.grim.grimac.checks.impl.pingspoof;

import ac.grim.grimac.checks.CheckData;
import ac.grim.grimac.checks.type.PacketCheck;
import ac.grim.grimac.player.GrimPlayer;
import ac.grim.grimac.utils.data.Pair;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.login.server.WrapperLoginServerPluginRequest;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientKeepAlive;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientWindowConfirmation;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerKeepAlive;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerWindowConfirmation;

import java.util.LinkedList;
import java.util.Queue;

// Detects players sending unknown Transaction packets
@CheckData(name = "PingSpoof I")
public class PingSpoofI extends PacketCheck {

    final Queue<Pair<Short, Long>> transactionMap = new LinkedList<>();

    public PingSpoofI(GrimPlayer player) {
        super(player);
    }

    @Override
    public void onPacketSend(PacketSendEvent event) {
        if (event.getPacketType() == PacketType.Play.Server.WINDOW_CONFIRMATION) {
            WrapperPlayServerWindowConfirmation packet = new WrapperPlayServerWindowConfirmation(event);
            transactionMap.add(new Pair<>(packet.getActionId(), System.nanoTime()));
        }
    }

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        if (event.getPacketType() == PacketType.Play.Client.WINDOW_CONFIRMATION) {
            WrapperPlayClientWindowConfirmation packet = new WrapperPlayClientWindowConfirmation(event);
            long id = packet.getActionId();
            boolean hasID = false;

            for (Pair<Short, Long> iterator : transactionMap) {
                if (iterator.getFirst() == id) {
                    hasID = true;
                    break;
                }
            }

            if (!hasID) {
                event.setCancelled(true);
                player.kick(getCheckName(), "UNKNOWN (ID=" + id + ")");
            } else { // Found the ID, remove stuff until we get to it (to stop very slow memory leaks)
                Pair<Short, Long> data;
                do {
                    data = transactionMap.poll();
                    if (data == null) {
                        break;
                    }
                } while (data.getFirst() != id);
            }
        }
    }
}
