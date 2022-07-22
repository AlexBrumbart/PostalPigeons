package de.alexbrumbart.postalpigeons.blocks;

import de.alexbrumbart.postalpigeons.ModRegistries;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;

public class MailReceptorBlockEntity extends BlockEntity implements MenuProvider {
    private final ItemStackHandler inventory = new ItemStackHandler(18) { // TODO Integration mit Hoppern und Redstone

        @Override
        protected void onContentsChanged(int slot) {
            MailReceptorBlockEntity.this.setChanged();
        }
    };
    private String name = "";

    public MailReceptorBlockEntity(BlockPos pos, BlockState state) {
        super(ModRegistries.MAIL_RECEPTOR_TE.get(), pos, state);
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void putInventory(ItemStackHandler otherInventory) {
        for (int i = 0; i < otherInventory.getSlots(); i++) {
            ItemStack stack = ItemHandlerHelper.insertItemStacked(inventory, otherInventory.extractItem(i, 64, false), false);
            if (!stack.isEmpty())
                otherInventory.insertItem(i, stack, false);
        }
    }

    public void onRemove() {
        if (level != null) {
            for (int i = 0; i < 18; i++) {
                if (!inventory.getStackInSlot(i).isEmpty()) {
                    Containers.dropItemStack(level, worldPosition.getX(), worldPosition.getY(), worldPosition.getZ(), inventory.extractItem(i, 64, false));
                }
            }
        }
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("postalpigeon.container.mail_receptor", name);
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new MailReceptorContainer(containerId, playerInventory, inventory, ContainerLevelAccess.create(level, worldPosition));
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);

        tag.put("inventory", inventory.serializeNBT());
        tag.putString("name", name);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);

        inventory.deserializeNBT(tag.getCompound("inventory"));
        name = tag.getString("name");
    }
}
