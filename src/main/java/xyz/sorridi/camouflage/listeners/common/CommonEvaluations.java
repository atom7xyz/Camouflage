package xyz.sorridi.camouflage.listeners.common;

import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.BlockPosition;
import fun.aevy.aevycore.struct.elements.Reloadable;
import fun.aevy.aevycore.utils.configuration.elements.CoolConfig;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import xyz.sorridi.camouflage.Camouflage;
import xyz.sorridi.camouflage.CommonStorage;
import xyz.sorridi.camouflage.entries.Camo;
import xyz.sorridi.camouflage.utils.raytracing.BoundingBox;
import xyz.sorridi.camouflage.utils.raytracing.RayTrace;
import xyz.sorridi.farmpvp.structure.blocks.Flag;
import xyz.sorridi.farmpvp.structure.elements.FarmPlayer;
import xyz.sorridi.farmpvp.structure.managers.DataManager;

/**
 * Classe che contiene metodi di valutazione comuni ai listeners.
 *
 * @author Sorridi
 * @since 1.0
 */
public class CommonEvaluations implements Reloadable
{
    private final CoolConfig    coolConfig;
    private final DataManager   dataManager;
    private final CommonStorage commonStorage;

    private int     nearRange;
    private double  raytracingAccuracy;
    private String  bypassPermission;

    public CommonEvaluations(Camouflage camouflage)
    {
        coolConfig      = camouflage.getCoolConfig();
        dataManager     = camouflage.getDataManager();
        commonStorage   = camouflage.getCommonStorage();

        camouflage.addReloadable(this);
    }

    /**
     * Estrae la posizione del blocco dal pacchetto.
     * @param packetContainer {@link PacketContainer} da cui estrarre la posizione.
     * @return {@link BlockPosition} del blocco.
     */
    public BlockPosition getBlockPosition(PacketContainer packetContainer)
    {
        return packetContainer.getBlockPositionModifier().read(0);
    }

    /**
     * Controlla se nella {@link BlockPosition} è presente un {@link Material#STANDING_BANNER}
     * @param blockPosition {@link BlockPosition} da controllare
     * @param world {@link World} in cui si trova la {@link BlockPosition}
     * @return Se il banner è presente o meno.
     */
    public boolean isBanner(BlockPosition blockPosition, World world)
    {
        return blockAt(blockPosition, world) == Material.STANDING_BANNER;
    }

    /**
     * Ritorna la posizione del blocco contenuta nel pacchetto.
     * @param blockPosition {@link BlockPosition} del blocco.
     * @param world {@link World} in cui si trova il blocco.
     * @return {@link Material} del blocco.
     */
    public Material blockAt(BlockPosition blockPosition, World world)
    {
        return world.getBlockAt(blockPosition.getX(), blockPosition.getY(), blockPosition.getZ()).getType();
    }

    /**
     * Controlla se la {@link Location} della {@link BlockPosition} è uguale alla {@link Location} della {@link Flag}.
     * @param blockPosition {@link BlockPosition} da controllare.
     * @param flag {@link Flag} da controllare.
     * @return Se le due {@link Location} sono uguali.
     */
    public boolean equalLocation(BlockPosition blockPosition, Flag flag)
    {
        Location location = flag.getBlock().getLocation();

        return  blockPosition.getX() == location.getX() &&
                blockPosition.getY() == location.getY() &&
                blockPosition.getZ() == location.getZ();
    }

    /**
     * Controlla se la distanza tra le due {@link Location} è minore o uguale alla distanza di visibilità.
     * @param x Prima {@link Location} da controllare.
     * @param y Seconda {@link Location} da controllare.
     * @return Se la distanza è minore o uguale alla distanza di visibilità.
     */
    public boolean isNear(Location x, Location y, int distance)
    {
        return x.distance(y) <= distance;
    }

