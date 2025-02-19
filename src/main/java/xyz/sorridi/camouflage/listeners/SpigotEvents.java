package xyz.sorridi.camouflage.listeners;

import fun.aevy.aevycore.utils.builders.ListenerBuilder;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import xyz.sorridi.camouflage.Camouflage;
import xyz.sorridi.camouflage.CommonStorage;
import xyz.sorridi.farmpvp.events.custom.*;
import xyz.sorridi.farmpvp.structure.blocks.Flag;
import xyz.sorridi.farmpvp.structure.elements.FarmPlayer;
import xyz.sorridi.farmpvp.structure.elements.Team;

/**
 * Listener che monitora gli eventi custom di {@link xyz.sorridi.farmpvp.FarmPVP}.
 *
 * @author Sorridi
 * @since 1.0
 */
public class SpigotEvents extends ListenerBuilder
{
    private final CommonStorage commonStorage;

    public SpigotEvents(Camouflage camouflage)
    {
        super(camouflage);
        commonStorage = camouflage.getCommonStorage();
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void on(FarmPlayerJoinEvent event)
    {
        FarmPlayer farmPlayer = event.getFarmPlayer();

        commonStorage.addRevealed(farmPlayer);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void on(FarmPlayerQuitEvent event)
    {
        FarmPlayer farmPlayer = event.getFarmPlayer();

        commonStorage.removeRevealed(farmPlayer);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void on(FarmFlagPlaceEvent event)
    {
        FarmPlayer farmPlayer   = event.getFarmPlayer();
        Flag flag               = farmPlayer.getFlag();

        if (event.isCancelled())
        {
            return;
        }

        commonStorage.addFlag(flag);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void on(FarmFlagBreakEvent event)
    {
        Flag flag           = event.getFlag();
        Location location   = event.getBlockBreakEvent().getBlock().getLocation();

        if (event.isCancelled())
        {
            return;
        }

        commonStorage.removeFlag(flag, location);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void on(TeamJoinEvent event)
    {
        FarmPlayer farmPlayer = event.getFarmPlayer();
        Team team             = event.getTeam();

        team.getPlayers()
                .stream()
                .filter(player -> player != farmPlayer)
                .forEach(player ->
                {
                    commonStorage.addRevealed(farmPlayer.getPlayer(), player.getFlag());
                    commonStorage.addRevealed(player.getPlayer(), farmPlayer.getFlag());
                });
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void on(TeamLeaveEvent event)
    {
        FarmPlayer farmPlayer = event.getFarmPlayer();
        Team team             = event.getTeam();

        team.getPlayers()
                .stream()
                .filter(player -> player != farmPlayer)
                .forEach(player ->
                {
                    commonStorage.removeRevealed(farmPlayer.getPlayer(), player.getFlag());
                    commonStorage.removeRevealed(player.getPlayer(), farmPlayer.getFlag());
                });
    }

    @Override
    public void reloadVars()
    {

    }

}