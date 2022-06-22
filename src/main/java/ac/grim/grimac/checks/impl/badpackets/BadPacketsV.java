package ac.grim.grimac.checks.impl.badpackets;

import ac.grim.grimac.checks.CheckData;
import ac.grim.grimac.checks.type.PacketCheck;
import ac.grim.grimac.player.GrimPlayer;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerFlying;

// Detects sending packets en-masse
@CheckData(name = "BadPackets V")
public class BadPacketsV extends PacketCheck {

    private int streak;

    public BadPacketsV(GrimPlayer player) {
        super(player);
    }

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        if (!WrapperPlayClientPlayerFlying.isFlying(event.getPacketType())
                && event.getPacketType() != PacketType.Play.Client.WINDOW_CONFIRMATION
                && event.getPacketType() != PacketType.Play.Client.PLUGIN_MESSAGE) {
            streak++;

        } else if (WrapperPlayClientPlayerFlying.isFlying(event.getPacketType())) {
            streak = 0;
        }

        if (streak >= 4) {
            flagAndAlert("Streak: " + streak
                    + " Packet: " + event.getPacketType().getName(), false);
        }
    }
}
