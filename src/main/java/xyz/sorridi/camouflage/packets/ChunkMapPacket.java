package xyz.sorridi.camouflage.packets;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import lombok.val;
import net.minecraft.server.v1_8_R3.PacketPlayOutMapChunk;
import xyz.sorridi.camouflage.Camouflage;
import xyz.sorridi.camouflage.CommonStorage;
import xyz.sorridi.camouflage.packets.struct.PlayerPacket;
import xyz.sorridi.camouflage.utils.ChunkUtils;

import static com.comphenix.protocol.PacketType.Play.Server.MAP_CHUNK;
import static com.comphenix.protocol.PacketType.Play.Server.MAP_CHUNK_BULK;

/**
 * Manipolazione del pacchetto relativo a {@link com.comphenix.protocol.PacketType.Play.Server#MAP_CHUNK_BULK}.
 *
 * @author Sorridi
 * @since 1.0
 */
public class ChunkMapPacket extends PlayerPacket
{
    private final CommonStorage commonStorage;

    private PacketPlayOutMapChunk.ChunkMap[] chunkMaps;
    private int[] xs;
    private int[] zs;

    public ChunkMapPacket(Camouflage camouflage, PacketContainer packet)
    {
        super(camouflage, packet);
        commonStorage = camouflage.getCommonStorage();
    }

    @Override
    public PlayerPacket with(Object... object)
    {
        for (Object entry : object)
        {
            if (entry instanceof Integer)
            {
                val temp = new int[] { (int) entry };

                xs = xs == null ? temp : xs;
                zs = zs == null ? temp : zs;
            }
            else if (entry instanceof int[])
            {
                val temp = (int[]) entry;

                xs = xs == null ? temp : xs;
                zs = zs == null ? temp : zs;
            }
            else if (entry instanceof PacketPlayOutMapChunk.ChunkMap)
            {
                val temp = new PacketPlayOutMapChunk.ChunkMap[] { (PacketPlayOutMapChunk.ChunkMap) entry };

                chunkMaps = chunkMaps == null ? temp : chunkMaps;
            }
            else if (entry instanceof PacketPlayOutMapChunk.ChunkMap[])
            {
                val temp = (PacketPlayOutMapChunk.ChunkMap[]) entry;

                chunkMaps = chunkMaps == null ? temp : chunkMaps;
            }
        }

        return this;
    }

    @Override
    public void injectInto(PacketContainer packet)
    {
        PacketType type = packet.getType();
        int index       = 0;

        if (type == MAP_CHUNK)
        {
            bufferScan(chunkMaps[0], index);
            savePacket(2, chunkMaps[0]);
        }
        else if (type == MAP_CHUNK_BULK)
        {
            for (val map : chunkMaps)
            {
                bufferScan(map, index++);
            }

            savePacket(2, chunkMaps);
        }
    }

    /**
     * Scansiona il buffer e lo modifica.
     * @param map Il chunk relativo.
     * @param chunkIndex L'indice del chunk.
     */
    private void bufferScan(PacketPlayOutMapChunk.ChunkMap map, int chunkIndex)
    {
        byte[] buffer   = map.a;
        int bitmask     = map.b;
        int index       = 0;

        int x = xs[chunkIndex] << 4;
        int y;
        int z = zs[chunkIndex] << 4;

        int newX = 0;
        int newY = 0;
        int newZ = 0;

        int shifted;

        for (int i = 0; i < ChunkUtils.CHUNK_SIZE; i++)
        {
            if ((bitmask & 1 << i) == 0)
            {
                continue;
            }

            y = i << 4;

            for (int j = 0; j < ChunkUtils.CHUNK_VOLUME; j++)
            {
                shifted = index << 1;

                if (index++ >= buffer.length)
                {
                    continue;
                }

                newX += j & 15;
                newY += j >> 8 & 15;
                newZ += j >> 4 & 15;

                bufferWrite(buffer, shifted, newX, newY, newZ);

                newX = x;
                newY = y;
                newZ = z;
            }
        }
    }

    /**
     * Scrive i dati nel buffer.
     * @param buffer Il buffer.
     * @param index L'indice sul quale scrivere.
     * @param x La coordinata x.
     * @param y La coordinata y.
     * @param z La coordinata z.
     */
    private void bufferWrite(byte[] buffer, int index, int x, int y, int z)
    {
        // Estrae il block id dal buffer.
        byte blockId = (byte) (((buffer[index] & 0xFF) | ((buffer[(index) + 1] & 0xFF) << 8)) >>> 4);

        if (blockId == ChunkUtils.BANNER_ID && commonStorage.coordsExists(x, y, z))
        {
            /* Maschera il blocco cambiandogli ID. */
            buffer[index]       = ChunkUtils.MASKED_ID;
            buffer[index + 1]   = ChunkUtils.S_MASKED_ID;
        }
    }

    /**
     * Sostituisce il pacchetto con quello modificato.
     * @param index L'indice del pacchetto.
     * @param object L'oggetto da salvare.
     */
    private void savePacket(int index, Object object)
    {
        packet.getModifier().write(index, object);
    }

    @Override
    public <T> PlayerPacket write(T object)
    {
        return null;
    }

}