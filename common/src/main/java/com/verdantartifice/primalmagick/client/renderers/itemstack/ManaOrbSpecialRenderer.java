package com.verdantartifice.primalmagick.client.renderers.itemstack;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.verdantartifice.primalmagick.client.renderers.itemstack.model.ManaOrbAdeptModel;
import com.verdantartifice.primalmagick.client.renderers.itemstack.model.ManaOrbApprenticeModel;
import com.verdantartifice.primalmagick.client.renderers.itemstack.model.ManaOrbArchmageModel;
import com.verdantartifice.primalmagick.client.renderers.itemstack.model.ManaOrbNuggetModel;
import com.verdantartifice.primalmagick.client.renderers.itemstack.model.ManaOrbWizardModel;
import com.verdantartifice.primalmagick.client.renderers.models.ModelLayersPM;
import com.verdantartifice.primalmagick.common.items.tools.ManaOrbItem;
import com.verdantartifice.primalmagick.common.misc.DeviceTier;
import com.verdantartifice.primalmagick.common.util.ResourceUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3fc;

import java.util.function.Consumer;

/**
 * Custom item stack renderer for a mana orb.
 */
public class ManaOrbSpecialRenderer implements SpecialModelRenderer<DeviceTier> {
    protected static final Identifier TEXTURE_APPRENTICE = ResourceUtils.loc("textures/entity/mana_orb/apprentice.png");
    protected static final Identifier TEXTURE_ADEPT = ResourceUtils.loc("textures/entity/mana_orb/adept.png");
    protected static final Identifier TEXTURE_WIZARD = ResourceUtils.loc("textures/entity/mana_orb/wizard.png");
    protected static final Identifier TEXTURE_ARCHMAGE = ResourceUtils.loc("textures/entity/mana_orb/archmage.png");
    protected static final Identifier TEXTURE_NUGGET = ResourceUtils.loc("textures/entity/mana_orb/nugget.png");

    protected static final int BOB_CYCLE_TIME_TICKS = 200;

    protected final Model<?> apprenticeCoreModel;
    protected final Model<?> adeptCoreModel;
    protected final Model<?> wizardCoreModel;
    protected final Model<?> archmageCoreModel;
    protected final Model<?> nuggetModel;
    protected final boolean animate;

    public ManaOrbSpecialRenderer(Model<?> apprenticeCoreModel, Model<?> adeptCoreModel, Model<?> wizardCoreModel, Model<?> archmageCoreModel,
                                  Model<?> nuggetModel, boolean animate) {
        this.apprenticeCoreModel = apprenticeCoreModel;
        this.adeptCoreModel = adeptCoreModel;
        this.wizardCoreModel = wizardCoreModel;
        this.archmageCoreModel = archmageCoreModel;
        this.nuggetModel = nuggetModel;
        this.animate = animate;
    }

    @Override
    public void submit(
            @Nullable DeviceTier tier,
            @NotNull PoseStack poseStack,
            @NotNull SubmitNodeCollector submitNodeCollector,
            int lightCoords,
            int overlayCoords,
            boolean hasFoil,
            int outlineColor) {
        Minecraft mc = Minecraft.getInstance();
        long time = mc.level == null ? 0L : mc.level.getGameTime();
        double partialTime = time + (double)mc.getDeltaTracker().getGameTimeDeltaPartialTick(false);
        double bobDelta = 0.0625D * Math.sin(partialTime * (2D * Math.PI / (double)BOB_CYCLE_TIME_TICKS));
        int rot = 2 * (int)(time % 360);

        Model<?> coreModel = this.getCoreModel(tier);
        Identifier coreTexture = getCoreTexture(tier);

        poseStack.pushPose();
        poseStack.scale(1.0F, -1.0F, -1.0F);

        // Draw the orb core
        poseStack.pushPose();
        submitNodeCollector.submitModelPart(coreModel.root(), poseStack, coreModel.renderType(coreTexture), lightCoords, overlayCoords, null, false, hasFoil, -1, null, outlineColor);
        poseStack.popPose();

        // Draw the orbiting nuggets
        for (int nuggetIndex = 0; nuggetIndex < 4; nuggetIndex++) {
            poseStack.pushPose();
            poseStack.mulPose(Axis.YP.rotationDegrees(45 + (90 * nuggetIndex)));
            if (this.animate) {
                poseStack.mulPose(Axis.YP.rotationDegrees(rot));
            }
            poseStack.translate(0.25D, -0.0625D, 0D);
            if (this.animate) {
                poseStack.translate(bobDelta, 0D, 0D);
            }
            submitNodeCollector.submitModelPart(this.nuggetModel.root(), poseStack, this.nuggetModel.renderType(TEXTURE_NUGGET), lightCoords, overlayCoords, null, false, hasFoil, -1, null, outlineColor);
            poseStack.popPose();
        }

        poseStack.popPose();
    }

