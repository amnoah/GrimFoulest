package ac.grim.grimac.checks.impl.inventory;

import ac.grim.grimac.checks.CheckData;
import ac.grim.grimac.checks.type.PacketCheck;
import ac.grim.grimac.player.GrimPlayer;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.player.ClientVersion;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientClientStatus;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerFlying;

import javax.print.DocFlavor;

@CheckData(name = "Inventory H")
public class InventoryH extends PacketCheck {

    private boolean was;
    private boolean open;

    public InventoryH(GrimPlayer player) {
        super(player);
    }

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        if (player.getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_9)) {
            return;
        }

        if (event.getPacketType() == PacketType.Play.Client.CLOSE_WINDOW) {
            if (was) {
                flagAndAlert("Close", false);
            }

        } else if (event.getPacketType() == PacketType.Play.Client.CLIENT_STATUS) {
            WrapperPlayClientClientStatus packet = new WrapperPlayClientClientStatus(event);

            if (packet.getAction() == WrapperPlayClientClientStatus.Action.OPEN_INVENTORY_ACHIEVEMENT) {
                open = true;
            }

        } else if (event.getPacketType() == PacketType.Play.Client.CLICK_WINDOW) {
            was = open;

            if (open) {
                flagAndAlert("Click", false);
                open = false;
            }

        } else if (WrapperPlayClientPlayerFlying.isFlying(event.getPacketType())) {
            open = false;
            was = false;
        }
    }
}
