package ac.grim.grimac.utils.math.fastmath;

import org.bukkit.util.Vector;

// Found in BetterFPS 1.2.0
// TODO: Implement this somewhere
public class JavaFastMath {

    public static FastMathType fastMathType = FastMathType.JAVA_MATH;

    public static float sin(float radians) {
        return (float) Math.sin(radians);
    }

    public static float cos(float radians) {
        return (float) Math.cos(radians);
    }

    public static Vector getJavaMathMovement(Vector wantedMovement, float f, float f2) {
        float sin = sin(f2 * 0.017453292f);
        float cos = cos(f2 * 0.017453292f);
        float bestTheoreticalX = (float) (sin * wantedMovement.getZ() + cos * wantedMovement.getX()) / (sin * sin + cos * cos) / f;
        float bestTheoreticalZ = (float) (-sin * wantedMovement.getX() + cos * wantedMovement.getZ()) / (sin * sin + cos * cos) / f;
        return new Vector(bestTheoreticalX, 0, bestTheoreticalZ);
    }
}
