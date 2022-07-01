package ac.grim.grimac.checks.impl.badpackets;

import ac.grim.grimac.checks.CheckData;
import ac.grim.grimac.checks.type.PacketCheck;
import ac.grim.grimac.player.GrimPlayer;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientClickWindow;

// Detects sending invalid CLICK_WINDOW packets
@CheckData(name = "BadPackets H")
public class BadPacketsH extends PacketCheck {

    public BadPacketsH(GrimPlayer player) {
        super(player);
    }

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        if (event.getPacketType() == PacketType.Play.Client.CLICK_WINDOW) {
            WrapperPlayClientClickWindow packet = new WrapperPlayClientClickWindow(event);

            // Detects sending invalid slots.
            if (packet.getWindowId() == 0) {
                if (packet.getSlot() > 44 || (packet.getSlot() != -999 && packet.getSlot() < 0)) {
                    player.kick(getCheckName(), event, "Invalid Slot (" + packet.getSlot() + ")");
                    return;
                }
            }

            // Detects sending invalid buttons.
            switch (packet.getWindowClickType()) {
                case PICKUP:
                    if (packet.getButton() != 0 && packet.getButton() != 1) {
                        player.kick(getCheckName(), event, "Pickup (" + packet.getButton() + ")");
                    }
                    return;

                case QUICK_MOVE:
                    if (packet.getButton() != 0 && packet.getButton() != 1) {
                        player.kick(getCheckName(), event, "Quick Move (" + packet.getButton() + ")");
                    }
                    return;

                case SWAP:
                    if (packet.getButton() != 0 && packet.getButton() != 1 && packet.getButton() != 2
                            && packet.getButton() != 8 && packet.getButton() != 40) {
                        player.kick(getCheckName(), event, "Swap (" + packet.getButton() + ")");
                    }
                    return;

                case CLONE:
                    if (packet.getButton() != 2) {
                        player.kick(getCheckName(), event, "Clone (" + packet.getButton() + ")");
                    }
                    return;

                case THROW:
                    if (packet.getButton() != 0 && packet.getButton() != 1) {
                        player.kick(getCheckName(), event, "Throw (" + packet.getButton() + ")");
                    }
                    return;

                case QUICK_CRAFT:
                    if (packet.getButton() != 0 && packet.getButton() != 1 && packet.getButton() != 2
                            && packet.getButton() != 4 && packet.getButton() != 5 && packet.getButton() != 6
                            && packet.getButton() != 8 && packet.getButton() != 9 && packet.getButton() != 10) {
                        player.kick(getCheckName(), event, "Quick Craft (" + packet.getButton() + ")");
                    }
                    return;

                case PICKUP_ALL:
                    if (packet.getButton() != 0) {
                        player.kick(getCheckName(), event, "Pickup All (" + packet.getButton() + ")");
                    }
            }
        }
    }
}
