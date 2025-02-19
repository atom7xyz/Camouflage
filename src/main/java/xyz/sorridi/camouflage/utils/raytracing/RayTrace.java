package xyz.sorridi.camouflage.utils.raytracing;

import lombok.Getter;
import lombok.val;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.util.Vector;

import java.util.ArrayList;

/**
 * @author CJP10, Sorridi
 * @since 1.0
 */
@Getter
public class RayTrace
{
    private final Vector origin;    // start position
    private final Vector direction; // direction in which the raytrace will go

    public RayTrace(Vector origin, Vector direction)
    {
        this.origin     = origin;
        this.direction  = direction;
    }

    public RayTrace(Location origin, Vector direction)
    {
        this.origin     = origin.toVector();
        this.direction  = direction;
    }

    /**
     * Gets a point on the raytrace at X blocks away.
     * @param blocksAway The amount of blocks away from the origin.
     * @return The point on the raytrace.
     */
    public Vector getPosition(double blocksAway)
    {
        return origin.clone().add(direction.clone().multiply(blocksAway));
    }

    /**
     * Checks if a position is contained within the raytrace.
     * @param position The position to check.
     * @return True if the position is contained within the raytrace.
     */
    public boolean isOnLine(Vector position)
    {
        double t = (position.getX() - origin.getX()) / direction.getX();

        return  position.getBlockY() == origin.getY() + (t * direction.getY()) &&
                position.getBlockZ() == origin.getZ() + (t * direction.getZ());
    }

    /**
     * Gets all positions on the raytrace.
     * @param blocksAway The amount of blocks away from the origin.
     * @param accuracy The accuracy of the raytrace.
     * @return All positions on the raytrace.
     */
    public ArrayList<Vector> traverse(double blocksAway, double accuracy)
    {
        ArrayList<Vector> positions = new ArrayList<>();

        for (double d = 0; d <= blocksAway; d += accuracy)
        {
            positions.add(getPosition(d));
        }

        return positions;
    }

    /**
     * Detects if the raytrace intersects.
     * @param min The minimum position.
     * @param max The maximum position.
     * @param blocksAway The amount of blocks away from the origin.
     * @param accuracy The accuracy of the raytrace.
     * @return The position of the intersection.
     */
    public Vector positionOfIntersection(Vector min, Vector max, double blocksAway, double accuracy)
    {
        val positions = traverse(blocksAway, accuracy);

        return positions
                .stream()
                .filter(pos -> intersects(pos, min, max))
                .findFirst()
                .orElse(null);
    }

    /**
     * Intersects the raytrace with a position.
     * @param min The minimum position.
     * @param max The maximum position.
     * @param blocksAway The amount of blocks away from the origin.
     * @param accuracy The accuracy of the raytrace.
     * @return If the raytrace intersects.
     */
    public boolean intersects(Vector min, Vector max, double blocksAway, double accuracy)
    {
        val positions = traverse(blocksAway, accuracy);

        return positions
                .stream()
                .anyMatch(pos -> intersects(pos, min, max));
    }

    /**
     * Detects if the raytrace intersects.
     * @param boundingBox The bounding box to intersect with.
     * @param blocksAway The amount of blocks away from the origin.
     * @param accuracy The accuracy of the raytrace.
     * @return The position of the intersection.
     */
    public Vector positionOfIntersection(BoundingBox boundingBox, double blocksAway, double accuracy)
    {
        val positions = traverse(blocksAway, accuracy);

        return positions
                .stream()
                .filter(pos -> intersects(pos, boundingBox.getMin(), boundingBox.getMax()))
                .findFirst()
                .orElse(null);
    }

    /**
     * Intersects the raytrace with a position.
     * @param boundingBox The bounding box to intersect with.
     * @param blocksAway The amount of blocks away from the origin.
     * @param accuracy The accuracy of the raytrace.
     * @return If the raytrace intersects.
     */
    public boolean intersects(BoundingBox boundingBox, double blocksAway, double accuracy)
    {
        val positions = traverse(blocksAway, accuracy);

        return positions
                .stream()
                .anyMatch(pos -> intersects(pos, boundingBox.getMin(), boundingBox.getMax()));
    }

    /**
     * Intersects the raytrace with a position, checks if the position is occupied by a block.
     * @param toIntersect The block to intersect with.
     * @param blocksAway The amount of blocks away from the origin.
     * @param accuracy The accuracy of the raytrace.
     * @return If the raytrace is blocked.
     */
    public boolean intersectBlocked(Block toIntersect, double blocksAway, double accuracy)
    {
        World world = toIntersect.getWorld();
        int pre     = 0;

        for (double d = 0; d <= blocksAway; pre = (int) d, d += accuracy)
        {
            if (pre == (int) d)
            {
                continue;
            }

            Vector position = getPosition(d);
            Block block     = position.toLocation(world).getBlock();

            if (block.getType() != Material.AIR && !block.isLiquid() && block.getType() != Material.STANDING_BANNER)
            {
                return true;
            }

            if (block.equals(toIntersect))
            {
                return false;
            }
        }

        return false;
    }

    /**
     * General intersection detection.
     * @param position The position to check.
     * @param min The minimum position.
     * @param max The maximum position.
     * @return If the raytrace intersects.
     */
    public static boolean intersects(Vector position, Vector min, Vector max)
    {
        return  position.getX() >= min.getX() && position.getX() <= max.getX() &&
                position.getY() >= min.getY() && position.getY() <= max.getY() &&
                position.getZ() >= min.getZ() && position.getZ() <= max.getZ();
    }

    /**
     * Draws the raytrace using particles.
     * @param world The world to draw the raytrace in.
     * @param blocksAway The amount of blocks away from the origin.
     * @param accuracy The accuracy of the raytrace.
     */
    public void highlight(World world, double blocksAway, double accuracy)
    {
        val positions = traverse(blocksAway, accuracy);

        positions.forEach(pos -> world.playEffect(pos.toLocation(world), Effect.COLOURED_DUST, 0));
    }

}