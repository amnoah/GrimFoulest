package ac.grim.grimac.checks.impl.inventory;

import ac.grim.grimac.checks.CheckData;
import ac.grim.grimac.checks.type.PacketCheck;
import ac.grim.grimac.player.GrimPlayer;
import ac.grim.grimac.utils.inventory.Inventory;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.item.ItemStack;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientClickWindow;

// Detects picking up items that don't exist
@CheckData(name = "Inventory K")
public class InventoryK extends PacketCheck {

    public InventoryK(GrimPlayer player) {
        super(player);
    }

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        if (event.getPacketType() == PacketType.Play.Client.CLICK_WINDOW) {
            WrapperPlayClientClickWindow packet = new WrapperPlayClientClickWindow(event);
            Inventory inventory = player.getInventory().inventory;

            try {
                if (packet.getCarriedItemStack() == null
                        || inventory.getSlot(packet.getSlot()).getItem() == null) {
                    return;
                }
            } catch (ArrayIndexOutOfBoundsException ignored) {
                return;
            }

            System.out.println("PACKET=" + packet.getCarriedItemStack()
                    + " SLOT=" + inventory.getSlot(packet.getSlot()).getItem());

            ItemStack packetItem = packet.getCarriedItemStack();
            ItemStack inventoryItem = inventory.getSlot(packet.getSlot()).getItem();

            if (packet.getWindowId() == 0
                    && packet.getWindowClickType() != WrapperPlayClientClickWindow.WindowClickType.SWAP
                    && packet.getWindowClickType() != WrapperPlayClientClickWindow.WindowClickType.QUICK_MOVE
                    && packet.getWindowClickType() != WrapperPlayClientClickWindow.WindowClickType.PICKUP_ALL) {
                if (!(packetItem.getAmount() == inventoryItem.getAmount()
                        && packetItem.getDamageValue() == inventoryItem.getDamageValue()
                        && packetItem.getLegacyData() == inventoryItem.getLegacyData()
                        && packetItem.getMaxDamage() == inventoryItem.getMaxDamage()
                        && packetItem.getMaxStackSize() == inventoryItem.getMaxStackSize()
                        && packetItem.getType() == inventoryItem.getType())) {
                    player.kick(getCheckName(), event, "Invalid ItemStack");
                }
            }
        }
    }
}
