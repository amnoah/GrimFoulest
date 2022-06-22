package ac.grim.grimac.checks.impl.badpackets;

import ac.grim.grimac.checks.CheckData;
import ac.grim.grimac.checks.type.PacketCheck;
import ac.grim.grimac.player.GrimPlayer;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerFlying;

// Detects ETB 0.1's SkinFlash
@CheckData(name = "BadPackets T")
public class BadPacketsT extends PacketCheck {

    private int streak;

    public BadPacketsT(GrimPlayer player) {
        super(player);
    }

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        if (event.getPacketType() == PacketType.Play.Client.CLIENT_SETTINGS) {
            streak++;

        } else if (WrapperPlayClientPlayerFlying.isFlying(event.getPacketType())) {
            streak = 0;
        }

        if (streak >= 3) {
            flagAndAlert("Streak: " + streak, false);
        }
    }
}
