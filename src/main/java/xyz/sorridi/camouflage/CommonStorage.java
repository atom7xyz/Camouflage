package xyz.sorridi.camouflage;

import fun.aevy.aevycore.utils.builders.AverageBuilder;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import xyz.sorridi.farmpvp.structure.blocks.Flag;
import xyz.sorridi.farmpvp.structure.elements.FarmPlayer;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.StampedLock;
import java.util.stream.Stream;

/**
 * Cache (concorrente e non) di dati utili.
 *
 * @author Sorridi
 * @since 1.0
 */
@Getter
public class CommonStorage
{
    private final ConcurrentHashMap<Player, Set<Flag>> revealedFlags;
    private final Set<Flag> flags;
    private final Set<String> coords;

    private final AverageBuilder    rayAverage;
    private final StampedLock       averageLock;

    public CommonStorage()
    {
        revealedFlags   = new ConcurrentHashMap<>();
        flags           = ConcurrentHashMap.newKeySet();
        coords          = new HashSet<>();

        rayAverage      = new AverageBuilder(100);
        averageLock     = new StampedLock();
    }

    /**
     * Aggiunge una {@link Flag} alla cache.
     * @param flag {@link Flag} da aggiungere.
     */
    public void addFlag(Flag flag)
    {
        Location location = flag.getLocation();

        flags.add(flag);
        addCoords(location.getBlockX(), location.getBlockY(), location.getBlockZ());
    }

    /**
     * Rimuove una {@link Flag} dalla cache.
     * @param flag {@link Flag} da rimuovere.
     * @param location {@link Location} della {@link Flag}.
     */
    public void removeFlag(Flag flag, Location location)
    {
        flags.remove(flag);

        revealedFlags
                .values()
                .stream()
                .filter(flags -> flags.contains(flag))
                .forEach(flags -> flags.remove(flag));

        removeCoords(location.getBlockX(), location.getBlockY(), location.getBlockZ());
    }

    /**
     * Stream delle {@link Flag} della cache.
     * @return Stream delle {@link Flag} della cache.
     */
    public Stream<Flag> streamFlags()
    {
        return flags.stream();
    }

    /**
     * Aggiunge le coordinate di una {@link Flag} alla cache.
     * @param string Coordinate della {@link Flag}.
     */
    public void addCoords(String string)
    {
        coords.add(string);
    }

    /**
     * Aggiunge le coordinate di una {@link Flag} alla cache.
     * @param x Coordinata X.
     * @param y Coordinata Y.
     * @param z Coordinata Z.
     */
    public void addCoords(int x, int y, int z)
    {
        addCoords(formatCoords(x, y, z));
    }

    /**
     * Rimuove le coordinate di una {@link Flag} dalla cache.
     * @param x Coordinata X.
     * @param y Coordinata Y.
     * @param z Coordinata Z.
     */
    public void removeCoords(int x, int y, int z)
    {
        coords.remove(formatCoords(x, y, z));
    }

    /**
     * Controlla se le coordinate di una {@link Flag} sono presenti nella cache.
     * @param x Coordinata X.
     * @param y Coordinata Y.
     * @param z Coordinata Z.
     * @return Se le coordinate sono presenti.
     */
    public boolean coordsExists(int x, int y, int z)
    {
        return coords.contains(formatCoords(x, y, z));
    }

    /**
     * Aggiunge il {@link FarmPlayer} che è in grado di vedere la {@link Flag}.
     * @param farmPlayer {@link FarmPlayer} che vede la {@link Flag}.
     */
    public void addRevealed(FarmPlayer farmPlayer)
    {
        revealedFlags.put(farmPlayer.getPlayer(), ConcurrentHashMap.newKeySet());
    }

    /**
     * Rimuove il {@link FarmPlayer} che è in grado di vedere la {@link Flag}.
     * @param farmPlayer {@link FarmPlayer} che non vede piu' la {@link Flag}.
     */
    public void removeRevealed(FarmPlayer farmPlayer)
    {
        revealedFlags.remove(farmPlayer.getPlayer());
    }

    /**
     * Controlla se il {@link FarmPlayer} è in grado di vedere la {@link Flag}.
     * @param player {@link Player} da controllare.
     * @param flag {@link Flag} da controllare.
     * @return Se il è in grado di vederla.
     */
    public boolean isRevealed(Player player, Flag flag)
    {
        return revealedFlags.get(player).contains(flag);
    }

    /**
     * Aggiunge la {@link Flag} alla lista delle quali {@link FarmPlayer} è in grado di vedere.
     * @param player {@link Player} che vede la {@link Flag}.
     * @param flag {@link Flag} che vede.
     */
    public void addRevealed(Player player, Flag flag)
    {
        revealedFlags.get(player).add(flag);
    }

    /**
     * Rimuove la {@link Flag} dalla lista delle quali {@link FarmPlayer} è in grado di vedere.
     * @param player {@link Player} che non vede piu' la {@link Flag}.
     * @param flag {@link Flag} che non vede piu'.
     */
    public void removeRevealed(Player player, Flag flag)
    {
        revealedFlags.get(player).remove(flag);
    }

    /**
     * Stream delle {@link Flag} che il {@link FarmPlayer} è in grado di vedere.
     * @param player {@link Player} da controllare.
     * @return Stream delle {@link Flag} che il {@link FarmPlayer} è in grado di vedere.
     */
    public Stream<Flag> streamRevealed(Player player)
    {
        return revealedFlags.get(player).stream();
    }

    /**
     * Formatta le coordinate in una stringa.
     * @param x Coordinata X.
     * @param y Coordinata Y.
     * @param z Coordinata Z.
     * @return Stringa formattata.
     */
    private String formatCoords(int x, int y, int z)
    {
        return x + ":" + y + ":" + z;
    }

}