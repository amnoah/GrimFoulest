package ac.grim.grimac.utils.math.fastmath;

import org.bukkit.util.Vector;

public class LegacyFastMath {

    private static final float[] SIN_TABLE_FAST = new float[4096];
    public static FastMathType fastMathType = FastMathType.LEGACY_MATH;

    static {
        for (int i = 0; i < 4096; ++i) {
            SIN_TABLE_FAST[i] = (float) Math.sin((i + 0.5F) / 4096.0F * ((float) Math.PI * 2F));
        }

        for (int i = 0; i < 360; i += 90) {
            SIN_TABLE_FAST[(int) (i * 11.377778F) & 4095] = (float) Math.sin(i * 0.017453292F);
        }
    }

    public static float sin(float par0) {
        return SIN_TABLE_FAST[(int) (par0 * 651.8986F) & 4095];
    }

    public static float cos(float par0) {
        return SIN_TABLE_FAST[(int) ((par0 + ((float) Math.PI / 2F)) * 651.8986F) & 4095];
    }

    public static Vector getLegacyMathMovement(Vector wantedMovement, float f, float f2) {
        float sin = sin(f2 * 0.017453292f);
        float cos = cos(f2 * 0.017453292f);
        float bestTheoreticalX = (float) (sin * wantedMovement.getZ() + cos * wantedMovement.getX()) / (sin * sin + cos * cos) / f;
        float bestTheoreticalZ = (float) (-sin * wantedMovement.getX() + cos * wantedMovement.getZ()) / (sin * sin + cos * cos) / f;
        return new Vector(bestTheoreticalX, 0, bestTheoreticalZ);
    }
}
