package ac.grim.grimac.checks.impl.pingspoof;

import ac.grim.grimac.checks.CheckData;
import ac.grim.grimac.checks.type.PacketCheck;
import ac.grim.grimac.player.GrimPlayer;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.player.ClientVersion;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerFlying;

// Detects sending Transaction packets without Flying packets
@CheckData(name = "PingSpoof F")
public class PingSpoofF extends PacketCheck {

    private int streak;

    public PingSpoofF(GrimPlayer player) {
        super(player);
    }

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        if (event.getPacketType() == PacketType.Play.Client.WINDOW_CONFIRMATION) {
            streak++;

        } else if (WrapperPlayClientPlayerFlying.isFlying(event.getPacketType())) {
            streak = 0;

        } else if (event.getPacketType() == PacketType.Play.Client.KEEP_ALIVE) {
            if (player.getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_9)) {
                if (streak >= 20) {
                    event.setCancelled(true);
                    player.kick(getCheckName(), "FLYING");
                }

            } else if (streak >= 10) {
                event.setCancelled(true);
                player.kick(getCheckName(), "FLYING");
            }
        }
    }
}
