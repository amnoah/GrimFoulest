package ac.grim.grimac.checks.impl.badpackets;

import ac.grim.grimac.checks.CheckData;
import ac.grim.grimac.checks.type.PacketCheck;
import ac.grim.grimac.player.GrimPlayer;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.event.ProtocolPacketEvent;
import com.github.retrooper.packetevents.protocol.item.ItemStack;
import com.github.retrooper.packetevents.protocol.item.type.ItemTypes;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.player.DiggingAction;
import com.github.retrooper.packetevents.protocol.world.BlockFace;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerBlockPlacement;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerDigging;

// Detects sending invalid PLACE and RELEASE order
@CheckData(name = "BadPackets L")
public class BadPacketsL extends PacketCheck {

    public int placeCount;
    public int releaseCount;

    public BadPacketsL(GrimPlayer player) {
        super(player);
    }

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        if (event.getPacketType() == PacketType.Play.Client.PLAYER_BLOCK_PLACEMENT) {
            WrapperPlayClientPlayerBlockPlacement packet = new WrapperPlayClientPlayerBlockPlacement(event);

            if (!packet.getItemStack().isPresent()) {
                return;
            }

            ItemStack itemStack = packet.getItemStack().get();

            if (packet.getFace() == BlockFace.OTHER && (itemStack.getType() == ItemTypes.BOW
                    || itemStack.getType().hasAttribute(ItemTypes.ItemAttribute.SWORD)
                    || itemStack.getType() == ItemTypes.GOLDEN_APPLE
                    || itemStack.getType() == ItemTypes.ENCHANTED_GOLDEN_APPLE
                    || (itemStack.getType().hasAttribute(ItemTypes.ItemAttribute.EDIBLE) && player.food < 20))) {
                ++placeCount;
                releaseCount = 0;
                check(event);
            }

        } else if (event.getPacketType() == PacketType.Play.Client.PLAYER_DIGGING) {
            WrapperPlayClientPlayerDigging packet = new WrapperPlayClientPlayerDigging(event);

            if (packet.getAction() == DiggingAction.RELEASE_USE_ITEM) {
                ++releaseCount;
                placeCount = 0;
                check(event);
            }
        }
    }

    // Detects sending multiple PLACE or RELEASE packets in a row.
    public void check(ProtocolPacketEvent<Object> event) {
        if (placeCount > 3) {
            player.kick(getCheckName(), event, "PLACE=" + placeCount);
        }

        if (releaseCount > 1) {
            player.kick(getCheckName(), event, "RELEASE=" + releaseCount);
        }
    }
}
