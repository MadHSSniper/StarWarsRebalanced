package data.scripts.world;

import org.lwjgl.util.vector.Vector2f;

/**
 *
 * This class manages the calculation of the coordinates for the Star Systems to be
 * used by the Star Wars mod by receiving X and Y offsets relative to the root
 * coordinates, returning the final value to be used by the systems. See
 * http://www.swgalaxymap.com/ and use Coruscant's location as root for relative
 * position calculation or call the getRoot() method from this class. Every
 * square in the SW Galaxy Map should be considered 1000 units wide for
 * calculation purposes.
 * 
 * @author joaonunes
 */
public class SW_SystemCalculator {

    private static final float X = 6500f;           //Root SS map position X
    private static final float Y = 17000f;           //Root SS map position Y
    private static final float MULTIPLIER = 0.7f;    //higher = spread systems

    /**
     *
     * Returns a Vector2f with X and Y root coordinates
     */
    public static Vector2f getRoot() {
        return new Vector2f(X, Y);
    }

    /**
     *
     * Returns X root coordinate
     */
    public static Float getRootX() {
        return X;
    }
    
    /**
     *
     * Returns Y root coordinate
     */
    public static Float getRootY() {
        return Y;
    }
    
    /**
     * 
     * Visit http://www.swgalaxymap.com/ to get X and Y coordinates of a system
     * 
     * @param x X coordinate from the map
     * @param y Y coordinate from the map
     */
    public static Vector2f getCoords(Float x, Float y) {
        return new Vector2f(X + (x * MULTIPLIER), Y + (y * MULTIPLIER));
    }

    /**
     *
     * Visit http://www.swgalaxymap.com/ to get X coordinate of a system
     * 
     * @param x X coordinate from the map
     */
    public static Float calcX(int x) {
        return X + (x * MULTIPLIER);
    }

    /**
     *
     * Visit http://www.swgalaxymap.com/ to get Y coordinate of a system
     * 
     * @param y Y coordinate from the map
     */
    public static Float calcY(int y) {
        return Y + (y * MULTIPLIER);
    }
}
