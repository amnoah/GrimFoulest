package ac.grim.grimac.utils.math.fastmath;

import org.bukkit.util.Vector;

public class VanillaMath {

    private static final float[] SIN_TABLE = new float[65536];
    public static FastMathType fastMathType = FastMathType.VANILLA_MATH;

    static {
        for (int i = 0; i < SIN_TABLE.length; ++i) {
            SIN_TABLE[i] = (float) StrictMath.sin(i * 3.141592653589793 * 2.0 / 65536.0);
        }
    }

    public static float sin(float f) {
        return SIN_TABLE[(int) (f * 10430.378f) & 0xFFFF];
    }

    public static float cos(float f) {
        return SIN_TABLE[(int) (f * 10430.378f + 16384.0f) & 0xFFFF];
    }

    public static Vector getVanillaMathMovement(Vector wantedMovement, float f, float f2) {
        float sin = sin(f2 * 0.017453292f);
        float cos = cos(f2 * 0.017453292f);
        float bestTheoreticalX = (float) (sin * wantedMovement.getZ() + cos * wantedMovement.getX()) / (sin * sin + cos * cos) / f;
        float bestTheoreticalZ = (float) (-sin * wantedMovement.getX() + cos * wantedMovement.getZ()) / (sin * sin + cos * cos) / f;
        return new Vector(bestTheoreticalX, 0, bestTheoreticalZ);
    }
}
