package ac.grim.grimac.checks.impl.badpackets;

import ac.grim.grimac.checks.CheckData;
import ac.grim.grimac.checks.type.PacketCheck;
import ac.grim.grimac.player.GrimPlayer;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientHeldItemChange;

@CheckData(name = "BadPackets B")
public class BadPacketsB extends PacketCheck {

    int lastSlot = -1;

    public BadPacketsB(GrimPlayer player) {
        super(player);
    }

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        if (event.getPacketType() == PacketType.Play.Client.HELD_ITEM_CHANGE) {
            WrapperPlayClientHeldItemChange packet = new WrapperPlayClientHeldItemChange(event);

            if (packet.getSlot() == lastSlot) {
                event.setCancelled(true);
                player.kick(getCheckName(), "Sent Same Slot");
                return;
            }

            if (packet.getSlot() < 0 || packet.getSlot() > 8) {
                event.setCancelled(true);
                player.kick(getCheckName(), "Invalid Slot");
                return;
            }

            lastSlot = packet.getSlot();
        }
    }
}
