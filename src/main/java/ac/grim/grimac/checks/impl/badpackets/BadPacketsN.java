package ac.grim.grimac.checks.impl.badpackets;

import ac.grim.grimac.checks.CheckData;
import ac.grim.grimac.checks.type.PacketCheck;
import ac.grim.grimac.player.GrimPlayer;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerFlying;

// Detects sending multiple packets in-between Flying
@CheckData(name = "BadPackets N")
public class BadPacketsN extends PacketCheck {

    private int streak;

    public BadPacketsN(GrimPlayer player) {
        super(player);
    }

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        if (!WrapperPlayClientPlayerFlying.isFlying(event.getPacketType())
                && event.getPacketType() != PacketType.Play.Client.WINDOW_CONFIRMATION
                && event.getPacketType() != PacketType.Play.Client.PLUGIN_MESSAGE
                && event.getPacketType() != PacketType.Play.Client.KEEP_ALIVE
                && event.getPacketType() != PacketType.Play.Client.CLICK_WINDOW
                && event.getPacketType() != PacketType.Play.Client.CREATIVE_INVENTORY_ACTION) {
            streak++;

            if (streak >= 6) {
                event.setCancelled(true);
                player.kick(getCheckName(), event.getPacketType().getName());
            }

        } else if (WrapperPlayClientPlayerFlying.isFlying(event.getPacketType())) {
            streak = 0;
        }
    }
}
