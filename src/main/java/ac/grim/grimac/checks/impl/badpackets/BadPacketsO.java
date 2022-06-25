package ac.grim.grimac.checks.impl.badpackets;

import ac.grim.grimac.checks.CheckData;
import ac.grim.grimac.checks.type.PacketCheck;
import ac.grim.grimac.player.GrimPlayer;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.player.DiggingAction;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerDigging;

// Detects LiquidBounce's AbortBreaking
@CheckData(name = "BadPackets O")
public class BadPacketsO extends PacketCheck {

    private boolean digging;
    private long lastSwing;

    public BadPacketsO(GrimPlayer player) {
        super(player);
    }

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        if (event.getPacketType() == PacketType.Play.Client.PLAYER_DIGGING) {
            WrapperPlayClientPlayerDigging packet = new WrapperPlayClientPlayerDigging(event);

            if (packet.getAction() == DiggingAction.START_DIGGING) {
                digging = true;
            } else if (packet.getAction() == DiggingAction.CANCELLED_DIGGING
                    || packet.getAction() == DiggingAction.FINISHED_DIGGING) {
                digging = false;
            }

        } else if (event.getPacketType() == PacketType.Play.Client.ANIMATION) {
            lastSwing = System.currentTimeMillis();
        }

        long timeSinceSwing = System.currentTimeMillis() - lastSwing;

        if (digging && timeSinceSwing > 51) {
            player.kick(getCheckName(), "SWING=" + timeSinceSwing, "You are sending too many packets!");
        }
    }
}
