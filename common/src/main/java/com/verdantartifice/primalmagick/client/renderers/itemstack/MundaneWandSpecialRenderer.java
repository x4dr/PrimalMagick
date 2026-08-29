package com.verdantartifice.primalmagick.client.renderers.itemstack;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.MapCodec;
import com.verdantartifice.primalmagick.common.util.ResourceUtils;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.special.NoDataSpecialModelRenderer;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3fc;

import java.util.function.Consumer;

/**
 * Custom item stack renderer for a mundane wand.
 *
 * @see com.verdantartifice.primalmagick.common.items.wands.MundaneWandItem
 */
public class MundaneWandSpecialRenderer implements NoDataSpecialModelRenderer {
    private static final Identifier CORE_MODEL = ResourceUtils.loc("mundane_wand_core");

    @Override
    public void submit(@NotNull PoseStack poseStack, @NotNull SubmitNodeCollector submitNodeCollector, int lightCoords, int overlayCoords, boolean hasFoil, int outlineColor) {
        // Render the wand core
        SubModelRenderHelper.submitItemModel(CORE_MODEL, ItemStack.EMPTY, poseStack, submitNodeCollector, lightCoords, overlayCoords, hasFoil, outlineColor);
    }

    @Override
    public void getExtents(@NotNull Consumer<Vector3fc> consumer) {
        SubModelRenderHelper.unitCubeExtents(consumer);
    }

    public record Unbaked() implements NoDataSpecialModelRenderer.Unbaked {
        public static final MundaneWandSpecialRenderer.Unbaked INSTANCE = new MundaneWandSpecialRenderer.Unbaked();
        public static final MapCodec<MundaneWandSpecialRenderer.Unbaked> MAP_CODEC = MapCodec.unit(INSTANCE);

        @Override
        public MundaneWandSpecialRenderer bake(@NotNull BakingContext bakingContext) {
            return new MundaneWandSpecialRenderer();
        }

        @Override
        @NotNull
        public MapCodec<MundaneWandSpecialRenderer.Unbaked> type() {
            return MAP_CODEC;
        }
    }
}
