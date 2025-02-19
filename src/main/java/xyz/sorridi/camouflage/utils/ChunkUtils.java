package xyz.sorridi.camouflage.utils;

import net.minecraft.server.v1_8_R3.Blocks;
import org.bukkit.craftbukkit.v1_8_R3.util.CraftMagicNumbers;

/**
 * Magic numbers degli IDs per la 1.8.8.
 *
 * @author Sorridi
 * @since 1.0
 */
public class ChunkUtils
{
    public static final int CHUNK_SIZE     = 16;
    public static final int CHUNK_VOLUME   = (int) Math.pow(CHUNK_SIZE, 3);

    public static byte BANNER_ID    = (byte) CraftMagicNumbers.getId(Blocks.STANDING_BANNER);
    public static byte ID           = (byte) ((CraftMagicNumbers.getId(Blocks.AIR) & 0xFF) << 4);
    public static byte MASKED_ID    = (byte) (ID & 0xFF);
    public static byte S_MASKED_ID  = (byte) ((ID >> 8) & 0xFF);

    public static int BANNER_TILE_ID = 6;
}