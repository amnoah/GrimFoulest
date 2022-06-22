package ac.grim.grimac.checks.impl.autoheal;

import ac.grim.grimac.checks.CheckData;
import ac.grim.grimac.checks.type.PacketCheck;
import ac.grim.grimac.player.GrimPlayer;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.player.DiggingAction;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientClickWindow;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerDigging;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerFlying;

// Detects FDPClient's AutoSoup
@CheckData(name = "AutoHeal F")
public class AutoHealF extends PacketCheck {

    private int streak;

    public AutoHealF(GrimPlayer player) {
        super(player);
    }

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        if (event.getPacketType() == PacketType.Play.Client.HELD_ITEM_CHANGE) { // Streak: 1, 3
            if (streak == 0 || streak == 2) {
                ++streak;
            }

        } else if (event.getPacketType() == PacketType.Play.Client.PLAYER_DIGGING) { // Streak: 2
            WrapperPlayClientPlayerDigging packet = new WrapperPlayClientPlayerDigging(event);

            if (packet.getAction() == DiggingAction.DROP_ITEM) {
                if (streak == 1) {
                    ++streak;
                }
            }

        } else if (WrapperPlayClientPlayerFlying.isFlying(event.getPacketType())) { // Reset Streak
            streak = 0;
        }

        if (streak == 3) {
            flagAndAlert("", false);
        }
    }
}
