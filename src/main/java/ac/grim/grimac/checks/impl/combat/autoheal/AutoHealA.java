package ac.grim.grimac.checks.impl.combat.autoheal;

import ac.grim.grimac.checks.CheckData;
import ac.grim.grimac.checks.type.PacketCheck;
import ac.grim.grimac.player.GrimPlayer;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.item.ItemStack;
import com.github.retrooper.packetevents.protocol.item.type.ItemTypes;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.player.DiggingAction;
import com.github.retrooper.packetevents.protocol.world.BlockFace;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerBlockPlacement;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerDigging;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerFlying;

// Detects this AutoHeal pattern:
// SLOT, BLOCK_PLACE, RELEASE_USE_ITEM, SLOT
@CheckData(name = "AutoHeal A")
public class AutoHealA extends PacketCheck {

    public long start;
    public int stage;

    public AutoHealA(GrimPlayer player) {
        super(player);
    }

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        if (event.getPacketType() == PacketType.Play.Client.HELD_ITEM_CHANGE) {
            if (stage == 0 || stage == 3) {
                if (stage == 0) {
                    start = System.currentTimeMillis();
                }

                ++stage;
            }

        } else if (event.getPacketType() == PacketType.Play.Client.PLAYER_BLOCK_PLACEMENT) {
            WrapperPlayClientPlayerBlockPlacement packet = new WrapperPlayClientPlayerBlockPlacement(event);

            if (packet.getItemStack().isPresent()) {
                ItemStack itemStack = packet.getItemStack().get();

                if (itemStack.getType() == ItemTypes.SPLASH_POTION
                        || itemStack.getType() == ItemTypes.MUSHROOM_STEW
                        || itemStack.getType() == ItemTypes.BOWL) {
                    if (packet.getFace() == BlockFace.OTHER) {
                        if (stage == 1) {
                            ++stage;
                        }
                    }
                }
            }

        } else if (event.getPacketType() == PacketType.Play.Client.PLAYER_DIGGING) {
            WrapperPlayClientPlayerDigging packet = new WrapperPlayClientPlayerDigging(event);

            if (packet.getAction() == DiggingAction.RELEASE_USE_ITEM) {
                if (stage == 2) {
                    ++stage;
                }
            }

        } else if (event.getPacketType() == PacketType.Play.Client.PLAYER_FLYING) {
            WrapperPlayClientPlayerFlying packet = new WrapperPlayClientPlayerFlying(event);

            if (!packet.hasPositionChanged() && !packet.hasRotationChanged()) {
                stage = 0;
            }
        }

        long timeDiff = System.currentTimeMillis() - start;

        if (stage == 4 && timeDiff < 99) {
            player.kick(getCheckName(), event, "TIME=" + (System.currentTimeMillis() - start));
        }
    }
}
