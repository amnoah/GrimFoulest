package ac.grim.grimac.checks.impl.badpackets.packetorder;

import ac.grim.grimac.checks.CheckData;
import ac.grim.grimac.checks.type.PacketCheck;
import ac.grim.grimac.player.GrimPlayer;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.player.DiggingAction;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerDigging;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerFlying;

// Detects swinging after destroying a block in the same tick
@CheckData(name = "PacketOrder C")
public class PacketOrderC extends PacketCheck {

    private boolean sent;

    public PacketOrderC(GrimPlayer player) {
        super(player);
    }

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        if (event.getPacketType() == PacketType.Play.Client.ANIMATION) {
            sent = true;

        } else if (event.getPacketType() == PacketType.Play.Client.PLAYER_DIGGING) {
            WrapperPlayClientPlayerDigging packet = new WrapperPlayClientPlayerDigging(event);

            if (packet.getAction() == DiggingAction.FINISHED_DIGGING && !sent) {
                event.setCancelled(true);
                player.kick(getCheckName(), "", "You are sending too many packets!");
            }

        } else if (WrapperPlayClientPlayerFlying.isFlying(event.getPacketType())) {
            sent = false;
        }
    }
}
