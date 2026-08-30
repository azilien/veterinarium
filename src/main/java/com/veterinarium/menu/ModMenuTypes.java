package com.veterinarium.menu;

import com.veterinarium.Veterinarium;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.network.IContainerFactory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModMenuTypes {
    public static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(ForgeRegistries.MENU_TYPES, Veterinarium.MODID);

    public static final RegistryObject<MenuType<OperatingTableMenu>> OPERATING_TABLE =
            MENUS.register("operating_table", () -> IForgeMenuType.create(OperatingTableMenu::new));
}
