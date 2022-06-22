package ac.grim.grimac.checks.impl.badpackets;

import ac.grim.grimac.checks.CheckData;
import ac.grim.grimac.checks.type.PacketCheck;
import ac.grim.grimac.player.GrimPlayer;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerFlying;

// Detects Dortware's Verus Disabler
@CheckData(name = "BadPackets R")
public class BadPacketsR extends PacketCheck {

    private int streak;

    public BadPacketsR(GrimPlayer player) {
        super(player);
    }

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        if (WrapperPlayClientPlayerFlying.isFlying(event.getPacketType())) {
            streak++;

        } else if (event.getPacketType() == PacketType.Play.Client.WINDOW_CONFIRMATION) {
            streak = 0;
        }

        if (streak >= 15) {
            flagAndAlert("Streak: " + streak, false);
        }
    }
}
