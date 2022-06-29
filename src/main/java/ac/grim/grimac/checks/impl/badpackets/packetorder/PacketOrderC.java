package ac.grim.grimac.checks.impl.badpackets.packetorder;

import ac.grim.grimac.checks.CheckData;
import ac.grim.grimac.checks.type.PacketCheck;
import ac.grim.grimac.player.GrimPlayer;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientInteractEntity;

// Detects sending INTERACT_ENTITY (ATTACK) without ANIMATION
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

        } else if (event.getPacketType() == PacketType.Play.Client.INTERACT_ENTITY) {
            WrapperPlayClientInteractEntity packet = new WrapperPlayClientInteractEntity(event);

            if (packet.getAction() == WrapperPlayClientInteractEntity.InteractAction.ATTACK && !sent) {
                player.kick(getCheckName(), event, "");
            }

            sent = false;
        }
    }
}
