package com.verdantartifice.primalmagick.client.renderers.itemstack;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.verdantartifice.primalmagick.common.items.wands.IHasWandComponents;
import com.verdantartifice.primalmagick.common.wands.WandCap;
import com.verdantartifice.primalmagick.common.wands.WandCore;
import com.verdantartifice.primalmagick.common.wands.WandGem;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3fc;

import java.util.function.Consumer;

/**
 * Custom item stack renderer for a modular wand or staff. Renders the wand's core, cap, and gem
 * component models, as determined by the item stack's appearance data.
 *
 * @see com.verdantartifice.primalmagick.common.items.wands.ModularWandItem
 * @see com.verdantartifice.primalmagick.common.items.wands.ModularStaffItem
 */
public class ModularWandSpecialRenderer implements SpecialModelRenderer<ModularWandSpecialRenderer.Appearance> {
    protected final boolean staff;

    public ModularWandSpecialRenderer(boolean staff) {
        this.staff = staff;
    }

    @Override
    public void submit(
            @Nullable Appearance appearance,
            @NotNull PoseStack poseStack,
            @NotNull SubmitNodeCollector submitNodeCollector,
            int lightCoords,
            int overlayCoords,
            boolean hasFoil,
            int outlineColor) {
        if (appearance == null) {
            return;
        }
        if (appearance.core() != null) {
            // Render the wand core
            SubModelRenderHelper.submitItemModel(
                    this.staff ? appearance.core().getStaffModelResourceLocationNamespace() : appearance.core().getWandModelResourceLocationNamespace(),
                    ItemStack.EMPTY, poseStack, submitNodeCollector, lightCoords, overlayCoords, hasFoil, outlineColor);
        }
        if (appearance.cap() != null) {
            // Render the wand cap
            SubModelRenderHelper.submitItemModel(
                    this.staff ? appearance.cap().getStaffModelResourceLocationNamespace() : appearance.cap().getWandModelResourceLocationNamespace(),
                    ItemStack.EMPTY, poseStack, submitNodeCollector, lightCoords, overlayCoords, hasFoil, outlineColor);
        }
        if (appearance.gem() != null) {
            // Render the wand gem
            SubModelRenderHelper.submitItemModel(
                    appearance.gem().getModelResourceLocationNamespace(),
                    ItemStack.EMPTY, poseStack, submitNodeCollector, lightCoords, overlayCoords, hasFoil, outlineColor);
        }
    }

    @Override
    public void getExtents(@NotNull Consumer<Vector3fc> consumer) {
        SubModelRenderHelper.unitCubeExtents(consumer);
    }

    @Override
    public @Nullable Appearance extractArgument(@NotNull ItemStack itemStack) {
        if (itemStack.getItem() instanceof IHasWandComponents wand) {
            return new Appearance(wand.getWandCoreAppearance(itemStack), wand.getWandCapAppearance(itemStack), wand.getWandGemAppearance(itemStack));
        } else {
            return null;
        }
    }

    /**
     * The visible component set of a modular wand or staff, extracted from an item stack.
     */
    public record Appearance(@Nullable WandCore core, @Nullable WandCap cap, @Nullable WandGem gem) {}

    public record Unbaked(boolean staff) implements SpecialModelRenderer.Unbaked<Appearance> {
        public static final MapCodec<ModularWandSpecialRenderer.Unbaked> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Codec.BOOL.optionalFieldOf("staff", false).forGetter(ModularWandSpecialRenderer.Unbaked::staff)
            ).apply(instance, ModularWandSpecialRenderer.Unbaked::new));

        @Override
        public ModularWandSpecialRenderer bake(@NotNull BakingContext bakingContext) {
            return new ModularWandSpecialRenderer(this.staff);
        }

        @Override
        @NotNull
        public MapCodec<ModularWandSpecialRenderer.Unbaked> type() {
            return MAP_CODEC;
        }
    }
}
