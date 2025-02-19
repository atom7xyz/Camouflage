package xyz.sorridi.camouflage.listeners.map.struct;

import com.comphenix.protocol.events.PacketContainer;
import net.minecraft.server.v1_8_R3.PacketPlayOutMapChunk;

/**
 * Interfaccia che contiene i metodi per la gestione dei pacchetti di {@link com.comphenix.protocol.PacketType.Play.Server#MAP_CHUNK_BULK}
 *
 * @author Sorridi
 * @since 1.0
 */
public interface MapBulkPacket
{
    int[] getXs(PacketContainer packetContainer);

    int[] getZs(PacketContainer packetContainer);

    PacketPlayOutMapChunk.ChunkMap[] getChunkMaps(PacketContainer packetContainer);
}
