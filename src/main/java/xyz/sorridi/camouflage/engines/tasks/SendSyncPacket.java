package xyz.sorridi.camouflage.engines.tasks;

import com.comphenix.protocol.events.PacketContainer;
import fun.aevy.aevycore.struct.elements.scheduler.Scheduler;
import lombok.val;
import org.bukkit.entity.Player;
import xyz.sorridi.camouflage.Camouflage;

import java.lang.reflect.InvocationTargetException;

/**
 * {@link Scheduler} che si occupa d'inviare i pacchetti in modo sincrono.
 *
 *  @author Sorridi
 *  @since 1.0
 */
public class SendSyncPacket extends Scheduler
{
    private final Camouflage camouflage;

    public SendSyncPacket(Camouflage camouflage)
    {
        super(camouflage);
        this.camouflage = camouflage;
    }

    @Override
    public void runTask(Object... objects)
    {
        val player = (Player) objects[0];
        val packet = (PacketContainer) objects[1];

        runTask(() ->
        {
            try
            {
                camouflage.getProtocolManager().sendServerPacket(player, packet);
            }
            catch (InvocationTargetException e)
            {
                e.printStackTrace();
                camouflage.errorMessage("Could not send %s to %s", packet.getType(), player);
            }
        });
    }

}
