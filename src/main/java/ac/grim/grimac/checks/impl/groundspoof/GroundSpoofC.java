package ac.grim.grimac.checks.impl.groundspoof;

import ac.grim.grimac.checks.CheckData;
import ac.grim.grimac.checks.type.PacketCheck;
import ac.grim.grimac.player.GrimPlayer;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerFlying;

@CheckData(name = "GroundSpoof C")
public class GroundSpoofC extends PacketCheck {

    public GroundSpoofC(GrimPlayer player) {
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

            // Player is inside unloaded chunk
            if (player.getSetbackTeleportUtil().insideUnloadedChunk()) {
                return;
            }

            // Player just teleported
            if (player.packetStateData.lastPacketWasTeleport) {
                return;
            }

            // Player was on a ghost block
            if (player.getSetbackTeleportUtil().blockOffsets) {
                return;
            }

            boolean calcOnGround = (player.onGround || player.lastOnGround) && (posY % 1.0 == 0.1665803780930375
                    || posY % 1.0 == 0.5 || posY % 1.0 == 0.015625 || posY % 1.0 == 0.0625 || posY % 1.0 == 0.0
                    || posY % 1.0 == 0.09375 || posY % 1.0 == 0.1875);

            // TODO: False flags 0.0 when player stuck inside block / recently teleported
            if (!hasPos && !hasLook && isOnGround != calcOnGround) {
                flagAndAlert("(Y: " + posY % 1.0D + ")", true);
            }
        }
    }
}
