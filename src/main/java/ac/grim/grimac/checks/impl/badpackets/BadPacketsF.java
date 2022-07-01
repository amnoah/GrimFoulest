package ac.grim.grimac.checks.impl.badpackets;

import ac.grim.grimac.checks.CheckData;
import ac.grim.grimac.checks.type.PacketCheck;
import ac.grim.grimac.player.GrimPlayer;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientInteractEntity;

// Detects sending invalid INTERACT_ENTITY packets
@CheckData(name = "BadPackets F")
public class BadPacketsF extends PacketCheck {

    public int attacks;

    public BadPacketsF(GrimPlayer player) {
        super(player);
    }

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        if (event.getPacketType() == PacketType.Play.Client.INTERACT_ENTITY) {
            WrapperPlayClientInteractEntity packet = new WrapperPlayClientInteractEntity(event);

            // Detects interacting with yourself.
            if (packet.getEntityId() == player.entityID) {
                player.kick(getCheckName(), event, "Self Interact");
                return;
            }

            // Detects interacting with invalid entities.
            if (player.compensatedEntities.getEntity(packet.getEntityId()) == null) {
                player.kick(getCheckName(), event, "Invalid Entity");
                return;
            }

            // Detects attacking without swinging.
            if (packet.getAction() == WrapperPlayClientInteractEntity.InteractAction.ATTACK) {
                if (++attacks > 2) {
                    player.kick(getCheckName(), event, "NoSwing");
                }
            }

        } else if (event.getPacketType() == PacketType.Play.Client.ANIMATION) {
            attacks = 0;
        }
    }
}
