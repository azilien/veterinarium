package com.veterinarium.registry;

import com.veterinarium.Veterinarium;
import com.veterinarium.item.MedicalFileItem;
import com.veterinarium.item.ScalpelItem;
import com.veterinarium.item.SutureKitItem;
import com.veterinarium.item.SyringeItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, Veterinarium.MODID);

    // BlockItems
    public static final RegistryObject<Item> OPERATING_TABLE = ITEMS.register("operating_table",
            () -> new BlockItem(ModBlocks.OPERATING_TABLE.get(), new Item.Properties()));
    public static final RegistryObject<Item> ANALYSIS_TABLE = ITEMS.register("analysis_table",
            () -> new BlockItem(ModBlocks.ANALYSIS_TABLE.get(), new Item.Properties()));
    public static final RegistryObject<Item> INFIRMARY = ITEMS.register("infirmary",
            () -> new BlockItem(ModBlocks.INFIRMARY.get(), new Item.Properties()));

    // Tools
    public static final RegistryObject<Item> SCALPEL = ITEMS.register("scalpel",
            () -> new ScalpelItem(new Item.Properties().durability(250)));

    public static final RegistryObject<Item> SUTURE_KIT = ITEMS.register("suture_kit",
            () -> new SutureKitItem(new Item.Properties().durability(64)));

    public static final RegistryObject<Item> SYRINGE = ITEMS.register("syringe",
            () -> new SyringeItem(new Item.Properties().durability(32)));

    public static final RegistryObject<Item> MEDICAL_FILE = ITEMS.register("medical_file",
            () -> new MedicalFileItem(new Item.Properties().stacksTo(1)));

    // Consumables / Crafting
    public static final RegistryObject<Item> BANDAGE = ITEMS.register("bandage",
            () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> ANESTHETIC = ITEMS.register("anesthetic",
            () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> WOUNDED_WOLF_SPAWN_EGG = ITEMS.register("wounded_wolf_spawn_egg",
            () -> new ForgeSpawnEggItem(ModEntities.WOUNDED_WOLF, 0xD7C9B5, 0xB22222, new Item.Properties()));

    public static final RegistryObject<Item> WOUNDED_CAT_SPAWN_EGG = ITEMS.register("wounded_cat_spawn_egg",
            () -> new ForgeSpawnEggItem(ModEntities.WOUNDED_CAT, 0xE6C8A0, 0xFF5555, new Item.Properties()));

    public static final RegistryObject<Item> WOUNDED_HORSE_SPAWN_EGG = ITEMS.register("wounded_horse_spawn_egg",
            () -> new ForgeSpawnEggItem(ModEntities.WOUNDED_HORSE, 0xF0E6D2, 0xAA0000, new Item.Properties()));

    public static final RegistryObject<Item> HOSPITAL_HUT = ITEMS.register("hospital_hut",
            () -> new BlockItem(ModBlocks.HOSPITAL_HUT.get(), new Item.Properties()));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
