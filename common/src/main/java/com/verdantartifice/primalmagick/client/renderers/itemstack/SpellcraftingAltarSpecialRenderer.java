package com.verdantartifice.primalmagick.client.renderers.itemstack;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.mojang.serialization.MapCodec;
import com.verdantartifice.primalmagick.client.renderers.models.ModelLayersPM;
import com.verdantartifice.primalmagick.client.renderers.tile.model.SpellcraftingAltarRingModel;
import com.verdantartifice.primalmagick.common.util.ResourceUtils;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.special.NoDataSpecialModelRenderer;
import net.minecraft.client.resources.model.sprite.SpriteGetter;
import net.minecraft.client.resources.model.sprite.SpriteId;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3fc;

import java.util.function.Consumer;

/**
 * Custom item stack renderer for spellcrafting altars.
 */
public class SpellcraftingAltarSpecialRenderer implements NoDataSpecialModelRenderer {
    /** Client item model (assets/primalmagick/items/spellcrafting_altar_base.json) used to draw the altar base */
    public static final Identifier BASE_MODEL = ResourceUtils.loc("spellcrafting_altar_base");
    // Stitched into the blocks atlas under entity/...; the block entities mapper supplies that prefix
    private static final SpriteId RING_TEXTURE = Sheets.BLOCK_ENTITIES_MAPPER.apply(ResourceUtils.loc("spellcrafting_altar/spellcrafting_altar_ring"));

    private final SpriteGetter sprites;
    protected final SpellcraftingAltarRingModel model;

    public SpellcraftingAltarSpecialRenderer(SpriteGetter sprites, SpellcraftingAltarRingModel model) {
        this.sprites = sprites;
        this.model = model;
    }

    @Override
    public void submit(@NotNull PoseStack poseStack, @NotNull SubmitNodeCollector submitNodeCollector, int lightCoords, int overlayCoords, boolean hasFoil, int outlineColor) {
        // Draw the altar base
        SubModelRenderHelper.submitItemModel(BASE_MODEL, ItemStack.EMPTY, poseStack, submitNodeCollector, lightCoords, overlayCoords, hasFoil, outlineColor);

        // Draw the altar ring
        poseStack.pushPose();
        poseStack.translate(0.5D, 0D, 0.5D);
        poseStack.translate(0D, 2.4D, 0D);    // Model position correction
        poseStack.mulPose(Axis.YP.rotationDegrees(90F));  // Model rotation correction
        poseStack.scale(1.0F, -1.0F, -1.0F);
        submitNodeCollector.submitModelPart(this.model.root(), poseStack, RING_TEXTURE.renderType(RenderTypes::entitySolid), lightCoords, overlayCoords,
                this.sprites.get(RING_TEXTURE), false, hasFoil, -1, null, outlineColor);
        poseStack.popPose();
    }

    @Override
    public void getExtents(@NotNull Consumer<Vector3fc> consumer) {
        SubModelRenderHelper.unitCubeExtents(consumer);
        PoseStack poseStack = new PoseStack();
        this.model.root().getExtentsForGui(poseStack, consumer);
    }

    public record Unbaked() implements NoDataSpecialModelRenderer.Unbaked {
        public static final SpellcraftingAltarSpecialRenderer.Unbaked INSTANCE = new SpellcraftingAltarSpecialRenderer.Unbaked();
        public static final MapCodec<SpellcraftingAltarSpecialRenderer.Unbaked> MAP_CODEC = MapCodec.unit(INSTANCE);

        @Override
        public SpellcraftingAltarSpecialRenderer bake(@NotNull BakingContext bakingContext) {
            return new SpellcraftingAltarSpecialRenderer(
                    bakingContext.sprites(),
                    new SpellcraftingAltarRingModel(bakingContext.entityModelSet().bakeLayer(ModelLayersPM.SPELLCRAFTING_ALTAR_RING))
            );
        }

        @Override
        @NotNull
        public MapCodec<SpellcraftingAltarSpecialRenderer.Unbaked> type() {
            return MAP_CODEC;
        }
    }
}
