package ac.grim.grimac.checks.impl.movement;

import ac.grim.grimac.checks.type.PacketCheck;
import ac.grim.grimac.player.GrimPlayer;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.util.Vector3d;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerFlying;

public class SetbackBlocker extends PacketCheck {

    public SetbackBlocker(GrimPlayer playerData) {
        super(playerData);
    }

    public void onPacketReceive(PacketReceiveEvent event) {
        if (player.disableGrim) {
            return;
        }

        // Player is in a vehicle
        if (event.getPacketType() == PacketType.Play.Client.INTERACT_ENTITY) {
            if (player.getSetbackTeleportUtil().cheatVehicleInterpolationDelay > 0) {
                event.setCancelled(true);
            }
        }

        // Don't block teleport packets
        if (player.packetStateData.lastPacketWasTeleport) {
            return;
        }

        if (WrapperPlayClientPlayerFlying.isFlying(event.getPacketType())) {
            // The player must obey setbacks
            if (player.getSetbackTeleportUtil().shouldBlockMovement()) {
                event.setCancelled(true);
            }

            // Look is the only valid packet to send while in a vehicle
            if (player.compensatedEntities.getSelf().inVehicle() && event.getPacketType() != PacketType.Play.Client.PLAYER_ROTATION && !player.packetStateData.lastPacketWasTeleport) {
                event.setCancelled(true);
            }

            // The player is sleeping, should be safe to block position packets
            if (player.isInBed && new Vector3d(player.x, player.y, player.z).distanceSquared(player.bedPosition) > 1) {
                event.setCancelled(true);
            }

            // Player is dead
            if (player.compensatedEntities.getSelf().isDead) {
                event.setCancelled(true);
            }
        }

        if (event.getPacketType() == PacketType.Play.Client.VEHICLE_MOVE) {
            if (player.getSetbackTeleportUtil().shouldBlockMovement()) {
                event.setCancelled(true);
            }

            // Don't let a player move a vehicle when not in a vehicle
            if (!player.compensatedEntities.getSelf().inVehicle()) {
                event.setCancelled(true);
            }

            // A player is sleeping while in a vehicle
            if (player.isInBed) {
                event.setCancelled(true);
            }

            // Player is dead
            if (player.compensatedEntities.getSelf().isDead) {
                event.setCancelled(true);
            }
        }
    }
}
