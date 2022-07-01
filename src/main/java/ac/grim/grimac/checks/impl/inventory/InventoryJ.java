package ac.grim.grimac.checks.impl.inventory;

import ac.grim.grimac.checks.CheckData;
import ac.grim.grimac.checks.type.PacketCheck;
import ac.grim.grimac.player.GrimPlayer;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.player.ClientVersion;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientClickWindow;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerFlying;

import java.util.Objects;

@CheckData(name = "Inventory J")
public class InventoryJ extends PacketCheck {

    public int stage;
    public int slot;

    public InventoryJ(GrimPlayer player) {
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

            if (packet.getWindowClickType() == WrapperPlayClientClickWindow.WindowClickType.QUICK_MOVE
                    && packet.getButton() == 0) {
                if (stage == 0 && packet.getCarriedItemStack().isEmpty()) {
                    stage = 1;
                } else if (stage == 1 && packet.getCarriedItemStack().isEmpty() && Objects.equals(this.slot, slot)) {
                    flagAndAlert("", false);
                    stage = 0;
                } else {
                    stage = 0;
                }

            } else {
                stage = 0;
            }

            this.slot = slot;

        } else if (WrapperPlayClientPlayerFlying.isFlying(event.getPacketType())) {
            stage = 0;
        }
    }
}
