package com.veterinarium.registry;

import com.veterinarium.Veterinarium;
import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;

public class ModSounds {
    public static final DeferredRegister<SoundEvent> SOUNDS =
            DeferredRegister.create(Registries.SOUND_EVENT, Veterinarium.MODID);

    public static final DeferredHolder<SoundEvent, SoundEvent> MONITOR_BEEP = SOUNDS.register("monitor_beep",
            () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Veterinarium.MODID, "monitor_beep")));
    public static final DeferredHolder<SoundEvent, SoundEvent> SCALPEL_CUT = SOUNDS.register("scalpel_cut",
            () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Veterinarium.MODID, "scalpel_cut")));
    public static final DeferredHolder<SoundEvent, SoundEvent> SUTURE = SOUNDS.register("suture",
            () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Veterinarium.MODID, "suture")));
    public static final DeferredHolder<SoundEvent, SoundEvent> HEAL_SUCCESS = SOUNDS.register("heal_success",
            () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Veterinarium.MODID, "heal_success")));
    public static final DeferredHolder<SoundEvent, SoundEvent> MUTATION = SOUNDS.register("mutation",
            () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Veterinarium.MODID, "mutation")));
    public static final DeferredHolder<SoundEvent, SoundEvent> SPHERE_CAPTURE = SOUNDS.register("sphere_capture",
            () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Veterinarium.MODID, "sphere_capture")));
    public static final DeferredHolder<SoundEvent, SoundEvent> SPHERE_RELEASE = SOUNDS.register("sphere_release",
            () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Veterinarium.MODID, "sphere_release")));
    public static final DeferredHolder<SoundEvent, SoundEvent> URGENCY_BELL = SOUNDS.register("urgency_bell",
            () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Veterinarium.MODID, "urgency_bell")));
    public static final DeferredHolder<SoundEvent, SoundEvent> EPIDEMIC = SOUNDS.register("epidemic",
            () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Veterinarium.MODID, "epidemic")));
    public static final DeferredHolder<SoundEvent, SoundEvent> CONTAMINATOR_AMBIENT = SOUNDS.register("contaminator_ambient",
            () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Veterinarium.MODID, "contaminator_ambient")));

    public static void register(IEventBus bus) {
        SOUNDS.register(bus);
    }
}
