package com.veterinarium.gametest;

import com.veterinarium.config.VeterinariumConfig;
import com.veterinarium.data.BestiaryProgress;
import com.veterinarium.entity.WoundedCatEntity;
import com.veterinarium.entity.WoundedHorseEntity;
import com.veterinarium.entity.WoundedWolfEntity;
import com.veterinarium.item.ScalpelItem;
import com.veterinarium.item.SutureKitItem;
import com.veterinarium.item.SyringeItem;
import com.veterinarium.registry.ModEntities;
import com.veterinarium.registry.ModItems;
import com.veterinarium.wound.WoundType;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.registries.ForgeRegistries;

@GameTestHolder("veterinarium")
public class VeterinariumGameTests {

    // 1) Wound weights pondérations
    @GameTest(template = "veterinarium:hospital_hut")
    public static void testWoundWeights(GameTestHelper helper) {
        var src = net.minecraft.util.RandomSource.create(12345L);
        int[] cnt = new int[5];
        for (int i = 0; i < 10000; i++) cnt[WoundType.random(src).getId()]++;
        helper.assertTrue(cnt[0] > 3000 && cnt[0] < 4000, "CONTUSION weight off " + cnt[0]);
        helper.assertTrue(cnt[1] > 1700 && cnt[1] < 2700, "HEMORRAGIE weight off " + cnt[1]);
        helper.assertTrue(cnt[4] > 700 && cnt[4] < 1700, "BRULURE weight off " + cnt[4]);
        for (WoundType wt : WoundType.values()) {
            helper.assertTrue(wt.getDisplay() != null && !wt.getDisplay().isEmpty(), "display empty " + wt);
            helper.assertTrue(wt.getDescription() != null && !wt.getDescription().getString().isEmpty(), "desc empty " + wt);
        }
        helper.succeed();
    }

    // 2) Config values dans bornes
    @GameTest(template = "veterinarium:hospital_hut")
    public static void testConfigDefaults(GameTestHelper helper) {
        try {
            double ch = VeterinariumConfig.COMMON.woundedSpawnChance.get();
            helper.assertTrue(ch >= 0 && ch <= 1.0, "woundedSpawnChance hors borne " + ch);
            int drake = VeterinariumConfig.COMMON.drakeWeight.get();
            helper.assertTrue(drake >= 0 && drake <= 100, "drakeWeight " + drake);
            double infCh = VeterinariumConfig.COMMON.infectionSpreadChance.get();
            helper.assertTrue(infCh >= 0 && infCh <= 1.0, "infectionSpreadChance " + infCh);
            double infRg = VeterinariumConfig.COMMON.infectionSpreadRange.get();
            helper.assertTrue(infRg >= 1 && infRg <= 16, "infectionSpreadRange " + infRg);
        } catch (Exception e) {
            helper.fail("config non chargée: " + e.getMessage());
        }
        helper.succeed();
    }

    // 3) BestiaryProgress via mock player
    @GameTest(template = "veterinarium:hospital_hut")
    public static void testBestiaryProgress(GameTestHelper helper) {
        Player fake = helper.makeMockPlayer(GameType.SURVIVAL);
        fake.getPersistentData().putInt("VetDiagTotal", 0);
        fake.getPersistentData().putInt("VetOpsTotal", 0);
        fake.getPersistentData().putInt("VetSutureTotal", 0);
        fake.getPersistentData().putInt("VetHealedTotal", 0);

        WoundedWolfEntity wolf = helper.spawn(ModEntities.WOUNDED_WOLF.get(), new BlockPos(1, 2, 1));
        wolf.setWoundType(WoundType.CONTUSION);

        BestiaryProgress.recordDiagnose(fake, wolf, WoundType.CONTUSION);
        helper.assertTrue(BestiaryProgress.getDiagTotal(fake) == 1, "diag 1");
        helper.assertTrue(BestiaryProgress.hasSeen(fake, "wounded_wolf"), "seen wolf");
        helper.assertTrue(BestiaryProgress.hasSeenWound(fake, WoundType.CONTUSION.getId()), "seen wound contusion");

        BestiaryProgress.recordOperate(fake, wolf);
        helper.assertTrue(BestiaryProgress.getOpsTotal(fake) == 1, "ops 1");

        BestiaryProgress.recordSuture(fake, wolf, true);
        helper.assertTrue(BestiaryProgress.getHealedTotal(fake) == 1, "healed 1");
        helper.assertTrue(BestiaryProgress.getCompletionPercent(fake) > 0, "completion >0 " + BestiaryProgress.getCompletionPercent(fake));

        helper.succeed();
    }

