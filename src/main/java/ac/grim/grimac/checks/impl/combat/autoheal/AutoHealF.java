package ac.grim.grimac.checks.impl.combat.autoheal;

import ac.grim.grimac.checks.CheckData;
import ac.grim.grimac.checks.type.PacketCheck;
import ac.grim.grimac.player.GrimPlayer;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.player.DiggingAction;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerDigging;

// Detects LiquidBounce's AutoHeal
@CheckData(name = "AutoHeal F")
public class AutoHealF extends PacketCheck {

    private int streak;

    public AutoHealF(GrimPlayer player) {
        super(player);
    }

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        if (event.getPacketType() == PacketType.Play.Client.HELD_ITEM_CHANGE) {
            if (streak == 0 || streak == 2) {
                ++streak;
            }

        } else if (event.getPacketType() == PacketType.Play.Client.PLAYER_DIGGING) {
            WrapperPlayClientPlayerDigging packet = new WrapperPlayClientPlayerDigging(event);

            if (packet.getAction() == DiggingAction.DROP_ITEM) {
                if (streak == 1) {
                    ++streak;
                }
            }

        } else if (event.getPacketType() == PacketType.Play.Client.PLAYER_FLYING
                || event.getPacketType() == PacketType.Play.Client.PLAYER_POSITION
                || event.getPacketType() == PacketType.Play.Client.PLAYER_POSITION_AND_ROTATION) {
            streak = 0;
        }

        if (streak == 3) {
            event.setCancelled(true);
            player.kick(getCheckName(), "", "You are sending too many packets!");
        }
    }
}
