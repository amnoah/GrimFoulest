package ac.grim.grimac.utils.inventory;

import ac.grim.grimac.player.GrimPlayer;
import ac.grim.grimac.utils.inventory.inventory.AbstractContainerMenu;
import ac.grim.grimac.utils.inventory.slot.EquipmentSlot;
import ac.grim.grimac.utils.inventory.slot.ResultSlot;
import ac.grim.grimac.utils.inventory.slot.Slot;
import com.github.retrooper.packetevents.protocol.item.ItemStack;
import com.github.retrooper.packetevents.protocol.item.type.ItemType;
import com.github.retrooper.packetevents.protocol.player.GameMode;
import lombok.Getter;

public class Inventory extends AbstractContainerMenu {
    public static final int SLOT_OFFHAND = 45;
    public static final int HOTBAR_OFFSET = 36;
    public static final int ITEMS_START = 9;
    public static final int ITEMS_END = 45;
    private static final int SLOT_HELMET = 4;
    private static final int SLOT_CHESTPLATE = 5;
    private static final int SLOT_LEGGINGS = 6;
    private static final int SLOT_BOOTS = 7;
    private static final int TOTAL_SIZE = 46;
    public int selected = 0;
    @Getter
    InventoryStorage inventoryStorage;

    public Inventory(GrimPlayer player, InventoryStorage inventoryStorage) {
        this.inventoryStorage = inventoryStorage;

        setPlayer(player);
        setPlayerInventory(this);

        // Result slot
        addSlot(new ResultSlot(inventoryStorage, 0));
        // Crafting slots
        for (int i = 0; i < 4; i++) {
            addSlot(new Slot(inventoryStorage, i));
        }
        for (int i = 0; i < 4; i++) {
            addSlot(new EquipmentSlot(EquipmentType.byArmorID(i), inventoryStorage, i + 4));
        }
        // Inventory slots
        for (int i = 0; i < 9 * 4; i++) {
            addSlot(new Slot(inventoryStorage, i + 9));
        }
        // Offhand
        addSlot(new Slot(inventoryStorage, 45));
    }

    public ItemStack getHelmet() {
        return inventoryStorage.getItem(SLOT_HELMET);
    }

    public ItemStack getChestplate() {
        return inventoryStorage.getItem(SLOT_CHESTPLATE);
    }

    public ItemStack getLeggings() {
        return inventoryStorage.getItem(SLOT_LEGGINGS);
    }

    public ItemStack getBoots() {
        return inventoryStorage.getItem(SLOT_BOOTS);
    }

    public ItemStack getOffhand() {
        return inventoryStorage.getItem(SLOT_OFFHAND);
    }

    public boolean hasItemType(ItemType item) {
        for (int i = 0; i < inventoryStorage.items.length; ++i) {
            if (inventoryStorage.getItem(i).getType() == item) {
                return true;
            }
        }
        return false;
    }

    public ItemStack getHeldItem() {
        return inventoryStorage.getItem(selected + HOTBAR_OFFSET);
    }

    public void setHeldItem(ItemStack item) {
        inventoryStorage.setItem(selected + HOTBAR_OFFSET, item);
    }

    public ItemStack getOffhandItem() {
        return inventoryStorage.getItem(SLOT_OFFHAND);
    }

    public boolean add(ItemStack p_36055_) {
        return add(-1, p_36055_);
    }

    public int getFreeSlot() {
        for (int i = 0; i < inventoryStorage.items.length; ++i) {
            if (inventoryStorage.getItem(i).isEmpty()) {
                return i;
            }
        }

        return -1;
    }

    public int getSlotWithRemainingSpace(ItemStack toAdd) {
        if (hasRemainingSpaceForItem(getHeldItem(), toAdd)) {
            return selected;
        } else if (hasRemainingSpaceForItem(getOffhandItem(), toAdd)) {
            return 40;
        } else {
            for (int i = ITEMS_START; i <= ITEMS_END; ++i) {
                if (hasRemainingSpaceForItem(inventoryStorage.getItem(i), toAdd)) {
                    return i;
                }
            }

            return -1;
        }
    }

