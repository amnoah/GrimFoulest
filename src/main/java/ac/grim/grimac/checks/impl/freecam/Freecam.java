package ac.grim.grimac.checks.impl.freecam;

import ac.grim.grimac.checks.CheckData;
import ac.grim.grimac.checks.type.PacketCheck;
import ac.grim.grimac.player.GrimPlayer;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerFlying;

// Detects Freecam & Blink
@CheckData(name = "Freecam")
public class Freecam extends PacketCheck {

    private int streak;

    public Freecam(GrimPlayer player) {
        super(player);
    }

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        if (event.getPacketType() == PacketType.Play.Client.WINDOW_CONFIRMATION) {
            streak++;

        } else if (WrapperPlayClientPlayerFlying.isFlying(event.getPacketType())) {
            streak = 0;

        } else if (event.getPacketType() == PacketType.Play.Client.KEEP_ALIVE) {
            if (streak >= 10) {
                flagAndAlert("Streak: " + streak, false);
            }
        }
    }
}
