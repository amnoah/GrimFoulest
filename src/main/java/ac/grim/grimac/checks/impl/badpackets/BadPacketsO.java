package ac.grim.grimac.checks.impl.badpackets;

import ac.grim.grimac.checks.CheckData;
import ac.grim.grimac.checks.type.PacketCheck;
import ac.grim.grimac.player.GrimPlayer;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientClickWindow;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientInteractEntity;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerFlying;

// Detects Dortware's Infinite Durability
@CheckData(name = "BadPackets O")
public class BadPacketsO extends PacketCheck {

    private int streak;

    public BadPacketsO(GrimPlayer player) {
        super(player);
    }

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        if (event.getPacketType() == PacketType.Play.Client.ANIMATION) { // Streak: 1
            if (streak == 0) {
                ++streak;
            }

        } else if (event.getPacketType() == PacketType.Play.Client.CLICK_WINDOW) { // Streak: 2, 4
            WrapperPlayClientClickWindow packet = new WrapperPlayClientClickWindow(event);

            if (packet.getWindowId() == 0
                    && packet.getWindowClickType() == WrapperPlayClientClickWindow.WindowClickType.SWAP) {
                if (streak == 1 || streak == 3) {
                    ++streak;
                }
            }

        } else if (event.getPacketType() == PacketType.Play.Client.INTERACT_ENTITY) { // Streak: 3
            WrapperPlayClientInteractEntity packet = new WrapperPlayClientInteractEntity(event);

            if (packet.getAction() == WrapperPlayClientInteractEntity.InteractAction.ATTACK) {
                if (streak == 2) {
                    ++streak;
                }
            }

        } else if (WrapperPlayClientPlayerFlying.isFlying(event.getPacketType())) {
            streak = 0;
        }

        if (streak == 4) {
            event.setCancelled(true);
            player.kick(getCheckName(), "Infinite Durability");
        }
    }
}
