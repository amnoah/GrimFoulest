package ac.grim.grimac.checks.impl.combat.autoheal;

import ac.grim.grimac.checks.CheckData;
import ac.grim.grimac.checks.type.PacketCheck;
import ac.grim.grimac.player.GrimPlayer;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.world.BlockFace;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientClickWindow;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerBlockPlacement;

// Detects Serene's AutoHeal
// Detects Lite's AutoHeal
@CheckData(name = "AutoHeal E")
public class AutoHealE extends PacketCheck {

    private int stage;

    public AutoHealE(GrimPlayer player) {
        super(player);
    }

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        if (event.getPacketType() == PacketType.Play.Client.CLICK_WINDOW) {
            WrapperPlayClientClickWindow packet = new WrapperPlayClientClickWindow(event);
            WrapperPlayClientClickWindow.WindowClickType clickType = packet.getWindowClickType();

            if (packet.getWindowId() == 0 && clickType == WrapperPlayClientClickWindow.WindowClickType.SWAP) {
                if (stage == 0) {
                    ++stage;
                }
            }

        } else if (event.getPacketType() == PacketType.Play.Client.HELD_ITEM_CHANGE) {
            if (stage == 1 || stage == 3) {
                ++stage;
            }

        } else if (event.getPacketType() == PacketType.Play.Client.PLAYER_BLOCK_PLACEMENT) {
            WrapperPlayClientPlayerBlockPlacement packet = new WrapperPlayClientPlayerBlockPlacement(event);

            if (packet.getFace() == BlockFace.OTHER) {
                if (stage == 2) {
                    ++stage;
                }
            }

        } else if (event.getPacketType() == PacketType.Play.Client.PLAYER_FLYING
                || event.getPacketType() == PacketType.Play.Client.PLAYER_POSITION
                || event.getPacketType() == PacketType.Play.Client.PLAYER_POSITION_AND_ROTATION) {
            stage = 0;
        }

        if (stage == 4) {
            event.setCancelled(true);
            player.kick(getCheckName(), "", "You are sending too many packets!");
        }
    }
}
