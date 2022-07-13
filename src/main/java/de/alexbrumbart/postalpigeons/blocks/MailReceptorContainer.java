package de.alexbrumbart.postalpigeons.blocks;

import de.alexbrumbart.postalpigeons.ModRegistries;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;

public class MailReceptorContainer extends AbstractContainerMenu {
    private final ContainerLevelAccess access;

    public MailReceptorContainer(int containerId, Inventory playerInventory) {
        this(containerId, playerInventory, new ItemStackHandler(18), ContainerLevelAccess.NULL);
    }

    public MailReceptorContainer(int containerId, Inventory playerInventory, ItemStackHandler inventory, ContainerLevelAccess access) {
        super(ModRegistries.MAIL_RECEPTOR_MENU.get(), containerId);

        this.access = access;

        final int firstX = 8;
        final int firstY = 18;
        final int playerY = 68;
        final int hotbarY = 126;
        final int spacing = 18;

        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 9; j++) {
                addSlot(new SlotItemHandler(inventory, (i * 9) + j, firstX + (j * spacing), firstY + (i * spacing)));
            }
        }

        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 9; column++) {
                addSlot(new Slot(playerInventory, 9 + (row * 9) + column, firstX + (column * spacing), playerY + (row * spacing)));
            }
        }

        for (int x = 0; x < 9; x++) {
            addSlot(new Slot(playerInventory, x, firstX + (x * spacing), hotbarY));
        }
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack stack = ItemStack.EMPTY;
        Slot slot = slots.get(index);
        if (slot.hasItem()) {
            ItemStack stackSlot = slot.getItem();
            stack = stackSlot.copy();

            if (index < 18) {
                if (!moveItemStackTo(stackSlot, 18, 54, true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!moveItemStackTo(stackSlot, 0, 18, false)) {
                return ItemStack.EMPTY;
            }

            if (stackSlot.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }

        return stack;
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(access, player, ModRegistries.MAIL_RECEPTOR.get());
    }
}
