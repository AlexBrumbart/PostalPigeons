package de.alexbrumbart.postalpigeons.entity;

import de.alexbrumbart.postalpigeons.ModRegistries;
import de.alexbrumbart.postalpigeons.PostalPigeons;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.goal.FloatGoal;
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
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.jetbrains.annotations.Nullable;

@Mod.EventBusSubscriber(modid = PostalPigeons.ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Pigeon extends Animal implements FlyingAnimal {
    @Nullable
    private BlockPos homePos;
    @Nullable
    private BlockPos goalPos;

    public Pigeon(EntityType<? extends Animal> entityType, Level level) {
        super(entityType, level);

        moveControl = new FlyingMoveControl(this, 10, false);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new PanicGoal(this, 1.4D));
        this.goalSelector.addGoal(3, new TemptGoal(this, 1.0D, Ingredient.of(Items.WHEAT_SEEDS), false));
        this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));
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

    public void setHomePos(@Nullable BlockPos homePos) {
        this.homePos = homePos;
        LogManager.getLogger(PostalPigeons.ID).info("set home pos for: " + toString() + " at " + homePos); // TODO entfernen
    }

    public void setGoalPos(@Nullable BlockPos goalPos) {
        this.goalPos = goalPos;
        LogManager.getLogger(PostalPigeons.ID).info("set goal pos for: " + toString() + " at " + goalPos); // TODO entfernen
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);

        if (homePos != null)
            tag.put("home", NbtUtils.writeBlockPos(homePos));
        if (goalPos != null)
            tag.put("goal", NbtUtils.writeBlockPos(goalPos));
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);

        if (tag.contains("home"))
            homePos = NbtUtils.readBlockPos(tag.getCompound("home"));
        if (tag.contains("goal"))
            goalPos = NbtUtils.readBlockPos(tag.getCompound("goal"));
    }

    @SubscribeEvent
    public static void attributeCreation(EntityAttributeCreationEvent event) {
        event.put(ModRegistries.PIGEON.get(), LivingEntity.createLivingAttributes().add(Attributes.MAX_HEALTH, 10.0).add(Attributes.FOLLOW_RANGE, 40.0).add(Attributes.MOVEMENT_SPEED, 0.3D).add(Attributes.FLYING_SPEED, 0.6D).build());
    }
}
