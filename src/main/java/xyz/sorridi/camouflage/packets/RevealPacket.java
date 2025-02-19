package xyz.sorridi.camouflage.packets;

import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.BlockPosition;
import com.comphenix.protocol.wrappers.WrappedBlockData;
import lombok.val;
import org.bukkit.Location;
import org.bukkit.Material;
import org.jetbrains.annotations.Nullable;
import xyz.sorridi.camouflage.Camouflage;
import xyz.sorridi.camouflage.packets.struct.PlayerPacket;
import xyz.sorridi.farmpvp.structure.blocks.Flag;

import static com.comphenix.protocol.PacketType.Play.Server.BLOCK_CHANGE;

/**
 * Manipolazione del pacchetto relativo a {@link com.comphenix.protocol.PacketType.Play.Server#BLOCK_CHANGE}.
 *
 * @author Sorridi
 * @since 1.0
 */
public class RevealPacket extends PlayerPacket
{
    private BlockPosition blockPosition;

    public RevealPacket(Camouflage camouflage)
    {
        super(camouflage, BLOCK_CHANGE);
    }

    @Override
    public RevealPacket with(Object... object)
    {
        for (Object entry : object)
        {
            if (entry instanceof Location)
            {
                Location location = (Location) entry;

                int x = location.getBlockX();
                int y = location.getBlockY();
                int z = location.getBlockZ();

                blockPosition = new BlockPosition(x, y, z);
            }
        }

        return this;
    }

    /**
     * Scrive il pacchetto.
     * @param flag {@link Flag} da scrivere.
     * @return Il {@link RevealPacket}.
     */
    public RevealPacket write(@Nullable Flag flag)
    {
        Material material   = flag == null ? Material.AIR : flag.getBlock().getType();
        val wrapped         = WrappedBlockData.createData(material);

        if (flag != null)
        {
            wrapped.setData(flag.getBlock().getData());
        }

        packet.getBlockPositionModifier().write(0, blockPosition);
        packet.getBlockData().write(0, wrapped);
        return this;
    }

    @Override
    public void injectInto(PacketContainer packet) { }

    @Override
    public <T> PlayerPacket write(T object)
    {
        return write((Flag) object);
    }

}