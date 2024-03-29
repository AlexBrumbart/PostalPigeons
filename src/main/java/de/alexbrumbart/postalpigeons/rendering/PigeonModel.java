package de.alexbrumbart.postalpigeons.rendering;

import de.alexbrumbart.postalpigeons.PostalPigeons;
import de.alexbrumbart.postalpigeons.entity.Pigeon;
import net.minecraft.client.model.AgeableListModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

import java.util.List;

public class PigeonModel extends AgeableListModel<Pigeon> {
    public static final ModelLayerLocation MODEL_LOCATION = new ModelLayerLocation(new ResourceLocation(PostalPigeons.ID, "pigeon"), "main");

    private final ModelPart torso;
    private final ModelPart head;
    private final ModelPart beak;
    private final ModelPart leftWing;
    private final ModelPart rightWing;
    private final ModelPart leftLeg;
    private final ModelPart rightLeg;

    public PigeonModel(ModelPart root) {
        this.torso = root.getChild("torso");
        this.head = root.getChild("head");
        this.beak = root.getChild("beak");
        this.leftWing = root.getChild("left_wing");
        this.rightWing = root.getChild("right_wing");
        this.leftLeg = root.getChild("left_leg");
        this.rightLeg = root.getChild("right_leg");
    }

    @Override
    protected Iterable<ModelPart> headParts() {
        return List.of();
    }

    @Override
    protected Iterable<ModelPart> bodyParts() {
        return List.of(head, beak, torso, leftWing, rightWing, leftLeg, rightLeg);
    }

    @Override
    public void setupAnim(Pigeon entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.head.xRot = 0.1745F + headPitch * 0.25F * ((float)Math.PI / 180F);
        this.head.yRot = netHeadYaw * ((float)Math.PI / 180F);
        this.beak.xRot = this.head.xRot;
        this.beak.yRot = this.head.yRot;
        this.rightWing.zRot = (Mth.cos(limbSwing * 0.6662F) + 1F) * 1.4F * limbSwingAmount;
        this.leftWing.zRot = (Mth.cos(limbSwing * 0.6662F + (float)Math.PI) - 1F) * 1.4F * limbSwingAmount;
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        partdefinition.addOrReplaceChild("torso", CubeListBuilder.create().texOffs(0, 0).addBox(-3.0F, -7.0F, -3.0F, 6.0F, 4.0F, 7.0F), PartPose.offset(0.0F, 24.0F, 0.0F));
        partdefinition.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 22).addBox(-1.5F, -8.0F, -1.5F, 3.0F, 7.0F, 3.0F), PartPose.offsetAndRotation(0.0F, 20.0F, -1.0F, 0.1745F, 0.0F, 0.0F));
        partdefinition.addOrReplaceChild("beak", CubeListBuilder.create().texOffs(9, 12).addBox(-0.5F, -6.0F, -2.5F, 1.0F, 1.0F, 1.0F), PartPose.offsetAndRotation(0.0F, 20.0F, -1.0F, 0.1745F, 0.0F, 0.0F));
        partdefinition.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(0, 12).addBox(1.0F, -4.0F, 0.0F, 1.0F, 4.0F, 1.0F), PartPose.offset(0.0F, 24.0F, 0.0F));
        partdefinition.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(0, 0).addBox(-2.0F, -4.0F, 0.0F, 1.0F, 4.0F, 1.0F), PartPose.offset(0.0F, 24.0F, 0.0F));
        partdefinition.addOrReplaceChild("left_wing", CubeListBuilder.create().texOffs(15, 12).addBox(0.0F, 0.0F, -2.0F, 1.0F, 3.0F, 6.0F), PartPose.offset(3.0F, 17.0F, 0.0F));
        partdefinition.addOrReplaceChild("right_wing", CubeListBuilder.create().texOffs(0, 12).addBox(-1.0F, 0.0F, -2.0F, 1.0F, 3.0F, 6.0F), PartPose.offset(-3.0F, 17.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 32, 32);
    }
}
