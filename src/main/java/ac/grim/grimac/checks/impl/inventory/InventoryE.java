package ac.grim.grimac.checks.impl.inventory;

import ac.grim.grimac.checks.CheckData;
import ac.grim.grimac.checks.type.PacketCheck;
import ac.grim.grimac.player.GrimPlayer;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientClickWindow;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerFlying;

// Detects Serene's AutoPot & AutoSoup
@CheckData(name = "Inventory E")
public class InventoryE extends PacketCheck {

    private int streak;

    public InventoryE(GrimPlayer player) {
        super(player);
    }

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        if (event.getPacketType() == PacketType.Play.Client.CLICK_WINDOW) { // Streak: 1
            WrapperPlayClientClickWindow packet = new WrapperPlayClientClickWindow(event);
            WrapperPlayClientClickWindow.WindowClickType clickType = packet.getWindowClickType();

            if (packet.getWindowId() == 0 && clickType == WrapperPlayClientClickWindow.WindowClickType.SWAP) {
                if (streak == 0) {
                    ++streak;
                }
            }

        } else if (event.getPacketType() == PacketType.Play.Client.HELD_ITEM_CHANGE) { // Streak: 2, 3
            if (streak == 1 || streak == 2) {
                ++streak;
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
