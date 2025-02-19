package xyz.sorridi.camouflage.engines.struct;

import fun.aevy.aevycore.struct.elements.Reloadable;
import fun.aevy.aevycore.utils.builders.AverageBuilder;
import fun.aevy.aevycore.utils.configuration.elements.CoolConfig;
import lombok.Getter;
import xyz.sorridi.camouflage.Camouflage;
import xyz.sorridi.camouflage.CommonStorage;
import xyz.sorridi.camouflage.listeners.common.CommonEvaluations;

import java.util.concurrent.locks.StampedLock;

/**
 * Rappresentazione di un Thread sempre in esecuzione, utile per eseguire azioni in modo asincrono.
 *
 * @author Sorridi
 * @since 1.0
 */
@Getter
public abstract class Engine extends Thread implements Reloadable
{
    protected final Camouflage camouflage;
    protected final CoolConfig coolConfig;

    protected final CommonEvaluations   commonEvaluations;
    protected final CommonStorage       commonStorage;

    protected int       updateInterval; // Intervallo tra un'esecuzione e l'altra.
    protected boolean   execution;      // Se l'esecuzione è attiva.
    protected String    engineName;     // Nome del thread.

    private final AverageBuilder average;
    private final StampedLock    lock;

    public Engine(Camouflage camouflage, String engineName)
    {
        this.camouflage         = camouflage;
        this.coolConfig         = camouflage.getCoolConfig();
        this.commonEvaluations  = camouflage.getCommonEvaluations();
        this.commonStorage      = camouflage.getCommonStorage();
        this.execution          = true;
        this.engineName         = engineName;
        this.average            = camouflage.getCommonStorage().getRayAverage();
        this.lock               = camouflage.getCommonStorage().getAverageLock();

        init();

        camouflage.addReloadable(this);

        setName(engineName);
        start();
    }

    @SuppressWarnings("BusyWait")
    @Override
    public void run()
    {
        camouflage.infoMessage("Engine %s started.", engineName);

        while (execution)
        {
            try
            {
                startAvg();
                action();
                endAvg();

                sleep(updateInterval);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }

        camouflage.infoMessage("Engine %s stopped.", engineName);
    }

    /**
     * Salva il tempo di start delle azioni dell'{@link Engine}.
     */
    private void startAvg()
    {
        average.setStart();
    }

    /**
     * Calcola il tempo medio di esecuzione delle azioni dell'{@link Engine}.
     */
    private void endAvg()
    {
        long stamp = lock.writeLock();
        average.setEnd();
        lock.unlockWrite(stamp);
    }

    /**
     * Azione che verra' eseguita dall'{@link Engine}.
     */
    public abstract void action();

    /**
     * Metodo che viene chiamato quando l'{@link Engine} viene avviato.
     */
    public abstract void init();

    /**
     * Ferma l'esecuzione dell'{@link Engine}.
     */
    public void stopExecution()
    {
        execution = false;
    }

}