    /**
     * Controlla se la distanza tra {@link Player} e {@link Flag} è minore o uguale alla distanza di visibilità.
     * @param player {@link Player} da controllare.
     * @param flag {@link Flag} da controllare.
     * @return Se la distanza è minore o uguale alla distanza di visibilità.
     */
    public boolean isNear(Player player, Flag flag)
    {
        return isNear(player.getLocation(), flag.getLocation(), nearRange);
    }

    /**
     * Controlla se la {@link Flag} deve essere nascosta al {@link Player}.
     * @param flag {@link Flag} da controllare.
     * @param player {@link Player} da controllare.
     * @return Se la {@link Flag} deve essere nascosta.
     */
    public boolean shouldBeHidden(Flag flag, Player player)
    {
        FarmPlayer owner    = flag.getOwner();
        FarmPlayer target   = dataManager.getPlayer(player);

        return  !owner.equals(target)                   &&
                !player.hasPermission(bypassPermission) &&
                !commonStorage.isRevealed(player, flag) &&
                !isNear(player, flag)                   &&
                !raysClear(player, flag);
    }

    /**
     * Controlla se la {@link Flag} deve essere nascosta al {@link Player}.
     * @param flag {@link Flag} da controllare.
     * @param player {@link Player} da controllare.
     * @return Se la {@link Flag} deve essere nascosta.
     */
    public boolean shouldBeHiddenTracker(Flag flag, Player player)
    {
        FarmPlayer owner    = flag.getOwner();
        FarmPlayer target   = dataManager.getPlayer(player);

        return  !owner.equals(target)                   &&
                !player.hasPermission(bypassPermission) &&
                isNear(player, flag)                    &&
                !raysClear(player, flag);
    }

    /**
     * Calcola il raytracing tra il {@link Player} e la {@link Flag}.
     * @param player {@link Player} da controllare.
     * @param flag {@link Flag} da controllare.
     * @return Se il ray non è bloccato da dei blocchi.
     */
    public boolean raysClear(Player player, Flag flag)
    {
        World world     = player.getWorld();
        Block blockBot  = flag.getBlock();

        Location eyeLocation    = player.getEyeLocation();
        Vector eyeVector        = eyeLocation.toVector();

        RayTrace rayTraceBot, rayTraceTop;
        boolean result;

        /*
         * Raytrace parte della bandiera inferiore.
         */
        BoundingBox boundingBot = new BoundingBox(blockBot);
        Vector directionBot     = boundingBot.midPoint().subtract(eyeVector).normalize();

        rayTraceBot = new RayTrace(eyeLocation, directionBot);

        result = rayTraceBot.intersectBlocked(blockBot, nearRange, raytracingAccuracy);

        if (result)
        {
            /*
             * Raytrace parte della bandiera superiore.
             */
            Block blockTop = world.getBlockAt(blockBot.getLocation().add(0, 1, 0));

            BoundingBox boundingTop = new BoundingBox(blockTop).growMin(0, -1, 0).growMax(0, 1, 0);
            Vector directionTop     = boundingTop.midPoint().subtract(eyeVector).normalize();

            rayTraceTop = new RayTrace(eyeLocation, directionTop);

            result = rayTraceTop.intersectBlocked(blockTop, nearRange, raytracingAccuracy);
        }

        /*
        if (debug)
        {
            rayTraceBot.highlight(world, nearRange, raytracingAccuracy);

            if (rayTraceTop != null)
            {
                rayTraceTop.highlight(world, nearRange, raytracingAccuracy);
            }
        }
        */

        return !result;
    }

    @Override
    public void reloadVars()
    {
        nearRange           = (int) coolConfig.getValue(Camo.NearCheck.RANGE, 1);
        raytracingAccuracy  = (double) coolConfig.getValue(Camo.RayTracingCheck.ACCURACY, 0.05);
        bypassPermission    = (String) coolConfig.getValue(Camo.Perms.BYPASS_CHECKS, "camouflage.bypass");
    }

}