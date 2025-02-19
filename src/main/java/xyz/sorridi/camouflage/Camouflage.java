package xyz.sorridi.camouflage;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import fun.aevy.aevycore.struct.elements.AevyDependent;
import lombok.Getter;
import xyz.sorridi.camouflage.commands.CamouflageCommand;
import xyz.sorridi.camouflage.engines.RayTracingEngine;
import xyz.sorridi.camouflage.entries.Camo;
import xyz.sorridi.camouflage.listeners.SpigotEvents;
import xyz.sorridi.camouflage.listeners.blocks.BlockChangeListener;
import xyz.sorridi.camouflage.listeners.common.CommonEvaluations;
import xyz.sorridi.camouflage.listeners.entities.TileEntityListener;
import xyz.sorridi.camouflage.listeners.map.bulk.MapBulkSyncListener;
import xyz.sorridi.camouflage.listeners.map.single.MapSyncListener;
import xyz.sorridi.farmpvp.FarmPVP;
import xyz.sorridi.farmpvp.structure.managers.DataManager;
import xyz.sorridi.farmpvp.structure.managers.TeamsManager;

@Getter
public class Camouflage extends AevyDependent
{
    private DataManager         dataManager;
    private TeamsManager        teamsManager;
    private CommonStorage       commonStorage;
    private CommonEvaluations   commonEvaluations;
    private ProtocolManager     protocolManager;

    private RayTracingEngine    rayTracingEngine;

    private TileEntityListener  tileEntityListener;
    private BlockChangeListener blockChangeListener;
    private MapSyncListener     mapSyncListener;
    private MapBulkSyncListener mapBulkSyncListener;

    @Override
    public void onEnable()
    {
        enable(this);

        FarmPVP farmPVP = getPlugin(FarmPVP.class).getInstance();
        dataManager     = farmPVP.getDataManager();
        teamsManager    = farmPVP.getTeamsManager();

        protocolManager = ProtocolLibrary.getProtocolManager();

        commonStorage       = new CommonStorage();
        commonEvaluations   = new CommonEvaluations(this);

        /*
         * Registra i comandi.
         */
        new CamouflageCommand(this, "camouflage")
                .setUsage(Camo.Usages.CAMOUFLAGE)
                .build();

        /*
         * Registra gli eventi.
         */
        new SpigotEvents(this);

        /*
         * Inizializza le task.
         */
        rayTracingEngine    = new RayTracingEngine(this, "ray-engine");
        tileEntityListener  = new TileEntityListener(this);
        blockChangeListener = new BlockChangeListener(this);

        mapSyncListener     = new MapSyncListener(this);
        mapBulkSyncListener = new MapBulkSyncListener(this);

        protocolManager.addPacketListener(mapSyncListener);
        protocolManager.addPacketListener(mapBulkSyncListener);
        protocolManager.addPacketListener(tileEntityListener);
        protocolManager.addPacketListener(blockChangeListener);
    }

    @Override
    public void onDisable()
    {
        protocolManager.removePacketListeners(this);
        rayTracingEngine.stopExecution();
    }

}