package com.verdantartifice.primalmagick.common.items.misc;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

/**
 * Definition of a block item for a spellcrafting altar.
 * 
 * @author Daedalus4096
 */
public abstract class SpellcraftingAltarBlockItem extends BlockItem {

    public SpellcraftingAltarBlockItem(Block block, Item.Properties properties) {
        super(block, properties);
    }

}
