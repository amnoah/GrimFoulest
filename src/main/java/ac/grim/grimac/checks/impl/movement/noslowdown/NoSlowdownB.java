package ac.grim.grimac.checks.impl.movement.noslowdown;

import ac.grim.grimac.checks.CheckData;
import ac.grim.grimac.checks.type.PacketCheck;
import ac.grim.grimac.player.GrimPlayer;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.player.DiggingAction;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerDigging;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerFlying;

@CheckData(name = "NoSlowdown B", setback = 5)
public class NoSlowdownB extends PacketCheck {

    private int streak;

    public NoSlowdownB(GrimPlayer player) {
        super(player);
    }

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        if (event.getPacketType() == PacketType.Play.Client.PLAYER_DIGGING) {
            WrapperPlayClientPlayerDigging packet = new WrapperPlayClientPlayerDigging(event);

            if (packet.getAction() == DiggingAction.RELEASE_USE_ITEM) {
                if (streak % 2 == 0) {
                    streak++;
                } else {
                    streak = 0;
                }
            }

        } else if (WrapperPlayClientPlayerFlying.isFlying(event.getPacketType())) {
            if (streak % 2 != 0) {
                streak++;
            } else {
                streak = 0;
            }
        }

        if (streak > 4) {
            flagAndAlert("Streak: " + streak, true);
        }
    }
}
