package xyz.sorridi.camouflage.engines;

import fun.aevy.aevycore.struct.elements.scheduler.Scheduler;
import lombok.Getter;
import lombok.val;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import xyz.sorridi.camouflage.Camouflage;
import xyz.sorridi.camouflage.engines.struct.Engine;
import xyz.sorridi.camouflage.entries.Camo;
import xyz.sorridi.camouflage.packets.RevealPacket;
import xyz.sorridi.camouflage.packets.TileEntityPacket;
import xyz.sorridi.farmpvp.structure.blocks.Flag;
import xyz.sorridi.farmpvp.structure.managers.DataManager;
import xyz.sorridi.farmpvp.structure.managers.TeamsManager;

/**
 * {@link Engine} che si occupa di calcolare la distanza di ogni player da ogni bandiera e d'inviare pacchetti
 * quali {@link RevealPacket} e {@link TileEntityPacket} in base a questa distanza, per mostrare e nascondere le
 * bandiere nemiche.
 *
 * @author Sorridi
 * @since 1.0
 */
@Getter
public class RayTracingEngine extends Engine
{
    private DataManager   dataManager;
    private TeamsManager  teamsManager;

    public RayTracingEngine(Camouflage camouflage, String name)
    {
        super(camouflage, name);
    }

    @Override
    public void action()
    {
        boolean result;

        val revealed    = commonStorage.getRevealedFlags().keySet();
        val flags       = commonStorage.getFlags();

        /*
         * Compara ogni player con ogni bandiera.
         */
        for (Player player : revealed)
        {
            for (Flag flag : flags)
            {
                result = commonEvaluations.shouldBeHiddenTracker(flag, player);

                if (commonStorage.isRevealed(player, flag))
                {
                    if (result)
                    {
                        action(player, flag, false);
                        commonStorage.removeRevealed(player, flag);
                    }
                }
                else
                {
                    if (!result)
                    {
                        action(player, flag, true);
                        commonStorage.addRevealed(player, flag);
                    }
                }
            }
        }
    }

    /**
     * Manda i pacchetti di {@link RevealPacket} e {@link TileEntityPacket} della bandiera.
     * @param player {@link Player} a cui inviare i pacchetti.
     * @param flag {@link Flag} della quale inviare i pacchetti.
     * @param reveal Se inviare il {@link TileEntityPacket} o meno.
     */
    private void action(Player player, Flag flag, boolean reveal)
    {
        Location location = flag.getLocation();

        new RevealPacket(camouflage)
                .to(player)
                .with(location)
                .write(reveal ? flag : null)
                .send(true);

        if (reveal)
        {
            val packet = new TileEntityPacket(camouflage).to(player).with(location);

            new Scheduler(camouflage)
            {
                @Override
                public void runTask(Object... objects)
                {
                    runTask(() ->
                    {
                        packet.write(flag);
                        packet.send(true);
                    });
                }
            }.runTask();
        }
    }

    @Override
    public void init()
    {
        dataManager     = camouflage.getDataManager();
        teamsManager    = camouflage.getTeamsManager();
    }

    @Override
    public void reloadVars()
    {
        updateInterval = (int) coolConfig.getValue(Camo.TrackingEngine.UPDATE_INTERVAL, 10);
    }

}