package ac.grim.grimac.checks.impl.inventory;

import ac.grim.grimac.checks.CheckData;
import ac.grim.grimac.checks.type.PacketCheck;
import ac.grim.grimac.player.GrimPlayer;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.player.ClientVersion;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientClickWindow;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientClientStatus;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerFlying;

import java.awt.*;
import java.util.Objects;

@CheckData(name = "Inventory I")
public class InventoryI extends PacketCheck {

    private boolean pressed = false;
    private int slot = -1;
    private int button = -1;

    public InventoryI(GrimPlayer player) {
        super(player);
    }

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        if (player.getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_9)) {
            return;
        }

        if (event.getPacketType() == PacketType.Play.Client.CLICK_WINDOW) {
            WrapperPlayClientClickWindow packet = new WrapperPlayClientClickWindow(event);
            int slot = packet.getSlot();
            int button = packet.getButton();

            if (packet.getWindowClickType() == WrapperPlayClientClickWindow.WindowClickType.SWAP
                    && packet.getWindowId() != 0) {
                if (pressed && this.slot != slot && this.button == button) {
                    event.setCancelled(true);
                    flagAndAlert("", false);
                }

                pressed = true;
            }

            this.slot = slot;
            this.button = button;

        } else if (WrapperPlayClientPlayerFlying.isFlying(event.getPacketType())) {
            pressed = false;
        }
    }
}
