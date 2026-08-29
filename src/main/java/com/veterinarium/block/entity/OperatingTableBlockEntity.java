package com.veterinarium.block.entity;

import com.veterinarium.wound.WoundType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.ItemStackHandler;

public class OperatingTableBlockEntity extends BlockEntity {
    private final ItemStackHandler handler = new ItemStackHandler(3) {
        @Override
        protected void onContentsChanged(int slot) { setChanged(); if (level!=null) level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3); }
        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            if (slot==0) return stack.is(com.veterinarium.registry.ModItems.BANDAGE.get());
            if (slot==1) return stack.is(com.veterinarium.registry.ModItems.ANESTHETIC.get());
            if (slot==2) return stack.is(com.veterinarium.registry.ModItems.COMPRESSION_BANDAGE.get());
            return false;
        }
    };
    private LazyOptional<ItemStackHandler> lazyHandler = LazyOptional.of(() -> handler);

    public OperatingTableBlockEntity(BlockPos pos, BlockState state) {
        super(com.veterinarium.registry.ModBlockEntities.OPERATING_TABLE.get(), pos, state);
    }

    public ItemStackHandler getHandler() { return handler; }

    public boolean consumeIfNeeded(WoundType wt, boolean isScalpel) {
        // Scalpel needs anesthetic for FRACTURE/INFECTION/BRULURE
        // Suture needs bandage for HEMORRAGIE/INFECTION/BRULURE/SAIGNEMENT
        boolean consumed = false;
        if (isScalpel) {
            if (!wt.needsAnesthetic()) return true;
            for (int i=0;i<handler.getSlots();i++) {
                ItemStack s = handler.getStackInSlot(i);
                if (!s.isEmpty() && s.is(com.veterinarium.registry.ModItems.ANESTHETIC.get())) { s.shrink(1); consumed = true; break; }
            }
        } else {
            if (!wt.needsBandage()) return true;
            for (int i=0;i<handler.getSlots();i++) {
                ItemStack s = handler.getStackInSlot(i);
                if (!s.isEmpty() && s.is(com.veterinarium.registry.ModItems.BANDAGE.get())) { s.shrink(1); consumed = true; break; }
            }
        }
        if (consumed && level instanceof net.minecraft.server.level.ServerLevel sl) {
            sl.sendParticles(net.minecraft.core.particles.ParticleTypes.HAPPY_VILLAGER, worldPosition.getX()+0.5, worldPosition.getY()+1.2, worldPosition.getZ()+0.5, 3, 0.3, 0.3, 0.3, 0.1);
        }
        return consumed;
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put("Inv", handler.serializeNBT(registries));
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains("Inv")) handler.deserializeNBT(registries, tag.getCompound("Inv"));
    }

    @Override
    public void onLoad() {
        super.onLoad();
        lazyHandler = LazyOptional.of(() -> handler);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        lazyHandler.invalidate();
    }

    @SuppressWarnings("unchecked")
    public <T> LazyOptional<T> getCapability(net.minecraftforge.common.capabilities.Capability<T> cap, net.minecraft.core.Direction side) {
        if (cap == net.minecraftforge.common.capabilities.ForgeCapabilities.ITEM_HANDLER) return lazyHandler.cast();
        return super.getCapability(cap, side);
    }
}