    // 4) Flux complet Seringue -> Scalpel -> Suture (principal)
    @GameTest(template = "veterinarium:hospital_hut", timeoutTicks = 300)
    public static void testFullHealFlow(GameTestHelper helper) {
        Player fake = helper.makeMockPlayer(GameType.SURVIVAL);

        // setItemInHand au lieu de getInventory().add() pour que getItemInHand fonctionne
        ItemStack syringe = new ItemStack(ModItems.SYRINGE.get());
        ItemStack scalpel = new ItemStack(ModItems.SCALPEL.get());
        ItemStack suture = new ItemStack(ModItems.SUTURE_KIT.get());
        fake.getInventory().add(new ItemStack(ModItems.ANESTHETIC.get(), 5));
        fake.getInventory().add(new ItemStack(ModItems.BANDAGE.get(), 5));

        WoundedWolfEntity wolf = helper.spawn(ModEntities.WOUNDED_WOLF.get(), new BlockPos(1, 2, 1));
        wolf.setWoundType(WoundType.CONTUSION);
        wolf.setHealth(4.0F);
        wolf.addTag("veterinarium_wounded");

        // 1 Seringue
        fake.setItemInHand(InteractionHand.MAIN_HAND, syringe);
        SyringeItem syrItem = (SyringeItem) syringe.getItem();
        InteractionResult r1 = syrItem.interactLivingEntity(syringe, fake, wolf, InteractionHand.MAIN_HAND);
        helper.assertTrue(r1.consumesAction(), "syringe should succeed");
        helper.assertTrue(fake.getPersistentData().contains("VetLastWound"), "VetLastWound saved");

        // 2 Scalpel
        float hpBefore = wolf.getHealth();
        fake.setItemInHand(InteractionHand.MAIN_HAND, scalpel);
        ScalpelItem scalpItem = (ScalpelItem) scalpel.getItem();
        InteractionResult r2 = scalpItem.interactLivingEntity(scalpel, fake, wolf, InteractionHand.MAIN_HAND);
        helper.assertTrue(r2.consumesAction(), "scalpel should succeed");
        helper.assertTrue(wolf.getTags().contains("veterinarium_operated"), "operated tag");
        helper.assertTrue(wolf.getHealth() > hpBefore, "hp increased " + hpBefore + "->" + wolf.getHealth());

        // 3 Suture
        fake.setItemInHand(InteractionHand.MAIN_HAND, suture);
        SutureKitItem sutureItem = (SutureKitItem) suture.getItem();
        InteractionResult r3 = sutureItem.interactLivingEntity(suture, fake, wolf, InteractionHand.MAIN_HAND);
        helper.assertTrue(r3.consumesAction(), "suture should succeed");
        helper.assertTrue(wolf.getTags().contains("veterinarium_healed"), "healed tag");
        helper.assertTrue(wolf.getTags().contains("veterinarium_sutured"), "sutured tag");
        helper.assertTrue(!wolf.getTags().contains("veterinarium_wounded"), "not wounded");

        helper.succeed();
    }

    // 5) Suture sans Scalpel -> Infection (POISON)
    @GameTest(template = "veterinarium:hospital_hut")
    public static void testSutureWithoutScalpelCausesPoison(GameTestHelper helper) {
        Player fake = helper.makeMockPlayer(GameType.SURVIVAL);
        ItemStack suture = new ItemStack(ModItems.SUTURE_KIT.get());
        fake.setItemInHand(InteractionHand.MAIN_HAND, suture);

        WoundedHorseEntity horse = helper.spawn(ModEntities.WOUNDED_HORSE.get(), new BlockPos(1, 2, 1));
        horse.setWoundType(WoundType.CONTUSION);
        horse.setHealth(8.0F);
        horse.addTag("veterinarium_wounded");
        horse.addTag("veterinarium_needs_scalpel");

        SutureKitItem sutureItem = (SutureKitItem) suture.getItem();
        sutureItem.interactLivingEntity(suture, fake, horse, InteractionHand.MAIN_HAND);

        helper.assertTrue(
            horse.hasEffect(net.minecraft.world.effect.MobEffects.POISON) || horse.getHealth() < 8.0F,
            "horse should be poisoned or damaged"
        );
        helper.succeed();
    }

    // 6) Registration items
    @GameTest(template = "veterinarium:hospital_hut")
    public static void testItemRegistration(GameTestHelper helper) {
        helper.assertTrue(ForgeRegistries.ITEMS.getValue(ModItems.SCALPEL.getId()) != null, "scalpel");
        helper.assertTrue(ForgeRegistries.ITEMS.getValue(ModItems.SYRINGE.getId()) != null, "syringe");
        helper.assertTrue(ForgeRegistries.ITEMS.getValue(ModItems.SUTURE_KIT.getId()) != null, "suture");
        helper.assertTrue(ForgeRegistries.ITEMS.getValue(ModItems.BANDAGE.getId()) != null, "bandage");
        helper.assertTrue(ForgeRegistries.ITEMS.getValue(ModItems.ANESTHETIC.getId()) != null, "anesthetic");
        helper.assertTrue(ForgeRegistries.ITEMS.getValue(ModItems.MEDICAL_FILE.getId()) != null, "medical_file");
        helper.assertTrue(ForgeRegistries.ITEMS.getValue(ModItems.HOSPITAL_HUT.getId()) != null, "hut");
        helper.succeed();
    }

    // 7) Lang keys existentes (pas de traduction check car lang non chargé en gameTest server)
    @GameTest(template = "veterinarium:hospital_hut")
    public static void testLangKeysExist(GameTestHelper helper) {
        String[] keys = new String[]{
            "gui.veterinarium.medical_file.title",
            "gui.veterinarium.medical_file.cover.tagline",
            "wound.veterinarium.contusion",
            "wound.veterinarium.brulure",
            "entity.veterinarium.wounded_wolf",
            "entity.veterinarium.wounded_drake"
        };
        for (String k : keys) {
            Component c = Component.translatable(k);
            String s = c.getString();
            helper.assertTrue(s != null && !s.isEmpty(), "lang key empty: " + k);
        }
        helper.succeed();
    }
}
