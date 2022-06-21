package ac.grim.grimac.checks.impl.badpackets;

import ac.grim.grimac.checks.CheckData;
import ac.grim.grimac.checks.type.PacketCheck;
import ac.grim.grimac.player.GrimPlayer;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerFlying;

@CheckData(name = "BadPackets N")
public class BadPacketsN extends PacketCheck {

    private static final double HARD_CODED_BORDER = 2.9999999E7D;

    public BadPacketsN(GrimPlayer player) {
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

            // Player location is above max value (max world border)
            if (Math.abs(packet.getLocation().getX()) > HARD_CODED_BORDER
                    || Math.abs(packet.getLocation().getZ()) > HARD_CODED_BORDER) {
                flagAndAlert("Max Value");
            }
        }
    }
}
