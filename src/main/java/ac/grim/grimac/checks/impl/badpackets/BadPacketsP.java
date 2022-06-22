package ac.grim.grimac.checks.impl.badpackets;

import ac.grim.grimac.checks.CheckData;
import ac.grim.grimac.checks.type.PacketCheck;
import ac.grim.grimac.player.GrimPlayer;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientKeepAlive;

// Detects sending multiple negative KeepAlive packets in a row
@CheckData(name = "BadPackets P")
public class BadPacketsP extends PacketCheck {

    public int streak;

    public BadPacketsP(GrimPlayer player) {
        super(player);
    }

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        if (event.getPacketType() == PacketType.Play.Client.KEEP_ALIVE) {
            WrapperPlayClientKeepAlive packet = new WrapperPlayClientKeepAlive(event);

            if (packet.getId() == -1) {
                ++streak;
            } else {
                streak = 0;
            }

            if (streak >= 2) {
                event.setCancelled(true);
                player.kick(getCheckName(), "Illegal KeepAlive");
            }
        }
    }
}