    @Override
    public void getExtents(@NotNull Consumer<Vector3fc> consumer) {
        PoseStack poseStack = new PoseStack();
        poseStack.scale(1.0F, -1.0F, -1.0F);
        this.apprenticeCoreModel.root().getExtentsForGui(poseStack, consumer);
        this.adeptCoreModel.root().getExtentsForGui(poseStack, consumer);
        this.wizardCoreModel.root().getExtentsForGui(poseStack, consumer);
        this.archmageCoreModel.root().getExtentsForGui(poseStack, consumer);
        for (int nuggetIndex = 0; nuggetIndex < 4; nuggetIndex++) {
            poseStack.pushPose();
            poseStack.mulPose(Axis.YP.rotationDegrees(45 + (90 * nuggetIndex)));
            poseStack.translate(0.25D, -0.0625D, 0D);
            this.nuggetModel.root().getExtentsForGui(poseStack, consumer);
            poseStack.popPose();
        }
    }

    @Override
    public @Nullable DeviceTier extractArgument(@NotNull ItemStack itemStack) {
        if (itemStack.getItem() instanceof ManaOrbItem manaOrbItem) {
            return manaOrbItem.getDeviceTier();
        } else {
            return null;
        }
    }

    private Model<?> getCoreModel(@Nullable DeviceTier tier) {
        return tier == null ? this.apprenticeCoreModel : switch (tier) {
            case BASIC -> this.apprenticeCoreModel;
            case ENCHANTED -> this.adeptCoreModel;
            case FORBIDDEN -> this.wizardCoreModel;
            case HEAVENLY, CREATIVE -> this.archmageCoreModel;
        };
    }

    private static Identifier getCoreTexture(@Nullable DeviceTier tier) {
        return tier == null ? TEXTURE_APPRENTICE : switch (tier) {
            case BASIC -> TEXTURE_APPRENTICE;
            case ENCHANTED -> TEXTURE_ADEPT;
            case FORBIDDEN -> TEXTURE_WIZARD;
            case HEAVENLY, CREATIVE -> TEXTURE_ARCHMAGE;
        };
    }

    public record Unbaked(boolean animate) implements SpecialModelRenderer.Unbaked<DeviceTier> {
        public static final MapCodec<ManaOrbSpecialRenderer.Unbaked> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Codec.BOOL.optionalFieldOf("animate", true).forGetter(ManaOrbSpecialRenderer.Unbaked::animate)
            ).apply(instance, ManaOrbSpecialRenderer.Unbaked::new));

        @Override
        public ManaOrbSpecialRenderer bake(@NotNull BakingContext bakingContext) {
            return new ManaOrbSpecialRenderer(
                    new ManaOrbApprenticeModel(bakingContext.entityModelSet().bakeLayer(ModelLayersPM.MANA_ORB_APPRENTICE)),
                    new ManaOrbAdeptModel(bakingContext.entityModelSet().bakeLayer(ModelLayersPM.MANA_ORB_ADEPT)),
                    new ManaOrbWizardModel(bakingContext.entityModelSet().bakeLayer(ModelLayersPM.MANA_ORB_WIZARD)),
                    new ManaOrbArchmageModel(bakingContext.entityModelSet().bakeLayer(ModelLayersPM.MANA_ORB_ARCHMAGE)),
                    new ManaOrbNuggetModel(bakingContext.entityModelSet().bakeLayer(ModelLayersPM.MANA_ORB_NUGGET)),
                    this.animate
            );
        }

        @Override
        @NotNull
        public MapCodec<ManaOrbSpecialRenderer.Unbaked> type() {
            return MAP_CODEC;
        }
    }
}
