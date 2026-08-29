package com.verdantartifice.primalmagick.client.renderers.itemstack;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;
import org.joml.Vector3fc;

import java.util.function.Consumer;

/**
 * Helpers for special model renderers that need to draw a JSON-defined item model (e.g. a wand core or an
 * arcanometer base) as part of their output. Replaces the pre-26.1 pattern of looking up a standalone
 * {@code BakedModel} through the model manager and feeding it to {@code ItemRenderer.renderModelLists}.
 */
public final class SubModelRenderHelper {
    private SubModelRenderHelper() {}

    /**
     * Resolve the client item model registered under the given identifier (i.e. {@code assets/<ns>/items/<path>.json})
     * and submit it for rendering with the current pose. No display-context transform is applied; the caller's
     * pose is expected to already include the transform of the enclosing item. When {@code hasFoil} is set the
     * sub-model is rendered with an enchantment glint regardless of the supplied stack.
     */
    public static void submitItemModel(@NotNull Identifier modelId, @NotNull ItemStack stack, @NotNull PoseStack poseStack,
                                       @NotNull SubmitNodeCollector collector, int lightCoords, int overlayCoords, boolean hasFoil, int outlineColor) {
        Minecraft mc = Minecraft.getInstance();
        ItemModel model = mc.getModelManager().getItemModel(modelId);
        ItemStackRenderState state = new ItemStackRenderState();
        ItemStack renderStack = stack;
        if (hasFoil && !stack.hasFoil()) {
            // Sub-models are resolved from a stand-in stack, so carry the enclosing item's glint over explicitly
            renderStack = stack.isEmpty() ? new ItemStack(Items.STICK) : stack.copy();
            renderStack.set(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true);
        }
        model.update(state, renderStack, mc.getItemModelResolver(), ItemDisplayContext.NONE, mc.level, null, 0);
        state.submit(poseStack, collector, lightCoords, overlayCoords, outlineColor);
    }

    /**
     * Report the corners of the unit block cube as GUI extents. Item models rendered through
     * {@link #submitItemModel} live inside the unit cube, so this is a safe bound for GUI fitting.
     */
    public static void unitCubeExtents(@NotNull Consumer<Vector3fc> consumer) {
        for (int x = 0; x <= 1; x++) {
            for (int y = 0; y <= 1; y++) {
                for (int z = 0; z <= 1; z++) {
                    consumer.accept(new Vector3f(x, y, z));
                }
            }
        }
    }
}
