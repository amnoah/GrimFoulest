package ac.grim.grimac.checks.impl.badpackets.pingspoof;

import ac.grim.grimac.checks.CheckData;
import ac.grim.grimac.checks.type.PacketCheck;
import ac.grim.grimac.player.GrimPlayer;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerFlying;

// Detects delaying the Position idle packet
@CheckData(name = "PingSpoof F")
public class PingSpoofF extends PacketCheck {

    private int noReminderTicks;

    public PingSpoofF(GrimPlayer player) {
        super(player);
    }

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        if (WrapperPlayClientPlayerFlying.isFlying(event.getPacketType())) {
            WrapperPlayClientPlayerFlying packet = new WrapperPlayClientPlayerFlying(event);

            if (packet.hasPositionChanged()) {
                noReminderTicks = 0;
            } else {
                noReminderTicks++;
            }

        } else if (event.getPacketType() == PacketType.Play.Client.STEER_VEHICLE) {
            noReminderTicks = 0; // Exempt vehicles
        }

        if (noReminderTicks > 20) {
            player.kick(getCheckName(), event, "POSITION");
        }
    }
}
