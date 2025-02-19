package xyz.sorridi.camouflage.packets;

import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.BlockPosition;
import org.bukkit.Location;
import org.jetbrains.annotations.Nullable;
import xyz.sorridi.camouflage.Camouflage;
import xyz.sorridi.camouflage.packets.struct.PlayerPacket;
import xyz.sorridi.camouflage.utils.ChunkUtils;
import xyz.sorridi.farmpvp.structure.blocks.Flag;

import static com.comphenix.protocol.PacketType.Play.Server.TILE_ENTITY_DATA;

/**
 * Manipolazione del pacchetto relativo a {@link com.comphenix.protocol.PacketType.Play.Server#TILE_ENTITY_DATA}.
 *
 * @author Sorridi
 * @since 1.0
 */
public class TileEntityPacket extends PlayerPacket
{
    private int x, y, z;

    public TileEntityPacket(Camouflage camouflage)
    {
        super(camouflage, TILE_ENTITY_DATA);
    }

    /**
     * Scrive il pacchetto.
     * @param flag {@link Flag} da scrivere.
     * @return Il {@link RevealPacket}.
     */
    public TileEntityPacket write(@Nullable Flag flag)
    {
        if (flag != null)
        {
            packet.getBlockPositionModifier().write(0, new BlockPosition(x, y, z));
            packet.getModifier().write(1, ChunkUtils.BANNER_TILE_ID);
            packet.getModifier().write(2, flag.getNbtTileEntity().getCompound());
        }

        return this;
    }

    @Override
    public TileEntityPacket with(Object... object)
    {
        for (Object entry : object)
        {
            if (entry instanceof Location)
            {
                Location location = (Location) entry;
                this.x = location.getBlockX();
                this.y = location.getBlockY();
                this.z = location.getBlockZ();
            }
        }

        return this;
    }

    @Override
    public <T> PlayerPacket write(T object)
    {
        return write((Flag) object);
    }

    @Override
    public void injectInto(PacketContainer packet) { }

}