package ac.grim.grimac.checks.impl.badpackets;

import ac.grim.grimac.checks.CheckData;
import ac.grim.grimac.checks.type.PacketCheck;
import ac.grim.grimac.player.GrimPlayer;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientSteerVehicle;

// Detects sending invalid STEER_VEHICLE packets
@CheckData(name = "BadPackets D")
public class BadPacketsD extends PacketCheck {

    public BadPacketsD(GrimPlayer player) {
        super(player);
    }

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        if (event.getPacketType() == PacketType.Play.Client.STEER_VEHICLE) {
            WrapperPlayClientSteerVehicle packet = new WrapperPlayClientSteerVehicle(event);
            float forwards = Math.abs(packet.getForward());
            float sideways = Math.abs(packet.getSideways());

            // Detects sending packets while not being in a vehicle.
            if (!player.compensatedEntities.getSelf().inVehicle()) {
                player.kick(getCheckName(), event, "Not In Vehicle");
                return;
            }

            // Detects sending invalid forwards and sideways values.
            if (forwards > 0.9800000190734863 || sideways > 0.9800000190734863) {
                player.kick(getCheckName(), event, "Vehicle Steer");
            }
        }
    }
}
