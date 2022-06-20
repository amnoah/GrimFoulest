package ac.grim.grimac.checks.impl.badpackets;

import ac.grim.grimac.checks.CheckData;
import ac.grim.grimac.checks.type.PacketCheck;
import ac.grim.grimac.player.GrimPlayer;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.player.DiggingAction;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerDigging;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerFlying;

@CheckData(name = "BadPackets Q")
public class BadPacketsQ extends PacketCheck {

    private boolean sent;

    public BadPacketsQ(GrimPlayer player) {
        super(player);
    }

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        if (event.getPacketType() == PacketType.Play.Client.PLAYER_DIGGING) {
            WrapperPlayClientPlayerDigging packet = new WrapperPlayClientPlayerDigging(event);

            if (packet.getAction() == DiggingAction.FINISHED_DIGGING && sent) {
                flagAndAlert("Swing After Destroy");
            }

        } else if (event.getPacketType() == PacketType.Play.Client.ANIMATION) {
            sent = true;

        } else if (WrapperPlayClientPlayerFlying.isFlying(event.getPacketType())) {
            sent = false;
        }
    }
}
