package ac.grim.grimac.checks.impl.badpackets;

import ac.grim.grimac.checks.CheckData;
import ac.grim.grimac.checks.type.PacketCheck;
import ac.grim.grimac.player.GrimPlayer;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerFlying;

// Detects sending packets in-sync with Flying
@CheckData(name = "BadPackets L")
public class BadPacketsL extends PacketCheck {

    private int streak;

    public BadPacketsL(GrimPlayer player) {
        super(player);
    }

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        if (!WrapperPlayClientPlayerFlying.isFlying(event.getPacketType())
                && event.getPacketType() != PacketType.Play.Client.WINDOW_CONFIRMATION
                && event.getPacketType() != PacketType.Play.Client.STEER_VEHICLE
                && event.getPacketType() != PacketType.Play.Client.CLICK_WINDOW) {
            if (streak % 2 == 0) {
                streak++;

                if (streak >= 10) {
                    event.setCancelled(true);
                    player.kick(getCheckName(), event.getPacketType().getName());
                }
            } else {
                streak = 0;
            }

        } else if (WrapperPlayClientPlayerFlying.isFlying(event.getPacketType())) {
            if (streak % 2 != 0) {
                streak++;
            } else {
                streak = 0;
            }
        }
    }
}