    private boolean hasRemainingSpaceForItem(ItemStack one, ItemStack two) {
        return !one.isEmpty() && ItemStack.isSameItemSameTags(one, two) && one.getAmount() < one.getMaxStackSize() && one.getAmount() < getMaxStackSize();
    }

    private int addResource(ItemStack resource) {
        int i = getSlotWithRemainingSpace(resource);
        if (i == -1) {
            i = getFreeSlot();
        }

        return i == -1 ? resource.getAmount() : addResource(i, resource);
    }

    private int addResource(int slot, ItemStack stack) {
        int i = stack.getAmount();
        ItemStack itemstack = inventoryStorage.getItem(slot);

        if (itemstack.isEmpty()) {
            itemstack = stack.copy();
            itemstack.setAmount(0);
            inventoryStorage.setItem(slot, itemstack);
        }

        int j = Math.min(i, itemstack.getMaxStackSize() - itemstack.getAmount());

        if (j > getMaxStackSize() - itemstack.getAmount()) {
            j = getMaxStackSize() - itemstack.getAmount();
        }

        if (j != 0) {
            i = i - j;
            itemstack.grow(j);
        }
        return i;
    }

    public boolean add(int p_36041_, ItemStack p_36042_) {
        if (p_36042_.isEmpty()) {
            return false;
        } else {
            if (p_36042_.isDamaged()) {
                if (p_36041_ == -1) {
                    p_36041_ = getFreeSlot();
                }

                if (p_36041_ >= 0) {
                    inventoryStorage.setItem(p_36041_, p_36042_.copy());
                    p_36042_.setAmount(0);
                    return true;
                } else if (player.gamemode == GameMode.CREATIVE) {
                    p_36042_.setAmount(0);
                    return true;
                } else {
                    return false;
                }
            } else {
                int i;
                do {
                    i = p_36042_.getAmount();
                    if (p_36041_ == -1) {
                        p_36042_.setAmount(addResource(p_36042_));
                    } else {
                        p_36042_.setAmount(addResource(p_36041_, p_36042_));
                    }
                } while (!p_36042_.isEmpty() && p_36042_.getAmount() < i);

                if (p_36042_.getAmount() == i && player.gamemode == GameMode.CREATIVE) {
                    p_36042_.setAmount(0);
                    return true;
                } else {
                    return p_36042_.getAmount() < i;
                }
            }
        }
    }

    @Override
    public ItemStack quickMoveStack(int slotID) {
        ItemStack original = ItemStack.EMPTY;
        Slot slot = getSlots().get(slotID);

        if (slot != null && slot.hasItem()) {
            ItemStack toMove = slot.getItem();
            original = toMove.copy();
            EquipmentType equipmentslot = EquipmentType.getEquipmentSlotForItem(original);
            if (slotID == 0) {
                if (!moveItemStackTo(toMove, 9, 45, true)) {
                    return ItemStack.EMPTY;
                }
            } else if (slotID < 5) {
                if (!moveItemStackTo(toMove, 9, 45, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (slotID < 9) {
                if (!moveItemStackTo(toMove, 9, 45, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (equipmentslot.isArmor() && !getSlots().get(8 - equipmentslot.getIndex()).hasItem()) {
                int i = 8 - equipmentslot.getIndex();
                if (!moveItemStackTo(toMove, i, i + 1, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (equipmentslot == EquipmentType.OFFHAND && !getSlots().get(45).hasItem()) {
                if (!moveItemStackTo(toMove, 45, 46, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (slotID < 36) {
                if (!moveItemStackTo(toMove, 36, 45, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (slotID < 45) {
                if (!moveItemStackTo(toMove, 9, 36, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (!moveItemStackTo(toMove, 9, 45, false)) {
                return ItemStack.EMPTY;
            }

            if (toMove.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            }

            if (toMove.getAmount() == original.getAmount()) {
                return ItemStack.EMPTY;
            }
        }

        return original;
    }

    @Override
    public boolean canTakeItemForPickAll(ItemStack p_38908_, Slot p_38909_) {
        return p_38909_.inventoryStorageSlot != 0; // Result slot
    }
}
