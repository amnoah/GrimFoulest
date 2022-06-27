package ac.grim.grimac.checks.impl.combat.autoheal;

import ac.grim.grimac.checks.CheckData;
import ac.grim.grimac.checks.type.PacketCheck;
import ac.grim.grimac.player.GrimPlayer;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.world.BlockFace;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientHeldItemChange;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerBlockPlacement;

// Detects Generic AutoHeal
@CheckData(name = "AutoHeal G")
public class AutoHealG extends PacketCheck {

    private int stage;
    private int lastSlot = 0;
    private int lastLastSlot = 0;

    public AutoHealG(GrimPlayer player) {
        super(player);
    }

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        if (event.getPacketType() == PacketType.Play.Client.HELD_ITEM_CHANGE) {
            WrapperPlayClientHeldItemChange packet = new WrapperPlayClientHeldItemChange(event);

            if (stage == 0) {
                ++stage;
            }

            if (stage == 2) {
                if (packet.getSlot() == lastLastSlot) {
                    ++stage;
                    flagAndAlert("", false);
                }
            }

            lastLastSlot = lastSlot;
            lastSlot = packet.getSlot();

        } else if (event.getPacketType() == PacketType.Play.Client.PLAYER_BLOCK_PLACEMENT) {
            WrapperPlayClientPlayerBlockPlacement packet = new WrapperPlayClientPlayerBlockPlacement(event);

            if (packet.getFace() == BlockFace.OTHER) {
                if (stage == 1) {
                    ++stage;
                }
            }

        } else if (event.getPacketType() == PacketType.Play.Client.PLAYER_FLYING
                || event.getPacketType() == PacketType.Play.Client.PLAYER_POSITION
                || event.getPacketType() == PacketType.Play.Client.PLAYER_POSITION_AND_ROTATION) {
            stage = 0;
        }

        if (stage == 3) {
            event.setCancelled(true);
            player.kick(getCheckName(), "", "You are sending too many packets!");
        }
    }
}
