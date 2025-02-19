package xyz.sorridi.camouflage.packets.struct;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import lombok.Getter;
import lombok.SneakyThrows;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import xyz.sorridi.camouflage.Camouflage;
import xyz.sorridi.camouflage.engines.tasks.SendSyncPacket;

/**
 * Struttura principale per manipolare i {@link PacketContainer} e creare nuovi pacchetti.
 *
 * @author Sorridi
 * @since 1.0
 */
@Getter
public abstract class PlayerPacket
{
    protected final Camouflage camouflage;

    protected Player            player;
    protected PacketContainer   packet;

    protected boolean inject;

    public PlayerPacket(Camouflage camouflage, PacketContainer packet)
    {
        this.camouflage = camouflage;
        this.packet     = packet;
    }

    public PlayerPacket(Camouflage camouflage, PacketType packetType)
    {
        this.camouflage = camouflage;
        this.packet     = new PacketContainer(packetType);
    }

    public PlayerPacket to(@NotNull Player player)
    {
        this.player = player;
        return this;
    }

    public PlayerPacket injectFirst()
    {
        this.inject = true;
        return this;
    }

    @SneakyThrows
    public void send(boolean sync)
    {
        if (player == null || packet == null || !player.isOnline())
        {
            return;
        }

        if (sync)
        {
            if (inject)
            {
                injectInto(packet);
            }
            else
            {
                new SendSyncPacket(camouflage).runTask(player, packet);
            }
        }
        else
        {
            camouflage.getProtocolManager().sendServerPacket(player, packet);
        }
    }

    public abstract PlayerPacket with(Object... object);

    public abstract <T> PlayerPacket write(T object);

    public abstract void injectInto(PacketContainer packet);

}