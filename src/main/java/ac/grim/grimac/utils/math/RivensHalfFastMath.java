package ac.grim.grimac.utils.math;

// Found in BetterFPS 1.2.0
// TODO: Implement this somewhere
public class RivensHalfFastMath {

    private static final float SIN_TO_COS = 1.5707964f;
    private static final int SIN_BITS = 12;
    private static final int SIN_MASK = ~(-1 << SIN_BITS);
    private static final int SIN_MASK2 = SIN_MASK >> 1;
    private static final int SIN_COUNT = SIN_MASK + 1;
    private static final int SIN_COUNT2 = SIN_MASK2 + 1;
    private static final float RAD_FULL = 6.2831855f;
    private static final float RAD_TO_INDEX = SIN_COUNT / RAD_FULL;
    private static final float[] SIN_TABLE_HALF = new float[SIN_COUNT2];

    static {
        for (int i = 0; i < SIN_COUNT2; ++i) {
            SIN_TABLE_HALF[i] = (float) Math.sin((i + Math.min(1, i % (SIN_COUNT / 4)) * 0.5) / SIN_COUNT * RAD_FULL);
        }
    }

    public static float sin(float rad) {
        int index1 = (int) (rad * RAD_TO_INDEX) & SIN_MASK;
        int index2 = index1 & SIN_MASK2;
        int mul = (index1 == index2) ? 1 : -1;
        return SIN_TABLE_HALF[index2] * mul;
    }

    public static float cos(float rad) {
        return sin(rad + SIN_TO_COS);
    }
}
