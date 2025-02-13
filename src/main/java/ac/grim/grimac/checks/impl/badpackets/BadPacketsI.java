package ac.grim.grimac.checks.impl.badpackets;

import ac.grim.grimac.checks.CheckData;
import ac.grim.grimac.checks.type.PacketCheck;
import ac.grim.grimac.player.GrimPlayer;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.player.ClientVersion;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerFlying;

@CheckData(name = "BadPackets I")
public class BadPacketsI extends PacketCheck {

    boolean sentHeldItem = false;

    public BadPacketsI(GrimPlayer playerData) {
        super(playerData);
    }

    public void onPacketReceive(PacketReceiveEvent event) {
        if (event.getPacketType() == PacketType.Play.Client.HELD_ITEM_CHANGE) { // idle packet
            // Due to a bug in 1.8 clients, this check isn't possible for 1.8 clients
            // Instead, we must tick "using item" with flying packets like the server does
            if (sentHeldItem && player.isTickingReliablyFor(3)
                    && player.getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_9)) {
                event.setCancelled(true);
                player.kick(getCheckName(), "", "You are sending too many packets!");
                return;
            } else {
                sentHeldItem = true;
            }
        }

        if (WrapperPlayClientPlayerFlying.isFlying(event.getPacketType())) {
            sentHeldItem = false;
        }
    }
}
