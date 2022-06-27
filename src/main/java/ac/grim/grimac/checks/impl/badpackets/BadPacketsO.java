package ac.grim.grimac.checks.impl.badpackets;

import ac.grim.grimac.checks.CheckData;
import ac.grim.grimac.checks.type.PacketCheck;
import ac.grim.grimac.player.GrimPlayer;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientInteractEntity;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerFlying;

@CheckData(name = "BadPackets O")
public class BadPacketsO extends PacketCheck {

    private boolean sent;
    private boolean interact;

    public BadPacketsO(GrimPlayer player) {
        super(player);
    }

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        if (event.getPacketType() == PacketType.Play.Client.INTERACT_ENTITY) {
            WrapperPlayClientInteractEntity packet = new WrapperPlayClientInteractEntity(event);

            if (packet.getAction() == WrapperPlayClientInteractEntity.InteractAction.INTERACT) {
                if (sent && !interact) {
                    flagAndAlert("", false);
                    sent = false;
                }

                interact = true;
            }

        } else if (event.getPacketType() == PacketType.Play.Client.PLAYER_BLOCK_PLACEMENT) {
            sent = true;

        } else if (WrapperPlayClientPlayerFlying.isFlying(event.getPacketType())) {
            sent = false;
            interact = false;
        }
    }
}
