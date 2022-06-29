package ac.grim.grimac.checks.impl.badpackets;

import ac.grim.grimac.checks.CheckData;
import ac.grim.grimac.checks.type.PacketCheck;
import ac.grim.grimac.player.GrimPlayer;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerFlying;

// Detects sending sign packets without receiving block change packets
@CheckData(name = "BadPackets S")
public class BadPacketsS extends PacketCheck {

    public boolean sentSign;
    public boolean sentBlockChange;

    public BadPacketsS(GrimPlayer player) {
        super(player);
    }

    @Override
    public void onPacketSend(PacketSendEvent event) {
        if (event.getPacketType() == PacketType.Play.Server.BLOCK_CHANGE) {
            sentBlockChange = true;
        }
    }

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        if (event.getPacketType() == PacketType.Play.Client.UPDATE_SIGN) {
            sentSign = true;

        } else if (WrapperPlayClientPlayerFlying.isFlying(event.getPacketType())) {
            if (sentSign && !sentBlockChange) {
                player.kick(getCheckName(), event, "No Block Change");
                return;
            }

            sentSign = false;
            sentBlockChange = false;
        }
    }
}
