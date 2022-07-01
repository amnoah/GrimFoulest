package ac.grim.grimac.checks.impl.badpackets;

import ac.grim.grimac.checks.CheckData;
import ac.grim.grimac.checks.type.PacketCheck;
import ac.grim.grimac.player.GrimPlayer;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerFlying;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientTabComplete;

// Detects sending invalid TAB_COMPLETE packets
@CheckData(name = "BadPackets E")
public class BadPacketsE extends PacketCheck {

    public int streak;

    public BadPacketsE(GrimPlayer player) {
        super(player);
    }

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        if (event.getPacketType() == PacketType.Play.Client.TAB_COMPLETE) {
            WrapperPlayClientTabComplete packet = new WrapperPlayClientTabComplete(event);
            String message = packet.getText();

            // Detects sending blank tab complete packets.
            if (message.equals("")) {
                player.kick(getCheckName(), event, "Empty");
            }

            // Detects sending multiple packets at once.
            if (++streak > 1) {
                player.kick(getCheckName(), event, "Streak (" + streak + ")");
            }

        } else if (WrapperPlayClientPlayerFlying.isFlying(event.getPacketType())) {
            streak = 0;
        }
    }
}
