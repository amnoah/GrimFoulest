package ac.grim.grimac.checks.impl.badpackets;

import ac.grim.grimac.checks.CheckData;
import ac.grim.grimac.checks.type.PacketCheck;
import ac.grim.grimac.player.GrimPlayer;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerFlying;

// Detects moving outside of the world border
@CheckData(name = "BadPackets J")
public class BadPacketsJ extends PacketCheck {

    private static final double HARD_CODED_BORDER = 2.9999999E7D;

    public BadPacketsJ(GrimPlayer player) {
        super(player);
    }

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        if (WrapperPlayClientPlayerFlying.isFlying(event.getPacketType())) {
            WrapperPlayClientPlayerFlying packet = new WrapperPlayClientPlayerFlying(event);

            // Player just teleported
            if (player.packetStateData.lastPacketWasTeleport) {
                return;
            }

            // Position hasn't changed
            if (!packet.hasPositionChanged()) {
                return;
            }

            // Player location is above the max world border value
            if (Math.abs(packet.getLocation().getX()) > HARD_CODED_BORDER
                    || Math.abs(packet.getLocation().getZ()) > HARD_CODED_BORDER) {
                event.setCancelled(true);
                player.kick(getCheckName(), "Outside Border");
            }
        }
    }
}
