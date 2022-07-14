package de.alexbrumbart.postalpigeons.blocks;

import de.alexbrumbart.postalpigeons.ModRegistries;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;

public class PigeonCoopContainer extends AbstractContainerMenu {
    private final ContainerLevelAccess access;

    public PigeonCoopContainer(int containerId, Inventory playerInventory) {
        this(containerId, playerInventory, new ItemStackHandler(9), ContainerLevelAccess.NULL);
    }

    public PigeonCoopContainer(int containerId, Inventory playerInventory, ItemStackHandler inventory, ContainerLevelAccess access) {
        super(ModRegistries.PIGEON_COOP_MENU.get(), containerId);

        this.access = access;

        final int firstX = 8;
        final int firstY = 94;
        final int playerX = 49;
        final int playerY = 169;
        final int hotbarY = 227;
        final int spacing = 18;

        addSlot(new SlotItemHandler(inventory, 0, 8, 74) {

            @Override
            public boolean mayPlace(@NotNull ItemStack stack) {
                return stack.getItem() == Items.WHEAT_SEEDS;
            }
        });

        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 4; j++) {
                addSlot(new SlotItemHandler(inventory, 1 + (i * 4) + j, firstX + (j * spacing), firstY + (i * spacing)));
            }
        }

        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 9; column++) {
                addSlot(new Slot(playerInventory, 9 + (row * 9) + column, playerX + (column * spacing), playerY + (row * spacing)));
            }
        }

        for (int x = 0; x < 9; x++) {
            addSlot(new Slot(playerInventory, x, playerX + (x * spacing), hotbarY));
        }
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack stack = ItemStack.EMPTY;
        Slot slot = slots.get(index);
        if (slot.hasItem()) {
            ItemStack stackSlot = slot.getItem();
            stack = stackSlot.copy();

            if (index < 9) {
                if (!moveItemStackTo(stackSlot, 9, 45, true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!moveItemStackTo(stackSlot, 0, 9, false)) {
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
        return stillValid(access, player, ModRegistries.PIGEON_COOP.get());
    }

    public int getPigeonAmount() {
        return 0; // TODO finish
    }

    public int getRemainingPigeonAmount() {
        return 2; // TODO finish
    }
}
