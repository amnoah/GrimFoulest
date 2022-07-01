package ac.grim.grimac.checks.impl.badpackets;

import ac.grim.grimac.checks.CheckData;
import ac.grim.grimac.checks.type.PacketCheck;
import ac.grim.grimac.player.GrimPlayer;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.player.ClientVersion;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerFlying;

// Detects sending invalid ROTATION packets
@CheckData(name = "BadPackets B")
public class BadPacketsB extends PacketCheck {

    public double lastYaw;
    public double lastPitch;

    public BadPacketsB(GrimPlayer player) {
        super(player);
    }

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        if (WrapperPlayClientPlayerFlying.isFlying(event.getPacketType())) {
            WrapperPlayClientPlayerFlying packet = new WrapperPlayClientPlayerFlying(event);

            // Ignores certain invalid conditions.
            if (player.packetStateData.lastPacketWasTeleport
                    || player.getSetbackTeleportUtil().insideUnloadedChunk()
                    || !packet.hasRotationChanged()) {
                return;
            }

            // Detects sending an invalid pitch.
            if (Math.abs(packet.getLocation().getPitch()) > 90) {
                player.kick(getCheckName(), event, "Invalid Pitch");
                return;
            }

            // Detects sending identical yaw & pitch.
            if (lastYaw == packet.getLocation().getYaw()
                    && lastPitch == packet.getLocation().getPitch()
                    && !player.compensatedEntities.getSelf().inVehicle()
                    && !player.getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_9)) {
                player.kick(getCheckName(), event, "Identical Rotation");
                return;
            }

            lastYaw = packet.getLocation().getYaw();
            lastPitch = packet.getLocation().getPitch();
        }
    }
}
