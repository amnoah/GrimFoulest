package ac.grim.grimac.checks.impl.autoheal;

import ac.grim.grimac.checks.CheckData;
import ac.grim.grimac.checks.type.PacketCheck;
import ac.grim.grimac.player.GrimPlayer;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.player.DiggingAction;
import com.github.retrooper.packetevents.wrapper.play.client.*;

// Detects FDPClient's SimulateInventory
@CheckData(name = "AutoHeal G")
public class AutoHealG extends PacketCheck {

    private int streak;

    public AutoHealG(GrimPlayer player) {
        super(player);
    }

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        if (event.getPacketType() == PacketType.Play.Client.CLIENT_STATUS) { // Streak: 1
            WrapperPlayClientClientStatus packet = new WrapperPlayClientClientStatus(event);

            if (packet.getAction() == WrapperPlayClientClientStatus.Action.OPEN_INVENTORY_ACHIEVEMENT) {
                if (streak == 0) {
                    ++streak;
                }
            }

        } else if (event.getPacketType() == PacketType.Play.Client.CLICK_WINDOW) { // Streak: 2
            WrapperPlayClientClickWindow packet = new WrapperPlayClientClickWindow(event);
            WrapperPlayClientClickWindow.WindowClickType clickType = packet.getWindowClickType();

            if (packet.getWindowId() == 0 && clickType == WrapperPlayClientClickWindow.WindowClickType.QUICK_MOVE) {
                if (streak == 1) {
                    ++streak;
                }
            }

        } else if (event.getPacketType() == PacketType.Play.Client.CLOSE_WINDOW) { // Streak: 3
            WrapperPlayClientCloseWindow packet = new WrapperPlayClientCloseWindow(event);

            if (packet.getWindowId() == 0) {
                if (streak == 2) {
                    ++streak;
                }
            }

        } else if (WrapperPlayClientPlayerFlying.isFlying(event.getPacketType())) { // Reset Streak
            streak = 0;
        }

        if (streak == 3) {
            event.setCancelled(true);
            player.kick(getCheckName(), "");
        }
    }
}
