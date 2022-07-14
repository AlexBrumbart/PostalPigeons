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
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;

public class PigeonCoopBlockEntity extends BlockEntity implements MenuProvider {
    private static final Component name = Component.translatable("postalpigeons.container.pigeon_coop");

    // TODO Tauben Mob verweise
    private final ItemStackHandler inventory = new ItemStackHandler(9) {

        @Override
        protected void onContentsChanged(int slot) {
            PigeonCoopBlockEntity.this.setChanged();
        }
    };

    public PigeonCoopBlockEntity(BlockPos pos, BlockState state) {
        super(ModRegistries.PIGEON_COOP_TE.get(), pos, state);
    }

    public void onRemove() {
        if (level != null) {
            for (int i = 0; i < 10; i++) {
                if (!inventory.getStackInSlot(i).isEmpty()) {
                    Containers.dropItemStack(level, worldPosition.getX(), worldPosition.getY(), worldPosition.getZ(), inventory.extractItem(i, 64, false));
                }
            }
        }
    }

    @Override
    public Component getDisplayName() {
        return name;
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new PigeonCoopContainer(containerId, playerInventory, inventory, ContainerLevelAccess.create(level, worldPosition));
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);

        tag.put("inventory", inventory.serializeNBT());
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);

        inventory.deserializeNBT(tag.getCompound("inventory"));
    }
}
