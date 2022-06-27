package ac.grim.grimac.checks.impl.inventory;

import ac.grim.grimac.checks.CheckData;
import ac.grim.grimac.checks.type.PacketCheck;
import ac.grim.grimac.player.GrimPlayer;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientClickWindow;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientInteractEntity;

// Detects Dortware's InfiniteDurability
@CheckData(name = "Inventory E")
public class InventoryE extends PacketCheck {

    private int streak;

    public InventoryE(GrimPlayer player) {
        super(player);
    }

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        if (event.getPacketType() == PacketType.Play.Client.ANIMATION) {
            if (streak == 0) {
                ++streak;
            }

        } else if (event.getPacketType() == PacketType.Play.Client.CLICK_WINDOW) {
            WrapperPlayClientClickWindow packet = new WrapperPlayClientClickWindow(event);

            if (packet.getWindowId() == 0
                    && packet.getWindowClickType() == WrapperPlayClientClickWindow.WindowClickType.SWAP) {
                if (streak == 1 || streak == 3) {
                    ++streak;
                }
            }

        } else if (event.getPacketType() == PacketType.Play.Client.INTERACT_ENTITY) {
            WrapperPlayClientInteractEntity packet = new WrapperPlayClientInteractEntity(event);

            if (packet.getAction() == WrapperPlayClientInteractEntity.InteractAction.ATTACK) {
                if (streak == 2) {
                    ++streak;
                }
            }

        } else if (event.getPacketType() == PacketType.Play.Client.PLAYER_FLYING
                || event.getPacketType() == PacketType.Play.Client.PLAYER_POSITION
                || event.getPacketType() == PacketType.Play.Client.PLAYER_POSITION_AND_ROTATION) {
            streak = 0;
        }

        if (streak == 4) {
            event.setCancelled(true);
            player.kick(getCheckName(), "Infinite Durability", "You are sending too many packets!");
        }
    }
}
