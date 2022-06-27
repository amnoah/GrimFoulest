package ac.grim.grimac.checks.impl.inventory;

import ac.grim.grimac.checks.CheckData;
import ac.grim.grimac.checks.type.PacketCheck;
import ac.grim.grimac.player.GrimPlayer;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.*;

// Detects FDPClient's InvCleaner
@CheckData(name = "Inventory A")
public class InventoryA extends PacketCheck {

    private int streak;

    public InventoryA(GrimPlayer player) {
        super(player);
    }

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        if (event.getPacketType() == PacketType.Play.Client.CLIENT_STATUS) {
            WrapperPlayClientClientStatus packet = new WrapperPlayClientClientStatus(event);

            if (packet.getAction() == WrapperPlayClientClientStatus.Action.OPEN_INVENTORY_ACHIEVEMENT) {
                if (streak == 0) {
                    ++streak;
                }
            }

        } else if (event.getPacketType() == PacketType.Play.Client.CLICK_WINDOW) {
            WrapperPlayClientClickWindow packet = new WrapperPlayClientClickWindow(event);
            WrapperPlayClientClickWindow.WindowClickType clickType = packet.getWindowClickType();

            if (packet.getWindowId() == 0 && clickType == WrapperPlayClientClickWindow.WindowClickType.QUICK_MOVE) {
                if (streak == 1) {
                    ++streak;
                }
            }

        } else if (event.getPacketType() == PacketType.Play.Client.CLOSE_WINDOW) {
            WrapperPlayClientCloseWindow packet = new WrapperPlayClientCloseWindow(event);

            if (packet.getWindowId() == 0) {
                if (streak == 2) {
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
