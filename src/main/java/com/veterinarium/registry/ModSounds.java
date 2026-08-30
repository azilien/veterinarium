package com.veterinarium.registry;

import com.veterinarium.Veterinarium;
import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModSounds {
    public static final DeferredRegister<SoundEvent> SOUNDS =
            DeferredRegister.create(Registries.SOUND_EVENT, Veterinarium.MODID);

    public static final RegistryObject<SoundEvent> MONITOR_BEEP = SOUNDS.register("monitor_beep",
            () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Veterinarium.MODID, "monitor_beep")));
    public static final RegistryObject<SoundEvent> SCALPEL_CUT = SOUNDS.register("scalpel_cut",
            () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Veterinarium.MODID, "scalpel_cut")));
    public static final RegistryObject<SoundEvent> SUTURE = SOUNDS.register("suture",
            () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Veterinarium.MODID, "suture")));
    public static final RegistryObject<SoundEvent> HEAL_SUCCESS = SOUNDS.register("heal_success",
            () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Veterinarium.MODID, "heal_success")));
    public static final RegistryObject<SoundEvent> MUTATION = SOUNDS.register("mutation",
            () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Veterinarium.MODID, "mutation")));
    public static final RegistryObject<SoundEvent> SPHERE_CAPTURE = SOUNDS.register("sphere_capture",
            () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Veterinarium.MODID, "sphere_capture")));
    public static final RegistryObject<SoundEvent> SPHERE_RELEASE = SOUNDS.register("sphere_release",
            () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Veterinarium.MODID, "sphere_release")));
    public static final RegistryObject<SoundEvent> URGENCY_BELL = SOUNDS.register("urgency_bell",
            () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Veterinarium.MODID, "urgency_bell")));
    public static final RegistryObject<SoundEvent> EPIDEMIC = SOUNDS.register("epidemic",
            () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Veterinarium.MODID, "epidemic")));
    public static final RegistryObject<SoundEvent> CONTAMINATOR_AMBIENT = SOUNDS.register("contaminator_ambient",
            () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Veterinarium.MODID, "contaminator_ambient")));

    public static void register(IEventBus bus) {
        SOUNDS.register(bus);
    }
}
