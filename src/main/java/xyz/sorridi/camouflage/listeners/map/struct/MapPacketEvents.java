package xyz.sorridi.camouflage.listeners.map.struct;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import lombok.Getter;
import xyz.sorridi.camouflage.Camouflage;
import xyz.sorridi.camouflage.engines.RayTracingEngine;
import xyz.sorridi.farmpvp.structure.managers.DataManager;

/**
 * Wrapper di {@link PacketAdapter} per avere a portata di mano le classi utili.
 *
 * @author Sorridi
 * @since 1.0
 */
@Getter
public abstract class MapPacketEvents extends PacketAdapter
{
    protected final Camouflage          camouflage;
    protected final DataManager         dataManager;
    protected final RayTracingEngine    rayTracingEngine;

    public MapPacketEvents(Camouflage camouflage, ListenerPriority listenerPriority, PacketType type)
    {
        super(camouflage, listenerPriority, type);

        this.camouflage         = camouflage;
        this.dataManager        = camouflage.getDataManager();
        this.rayTracingEngine   = camouflage.getRayTracingEngine();
    }

    @Override
    public void onPacketReceiving(PacketEvent event) { }

    @Override
    public void onPacketSending(PacketEvent event) { }

    /**
     * Legge il contenuto del {@link PacketContainer}.
     * @param packetContainer Il {@link PacketContainer} da leggere.
     * @param index L'indice del contenuto da leggere.
     * @return Il contenuto letto.
     */
    public Object read(PacketContainer packetContainer, int index)
    {
        return packetContainer.getModifier().read(index);
    }

}