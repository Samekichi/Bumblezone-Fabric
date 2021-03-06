package net.telepathicgrunt.bumblezone;

import me.sargunvohra.mcmods.autoconfig1u.AutoConfig;
import me.sargunvohra.mcmods.autoconfig1u.serializer.JanksonConfigSerializer;
import nerdhub.cardinal.components.api.ComponentRegistry;
import nerdhub.cardinal.components.api.ComponentType;
import nerdhub.cardinal.components.api.event.EntityComponentCallback;
import nerdhub.cardinal.components.api.util.EntityComponents;
import nerdhub.cardinal.components.api.util.RespawnCopyStrategy;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.server.ServerStartCallback;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraft.world.biome.DefaultBiomeCreator;
import net.telepathicgrunt.bumblezone.blocks.BzBlocks;
import net.telepathicgrunt.bumblezone.configs.BzConfig;
import net.telepathicgrunt.bumblezone.dimension.BzDimension;
import net.telepathicgrunt.bumblezone.effects.BzEffects;
import net.telepathicgrunt.bumblezone.entities.BeeAggression;
import net.telepathicgrunt.bumblezone.entities.BzEntities;
import net.telepathicgrunt.bumblezone.entities.IPlayerComponent;
import net.telepathicgrunt.bumblezone.entities.PlayerComponent;
import net.telepathicgrunt.bumblezone.features.BzConfiguredFeatures;
import net.telepathicgrunt.bumblezone.features.BzFeatures;
import net.telepathicgrunt.bumblezone.features.decorators.BzPlacements;
import net.telepathicgrunt.bumblezone.items.BzItems;
import net.telepathicgrunt.bumblezone.items.DispenserItemSetup;
import net.telepathicgrunt.bumblezone.modCompat.ModChecker;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Bumblezone implements ModInitializer {

    public static final String MODID = "the_bumblezone";
    public static final Identifier MOD_DIMENSION_ID = new Identifier(Bumblezone.MODID, Bumblezone.MODID);

    public static BzConfig BZ_CONFIG;
    public static final Logger LOGGER = LogManager.getLogger(MODID);
    public static final ComponentType<IPlayerComponent> PLAYER_COMPONENT =
            ComponentRegistry.INSTANCE.registerIfAbsent(new Identifier(MODID, "player_component"), IPlayerComponent.class)
                    .attach(EntityComponentCallback.event(PlayerEntity.class), player -> new PlayerComponent());

    @Override
    public void onInitialize() {
        //Set up config
        AutoConfig.register(BzConfig.class, JanksonConfigSerializer::new);
        BZ_CONFIG = AutoConfig.getConfigHolder(BzConfig.class).getConfig();


        BzBlocks.registerBlocks();
        BzItems.registerItems();
        BzEffects.registerEffects();
        BzEntities.registerEntities();

        BzPlacements.registerPlacements();
        BzFeatures.registerFeatures();
        BzConfiguredFeatures.registerConfiguredFeatures();
        BzDimension.setupDimension();

        //attach component to player
        EntityComponents.setRespawnCopyStrategy(PLAYER_COMPONENT, RespawnCopyStrategy.INVENTORY);

        ServerStartCallback.EVENT.register((MinecraftServer world) -> BeeAggression.setupBeeHatingList(world.getWorld(World.OVERWORLD)));
        DispenserItemSetup.setupDispenserBehaviors();

        ModChecker.setupModCompat();
    }

    public static void reserveBiomeIDs() {
        //Reserve Bumblezone biome IDs for the json version to replace
        Registry.register(BuiltinRegistries.BIOME, new Identifier(Bumblezone.MODID, "hive_wall"), DefaultBiomeCreator.createNormalOcean(false));
        Registry.register(BuiltinRegistries.BIOME, new Identifier(Bumblezone.MODID, "hive_pillar"), DefaultBiomeCreator.createNormalOcean(false));
        Registry.register(BuiltinRegistries.BIOME, new Identifier(Bumblezone.MODID, "sugar_water_floor"), DefaultBiomeCreator.createNormalOcean(false));
    }
}
