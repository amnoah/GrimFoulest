package ac.grim.grimac.utils.math.fastmath;

import ac.grim.grimac.player.GrimPlayer;
import com.github.retrooper.packetevents.protocol.player.ClientVersion;
import org.bukkit.util.Vector;

public class OptifineFastMath {

    public static FastMathType fastMathType = FastMathType.OPTIFINE_MATH;
    private static final float[] SIN_TABLE_FAST = new float[4096];
    private static final float radToIndex = roundToFloat(651.8986469044033D);

    static {
        for (int j = 0; j < SIN_TABLE_FAST.length; ++j) {
            SIN_TABLE_FAST[j] = roundToFloat(StrictMath.sin(j * Math.PI * 2.0D / 4096.0D));
        }
    }

    public static float sin(float value) {
        return SIN_TABLE_FAST[(int) (value * radToIndex) & 4095];
    }

    public static float cos(float value) {
        return SIN_TABLE_FAST[(int) (value * radToIndex + 1024.0F) & 4095];
    }

    public static float roundToFloat(double d) {
        return (float) (Math.round(d * 1.0E8D) / 1.0E8D);
    }

    public static Vector getOptiFineMathMovement(GrimPlayer player, Vector wantedMovement, float f, float f2) {
        float sin = player.getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_8) ? OptifineFastMath.sin(f2 * 0.017453292f) : LegacyFastMath.sin(f2 * 0.017453292f);
        float cos = player.getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_8) ? OptifineFastMath.cos(f2 * 0.017453292f) : LegacyFastMath.cos(f2 * 0.017453292f);
        float bestTheoreticalX = (float) (sin * wantedMovement.getZ() + cos * wantedMovement.getX()) / (sin * sin + cos * cos) / f;
        float bestTheoreticalZ = (float) (-sin * wantedMovement.getX() + cos * wantedMovement.getZ()) / (sin * sin + cos * cos) / f;
        return new Vector(bestTheoreticalX, 0, bestTheoreticalZ);
    }
}
