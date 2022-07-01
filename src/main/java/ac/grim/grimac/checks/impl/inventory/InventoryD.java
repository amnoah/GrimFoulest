package ac.grim.grimac.checks.impl.inventory;

import ac.grim.grimac.checks.CheckData;
import ac.grim.grimac.checks.type.PacketCheck;
import ac.grim.grimac.player.GrimPlayer;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientHeldItemChange;

// Detects sending invalid inventory slots
@CheckData(name = "Inventory D")
public class InventoryD extends PacketCheck {

    public int lastSlot = -1;

    public InventoryD(GrimPlayer player) {
        super(player);
    }

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        if (event.getPacketType() == PacketType.Play.Client.HELD_ITEM_CHANGE) {
            WrapperPlayClientHeldItemChange packet = new WrapperPlayClientHeldItemChange(event);

            if (packet.getSlot() == lastSlot) {
                player.kick(getCheckName(), event, "Sent Same Slot");
                return;
            }

            if (packet.getSlot() < 0 || packet.getSlot() > 8) {
                player.kick(getCheckName(), event, "Invalid Slot");
                return;
            }

            lastSlot = packet.getSlot();
        }
    }
}
