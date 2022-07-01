package ac.grim.grimac.checks.impl.badpackets;

import ac.grim.grimac.checks.CheckData;
import ac.grim.grimac.checks.type.PacketCheck;
import ac.grim.grimac.player.GrimPlayer;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerFlying;

// Detects sending invalid UPDATE_SIGN packets
@CheckData(name = "BadPackets P")
public class BadPacketsP extends PacketCheck {

    public boolean sentUpdateSign;
    public boolean sentSignEditor;
    public boolean sentBlockChange;

    public BadPacketsP(GrimPlayer player) {
        super(player);
    }

    @Override
    public void onPacketSend(PacketSendEvent event) {
        if (event.getPacketType() == PacketType.Play.Server.OPEN_SIGN_EDITOR) {
            sentSignEditor = true;
        } else if (event.getPacketType() == PacketType.Play.Server.BLOCK_CHANGE) {
            sentBlockChange = true;
        }
    }

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        if (event.getPacketType() == PacketType.Play.Client.UPDATE_SIGN) {
            if (!sentSignEditor) {
                player.kick(getCheckName(), event, "No Sign Editor");
                return;
            }

            sentUpdateSign = true;
            sentSignEditor = false;

        } else if (WrapperPlayClientPlayerFlying.isFlying(event.getPacketType())) {
            if (sentUpdateSign && !sentBlockChange) {
                player.kick(getCheckName(), event, "No Block Change");
                return;
            }

            sentUpdateSign = false;
            sentBlockChange = false;
        }
    }
}
