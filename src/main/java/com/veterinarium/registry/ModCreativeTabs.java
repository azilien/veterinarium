package com.veterinarium.registry;

import com.veterinarium.Veterinarium;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Veterinarium.MODID);

    public static final RegistryObject<CreativeModeTab> VETERINARIUM_TAB = CREATIVE_MODE_TABS.register("veterinarium_tab",
            () -> CreativeModeTab.builder()
                    .icon(() -> new ItemStack(ModItems.SCALPEL.get()))
                    .title(Component.translatable("creativetab.veterinarium"))
                    .displayItems((pParameters, pOutput) -> {
                        pOutput.accept(ModItems.SCALPEL.get());
                        pOutput.accept(ModItems.SUTURE_KIT.get());
                        pOutput.accept(ModItems.SYRINGE.get());
                        pOutput.accept(ModItems.MEDICAL_FILE.get());
                        pOutput.accept(ModItems.BANDAGE.get());
                        pOutput.accept(ModItems.ANESTHETIC.get());
                        pOutput.accept(ModItems.OPERATING_TABLE.get());
                        pOutput.accept(ModItems.ANALYSIS_TABLE.get());
                        pOutput.accept(ModItems.INFIRMARY.get());
                        pOutput.accept(ModItems.HOSPITAL_HUT.get());
                        pOutput.accept(ModItems.WOUNDED_WOLF_SPAWN_EGG.get());
                        pOutput.accept(ModItems.WOUNDED_CAT_SPAWN_EGG.get());
                        pOutput.accept(ModItems.WOUNDED_HORSE_SPAWN_EGG.get());
                        pOutput.accept(ModItems.WOUNDED_FOX_SPAWN_EGG.get());
                        pOutput.accept(ModItems.WOUNDED_VILLAGER_SPAWN_EGG.get());
                    })
                    .build());

    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
    }
}
