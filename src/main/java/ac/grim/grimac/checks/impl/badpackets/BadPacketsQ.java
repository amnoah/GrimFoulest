package ac.grim.grimac.checks.impl.badpackets;

import ac.grim.grimac.checks.CheckData;
import ac.grim.grimac.checks.type.PacketCheck;
import ac.grim.grimac.player.GrimPlayer;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientClickWindow;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerBlockPlacement;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerFlying;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientWindowConfirmation;

// Detects oddities in the WindowConfirmation packet
@CheckData(name = "BadPackets Q")
public class BadPacketsQ extends PacketCheck {

    private int lastActionId;
    private int lastWindowId;

    public BadPacketsQ(GrimPlayer player) {
        super(player);
    }

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        if (event.getPacketType() == PacketType.Play.Client.WINDOW_CONFIRMATION) {
            WrapperPlayClientWindowConfirmation packet = new WrapperPlayClientWindowConfirmation(event);

            if (lastActionId != 0 && Math.abs(packet.getActionId()) > 10) {
                if (packet.getActionId() >= lastActionId && Math.abs(packet.getActionId() - lastActionId) > 2) {
                    flagAndAlert("(Lower Than) ActionID: " + packet.getActionId() + " Last: " + lastActionId, false);
                }
            }

            if (packet.getWindowId() != 0) {
                flagAndAlert("(Not Zero) WindowID: " + packet.getWindowId(), false);
            }

            if (!packet.isAccepted()) {
                flagAndAlert("(Not Accepted) WindowID: " + packet.getWindowId()
                        + ", ActionID: " + packet.getActionId(), false);
            }

            lastActionId = packet.getActionId();
            lastWindowId = packet.getWindowId();
        }
    }
}
