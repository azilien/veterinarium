package com.veterinarium.block.entity;

import com.veterinarium.wound.WoundType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class AnalysisTableBlockEntity extends BlockEntity {
    private int lastWoundId = 0;
    private String lastTarget = "";
    private int analysesDone = 0;

    public AnalysisTableBlockEntity(BlockPos pos, BlockState state) {
        super(com.veterinarium.registry.ModBlockEntities.ANALYSIS_TABLE.get(), pos, state);
    }

    public WoundType getLastWound() { return WoundType.fromId(lastWoundId); }
    public String getLastTarget() { return lastTarget; }
    public int getAnalysesDone() { return analysesDone; }

    public void setAnalysis(WoundType wt, String target) {
        this.lastWoundId = wt.getId();
        this.lastTarget = target;
        this.analysesDone++;
        setChanged();
        if (level != null) level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
    }

    public Component getAnalysisText() {
        WoundType wt = getLastWound();
        if (lastTarget.isEmpty()) return Component.literal("§7Aucune analyse en mémoire — utilise une §bSeringue §7sur une créature, puis reviens.");
        String req = "";
        if (wt.needsAnesthetic()) req += "§dAnesthésiant ";
        if (wt.needsBandage()) req += "§eBandage ";
        if (req.isEmpty()) req = "§aStandard";
        return Component.literal("§b[Table d'Analyse] §f" + lastTarget + " → " + wt.getDisplay() + " §7(" + req.trim() + "§7) §8#" + analysesDone);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putInt("LastWound", lastWoundId);
        tag.putString("LastTarget", lastTarget);
        tag.putInt("AnalysesDone", analysesDone);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        lastWoundId = tag.getInt("LastWound");
        lastTarget = tag.getString("LastTarget");
        analysesDone = tag.getInt("AnalysesDone");
    }
}
