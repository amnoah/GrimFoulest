package ac.grim.grimac.utils.math.fastmath;

import org.bukkit.util.Vector;

// Found in BetterFPS 1.2.0
// TODO: Implement this somewhere
public class RivensFullFastMath {

    private static final float SIN_TO_COS = 1.5707964f;
    private static final int SIN_BITS = 12;
    private static final int SIN_MASK = ~(-1 << SIN_BITS);
    private static final int SIN_COUNT = SIN_MASK + 1;
    private static final float[] SIN_TABLE = new float[SIN_COUNT];
    private static final float RAD_FULL = 6.2831855f;
    private static final float RAD_TO_INDEX = SIN_COUNT / RAD_FULL;
    public static FastMathType fastMathType = FastMathType.RIVENS_FULL_MATH;

    static {
        for (int i = 0; i < SIN_COUNT; ++i) {
            SIN_TABLE[i] = (float) Math.sin((i + Math.min(1, i % (SIN_COUNT / 4)) * 0.5) / SIN_COUNT * RAD_FULL);
        }
    }

    public static float sin(float rad) {
        return SIN_TABLE[(int) (rad * RAD_TO_INDEX) & SIN_MASK];
    }

    public static float cos(float rad) {
        return sin(rad + SIN_TO_COS);
    }

    public static Vector getRivensFullMathMovement(Vector wantedMovement, float f, float f2) {
        float sin = sin(f2 * 0.017453292f);
        float cos = cos(f2 * 0.017453292f);
        float bestTheoreticalX = (float) (sin * wantedMovement.getZ() + cos * wantedMovement.getX()) / (sin * sin + cos * cos) / f;
        float bestTheoreticalZ = (float) (-sin * wantedMovement.getX() + cos * wantedMovement.getZ()) / (sin * sin + cos * cos) / f;
        return new Vector(bestTheoreticalX, 0, bestTheoreticalZ);
    }
}
