package com.veterinarium.item;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

public class DnaSyringeFilledItem extends Item {
    public DnaSyringeFilledItem(Properties props) { super(props); }
    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.literal("§5Seringue ADN - Remplie"));
        tooltip.add(Component.literal("§7Contient ADN de créature blessée"));
        tooltip.add(Component.literal("§7(Conserve pour craft sérum)"));
        tooltip.add(Component.literal("§6→ Craft Sérum Hellfire (blaze+nether wart+magma)"));
    }
}
