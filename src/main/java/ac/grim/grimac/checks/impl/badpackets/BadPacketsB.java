package ac.grim.grimac.checks.impl.badpackets;

import ac.grim.grimac.checks.CheckData;
import ac.grim.grimac.checks.type.PacketCheck;
import ac.grim.grimac.player.GrimPlayer;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.player.DiggingAction;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerDigging;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerFlying;

// Detects swinging after destroying a block in the same tick
@CheckData(name = "BadPackets B")
public class BadPacketsB extends PacketCheck {

    private boolean sentAnimation;

    public BadPacketsB(GrimPlayer player) {
        super(player);
    }

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        if (event.getPacketType() == PacketType.Play.Client.PLAYER_DIGGING) {
            WrapperPlayClientPlayerDigging packet = new WrapperPlayClientPlayerDigging(event);

            if (sentAnimation && packet.getAction() == DiggingAction.FINISHED_DIGGING) {
                event.setCancelled(true);
                player.kick(getCheckName(), "Swing After Destroy");
            }

        } else if (event.getPacketType() == PacketType.Play.Client.ANIMATION) {
            sentAnimation = true;

        } else if (WrapperPlayClientPlayerFlying.isFlying(event.getPacketType())) {
            sentAnimation = false;
        }
    }
}
