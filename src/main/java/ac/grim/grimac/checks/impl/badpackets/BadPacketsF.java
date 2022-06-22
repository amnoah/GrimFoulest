package ac.grim.grimac.checks.impl.badpackets;

import ac.grim.grimac.checks.CheckData;
import ac.grim.grimac.checks.type.PacketCheck;
import ac.grim.grimac.player.GrimPlayer;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientInteractEntity;

// Detects attacking entities without swinging
@CheckData(name = "BadPackets H")
public class BadPacketsF extends PacketCheck {

    private int streak;

    public BadPacketsF(GrimPlayer player) {
        super(player);
    }

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        if (event.getPacketType() == PacketType.Play.Client.INTERACT_ENTITY) {
            WrapperPlayClientInteractEntity packet = new WrapperPlayClientInteractEntity(event);

            if (packet.getAction() != WrapperPlayClientInteractEntity.InteractAction.ATTACK) {
                return;
            }

            if (++streak > 2) {
                event.setCancelled(true);
                player.kick(getCheckName(), "NoSwing");
            }

        } else if (event.getPacketType() == PacketType.Play.Client.ANIMATION) {
            streak = 0;
        }
    }
}
