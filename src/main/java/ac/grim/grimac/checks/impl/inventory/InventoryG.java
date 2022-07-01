package ac.grim.grimac.checks.impl.inventory;

import ac.grim.grimac.checks.CheckData;
import ac.grim.grimac.checks.type.PacketCheck;
import ac.grim.grimac.player.GrimPlayer;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerFlying;

@CheckData(name = "Inventory G")
public class InventoryG extends PacketCheck {

    public boolean click;
    public boolean swing;

    public InventoryG(GrimPlayer player) {
        super(player);
    }

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        if (event.getPacketType() == PacketType.Play.Client.ANIMATION) {
            swing = true;

        } else if (event.getPacketType() == PacketType.Play.Client.CLICK_WINDOW) {
            click = true;

        } else if (WrapperPlayClientPlayerFlying.isFlying(event.getPacketType())) {
            if (swing && click) {
                flagAndAlert("", false);
            }

            swing = false;
            click = false;
        }
    }
}
