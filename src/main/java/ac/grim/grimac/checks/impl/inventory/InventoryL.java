package ac.grim.grimac.checks.impl.inventory;

import ac.grim.grimac.checks.CheckData;
import ac.grim.grimac.checks.type.PacketCheck;
import ac.grim.grimac.player.GrimPlayer;
import ac.grim.grimac.utils.inventory.Inventory;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.item.ItemStack;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerBlockPlacement;

// Detects placing blocks/items that don't exist
@CheckData(name = "Inventory L")
public class InventoryL extends PacketCheck {

    public InventoryL(GrimPlayer player) {
        super(player);
    }

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        if (event.getPacketType() == PacketType.Play.Client.PLAYER_BLOCK_PLACEMENT) {
            WrapperPlayClientPlayerBlockPlacement packet = new WrapperPlayClientPlayerBlockPlacement(event);
            Inventory inventory = player.getInventory().inventory;

            if (!packet.getItemStack().isPresent()) {
                return;
            }

            ItemStack packetItem = packet.getItemStack().get();
            ItemStack inventoryItem = inventory.getHeldItem();

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
