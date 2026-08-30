package com.veterinarium.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

import java.util.List;

public class HospitalHutBlockEntity extends BlockEntity {
    private int healedCount = 0;
    private int tickCounter = 0;
    private int hutLevel = 1; // 1-5
    // Contrats journaliers
    private int contractDay = -1;
    private int contractType = 0; // 0=HEAL_ANY,1=WOLF,2=CAT,3=HORSE,4=FOX,5=VILLAGER,6=DRAKE,7=INFECTION,8=CAPTURE
    private int contractNeeded = 2;
    private int contractProgress = 0;
    private boolean contractClaimed = false;
    // Stock healer (personnel soignant)
    private final net.minecraftforge.items.ItemStackHandler healerInv = new net.minecraftforge.items.ItemStackHandler(6) {
        @Override protected void onContentsChanged(int slot) { setChanged(); if (level!=null) level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3); }
        @Override public boolean isItemValid(int slot, net.minecraft.world.item.ItemStack stack) {
            return stack.is(com.veterinarium.registry.ModItems.BANDAGE.get()) || stack.is(com.veterinarium.registry.ModItems.ANESTHETIC.get()) || stack.is(com.veterinarium.registry.ModItems.VET_SPHERE.get()) || stack.is(net.minecraft.world.item.Items.EMERALD);
        }
    };
    private net.minecraftforge.common.util.LazyOptional<net.minecraftforge.items.ItemStackHandler> healerHandler = net.minecraftforge.common.util.LazyOptional.of(() -> healerInv);
    public net.minecraftforge.items.ItemStackHandler getHealerInv() { return healerInv; }

    public HospitalHutBlockEntity(BlockPos pos, BlockState state) {
        super(com.veterinarium.registry.ModBlockEntities.HOSPITAL_HUT.get(), pos, state);
    }

    public int getRadius() { return 12 + hutLevel * 4; } // 16,20,24,28,32
    public float getHealAmount() { return 1.0f + hutLevel * 0.5f; } // 1.5,2.0,2.5,3.0,3.5
    public int getHutLevel() { return hutLevel; }
    public boolean tryUpgrade(net.minecraft.world.entity.player.Player player) {
        if (hutLevel >= 5) return false;
        int needBricks = switch (hutLevel) { case 1 -> 8; case 2 -> 8; case 3 -> 12; case 4 -> 16; default -> 99; };
        int needBandage = switch (hutLevel) { case 1 -> 4; case 2 -> 8; case 3 -> 12; case 4 -> 16; default -> 99; };
        int needDiamond = (hutLevel == 2) ? 1 : 0;
        int needEmerald = switch (hutLevel) { case 3 -> 2; case 4 -> 4; default -> 0; };
        int needNetherite = (hutLevel == 4) ? 1 : 0;
        int hasBricks = countItem(player, net.minecraft.world.item.Items.BRICK);
        int hasBandage = countItem(player, com.veterinarium.registry.ModItems.BANDAGE.get());
        int hasDiamond = countItem(player, net.minecraft.world.item.Items.DIAMOND);
        int hasEmerald = countItem(player, net.minecraft.world.item.Items.EMERALD);
        int hasNetherite = countItem(player, net.minecraft.world.item.Items.NETHERITE_INGOT);
        if (hasBricks < needBricks || hasBandage < needBandage || hasDiamond < needDiamond || hasEmerald < needEmerald || hasNetherite < needNetherite) return false;
        consumeItem(player, net.minecraft.world.item.Items.BRICK, needBricks);
        consumeItem(player, com.veterinarium.registry.ModItems.BANDAGE.get(), needBandage);
        if (needDiamond>0) consumeItem(player, net.minecraft.world.item.Items.DIAMOND, needDiamond);
        if (needEmerald>0) consumeItem(player, net.minecraft.world.item.Items.EMERALD, needEmerald);
        if (needNetherite>0) consumeItem(player, net.minecraft.world.item.Items.NETHERITE_INGOT, needNetherite);
        hutLevel++;
        setChanged();
        if (level != null) level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        return true;
    }
    private int countItem(net.minecraft.world.entity.player.Player p, net.minecraft.world.item.Item item) {
        int c=0; for (var s : p.getInventory().items) if (!s.isEmpty() && s.is(item)) c+=s.getCount();
        for (var s : p.getInventory().offhand) if (!s.isEmpty() && s.is(item)) c+=s.getCount();
        return c;
    }
    private void consumeItem(net.minecraft.world.entity.player.Player p, net.minecraft.world.item.Item item, int need) {
        for (var s : p.getInventory().items) {
            if (s.isEmpty() || !s.is(item)) continue;
            int take = Math.min(s.getCount(), need);
            s.shrink(take); need-=take; if (need<=0) return;
        }
        for (var s : p.getInventory().offhand) {
            if (s.isEmpty() || !s.is(item)) continue;
            int take = Math.min(s.getCount(), need);
            s.shrink(take); need-=take; if (need<=0) return;
        }
    }
    private boolean consumeHealerStock(int needBandage, int needAnesthetic) {
        int hasB=0, hasA=0;
        for (int i=0;i<healerInv.getSlots();i++) {
            var s = healerInv.getStackInSlot(i);
            if (s.is(com.veterinarium.registry.ModItems.BANDAGE.get())) hasB += s.getCount();
            if (s.is(com.veterinarium.registry.ModItems.ANESTHETIC.get())) hasA += s.getCount();
        }
        if (hasB < needBandage || hasA < needAnesthetic) return false;
        // consomme
        for (int i=0;i<healerInv.getSlots() && (needBandage>0 || needAnesthetic>0);i++) {
            var s = healerInv.getStackInSlot(i);
            if (s.is(com.veterinarium.registry.ModItems.BANDAGE.get()) && needBandage>0) {
                int take = Math.min(s.getCount(), needBandage);
                s.shrink(take); needBandage-=take;
            }
            if (s.is(com.veterinarium.registry.ModItems.ANESTHETIC.get()) && needAnesthetic>0) {
                int take = Math.min(s.getCount(), needAnesthetic);
                s.shrink(take); needAnesthetic-=take;
            }
        }
        return true;
    }
    private boolean consumeFromNearbyTable(Level lvl, BlockPos center, int needBandage, int needAnesthetic) {
        for (int dx=-5;dx<=5;dx++) for(int dy=-2;dy<=2;dy++) for(int dz=-5;dz<=5;dz++) {
            var be = lvl.getBlockEntity(center.offset(dx,dy,dz));
            if (be instanceof com.veterinarium.block.entity.OperatingTableBlockEntity table) {
                boolean hasB=false, hasA=false;
                int slotB=-1, slotA=-1;
                for(int i=0;i<table.getHandler().getSlots();i++) {
                    var s = table.getHandler().getStackInSlot(i);
                    if (s.is(com.veterinarium.registry.ModItems.BANDAGE.get()) && s.getCount()>=needBandage) { hasB=true; slotB=i; }
                    if (s.is(com.veterinarium.registry.ModItems.ANESTHETIC.get()) && s.getCount()>=needAnesthetic) { hasA=true; slotA=i; }
                }
                if (hasB && hasA) {
                    table.getHandler().extractItem(slotB, needBandage, false);
                    table.getHandler().extractItem(slotA, needAnesthetic, false);
                    return true;
                }
            }
            // aussi chest à proximité
            if (be instanceof net.minecraft.world.level.block.entity.ChestBlockEntity chest) {
                var handler = chest.getCapability(net.minecraftforge.common.capabilities.ForgeCapabilities.ITEM_HANDLER, null);
                if (handler.isPresent()) {
                    var h = handler.orElse(null);
                    if (h != null) {
                        int foundB=0, foundA=0;
                        for(int i=0;i<h.getSlots();i++) {
                            var s = h.getStackInSlot(i);
                            if (s.is(com.veterinarium.registry.ModItems.BANDAGE.get())) foundB+=s.getCount();
                            if (s.is(com.veterinarium.registry.ModItems.ANESTHETIC.get())) foundA+=s.getCount();
                        }
                        if (foundB>=needBandage && foundA>=needAnesthetic) {
                            // consomme (simplifié: extrait)
                            int needB=needBandage, needAn=needAnesthetic;
                            for(int i=0;i<h.getSlots() && (needB>0 || needAn>0);i++) {
                                var s = h.getStackInSlot(i);
                                if (s.is(com.veterinarium.registry.ModItems.BANDAGE.get()) && needB>0) { int take=Math.min(s.getCount(), needB); h.extractItem(i, take, false); needB-=take; }
                                if (s.is(com.veterinarium.registry.ModItems.ANESTHETIC.get()) && needAn>0) { int take=Math.min(s.getCount(), needAn); h.extractItem(i, take, false); needAn-=take; }
                            }
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    public String getContractName() {
        return switch(contractType) {
            case 1 -> Component.translatable("message.veterinarium.hut.contract.type.wolves").getString();
            case 2 -> Component.translatable("message.veterinarium.hut.contract.type.cats").getString();
            case 3 -> Component.translatable("message.veterinarium.hut.contract.type.horses").getString();
            case 4 -> Component.translatable("message.veterinarium.hut.contract.type.foxes").getString();
            case 5 -> Component.translatable("message.veterinarium.hut.contract.type.villagers").getString();
            case 6 -> Component.translatable("message.veterinarium.hut.contract.type.drakes").getString();
            case 7 -> Component.translatable("message.veterinarium.hut.contract.type.infections").getString();
            case 8 -> Component.translatable("message.veterinarium.hut.contract.type.captures").getString();
            default -> Component.translatable("message.veterinarium.hut.contract.type.creatures").getString();
        };
    }
    public Component getContractDisplay() {
        if (contractDay==-1) return Component.translatable("message.veterinarium.hut.contract.none");
        Component status;
        if (contractClaimed) status = Component.translatable("message.veterinarium.hut.contract.status.completed");
        else if (contractProgress>=contractNeeded) status = Component.translatable("message.veterinarium.hut.contract.status.claim");
        else status = Component.literal("§7" + contractProgress + "/" + contractNeeded);
        String typeKey = switch(contractType) {
            case 0 -> "message.veterinarium.hut.contract.heal_any";
            case 1 -> "message.veterinarium.hut.contract.heal_wolves";
            case 2 -> "message.veterinarium.hut.contract.heal_cats";
            case 3 -> "message.veterinarium.hut.contract.heal_horses";
            case 4 -> "message.veterinarium.hut.contract.heal_foxes";
            case 5 -> "message.veterinarium.hut.contract.heal_villagers";
            case 6 -> "message.veterinarium.hut.contract.heal_drakes";
            case 7 -> "message.veterinarium.hut.contract.heal_infections";
            case 8 -> "message.veterinarium.hut.contract.capture";
            default -> "message.veterinarium.hut.contract.heal_any";
        };
        return Component.translatable("message.veterinarium.hut.contract.display", contractDay, Component.translatable(typeKey, contractNeeded), status);
    }
    public boolean isContractCompleted() { return contractProgress >= contractNeeded && !contractClaimed && contractDay!=-1; }
    public void onHealForContract(String entityKey, com.veterinarium.wound.WoundType wt, boolean isCapture) {
        if (contractDay==-1 || contractClaimed || contractProgress>=contractNeeded) return;
        boolean matches = switch(contractType) {
            case 0 -> true;
            case 1 -> entityKey.equals("wounded_wolf") || entityKey.equals("wolf");
            case 2 -> entityKey.equals("wounded_cat") || entityKey.equals("cat");
            case 3 -> entityKey.equals("wounded_horse") || entityKey.equals("horse");
            case 4 -> entityKey.equals("wounded_fox") || entityKey.equals("fox");
            case 5 -> entityKey.equals("wounded_villager") || entityKey.equals("villager");
            case 6 -> entityKey.equals("wounded_drake") || entityKey.equals("drake");
            case 7 -> wt == com.veterinarium.wound.WoundType.INFECTION;
            case 8 -> isCapture;
            default -> false;
        };
        if (matches) {
            contractProgress++;
            setChanged();
            if (level!=null) level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }
    public boolean tryClaimContract(net.minecraft.world.entity.player.Player player) {
        if (!isContractCompleted()) return false;
        contractClaimed = true;
        // récompense selon difficulté
        int emerald = switch(contractType) {
            case 6 -> 5; // drake
            case 7,8 -> 4;
            case 1,2,3,4,5 -> 3;
            default -> 2;
        };
        emerald += hutLevel; // bonus hut
        var lvl = this.level;
        if (lvl!=null) {
            // donne émeraudes
            var stack = new net.minecraft.world.item.ItemStack(net.minecraft.world.item.Items.EMERALD, emerald);
            if (!player.addItem(stack)) player.drop(stack, false);
            // bonus rare
            if (contractType==6 && lvl.random.nextFloat()<0.5f) {
                var serum = new net.minecraft.world.item.ItemStack(com.veterinarium.registry.ModItems.HELLFIRE_SERUM.get(), 1);
                if (!player.addItem(serum)) player.drop(serum, false);
            }
            if (contractType==8) {
                var sphere = new net.minecraft.world.item.ItemStack(com.veterinarium.registry.ModItems.VET_SPHERE.get(), 2);
                if (!player.addItem(sphere)) player.drop(sphere, false);
            }
            lvl.playSound(null, worldPosition, net.minecraft.sounds.SoundEvents.UI_TOAST_CHALLENGE_COMPLETE, net.minecraft.sounds.SoundSource.BLOCKS, 1.0f, 1.2f);
        }
        setChanged();
        if (level!=null) level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        return true;
    }

    public static void notifyNearbyHuts(Level level, BlockPos center, String entityKey, com.veterinarium.wound.WoundType wt, boolean isCapture) {
        if (level.isClientSide) return;
        AABB area = new AABB(center).inflate(64, 16, 64);
        // trouve huts proches via block scanning plus rapide: scan entities hut? On scan BlockPos via entities? On itère via level.getBlockEntity n'est pas rapide pour grande zone, mais 64 c'est ok via boucle BlockPos
        for (int dx=-64; dx<=64; dx+=16) for (int dy=-4; dy<=4; dy+=16) for (int dz=-64; dz<=64; dz+=16) {
            // sample
        }
        // brute force scan autour du centre 64 de rayon mais pas tous les blocs, on scan les huts via getEntitiesOfClass avec search for block entity via level.getBlockEntity pos list
        // Simplifié: cherche tous les huts chargés via level.blockEntity lookup via chunk? On fait boucle précise 64*64*32 ~130k checks, ok toutes les heals c'est lourd. On optimise: cherche huts via poi? Simpler: itère sur players proches huts? Pour MVP on scan 64 rayon complet mais c'est 129k * heals rarement -> acceptable
        for (BlockPos p : BlockPos.betweenClosed(center.offset(-64, -8, -64), center.offset(64, 8, 64))) {
            var be = level.getBlockEntity(p);
            if (be instanceof HospitalHutBlockEntity hut) {
                hut.onHealForContract(entityKey, wt, isCapture);
            }
        }
    }
    private void checkDailyContract(Level lvl) {
        if (lvl.isClientSide) return;
        int currentDay = (int)(lvl.getDayTime() / 24000L);
        if (contractDay != currentDay) {
            // Courbe de progression (0-9) puis aléatoire
            if (currentDay >= 0 && currentDay < 10) {
                int[][] ordered = {
                    {0,2}, // EP1 Contusion any
                    {1,2}, // EP2 Loup
                    {2,2}, // EP3 Chat (Fracture)
                    {5,2}, // EP4 Villageois (Héros)
                    {7,2}, // EP5 Infection quarantaine
                    {8,2}, // EP6 Sphère
                    {0,2}, // EP7 Brûlure (any mais burn via wound weight)
                    {6,1}, // EP8 Drake Boss
                    {8,2}, // EP9 Capture Drake
                    {0,3}  // EP10 Finale 3 any + urgences
                };
                int idx = Math.min(currentDay, ordered.length-1);
                contractType = ordered[idx][0];
                contractNeeded = ordered[idx][1];
            } else {
                contractType = lvl.random.nextInt(9); // 0-8
                float r = lvl.random.nextFloat();
                if (r < 0.20f) contractType = 0;
                else if (r < 0.30f) contractType = 1;
                else if (r < 0.40f) contractType = 2;
                else if (r < 0.50f) contractType = 5;
                else if (r < 0.60f) contractType = 7;
                else if (r < 0.72f) contractType = 8;
                else if (r < 0.82f) contractType = 4;
                else if (r < 0.92f) contractType = 3;
                else contractType = 6; // drake rare
                contractNeeded = switch(contractType) {
                    case 6 -> 1;
                    case 7,8 -> 2;
                    default -> 2 + lvl.random.nextInt(2); // 2-3
                };
                if (hutLevel >= 4 && lvl.random.nextFloat()<0.3f) contractNeeded++;
            }
            contractProgress = 0;
            contractClaimed = false;
            setChanged();
            lvl.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
            // annonce
            for (var p : lvl.players()) {
                if (p.distanceToSqr(worldPosition.getX(), worldPosition.getY(), worldPosition.getZ()) < 10000) {
                    p.displayClientMessage(Component.translatable("message.veterinarium.hut.contract.new", currentDay, getContractDisplay()), false);
                }
            }
        }
    }

    public void tick(Level level, BlockPos pos, BlockState state) {
        if (level.isClientSide) return;
        tickCounter++;
        if (tickCounter % 100 == 0) checkDailyContract(level);
        if (tickCounter % 40 != 0) return; // 2s

        int radius = getRadius();
        float heal = getHealAmount();
        AABB area = new AABB(pos).inflate(radius, 6, radius);
        List<LivingEntity> wounded = level.getEntitiesOfClass(LivingEntity.class, area,
                e -> e.getTags().contains("veterinarium_wounded") || e instanceof com.veterinarium.entity.WoundedWolfEntity w && !w.isHealed()
                        || e instanceof com.veterinarium.entity.WoundedCatEntity c && !c.isHealed()
                        || e instanceof com.veterinarium.entity.WoundedHorseEntity h && !h.isHealed()
                        || e instanceof com.veterinarium.entity.WoundedFoxEntity f && !f.isHealed()
                        || e instanceof com.veterinarium.entity.WoundedVillagerEntity v && !v.isHealed());

        for (LivingEntity e : wounded) {
            e.heal(heal);
            e.removeEffect(net.minecraft.world.effect.MobEffects.MOVEMENT_SLOWDOWN);
            // Hut aide à régénérer mais ne valide pas le soin (il faut Scalpel→Suture en survie)
            // On garde le tag wounded jusqu'à action manuelle, juste un petit bonus visuel si full HP
            if (e.getHealth() >= e.getMaxHealth() * 0.99f) {
                e.removeEffect(net.minecraft.world.effect.MobEffects.WEAKNESS);
            }
        }
        // Personnel soignant auto-opère (Hut Lv2+ toutes les 10s, besoin stock bande+anesth)
        if (hutLevel >= 2 && tickCounter % 200 == 0 && !wounded.isEmpty()) {
            boolean hasHealer = false;
            net.minecraft.world.entity.LivingEntity healerEntity = null;
            if (com.veterinarium.integration.MineColoniesIntegration.isLoaded()) {
                var citizens = level.getEntitiesOfClass(LivingEntity.class, new AABB(pos).inflate(10,6,10), e -> e.getClass().getName().toLowerCase().contains("citizen"));
                if (!citizens.isEmpty()) { hasHealer = true; healerEntity = citizens.get(level.random.nextInt(citizens.size())); }
            } else {
                hasHealer = true; // Hut lui-même = infirmier
            }
            if (hasHealer) {
                LivingEntity target = wounded.get(level.random.nextInt(wounded.size()));
                // consomme stock
                boolean consumed = consumeHealerStock(1,1) || consumeFromNearbyTable(level, pos, 1, 1);
                if (consumed) {
                    // soin auto complet
                    target.heal(10.0f);
                    target.removeEffect(net.minecraft.world.effect.MobEffects.MOVEMENT_SLOWDOWN);
                    target.removeEffect(net.minecraft.world.effect.MobEffects.WEAKNESS);
                    target.removeEffect(net.minecraft.world.effect.MobEffects.POISON);
                    target.removeEffect(net.minecraft.world.effect.MobEffects.WITHER);
                    target.addEffect(new net.minecraft.world.effect.MobEffectInstance(net.minecraft.world.effect.MobEffects.REGENERATION, 100, 1));
                    target.addTag("veterinarium_healed");
                    target.addTag("veterinarium_operated");
                    target.removeTag("veterinarium_wounded");
                    target.removeTag("veterinarium_needs_scalpel");
                    target.setCustomName(Component.translatable("message.veterinarium.hut.healer.custom_name", hutLevel));
                    target.setCustomNameVisible(true);
                    healedCount++;
                    if (target instanceof com.veterinarium.entity.WoundedWolfEntity w) w.setHealed(true);
                    if (target instanceof com.veterinarium.entity.WoundedCatEntity c) c.setHealed(true);
                    if (target instanceof com.veterinarium.entity.WoundedHorseEntity h) h.setHealed(true);
                    if (target instanceof com.veterinarium.entity.WoundedFoxEntity f) f.setHealed(true);
                    if (target instanceof com.veterinarium.entity.WoundedVillagerEntity v) v.setHealed(true);
                    if (target instanceof com.veterinarium.entity.WoundedDrakeEntity d) d.setHealed(true);
                    // notif contrat
                    try {
                        String ek = com.veterinarium.data.BestiaryProgress.entityKey(target);
                        onHealForContract(ek, com.veterinarium.wound.WoundType.CONTUSION, false);
                    } catch (Exception ignored) {}
                    if (healerEntity instanceof net.minecraft.world.entity.Mob mob) mob.getLookControl().setLookAt(target);
                    if (level instanceof net.minecraft.server.level.ServerLevel sl) {
                        sl.sendParticles(net.minecraft.core.particles.ParticleTypes.HEART, target.getX(), target.getY()+1.2, target.getZ(), 3, 0.2,0.2,0.2,0.1);
                        sl.sendParticles(net.minecraft.core.particles.ParticleTypes.HAPPY_VILLAGER, pos.getX()+0.5, pos.getY()+1.2, pos.getZ()+0.5, 2, 0.3,0.3,0.3,0.1);
                    }
                    level.playSound(null, pos, net.minecraft.sounds.SoundEvents.NOTE_BLOCK_PLING.value(), net.minecraft.sounds.SoundSource.BLOCKS, 0.8f, 1.5f);
                    // si MineColonies healer, boost vitesse
                    if (healerEntity != null) {
                        healerEntity.addEffect(new net.minecraft.world.effect.MobEffectInstance(net.minecraft.world.effect.MobEffects.MOVEMENT_SPEED, 60, 0));
                    }
                } else {
                    // stock vide -> particule triste
                    if (level instanceof net.minecraft.server.level.ServerLevel sl) {
                        sl.sendParticles(net.minecraft.core.particles.ParticleTypes.ANGRY_VILLAGER, pos.getX()+0.5, pos.getY()+1.5, pos.getZ()+0.5, 1, 0.2,0.2,0.2,0.1);
                    }
                }
            }
        }
        // Réputation paliers 25/50/75/100 (une fois par joueur)
        if (tickCounter % 100 == 0) {
            var nearest = level.getNearestPlayer(pos.getX(), pos.getY(), pos.getZ(), 32, false);
            if (nearest != null) {
                float rep = com.veterinarium.data.BestiaryProgress.getCompletionPercent(nearest);
                var pd = nearest.getPersistentData();
                if (rep >= 25 && !pd.getBoolean("VetRep25")) {
                    pd.putBoolean("VetRep25", true);
                    nearest.displayClientMessage(Component.translatable("message.veterinarium.hut.rep.25"), false);
                    // donne 2 bandages direct
                    var b = new net.minecraft.world.item.ItemStack(com.veterinarium.registry.ModItems.BANDAGE.get(), 2);
                    if (!nearest.addItem(b)) nearest.drop(b, false);
                    level.playSound(null, pos, net.minecraft.sounds.SoundEvents.PLAYER_LEVELUP, net.minecraft.sounds.SoundSource.BLOCKS, 1.0f, 1.2f);
                }
                if (rep >= 50 && !pd.getBoolean("VetRep50")) {
                    pd.putBoolean("VetRep50", true);
                    nearest.displayClientMessage(Component.translatable("message.veterinarium.hut.rep.50"), false);
                    nearest.addEffect(new net.minecraft.world.effect.MobEffectInstance(net.minecraft.world.effect.MobEffects.HERO_OF_THE_VILLAGE, 6000, 0));
                    // bonus sphères
                    var s = new net.minecraft.world.item.ItemStack(com.veterinarium.registry.ModItems.VET_SPHERE.get(), 2);
                    if (!nearest.addItem(s)) nearest.drop(s, false);
                }
                if (rep >= 75 && !pd.getBoolean("VetRep75")) {
                    pd.putBoolean("VetRep75", true);
                    nearest.displayClientMessage(Component.translatable("message.veterinarium.hut.rep.75"), false);
                    if (level instanceof net.minecraft.server.level.ServerLevel sl) {
                        var drake = com.veterinarium.registry.ModEntities.WOUNDED_DRAKE.get().create(sl);
                        if (drake != null) {
                            drake.moveTo(pos.getX()+2, pos.getY()+5, pos.getZ()+2, 0, 0);
                            drake.setCustomName(Component.translatable("message.veterinarium.hut.drake_rep75_name"));
                            drake.setCustomNameVisible(true);
                            sl.addFreshEntity(drake);
                        }
                    }
                }
                if (rep >= 100 && !pd.getBoolean("VetRep100")) {
                    pd.putBoolean("VetRep100", true);
                    nearest.displayClientMessage(Component.translatable("message.veterinarium.hut.rep.100"), false);
                    var nether = new net.minecraft.world.item.ItemStack(net.minecraft.world.item.Items.NETHERITE_INGOT, 1);
                    if (!nearest.addItem(nether)) nearest.drop(nether, false);
                    nearest.addEffect(new net.minecraft.world.effect.MobEffectInstance(net.minecraft.world.effect.MobEffects.HERO_OF_THE_VILLAGE, 12000, 1));
                    level.playSound(null, pos, net.minecraft.sounds.SoundEvents.UI_TOAST_CHALLENGE_COMPLETE, net.minecraft.sounds.SoundSource.BLOCKS, 1.0f, 1.0f);
                }
            }
        }
        // Ambulance drop-off : joueur qui porte un blessé arrive au Hut
        for (var p : level.getEntitiesOfClass(net.minecraft.world.entity.player.Player.class, area)) {
            if (p.getPassengers().isEmpty()) continue;
            for (var passenger : java.util.List.copyOf(p.getPassengers())) {
                if (passenger instanceof LivingEntity le && le.getTags().contains("vet_on_stretcher")) {
                    long start = p.getPersistentData().getLong("VetAmbulanceStart");
                    long elapsed = level.getGameTime() - start;
                    boolean isFast = start != 0 && elapsed < 1200; // <60s bonus
                    le.stopRiding();
                    le.removeTag("vet_on_stretcher");
                    // dépose à côté du Hut
                    BlockPos drop = pos.offset(level.random.nextInt(3)-1, 1, level.random.nextInt(3)-1);
                    le.moveTo(drop.getX()+0.5, drop.getY(), drop.getZ()+0.5, level.random.nextFloat()*360, 0);
                    p.removeEffect(net.minecraft.world.effect.MobEffects.MOVEMENT_SLOWDOWN);
                    p.getPersistentData().remove("VetAmbulanceStart");
                    p.getPersistentData().remove("VetAmbulanceEntity");
                    le.heal(4.0f);
                    le.addEffect(new net.minecraft.world.effect.MobEffectInstance(net.minecraft.world.effect.MobEffects.REGENERATION, 100, 1));
                    if (isFast) {
                        le.addEffect(new net.minecraft.world.effect.MobEffectInstance(net.minecraft.world.effect.MobEffects.DAMAGE_RESISTANCE, 200, 0));
                        p.displayClientMessage(Component.translatable("message.veterinarium.hut.ambulance.fast"), false);
                        le.spawnAtLocation(net.minecraft.world.item.Items.EMERALD, 1);
                        level.playSound(null, pos, net.minecraft.sounds.SoundEvents.PLAYER_LEVELUP, net.minecraft.sounds.SoundSource.BLOCKS, 1.0f, 1.5f);
                    } else {
                        p.displayClientMessage(Component.translatable("message.veterinarium.hut.ambulance.delivered"), false);
                    }
                    if (level instanceof net.minecraft.server.level.ServerLevel sl) {
                        sl.sendParticles(net.minecraft.core.particles.ParticleTypes.HEART, le.getX(), le.getY()+1, le.getZ(), 3, 0.2,0.2,0.2,0.1);
                        sl.sendParticles(net.minecraft.core.particles.ParticleTypes.HAPPY_VILLAGER, pos.getX()+0.5, pos.getY()+1.2, pos.getZ()+0.5, 3, 0.3,0.3,0.3,0.1);
                    }
                    level.playSound(null, pos, net.minecraft.sounds.SoundEvents.NOTE_BLOCK_PLING.value(), net.minecraft.sounds.SoundSource.BLOCKS, 0.8f, 1.5f);
                }
            }
        }

        if (com.veterinarium.integration.MineColoniesIntegration.isLoaded()) {
            List<LivingEntity> citizens = level.getEntitiesOfClass(LivingEntity.class, area,
                    e -> e.getClass().getName().toLowerCase().contains("citizen"));
            for (LivingEntity c : citizens) {
                if (c.getHealth() < c.getMaxHealth()) c.heal(heal*0.5f);
            }
        }
        // Effet particules coeur si patients (Lv3+)
        if (!wounded.isEmpty() && hutLevel >= 3 && level instanceof net.minecraft.server.level.ServerLevel sl) {
            if (tickCounter % 20 == 0) {
                for (LivingEntity e : wounded) {
                    sl.sendParticles(net.minecraft.core.particles.ParticleTypes.HEART, e.getX(), e.getY()+1.2, e.getZ(), 1, 0.2, 0.2, 0.2, 0.1);
                }
                sl.sendParticles(net.minecraft.core.particles.ParticleTypes.HAPPY_VILLAGER, pos.getX()+0.5, pos.getY()+1.2, pos.getZ()+0.5, 3, 0.5, 0.3, 0.5, 0.1);
            }
        }
        // Son monitor toutes les 10s si des patients (custom si dispo)
        if (!wounded.isEmpty() && tickCounter % 200 == 0) {
            try {
                level.playSound(null, pos, com.veterinarium.registry.ModSounds.MONITOR_BEEP.get(), net.minecraft.sounds.SoundSource.BLOCKS, 0.5f, 1.2f + hutLevel*0.1f);
            } catch (Exception e) {
                level.playSound(null, pos, net.minecraft.sounds.SoundEvents.NOTE_BLOCK_PLING.value(), net.minecraft.sounds.SoundSource.BLOCKS, 0.5f, 1.5f);
            }
        }
    }

    public int getPatientCount() {
        if (this.level == null) return 0;
        AABB area = new AABB(this.worldPosition).inflate(getRadius(), 6, getRadius());
        return this.level.getEntitiesOfClass(LivingEntity.class, area,
                e -> e.getTags().contains("veterinarium_wounded")).size();
    }

    public int getHealedCount() { return healedCount; }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putInt("HealedCount", healedCount);
        tag.putInt("HutLevel", hutLevel);
        tag.putInt("ContractDay", contractDay);
        tag.putInt("ContractType", contractType);
        tag.putInt("ContractNeeded", contractNeeded);
        tag.putInt("ContractProgress", contractProgress);
        tag.putBoolean("ContractClaimed", contractClaimed);
        tag.put("HealerInv", healerInv.serializeNBT(registries));
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        healedCount = tag.getInt("HealedCount");
        hutLevel = tag.contains("HutLevel") ? tag.getInt("HutLevel") : 1;
        if (hutLevel <1) hutLevel=1; if (hutLevel>5) hutLevel=5;
        contractDay = tag.contains("ContractDay") ? tag.getInt("ContractDay") : -1;
        contractType = tag.contains("ContractType") ? tag.getInt("ContractType") : 0;
        contractNeeded = tag.contains("ContractNeeded") ? tag.getInt("ContractNeeded") : 2;
        contractProgress = tag.contains("ContractProgress") ? tag.getInt("ContractProgress") : 0;
        contractClaimed = tag.getBoolean("ContractClaimed");
        if (tag.contains("HealerInv")) healerInv.deserializeNBT(registries, tag.getCompound("HealerInv"));
    }
    @Override public void onLoad() { super.onLoad(); healerHandler = net.minecraftforge.common.util.LazyOptional.of(() -> healerInv); }
    @Override public void invalidateCaps() { super.invalidateCaps(); healerHandler.invalidate(); }
    @SuppressWarnings("unchecked")
    public <T> net.minecraftforge.common.util.LazyOptional<T> getCapability(net.minecraftforge.common.capabilities.Capability<T> cap, net.minecraft.core.Direction side) {
        if (cap == net.minecraftforge.common.capabilities.ForgeCapabilities.ITEM_HANDLER) return healerHandler.cast();
        return super.getCapability(cap, side);
    }
}
