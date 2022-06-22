package ac.grim.grimac.checks.impl.inventory;

import ac.grim.grimac.checks.CheckData;
import ac.grim.grimac.checks.type.PacketCheck;
import ac.grim.grimac.player.GrimPlayer;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientClickWindow;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerFlying;

// Detects Envy's InvCleaner specifically
@CheckData(name = "Inventory J")
public class InventoryJ extends PacketCheck {

    private int streak;

    public InventoryJ(GrimPlayer player) {
        super(player);
    }

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        if (event.getPacketType() == PacketType.Play.Client.CLICK_WINDOW) { // Sets Streak
            WrapperPlayClientClickWindow packet = new WrapperPlayClientClickWindow(event);
            WrapperPlayClientClickWindow.WindowClickType clickType = packet.getWindowClickType();

            if (packet.getWindowId() == 0 && clickType == WrapperPlayClientClickWindow.WindowClickType.PICKUP) {
                if (packet.getSlot() != -999) {
                    if (streak == 0) {
                        ++streak;
                    }
                } else {
                    if (streak == 1) {
                        ++streak;
                    }
                }
            }

        } else if (WrapperPlayClientPlayerFlying.isFlying(event.getPacketType())) { // Reset Streak
            streak = 0;
        }

        if (streak == 2) {
            event.setCancelled(true);
            player.kick(getCheckName(), "");
        }
    }
}
