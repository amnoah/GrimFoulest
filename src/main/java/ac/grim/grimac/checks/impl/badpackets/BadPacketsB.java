package ac.grim.grimac.checks.impl.badpackets;

import ac.grim.grimac.checks.CheckData;
import ac.grim.grimac.checks.type.PacketCheck;
import ac.grim.grimac.player.GrimPlayer;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerFlying;

// Detects sending Rotation packets with an invalid pitch
@CheckData(name = "BadPackets B")
public class BadPacketsB extends PacketCheck {

    public BadPacketsB(GrimPlayer player) {
        super(player);
    }

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        if (player.packetStateData.lastPacketWasTeleport) {
            return;
        }

        if (WrapperPlayClientPlayerFlying.isFlying(event.getPacketType())) {
            WrapperPlayClientPlayerFlying packet = new WrapperPlayClientPlayerFlying(event);

            if (packet.hasRotationChanged() && Math.abs(packet.getLocation().getPitch()) > 90) {
                event.setCancelled(true);
                player.kick(getCheckName(), "Invalid Pitch", "Illegal position");
            }
        }
    }
}
