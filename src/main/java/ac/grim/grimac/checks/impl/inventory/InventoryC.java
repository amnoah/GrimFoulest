package ac.grim.grimac.checks.impl.inventory;

import ac.grim.grimac.checks.CheckData;
import ac.grim.grimac.checks.type.PacketCheck;
import ac.grim.grimac.player.GrimPlayer;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientClickWindow;

// Detects Envy's InvCleaner
// Detects ETB 0.1's InvCleaner
@CheckData(name = "Inventory C")
public class InventoryC extends PacketCheck {

    private int streak;

    public InventoryC(GrimPlayer player) {
        super(player);
    }

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        if (event.getPacketType() == PacketType.Play.Client.CLICK_WINDOW) {
            WrapperPlayClientClickWindow packet = new WrapperPlayClientClickWindow(event);
            WrapperPlayClientClickWindow.WindowClickType clickType = packet.getWindowClickType();

            if (packet.getWindowId() == 0 && clickType == WrapperPlayClientClickWindow.WindowClickType.PICKUP) {
                ++streak;
            }

        } else if (event.getPacketType() == PacketType.Play.Client.PLAYER_FLYING
                || event.getPacketType() == PacketType.Play.Client.PLAYER_POSITION
                || event.getPacketType() == PacketType.Play.Client.PLAYER_POSITION_AND_ROTATION) {
            streak = 0;
        }

        if (streak == 2) {
            event.setCancelled(true);
            player.kick(getCheckName(), "", "You are sending too many packets!");
        }
    }
}
