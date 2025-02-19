package xyz.sorridi.camouflage.listeners.blocks;

import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import lombok.val;
import org.bukkit.World;
import org.bukkit.entity.Player;
import xyz.sorridi.camouflage.Camouflage;
import xyz.sorridi.camouflage.CommonStorage;
import xyz.sorridi.camouflage.listeners.common.CommonEvaluations;

import static com.comphenix.protocol.PacketType.Play.Server.BLOCK_CHANGE;

/**
 * {@link PacketAdapter} che monitora i pacchetti di {@link com.comphenix.protocol.PacketType.Play.Server#BLOCK_CHANGE}
 * che vengono inviati dal server al client.
 *
 * @author Sorridi
 * @since 1.0
 */
public class BlockChangeListener extends PacketAdapter
{
    private final CommonEvaluations evaluations;
    private final CommonStorage     commonStorage;

    public BlockChangeListener(Camouflage camouflage)
    {
        super(camouflage, ListenerPriority.MONITOR, BLOCK_CHANGE);

        commonStorage   = camouflage.getCommonStorage();
        evaluations     = camouflage.getCommonEvaluations();
    }

    @Override
    public void onPacketReceiving(PacketEvent event) { }

    @Override
    public void onPacketSending(PacketEvent event)
    {
        PacketContainer packet  = event.getPacket();
        Player player           = event.getPlayer();
        World world             = player.getWorld();
        val blockPosition       = evaluations.getBlockPosition(packet);

        if (!evaluations.isBanner(blockPosition, world))
        {
            return;
        }

        commonStorage
                .streamFlags()
                .filter(flag -> evaluations.shouldBeHidden(flag, player))
                .forEach(flag ->
                {
                    if (evaluations.equalLocation(blockPosition, flag))
                    {
                        event.setReadOnly(false);
                        event.setCancelled(true);
                    }
                });
    }

}