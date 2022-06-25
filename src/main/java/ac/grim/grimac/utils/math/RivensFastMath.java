package ac.grim.grimac.utils.math;

// Found in BetterFPS 1.2.0
// TODO: Implement this somewhere
public class RivensFastMath {

    private static final int SIN_BITS = 12;
    private static final int SIN_MASK = ~(-1 << SIN_BITS);
    private static final int SIN_COUNT = SIN_MASK + 1;
    private static final float RAD_FULL = 6.2831855f;
    private static final float DEG_FULL = 360.0f;
    private static final float RAD_TO_INDEX = SIN_COUNT / RAD_FULL;
    private static final float DEG_TO_INDEX = SIN_COUNT / DEG_FULL;
    private static final float[] SIN_TABLE = new float[SIN_COUNT];
    private static final float[] COS_TABLE = new float[SIN_COUNT];

    static {
        for (int i = 0; i < SIN_COUNT; ++i) {
            SIN_TABLE[i] = (float) Math.sin((i + 0.5f) / SIN_COUNT * RAD_FULL);
            COS_TABLE[i] = (float) Math.cos((i + 0.5f) / SIN_COUNT * RAD_FULL);
        }

        for (int i = 0; i < 360; i += 90) {
            SIN_TABLE[(int) (i * DEG_TO_INDEX) & SIN_MASK] = (float) Math.sin(i * 3.141592653589793 / 180.0);
            COS_TABLE[(int) (i * DEG_TO_INDEX) & SIN_MASK] = (float) Math.cos(i * 3.141592653589793 / 180.0);
        }
    }

    public static float sin(float rad) {
        return SIN_TABLE[(int) (rad * RAD_TO_INDEX) & SIN_MASK];
    }

    public static float cos(float rad) {
        return COS_TABLE[(int) (rad * RAD_TO_INDEX) & SIN_MASK];
    }
}
