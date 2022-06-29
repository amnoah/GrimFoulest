package ac.grim.grimac.checks.impl.badpackets;

import ac.grim.grimac.checks.CheckData;
import ac.grim.grimac.checks.type.PacketCheck;
import ac.grim.grimac.player.GrimPlayer;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientInteractEntity;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerFlying;

@CheckData(name = "BadPackets K")
public class BadPacketsK extends PacketCheck {

    private boolean attack;
    private boolean interactAt;
    private boolean interact;

    public BadPacketsK(GrimPlayer player) {
        super(player);
    }

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        if (event.getPacketType() == PacketType.Play.Client.INTERACT_ENTITY) {
            WrapperPlayClientInteractEntity packet = new WrapperPlayClientInteractEntity(event);

            if (packet.getAction() == WrapperPlayClientInteractEntity.InteractAction.ATTACK) {
                if (!attack && interact != interactAt) {
                    flagAndAlert("INTERACT=" + interact + " INTERACT_AT=" + interactAt, false);
                    interact = false;
                    interactAt = false;
                }

                attack = true;

            } else if (packet.getAction() == WrapperPlayClientInteractEntity.InteractAction.INTERACT) {
                interact = true;

            } else if (packet.getAction() == WrapperPlayClientInteractEntity.InteractAction.INTERACT_AT) {
                if (!interactAt && interact) {
                    flagAndAlert("Interact", false);
                    interact = false;
                }

                interactAt = true;

            } else if (WrapperPlayClientPlayerFlying.isFlying(event.getPacketType())) {
                attack = false;
                interact = false;
                interactAt = false;
            }
        }
    }
}
