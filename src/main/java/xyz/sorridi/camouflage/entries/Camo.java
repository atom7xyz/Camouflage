package xyz.sorridi.camouflage.entries;

import fun.aevy.aevycore.utils.configuration.elements.annotations.Resource;

/**
 * Entries per {@link fun.aevy.aevycore.utils.configuration.elements.CoolConfig}.
 *
 * @author Sorridi
 * @since 1.0
 */
public class Camo
{
    private static final String PLUGIN_NAME     = "camouflage.";
    private static final String TRACKING_CHECKS = PLUGIN_NAME + "tracking-engine.";

    @Resource(path = "permissions")
    public enum Perms
    {
        RELOAD, BYPASS_CHECKS
    }

    @Resource(path = PLUGIN_NAME + "camo-command")
    public enum CamoCommand
    {
        VERSION, CONSUMPTION, RELOAD
    }

    @Resource(path = PLUGIN_NAME + "usages")
    public enum Usages
    {
        CAMOUFLAGE
    }

    @Resource(path = PLUGIN_NAME + "tracking-engine")
    public enum TrackingEngine
    {
        UPDATE_INTERVAL
    }

    @Resource(path = TRACKING_CHECKS + "near-check")
    public enum NearCheck
    {
        RANGE
    }

    @Resource(path = TRACKING_CHECKS + "raytracing-check")
    public enum RayTracingCheck
    {
        ACCURACY
    }

}