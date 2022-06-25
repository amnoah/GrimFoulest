package ac.grim.grimac.utils.math;

import ac.grim.grimac.player.GrimPlayer;
import ac.grim.grimac.utils.math.fastmath.*;
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

    public Vector getJavaMathMovement(Vector wantedMovement, float f, float f2) {
        float sin = JavaFastMath.sin(f2 * 0.017453292f);
        float cos = JavaFastMath.cos(f2 * 0.017453292f);
        float bestTheoreticalX = (float) (sin * wantedMovement.getZ() + cos * wantedMovement.getX()) / (sin * sin + cos * cos) / f;
        float bestTheoreticalZ = (float) (-sin * wantedMovement.getX() + cos * wantedMovement.getZ()) / (sin * sin + cos * cos) / f;
        return new Vector(bestTheoreticalX, 0, bestTheoreticalZ);
    }

    public Vector getLibGDXMathMovement(Vector wantedMovement, float f, float f2) {
        float sin = LibGDXFastMath.sin(f2 * 0.017453292f);
        float cos = LibGDXFastMath.cos(f2 * 0.017453292f);
        float bestTheoreticalX = (float) (sin * wantedMovement.getZ() + cos * wantedMovement.getX()) / (sin * sin + cos * cos) / f;
        float bestTheoreticalZ = (float) (-sin * wantedMovement.getX() + cos * wantedMovement.getZ()) / (sin * sin + cos * cos) / f;
        return new Vector(bestTheoreticalX, 0, bestTheoreticalZ);
    }

    public Vector getRivensMathMovement(Vector wantedMovement, float f, float f2) {
        float sin = RivensFastMath.sin(f2 * 0.017453292f);
        float cos = RivensFastMath.cos(f2 * 0.017453292f);
        float bestTheoreticalX = (float) (sin * wantedMovement.getZ() + cos * wantedMovement.getX()) / (sin * sin + cos * cos) / f;
        float bestTheoreticalZ = (float) (-sin * wantedMovement.getX() + cos * wantedMovement.getZ()) / (sin * sin + cos * cos) / f;
        return new Vector(bestTheoreticalX, 0, bestTheoreticalZ);
    }

    public Vector getRivensFullMathMovement(Vector wantedMovement, float f, float f2) {
        float sin = RivensFullFastMath.sin(f2 * 0.017453292f);
        float cos = RivensFullFastMath.cos(f2 * 0.017453292f);
        float bestTheoreticalX = (float) (sin * wantedMovement.getZ() + cos * wantedMovement.getX()) / (sin * sin + cos * cos) / f;
        float bestTheoreticalZ = (float) (-sin * wantedMovement.getX() + cos * wantedMovement.getZ()) / (sin * sin + cos * cos) / f;
        return new Vector(bestTheoreticalX, 0, bestTheoreticalZ);
    }

    public Vector getTaylorMathMovement(Vector wantedMovement, float f, float f2) {
        float sin = TaylorFastMath.sin(f2 * 0.017453292f);
        float cos = TaylorFastMath.cos(f2 * 0.017453292f);
        float bestTheoreticalX = (float) (sin * wantedMovement.getZ() + cos * wantedMovement.getX()) / (sin * sin + cos * cos) / f;
        float bestTheoreticalZ = (float) (-sin * wantedMovement.getX() + cos * wantedMovement.getZ()) / (sin * sin + cos * cos) / f;
        return new Vector(bestTheoreticalX, 0, bestTheoreticalZ);
    }

    public Vector getOptiFineMathMovement(Vector wantedMovement, float f, float f2) {
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

        // Gliding doesn't allow inputs, so, therefore we must rely on the old type of check for this.
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

            Vector vanillaMath = getVanillaMathMovement(trueMovement, 0.1f, player.xRot);
            Vector optifineMath = getOptiFineMathMovement(trueMovement, 0.1f, player.xRot);
            Vector javaMath = getJavaMathMovement(trueMovement, 0.1f, player.xRot);
            Vector libGDXMath = getLibGDXMathMovement(trueMovement, 0.1f, player.xRot);
            Vector rivensMath = getRivensMathMovement(trueMovement, 0.1f, player.xRot);
            Vector rivensFullMath = getRivensFullMathMovement(trueMovement, 0.1f, player.xRot);
            Vector taylorMath = getTaylorMathMovement(trueMovement, 0.1f, player.xRot);

            vanillaMath = new Vector(Math.abs(vanillaMath.getX()), 0, Math.abs(vanillaMath.getZ()));
            optifineMath = new Vector(Math.abs(optifineMath.getX()), 0, Math.abs(optifineMath.getZ()));
            javaMath = new Vector(Math.abs(javaMath.getX()), 0, Math.abs(javaMath.getZ()));
            libGDXMath = new Vector(Math.abs(libGDXMath.getX()), 0, Math.abs(libGDXMath.getZ()));
            rivensMath = new Vector(Math.abs(rivensMath.getX()), 0, Math.abs(rivensMath.getZ()));
            rivensFullMath = new Vector(Math.abs(rivensFullMath.getX()), 0, Math.abs(rivensFullMath.getZ()));
            taylorMath = new Vector(Math.abs(taylorMath.getX()), 0, Math.abs(taylorMath.getZ()));

            double minVanillaHorizontal = Math.min(vanillaMath.getX(), vanillaMath.getZ());
            minVanillaHorizontal = Math.min(minVanillaHorizontal, Math.abs(vanillaMath.getX() - vanillaMath.getZ()));

            double minOptiFineHorizontal = Math.min(optifineMath.getX(), optifineMath.getZ());
            minOptiFineHorizontal = Math.min(minOptiFineHorizontal, Math.abs(optifineMath.getX() - optifineMath.getZ()));

            double minJavaMathHorizontal = Math.min(javaMath.getX(), javaMath.getZ());
            minJavaMathHorizontal = Math.min(minJavaMathHorizontal, Math.abs(javaMath.getX() - javaMath.getZ()));

            double minLibGDXHorizontal = Math.min(libGDXMath.getX(), libGDXMath.getZ());
            minLibGDXHorizontal = Math.min(minLibGDXHorizontal, Math.abs(libGDXMath.getX() - libGDXMath.getZ()));

            double minRivensHorizontal = Math.min(rivensMath.getX(), rivensMath.getZ());
            minRivensHorizontal = Math.min(minRivensHorizontal, Math.abs(rivensMath.getX() - rivensMath.getZ()));

            double minRivensFullHorizontal = Math.min(rivensFullMath.getX(), rivensFullMath.getZ());
            minRivensFullHorizontal = Math.min(minRivensFullHorizontal, Math.abs(rivensFullMath.getX() - rivensFullMath.getZ()));

            double minTaylorMathHorizontal = Math.min(taylorMath.getX(), taylorMath.getZ());
            minTaylorMathHorizontal = Math.min(minTaylorMathHorizontal, Math.abs(taylorMath.getX() - taylorMath.getZ()));

            // TODO: wtf do i do with this
            System.out.println("(DEBUG)"
                    + " OFFSET=" + offset
                    + " VANILLA=" + minVanillaHorizontal
                    + " OPTIFINE=" + minOptiFineHorizontal
                    + " JAVAMATH=" + minJavaMathHorizontal
                    + " LIBGDX=" + minLibGDXHorizontal
                    + " RIVENS=" + minRivensHorizontal
                    + " RIVENSFULL=" + minRivensFullHorizontal
                    + " TAYLOR=" + minTaylorMathHorizontal);

            boolean newVanilla = minVanillaHorizontal < minOptiFineHorizontal;
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
