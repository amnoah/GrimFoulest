package ac.grim.grimac.utils.math;

// Found in BetterFPS 1.2.0
// TODO: Implement this somewhere
public class LibGDXFastMath {

    private static final float[] SIN_TABLE = new float[16384];

    static {
        for (int i = 0; i < 16384; ++i) {
            SIN_TABLE[i] = (float) Math.sin((i + 0.5f) / 16384.0f * 6.2831855f);
        }

        for (int i = 0; i < 360; i += 90) {
            SIN_TABLE[(int) (i * 45.511112f) & 0x3FFF] = (float) Math.sin(i * 0.017453292f);
        }
    }

    public static float sin(float radians) {
        return SIN_TABLE[(int) (radians * 2607.5945f) & 0x3FFF];
    }

    public static float cos(float radians) {
        return SIN_TABLE[(int) ((radians + 1.5707964f) * 2607.5945f) & 0x3FFF];
    }
}
