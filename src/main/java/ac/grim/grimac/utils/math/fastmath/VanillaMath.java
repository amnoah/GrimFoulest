package ac.grim.grimac.utils.math.fastmath;

public class VanillaMath {

    private static final float[] SIN_TABLE = new float[65536];

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
}
