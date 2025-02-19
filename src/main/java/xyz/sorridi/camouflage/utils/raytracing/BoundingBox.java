package xyz.sorridi.camouflage.utils.raytracing;

import lombok.Getter;
import lombok.val;
import net.minecraft.server.v1_8_R3.AxisAlignedBB;
import net.minecraft.server.v1_8_R3.BlockPosition;
import net.minecraft.server.v1_8_R3.WorldServer;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.util.Vector;

/**
 * @author CJP10, Sorridi
 * @since 1.0
 */
@Getter
public class BoundingBox
{
    private final Vector max;
    private final Vector min;

    public BoundingBox(Vector min, Vector max)
    {
        this.max = max;
        this.min = min;
    }

    public BoundingBox(AxisAlignedBB bb)
    {
        min = new Vector(bb.a, bb.b, bb.c);
        max = new Vector(bb.d, bb.e, bb.f);
    }

    /**
     * Create a BoundingBox from a block (1.8.8).
     * @param block The block.
     */
    public BoundingBox(Block block)
    {
        double x = block.getX();
        double y = block.getY();
        double z = block.getZ();

        BlockPosition blockPosition = new BlockPosition(x, y, z);
        WorldServer worldServer     = ((CraftWorld) block.getWorld()).getHandle();

        val blockNative = worldServer.getType(blockPosition).getBlock();
        blockNative.updateShape(worldServer, blockPosition);

        min = new Vector(x + blockNative.B(), y + blockNative.D(), z + blockNative.F());
        max = new Vector(x + blockNative.C(), y + blockNative.E(), z + blockNative.G());
    }

    /**
     * Create a BoundingBox from a location (1.8.8).
     * @param location The location.
     */
    public BoundingBox(Location location)
    {
        double x = location.getX();
        double y = location.getY();
        double z = location.getZ();

        BlockPosition blockPosition = new BlockPosition(x, y, z);
        WorldServer worldServer     = ((CraftWorld) location.getWorld()).getHandle();

        val blockNative = worldServer.getType(blockPosition).getBlock();
        blockNative.updateShape(worldServer, blockPosition);

        min = new Vector(x + blockNative.B(), y + blockNative.D(), z + blockNative.F());
        max = new Vector(x + blockNative.C(), y + blockNative.E(), z + blockNative.G());
    }

    /**
     * Gets the center of the BoundingBox.
     * @return The vector pointing at the center.
     */
    public Vector midPoint()
    {
        return max.clone().add(min).multiply(0.5);
    }

    /**
     * Grows the max value of the BoundingBox by a certain amount.
     * @param x The amount to grow in the x direction.
     * @param y The amount to grow in the y direction.
     * @param z The amount to grow in the z direction.
     * @return The new BoundingBox.
     */
    public BoundingBox growMax(double x, double y, double z)
    {
        x += max.getX();
        y += max.getY();
        z += max.getZ();

        max.setX(x).setY(y).setZ(z);

        return this;
    }

    /**
     * Grows the min value of the BoundingBox by a certain amount.
     * @param x The amount to grow in the x direction.
     * @param y The amount to grow in the y direction.
     * @param z The amount to grow in the z direction.
     * @return The new BoundingBox.
     */
    public BoundingBox growMin(double x, double y, double z)
    {
        x += min.getX();
        y += min.getY();
        z += min.getZ();

        min.setX(x).setY(y).setZ(z);

        return this;
    }

}