package com.veterinarium.menu;

import com.veterinarium.Veterinarium;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.network.IContainerFactory;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.registries.DeferredHolder;

public class ModMenuTypes {
    public static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(Registries.MENU, Veterinarium.MODID);

    public static final DeferredHolder<MenuType<?>, MenuType<OperatingTableMenu>> OPERATING_TABLE =
            MENUS.register("operating_table", () -> IMenuTypeExtension.create(OperatingTableMenu::new));
}
