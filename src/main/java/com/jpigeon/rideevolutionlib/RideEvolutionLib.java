package com.jpigeon.rideevolutionlib;

import com.jpigeon.rideevolutionlib.util.state.StateFlagTicker;
import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import org.slf4j.Logger;

@Mod(RideEvolutionLib.MODID)
public class RideEvolutionLib {
    public static final String MODID = "rideevolutionlib";
    public static final Logger LOGGER = LogUtils.getLogger();

    public RideEvolutionLib(IEventBus modEventBus, ModContainer modContainer) {
        modEventBus.addListener(this::commonSetup);

        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
        NeoForge.EVENT_BUS.register(StateFlagTicker.class);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
    }
}
