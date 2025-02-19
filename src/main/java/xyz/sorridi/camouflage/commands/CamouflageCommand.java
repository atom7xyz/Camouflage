package xyz.sorridi.camouflage.commands;

import fun.aevy.aevycore.utils.StringUtils;
import fun.aevy.aevycore.utils.VersioningUtils;
import fun.aevy.aevycore.utils.builders.AverageBuilder;
import fun.aevy.aevycore.utils.builders.CommandsBuilder;
import fun.aevy.aevycore.utils.formatting.MessageProperties;
import fun.aevy.aevycore.utils.formatting.Send;
import lombok.val;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import xyz.sorridi.camouflage.Camouflage;
import xyz.sorridi.camouflage.CommonStorage;
import xyz.sorridi.camouflage.entries.Camo;

import java.util.List;
import java.util.concurrent.locks.StampedLock;

/**
 * Comando "/camouflage".
 *
 * @author Sorridi
 * @since 1.0
 */
public class CamouflageCommand extends CommandsBuilder
{
    private CommonStorage commonStorage;

    private MessageProperties version, reload, consumption;

    private String reloadPerm;

    public CamouflageCommand(@NotNull Camouflage camouflage, @NotNull String command)
    {
        super(camouflage, command);
        this.commonStorage = camouflage.getCommonStorage();
    }

    @Override
    public boolean command(CommandSender commandSender, String[] strings)
    {
        int args = strings.length;

        if (args == 1)
        {
            String firstArg = strings[0].toLowerCase();

            switch (firstArg)
            {
                case "version":
                {
                    Send.message(commandSender, version);
                    return true;
                }
                case "reload":
                {
                    if (commandSender.hasPermission(reloadPerm))
                    {
                        aevyDependent.reloadCoolConfig();
                        aevyDependent.reloadReloadables();
                        Send.message(commandSender, reload);
                    }
                    else
                    {
                        Send.message(commandSender, noPerms);
                    }
                    return true;
                }
                case "consumption":
                {
                    AverageBuilder average  = commonStorage.getRayAverage();
                    StampedLock stampedLock = commonStorage.getAverageLock();

                    long stamp = stampedLock.readLock();
                    double value = average.get();
                    stampedLock.unlockRead(stamp);

                    consumption.replace("{average}", StringUtils.format(value, 6));

                    Send.message(commandSender, consumption);
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public List<String> tabComplete(String[] strings)
    {
        int length = strings.length;

        if (length == 1)
        {
            return List.of("version", "consumption", "reload");
        }

        return null;
    }

    @Override
    public void reloadVars()
    {
        val versioning = new VersioningUtils(aevyDependent);

        reloadPerm = (String) coolConfig.getValue(Camo.Perms.RELOAD);

        reload      = coolConfig.getProperties(Camo.CamoCommand.RELOAD);
        version     = coolConfig.getProperties(Camo.CamoCommand.VERSION);
        consumption  = coolConfig.getProperties(Camo.CamoCommand.CONSUMPTION);

        String ver  = versioning.getVersion();
        String hash = versioning.getHash();
        String site = versioning.getSite();
        val authors = versioning.getAuthors();

        val replace = new String[] { "{ver}", "{hash}", "{authors}", "{site}" };

        version.replace(replace, ver, hash, authors, site);
    }

}