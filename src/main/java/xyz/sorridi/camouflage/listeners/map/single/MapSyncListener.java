package xyz.sorridi.camouflage.listeners.map.single;

import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import lombok.val;
import net.minecraft.server.v1_8_R3.PacketPlayOutMapChunk;
import org.bukkit.entity.Player;
import xyz.sorridi.camouflage.Camouflage;
import xyz.sorridi.camouflage.listeners.map.struct.MapPacket;
import xyz.sorridi.camouflage.listeners.map.struct.MapPacketEvents;
import xyz.sorridi.camouflage.packets.ChunkMapPacket;

import static com.comphenix.protocol.PacketType.Play.Server.MAP_CHUNK;

/**
 * {@link PacketAdapter} che monitora i pacchetti di {@link com.comphenix.protocol.PacketType.Play.Server#MAP_CHUNK}
 * che vengono inviati dal server al client.
 *
 * @author Sorridi
 * @since 1.0
 */
public class MapSyncListener extends MapPacketEvents implements MapPacket
{

    public MapSyncListener(Camouflage camouflage)
    {
        super(camouflage, ListenerPriority.MONITOR, MAP_CHUNK);
    }

    @Override
    public void onPacketSending(PacketEvent event)
    {
        PacketContainer packet  = event.getPacket();
        Player player           = event.getPlayer();

        int x = getX(packet);
        int z = getZ(packet);
        val chunk = getChunkMap(packet);

        new ChunkMapPacket(camouflage, packet)
                .to(player)
                .with(x, z, chunk)
                .injectFirst()
                .send(true);
    }

    @Override
    public int getX(PacketContainer packetContainer)
    {
        return (int) read(packetContainer, 0) << 4;
    }

    @Override
    public int getZ(PacketContainer packetContainer)
    {
        return (int) read(packetContainer, 1) << 4;
    }

    @Override
    public PacketPlayOutMapChunk.ChunkMap getChunkMap(PacketContainer packetContainer)
    {
        return (PacketPlayOutMapChunk.ChunkMap) read(packetContainer, 2);
    }

}