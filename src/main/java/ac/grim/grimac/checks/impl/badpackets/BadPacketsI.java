package ac.grim.grimac.checks.impl.badpackets;

import ac.grim.grimac.checks.CheckData;
import ac.grim.grimac.checks.type.PacketCheck;
import ac.grim.grimac.player.GrimPlayer;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerFlying;

// Detects sending same yaw & pitch twice
@CheckData(name = "BadPackets I")
public class BadPacketsI extends PacketCheck {

    private double lastYaw;
    private double lastPitch;

    public BadPacketsI(GrimPlayer player) {
        super(player);
    }

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        if (WrapperPlayClientPlayerFlying.isFlying(event.getPacketType())) {
            WrapperPlayClientPlayerFlying packet = new WrapperPlayClientPlayerFlying(event);
            float posYaw = packet.getLocation().getYaw();
            float posPitch = packet.getLocation().getPitch();

            // Player is inside unloaded chunk
            if (player.getSetbackTeleportUtil().insideUnloadedChunk()) {
                return;
            }

            // Player just teleported
            if (player.packetStateData.lastPacketWasTeleport) {
                return;
            }

            // Player is in vehicle
            if (player.compensatedEntities.getSelf().inVehicle()) {
                return;
            }

            // Rotation hasn't changed
            if (!packet.hasRotationChanged()) {
                return;
            }

            // lastYaw and lastPitch are identical
            if (lastYaw == posYaw && lastPitch == posPitch) {
                event.setCancelled(true);
                player.kick(getCheckName(), "Identical Rotation");
                return;
            }

            lastYaw = posYaw;
            lastPitch = posPitch;
        }
    }
}
