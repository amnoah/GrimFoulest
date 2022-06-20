package ac.grim.grimac.checks.impl.badpackets;

import ac.grim.grimac.checks.CheckData;
import ac.grim.grimac.checks.type.PacketCheck;
import ac.grim.grimac.player.GrimPlayer;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerFlying;

@CheckData(name = "BadPackets S")
public class BadPacketsS extends PacketCheck {

    public BadPacketsS(GrimPlayer player) {
        super(player);
    }

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        if (WrapperPlayClientPlayerFlying.isFlying(event.getPacketType())) {
            WrapperPlayClientPlayerFlying packet = new WrapperPlayClientPlayerFlying(event);
            double posY = packet.getLocation().getY();
            boolean hasPos = packet.hasPositionChanged();
            boolean hasLook = packet.hasRotationChanged();
            boolean isOnGround = packet.isOnGround();

            if (player.packetStateData.lastPacketWasTeleport) {
                return;
            }

            boolean calcOnGround = (player.onGround || player.lastOnGround) && (posY % 1.0 == 0.1665803780930375D
                    || posY % 1.0 == 0.5D || posY % 1.0 == 0.015625D || posY % 1.0 == 0.0625D || posY % 1.0 == 0.0D
                    || posY % 1.0 == 0.09375D || posY % 1.0 == 0.1875D);

            if (!hasPos && !hasLook && isOnGround != calcOnGround) {
                flagAndAlert("Flying On Ground (Y: " + posY % 1.0D + ")");
            }
        }
    }
}
