package com.veterinarium.menu;

import com.veterinarium.block.entity.OperatingTableBlockEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.SlotItemHandler;

public class OperatingTableMenu extends AbstractContainerMenu {
    private final OperatingTableBlockEntity blockEntity;

    public OperatingTableMenu(int containerId, Inventory playerInventory, FriendlyByteBuf extraData) {
        this(containerId, playerInventory, playerInventory.player.level().getBlockEntity(extraData.readBlockPos()));
    }

    public OperatingTableMenu(int containerId, Inventory playerInventory, BlockEntity blockEntity) {
        super(ModMenuTypes.OPERATING_TABLE.get(), containerId);
        this.blockEntity = (OperatingTableBlockEntity) blockEntity;
        this.blockEntity.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(h -> {
            this.addSlot(new SlotItemHandler(h, 0, 48, 35));
            this.addSlot(new SlotItemHandler(h, 1, 84, 35));
            this.addSlot(new SlotItemHandler(h, 2, 120, 35));
        });
        addPlayerInventory(playerInventory);
        addPlayerHotbar(playerInventory);
    }

    @Override
    public boolean stillValid(Player player) {
        return blockEntity.getLevel() != null && blockEntity.getBlockPos() != null
                && player.distanceToSqr(blockEntity.getBlockPos().getX() + 0.5, blockEntity.getBlockPos().getY() + 0.5, blockEntity.getBlockPos().getZ() + 0.5) <= 64.0;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack result = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot != null && slot.hasItem()) {
            ItemStack stackInSlot = slot.getItem();
            result = stackInSlot.copy();
            int containerSlots = 3;
            if (index < containerSlots) {
                if (!this.moveItemStackTo(stackInSlot, containerSlots, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(stackInSlot, 0, containerSlots, false)) {
                return ItemStack.EMPTY;
            }
            if (stackInSlot.getCount() == 0) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
            if (stackInSlot.getCount() == result.getCount()) {
                return ItemStack.EMPTY;
            }
            slot.onTake(player, stackInSlot);
        }
        return result;
    }

    private void addPlayerInventory(Inventory playerInventory) {
        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 9; j++)
                this.addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
    }

    private void addPlayerHotbar(Inventory playerInventory) {
        for (int i = 0; i < 9; i++)
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 142));
    }
}
