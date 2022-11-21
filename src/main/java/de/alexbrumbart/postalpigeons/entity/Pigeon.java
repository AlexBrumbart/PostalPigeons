package de.alexbrumbart.postalpigeons.entity;

import de.alexbrumbart.postalpigeons.ModRegistries;
import de.alexbrumbart.postalpigeons.PostalPigeons;
import de.alexbrumbart.postalpigeons.blocks.MailReceptorBlockEntity;
import de.alexbrumbart.postalpigeons.blocks.PigeonCoopBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.FlyingAnimal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;

@Mod.EventBusSubscriber(modid = PostalPigeons.ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Pigeon extends Animal implements FlyingAnimal {
    private @Nullable BlockPos homePos;
    private @Nullable BlockPos goalPos;
    private boolean wasAway = false;
    private final ItemStackHandler inventory = new ItemStackHandler(8);

    public Pigeon(EntityType<? extends Animal> entityType, Level level) {
        super(entityType, level);

        moveControl = new FlyingMoveControl(this, 2, false);
    }

    @Override
    protected void registerGoals() {
        goalSelector.addGoal(0, new PanicGoal(this, 1.75D));
        goalSelector.addGoal(1, new FloatGoal(this));
        goalSelector.addGoal(2, new Pigeon.FindHomeGoal());
        goalSelector.addGoal(3, new Pigeon.MoveMailGoal());
        goalSelector.addGoal(4, new Pigeon.MoveHomeGoal());
        goalSelector.addGoal(5, new TemptGoal(this, 1.0D, Ingredient.of(Items.WHEAT_SEEDS), false));
        goalSelector.addGoal(6, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 10.0F));
        goalSelector.addGoal(8, new RandomLookAroundGoal(this));
    }

    @Override
    protected PathNavigation createNavigation(Level pLevel) {
        FlyingPathNavigation navigation = new FlyingPathNavigation(this, pLevel);
        navigation.setCanOpenDoors(false);
        navigation.setCanFloat(true);
        navigation.setCanPassDoors(true);

        return navigation;
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob otherParent) {
        return null;
    }

    @Override
    public boolean isFlying() {
        return !onGround;
    }

    @Override
    public boolean causeFallDamage(float pFallDistance, float pMultiplier, DamageSource pSource) {
        return false;
    }

    public void setHomePos(@Nullable BlockPos homePos) {
        this.homePos = homePos;
    }

    public void setGoalPos(@Nullable BlockPos goalPos) {
        this.goalPos = goalPos;
    }

    public boolean canSendAway() {
        return goalPos == null;
    }

    public void setWasAway(boolean wasAway) {
        this.wasAway = wasAway;
    }

    public void inputInventory(ItemStackHandler otherInventory) {
        for (int i = 0; i < otherInventory.getSlots(); i++) {
            ItemStack stack = ItemHandlerHelper.insertItemStacked(inventory, otherInventory.extractItem(i, 64, false), false);
            if (!stack.isEmpty())
                otherInventory.insertItem(i, stack, false);
        }
    }

    @Override
    protected void dropEquipment() {
        for (int i = 0; i < 8; i++) {
            ItemStack stack = inventory.extractItem(i, 64, false);
            if (!stack.isEmpty())
                spawnAtLocation(stack);
        }
    }

    @Override
    public void onRemovedFromWorld() {
        super.onRemovedFromWorld();

        if (homePos != null && getRemovalReason() == RemovalReason.KILLED) {
            PigeonCoopBlockEntity tile = (PigeonCoopBlockEntity) level.getBlockEntity(homePos);
            if (tile != null)
                tile.removePigeon(this, wasAway);
        }
    }

    @Override
    public boolean isFood(ItemStack stack) {
        return stack.getItem() == Items.WHEAT_SEEDS;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.CHICKEN_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource pDamageSource) {
        return SoundEvents.CHICKEN_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.CHICKEN_DEATH;
    }

    @Override
    protected void playStepSound(BlockPos pPos, BlockState pBlock) {
        this.playSound(SoundEvents.CHICKEN_STEP, 0.15F, 1.0F);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);

        if (homePos != null)
            tag.put("home", NbtUtils.writeBlockPos(homePos));
        if (goalPos != null)
            tag.put("goal", NbtUtils.writeBlockPos(goalPos));

        tag.put("inventory", inventory.serializeNBT());
        tag.putBoolean("wasAway", wasAway);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);

        if (tag.contains("home"))
            homePos = NbtUtils.readBlockPos(tag.getCompound("home"));
        if (tag.contains("goal"))
            goalPos = NbtUtils.readBlockPos(tag.getCompound("goal"));

        inventory.deserializeNBT(tag.getCompound("inventory"));
        wasAway = tag.getBoolean("wasAway");
    }

    @SubscribeEvent
    public static void attributeCreation(EntityAttributeCreationEvent event) {
        event.put(ModRegistries.PIGEON.get(), LivingEntity.createLivingAttributes().add(Attributes.MAX_HEALTH, 10.0).add(Attributes.FOLLOW_RANGE, 40.0).add(Attributes.MOVEMENT_SPEED, 0.3D).add(Attributes.FLYING_SPEED, 0.6D).build());
    }

    private class FindHomeGoal extends Goal {
        private final Pigeon pigeon = Pigeon.this; // Cache it to not always have to call Pigeon.this
        private int cooldown = 20;

        public FindHomeGoal() {
            setFlags(EnumSet.of(Goal.Flag.TARGET));
        }

        @Override
        public boolean canUse() {
            return pigeon.homePos == null && pigeon.isInLove() && --cooldown <= 0;
        }

        @Override
        public boolean canContinueToUse() {
            return false;
        }

        @Override
        public void start() {
            cooldown = 20;

            int x = pigeon.getBlockX();
            int y = pigeon.getBlockY();
            int z = pigeon.getBlockZ();
            for (BlockPos pos : BlockPos.betweenClosed(x - 3, y - 1, z - 3, x + 4, y + 2, z + 4)) {
                if (pigeon.level.getBlockState(pos).getBlock() == ModRegistries.PIGEON_COOP.get()) {
                    PigeonCoopBlockEntity tile = (PigeonCoopBlockEntity) pigeon.level.getBlockEntity(pos);
                    if (tile != null && tile.canIncorporatePigeon()) {
                        tile.addPigeon(pigeon);

                        return;
                    }
                }
            }
        }
    }

    private class MoveHomeGoal extends Goal {
        private final Pigeon pigeon = Pigeon.this; // Cache it to not always have to call Pigeon.this

        public MoveHomeGoal() {
            setFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            return pigeon.homePos != null && (pigeon.homePos.distToCenterSqr(pigeon.position()) > 25 || wasAway);
        }

        @Override
        public void stop() {
            pigeon.navigation.stop();
        }

        @Override
        public void tick() {
            BlockPos home = pigeon.homePos;

            if (home != null && pigeon.getNavigation().isDone()) {
                if (home.distToCenterSqr(pigeon.position()) > 400) {
                    Vec3 direction = new Vec3(home.getX() - pigeon.getX(), home.getY() - pigeon.getY(), home.getZ() - pigeon.getZ()).normalize();
                    Vec3 partHome = direction.scale(20).add(pigeon.getX(), pigeon.getY(), pigeon.getZ());
                    pigeon.navigation.moveTo(partHome.x, partHome.y, partHome.z, 1.5F);
                } else {
                    pigeon.navigation.moveTo(home.getX(), home.getY(), home.getZ(), 1.5F);
                }
            }

            if (home != null && wasAway && home.distToCenterSqr(pigeon.position()) < 36) {
                if (pigeon.level.getBlockEntity(home) instanceof PigeonCoopBlockEntity tile) {
                    tile.putInventory(inventory);
                    tile.setAtHome(uuid, true);
                    dropEquipment();
                } else {
                    pigeon.homePos = null;
                    dropEquipment();
                }

                wasAway = false;
                pigeon.navigation.stop();
            }
        }
    }

    private class MoveMailGoal extends Goal {
        private final Pigeon pigeon = Pigeon.this; // Cache it to not always have to call Pigeon.this

        public MoveMailGoal() {
            setFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            return pigeon.goalPos != null && pigeon.goalPos.distToCenterSqr(pigeon.position()) > 2;
        }

        @Override
        public void stop() {
            pigeon.navigation.stop();
        }

        @Override
        public void tick() {
            BlockPos goal = pigeon.goalPos;

            if (goal != null && pigeon.getNavigation().isDone()) {
                if (goal.distToCenterSqr(pigeon.position()) > 400) {
                    Vec3 direction = new Vec3(goal.getX() - pigeon.getX(), goal.getY() - pigeon.getY(), goal.getZ() - pigeon.getZ()).normalize();
                    Vec3 partGoal = direction.scale(20).add(pigeon.getX(), pigeon.getY(), pigeon.getZ());
                    pigeon.navigation.moveTo(partGoal.x, partGoal.y, partGoal.z, 1.5F);
                } else {
                    pigeon.navigation.moveTo(goal.getX(), goal.getY(), goal.getZ(), 1.5F);
                }
            }

            if (goal != null && goal.distToCenterSqr(pigeon.position()) < 4) {
                if (pigeon.level.getBlockEntity(goal) instanceof MailReceptorBlockEntity tile)
                    tile.putInventory(inventory);

                pigeon.goalPos = null;
                pigeon.navigation.stop();
            }
        }
    }
}
