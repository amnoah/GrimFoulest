package ac.grim.grimac.utils.math;

import ac.grim.grimac.player.GrimPlayer;
import com.github.retrooper.packetevents.protocol.player.ClientVersion;
import lombok.Getter;
import org.bukkit.util.Vector;

public class TrigHandler {

    final GrimPlayer player;
    private double buffer = 0;
    @Getter
    private boolean isVanillaMath = true;

    public TrigHandler(GrimPlayer player) {
        this.player = player;
    }

    public Vector getVanillaMathMovement(Vector wantedMovement, float f, float f2) {
        float sin = VanillaMath.sin(f2 * 0.017453292f);
        float cos = VanillaMath.cos(f2 * 0.017453292f);
        float bestTheoreticalX = (float) (sin * wantedMovement.getZ() + cos * wantedMovement.getX()) / (sin * sin + cos * cos) / f;
        float bestTheoreticalZ = (float) (-sin * wantedMovement.getX() + cos * wantedMovement.getZ()) / (sin * sin + cos * cos) / f;
        return new Vector(bestTheoreticalX, 0, bestTheoreticalZ);
    }

    public Vector getFastMathMovement(Vector wantedMovement, float f, float f2) {
        float sin = player.getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_8) ? OptifineFastMath.sin(f2 * 0.017453292f) : LegacyFastMath.sin(f2 * 0.017453292f);
        float cos = player.getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_8) ? OptifineFastMath.cos(f2 * 0.017453292f) : LegacyFastMath.cos(f2 * 0.017453292f);
        float bestTheoreticalX = (float) (sin * wantedMovement.getZ() + cos * wantedMovement.getX()) / (sin * sin + cos * cos) / f;
        float bestTheoreticalZ = (float) (-sin * wantedMovement.getX() + cos * wantedMovement.getZ()) / (sin * sin + cos * cos) / f;
        return new Vector(bestTheoreticalX, 0, bestTheoreticalZ);
    }

    public void setOffset(Vector oldVel, double offset) {
        // Offset too high, this is an outlier, ignore
        // We are checking in the range of 1e-3 to 5e-5, around what using the wrong trig system results in
        //
        // Ignore if 0 offset
        if (offset == 0 || offset > 1e-3) {
            return;
        }

        boolean flags = player.checkManager.getOffsetHandler().doesOffsetFlag(offset);
        buffer = Math.max(0, buffer);

        // Gliding doesn't allow inputs, so, therefore we must rely on the old type of check for this
        // This isn't too accurate but what choice do I have?
        if (player.isGliding) {
            buffer += flags ? 1 : -0.25;

            if (buffer > 5) {
                buffer = 0;
                isVanillaMath = !isVanillaMath;
            }

            return;
        }

        if (player.checkManager.getOffsetHandler().doesOffsetFlag(offset)) {
            Vector trueMovement = player.actualMovement.clone().subtract(oldVel);
            Vector correctMath = getVanillaMathMovement(trueMovement, 0.1f, player.xRot);
            Vector fastMath = getFastMathMovement(trueMovement, 0.1f, player.xRot);
            correctMath = new Vector(Math.abs(correctMath.getX()), 0, Math.abs(correctMath.getZ()));
            fastMath = new Vector(Math.abs(fastMath.getX()), 0, Math.abs(fastMath.getZ()));

            double minCorrectHorizontal = Math.min(correctMath.getX(), correctMath.getZ());
            // Support diagonal inputs
            minCorrectHorizontal = Math.min(minCorrectHorizontal, Math.abs(correctMath.getX() - correctMath.getZ()));

            double minFastMathHorizontal = Math.min(fastMath.getX(), fastMath.getZ());
            // Support diagonal inputs
            minFastMathHorizontal = Math.min(minFastMathHorizontal, Math.abs(fastMath.getX() - fastMath.getZ()));

            boolean newVanilla = minCorrectHorizontal < minFastMathHorizontal;
            buffer += newVanilla != isVanillaMath ? 1 : -0.25;

            if (buffer > 5) {
                buffer = 0;
                isVanillaMath = !isVanillaMath;
            }
        }
    }

    public float sin(float f) {
        return isVanillaMath ? VanillaMath.sin(f) : (player.getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_8) ? OptifineFastMath.sin(f) : LegacyFastMath.sin(f));
    }

    public float cos(float f) {
        return isVanillaMath ? VanillaMath.cos(f) : (player.getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_8) ? OptifineFastMath.cos(f) : LegacyFastMath.cos(f));
    }
}
