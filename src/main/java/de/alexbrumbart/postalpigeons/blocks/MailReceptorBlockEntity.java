package de.alexbrumbart.postalpigeons.blocks;

import de.alexbrumbart.postalpigeons.ModRegistries;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class MailReceptorBlockEntity extends BlockEntity implements MenuProvider{
    private final ItemStackHandler inventory = new ItemStackHandler(18) {

        @Override
        protected void onContentsChanged(int slot) {
            MailReceptorBlockEntity.this.setChanged();
        }
    };
    private final LazyOptional<IItemHandler> handler = LazyOptional.of(() -> inventory);
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

    // Copied from AbstractContainerMenu#getRedstoneSignalFromContainer()
    public int calculateRedstoneOutput() {
        int i = 0;
        float f = 0.0F;

        for(int j = 0; j < inventory.getSlots(); ++j) {
            ItemStack itemstack = inventory.getStackInSlot(j);
            if (!itemstack.isEmpty()) {
                f += itemstack.getCount() / (float) Math.min(64, itemstack.getMaxStackSize());
                ++i;
            }
        }

        f /= inventory.getSlots();
        return Mth.floor(f * 14.0F) + (i > 0 ? 1 : 0);
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("postalpigeon.container.mail_receptor", name);
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new MailReceptorContainer(containerId, playerInventory, inventory, ContainerLevelAccess.create(Objects.requireNonNull(level), worldPosition));
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

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ITEM_HANDLER)
            return handler.cast();

        return super.getCapability(cap, side);
    }
}
