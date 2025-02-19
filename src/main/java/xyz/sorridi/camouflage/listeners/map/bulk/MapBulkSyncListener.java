package xyz.sorridi.camouflage.listeners.map.bulk;

import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import lombok.val;
import net.minecraft.server.v1_8_R3.PacketPlayOutMapChunk;
import org.bukkit.entity.Player;
import xyz.sorridi.camouflage.Camouflage;
import xyz.sorridi.camouflage.listeners.map.struct.MapBulkPacket;
import xyz.sorridi.camouflage.listeners.map.struct.MapPacketEvents;
import xyz.sorridi.camouflage.packets.ChunkMapPacket;

import static com.comphenix.protocol.PacketType.Play.Server.MAP_CHUNK_BULK;

/**
 * {@link PacketAdapter} che monitora i pacchetti di {@link com.comphenix.protocol.PacketType.Play.Server#MAP_CHUNK_BULK}
 * che vengono inviati dal server al client.
 *
 * @author Sorridi
 * @since 1.0
 */
public class MapBulkSyncListener extends MapPacketEvents implements MapBulkPacket
{

    public MapBulkSyncListener(Camouflage camouflage)
    {
        super(camouflage, ListenerPriority.MONITOR, MAP_CHUNK_BULK);
    }

    @Override
    public void onPacketSending(PacketEvent event)
    {
        PacketContainer packet  = event.getPacket();
        Player player           = event.getPlayer();

        int[] xs    = getXs(packet);
        int[] zs    = getZs(packet);
        val chunks  = getChunkMaps(packet);

        new ChunkMapPacket(camouflage, packet)
                .to(player)
                .with(xs, zs, chunks)
                .injectFirst()
                .send(true);
    }

    @Override
    public int[] getXs(PacketContainer packetContainer)
    {
        return (int[]) read(packetContainer, 0);
    }

    @Override
    public int[] getZs(PacketContainer packetContainer)
    {
        return (int[]) read(packetContainer, 1);
    }

    @Override
    public PacketPlayOutMapChunk.ChunkMap[] getChunkMaps(PacketContainer packetContainer)
    {
        return (PacketPlayOutMapChunk.ChunkMap[]) read(packetContainer, 2);
    }

}