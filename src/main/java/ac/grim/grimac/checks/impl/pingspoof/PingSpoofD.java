package ac.grim.grimac.checks.impl.pingspoof;

import ac.grim.grimac.checks.CheckData;
import ac.grim.grimac.checks.type.PacketCheck;
import ac.grim.grimac.player.GrimPlayer;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerFlying;

// Detects delaying Transaction packets
// Note: may falsely kick players with lag spikes
@CheckData(name = "PingSpoof D")
public class PingSpoofD extends PacketCheck {

    private int streak;

    public PingSpoofD(GrimPlayer player) {
        super(player);
    }

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        if (WrapperPlayClientPlayerFlying.isFlying(event.getPacketType())) {
            streak++;

        } else if (event.getPacketType() == PacketType.Play.Client.WINDOW_CONFIRMATION) {
            streak = 0;
        }

        if (streak >= 40) {
            event.setCancelled(true);
            player.kick(getCheckName(), "DELAY");
        }
    }
}
