package ac.grim.grimac.checks.impl.badpackets.packetorder;

import ac.grim.grimac.checks.CheckData;
import ac.grim.grimac.checks.type.PacketCheck;
import ac.grim.grimac.player.GrimPlayer;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.player.DiggingAction;
import com.github.retrooper.packetevents.protocol.world.BlockFace;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerBlockPlacement;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerDigging;

// Detects sending RELEASE_USE_ITEM without BLOCK_PLACE
@CheckData(name = "PacketOrder B")
public class PacketOrderB extends PacketCheck {

    private boolean sent;

    public PacketOrderB(GrimPlayer player) {
        super(player);
    }

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        if (event.getPacketType() == PacketType.Play.Client.PLAYER_BLOCK_PLACEMENT) {
            WrapperPlayClientPlayerBlockPlacement packet = new WrapperPlayClientPlayerBlockPlacement(event);

            if (packet.getFace() == BlockFace.OTHER) {
                sent = true;
            }

        } else if (event.getPacketType() == PacketType.Play.Client.PLAYER_DIGGING) {
            WrapperPlayClientPlayerDigging packet = new WrapperPlayClientPlayerDigging(event);

            if (packet.getAction() == DiggingAction.RELEASE_USE_ITEM && !sent) {
                event.setCancelled(true);
                player.kick(getCheckName(), "", "You are sending too many packets!");
            }

            sent = false;
        }
    }
}
