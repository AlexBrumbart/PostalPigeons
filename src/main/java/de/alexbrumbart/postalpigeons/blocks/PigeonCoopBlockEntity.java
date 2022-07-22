package de.alexbrumbart.postalpigeons.blocks;

import de.alexbrumbart.postalpigeons.ModRegistries;
import de.alexbrumbart.postalpigeons.entity.Pigeon;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.AbstractMap;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

public class PigeonCoopBlockEntity extends BlockEntity implements MenuProvider {
    private static final Component name = Component.translatable("postalpigeons.container.pigeon_coop");
    private static final int maxPigeons = 8;

    private int remainingPigeons = 0;
    private final List<AbstractMap.SimpleEntry<UUID, Boolean>> pigeons = new LinkedList<>();
    private final AABB searchBox = new AABB(worldPosition.getX() - 5, worldPosition.getY() - 5, worldPosition.getZ() - 5, worldPosition.getX() + 6, worldPosition.getY() + 6, worldPosition.getZ() + 6);

    private final ItemStackHandler inventory = new ItemStackHandler(9) {

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            return slot != 0 || stack.getItem() == Items.WHEAT_SEEDS;
        }

        @Override
        protected void onContentsChanged(int slot) {
            PigeonCoopBlockEntity.this.setChanged();
        }
    };
    private final ContainerData data = new ContainerData() {

        @Override
        public int get(int index) {
            return index == 0 ? pigeons.size() : remainingPigeons;
        }

        @Override
        public void set(int index, int value) {
            if (index == 1)
               remainingPigeons = value;
        }

        @Override
        public int getCount() {
            return 2;
        }
    };

    public PigeonCoopBlockEntity(BlockPos pos, BlockState state) {
        super(ModRegistries.PIGEON_COOP_TE.get(), pos, state);
    }

    public void addPigeon(Pigeon pigeon) {
        if (pigeons.size() >= maxPigeons)
            return;

        pigeon.setHomePos(worldPosition);
        pigeons.add(new AbstractMap.SimpleEntry<>(pigeon.getUUID(), true));

        ItemStack seeds = inventory.extractItem(0, 64, false);
        seeds.shrink(10);
        inventory.setStackInSlot(0, seeds);
        remainingPigeons++;
    }

    public void removePigeon(Pigeon pigeon) {
        for (int i = 0; i < maxPigeons; i++) {
            if (pigeons.get(i).getKey().equals(pigeon.getUUID())) {
                pigeons.remove(i);

                return;
            }
        }
    }

    public void sendPigeon(BlockPos position) {
        // TODO let it cost some seeds to send pigeons

        if (remainingPigeons <= 0)
            return;

        Pigeon pigeonEntity = level.getEntitiesOfClass(Pigeon.class, searchBox).stream().filter(pigeon -> {
            for (var entry : pigeons) {
                if (entry.getKey().equals(pigeon.getUUID()))
                    return true;
            }

            return false;
        }).findFirst().orElse(null);

        if (pigeonEntity != null) {
            ItemStack seeds = inventory.extractItem(0, 64, false);

            pigeonEntity.inputInventory(inventory);
            pigeonEntity.setGoalPos(position);
            pigeonEntity.setWasAway(true);

            inventory.insertItem(0, seeds, false);

            remainingPigeons--;
        }
    }

    public void putInventory(ItemStackHandler otherInventory) {
        for (int i = 0; i < otherInventory.getSlots(); i++) {
            ItemStack stack = ItemHandlerHelper.insertItemStacked(inventory, otherInventory.extractItem(i, 64, false), false);
            if (!stack.isEmpty())
                otherInventory.insertItem(i, stack, false);
        }
    }

    public void setAtHome(UUID uuid, boolean atHome) {
        for (AbstractMap.SimpleEntry<UUID, Boolean> entry : pigeons) {
            if (entry.getKey().equals(uuid)) {
                entry.setValue(atHome);
                remainingPigeons = atHome ? remainingPigeons + 1 : remainingPigeons - 1;

                return;
            }
        }
    }

    public boolean canIncorporatePigeon() {
        return pigeons.size() < maxPigeons && inventory.getStackInSlot(0).getItem() == Items.WHEAT_SEEDS && inventory.getStackInSlot(0).getCount() >= 10;
    }

    public void onRemove() {
        if (level != null) {
            for (int i = 0; i < 10; i++) {
                if (!inventory.getStackInSlot(i).isEmpty()) {
                    Containers.dropItemStack(level, worldPosition.getX(), worldPosition.getY(), worldPosition.getZ(), inventory.extractItem(i, 64, false));
                }
            }

            var pigeonList = pigeons.stream().map(AbstractMap.SimpleEntry::getKey).toList();
            level.getEntitiesOfClass(Pigeon.class, searchBox).stream().forEach(pigeon -> {
                if (pigeonList.contains(pigeon.getUUID()))
                    pigeon.setHomePos(null);
            });
        }
    }

    @Override
    public Component getDisplayName() {
        return name;
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new PigeonCoopContainer(containerId, playerInventory, inventory, worldPosition, data, ContainerLevelAccess.create(level, worldPosition));
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);

        tag.put("inventory", inventory.serializeNBT());

        tag.putInt("pigeonAmount", pigeons.size());
        for (int i = 0; i < pigeons.size(); i++) {
            var entry = pigeons.get(i);

            CompoundTag pigeonTag = new CompoundTag();
            pigeonTag.putUUID("uuid", entry.getKey());
            pigeonTag.putBoolean("available", entry.getValue());
            tag.put("pigeon" + i, pigeonTag);
        }
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);

        inventory.deserializeNBT(tag.getCompound("inventory"));

        int i = tag.getInt("pigeonAmount");
        for (int j = 0; j < i; j++) {
            CompoundTag pigeonTag = tag.getCompound("pigeon" + j);

            AbstractMap.SimpleEntry<UUID, Boolean> entry = new AbstractMap.SimpleEntry<>(pigeonTag.getUUID("uuid"), pigeonTag.getBoolean("available"));
            pigeons.add(entry);
            if (entry.getValue())
                remainingPigeons++;
        }
    }
}
