package ac.grim.grimac.utils.math;

import ac.grim.grimac.player.GrimPlayer;
import ac.grim.grimac.utils.math.fastmath.*;
import lombok.Getter;
import org.bukkit.util.Vector;

import static ac.grim.grimac.utils.math.fastmath.JavaFastMath.getJavaMathMovement;
import static ac.grim.grimac.utils.math.fastmath.LibGDXFastMath.getLibGDXMathMovement;
import static ac.grim.grimac.utils.math.fastmath.OptifineFastMath.getOptiFineMathMovement;
import static ac.grim.grimac.utils.math.fastmath.RivensFastMath.getRivensMathMovement;
import static ac.grim.grimac.utils.math.fastmath.RivensFullFastMath.getRivensFullMathMovement;
import static ac.grim.grimac.utils.math.fastmath.TaylorFastMath.getTaylorMathMovement;
import static ac.grim.grimac.utils.math.fastmath.VanillaMath.getVanillaMathMovement;

public class TrigHandler {

    @Getter
    // TODO: Calculate this and set it
    private final FastMathType activeFastMath = FastMathType.VANILLA_MATH;
    private final GrimPlayer player;
    private double buffer = 0;
    @Getter
    @Deprecated
    private boolean isVanillaMath = true;

    public TrigHandler(GrimPlayer player) {
        this.player = player;
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

        // ! = for debug purposes
        if (!player.checkManager.getOffsetHandler().doesOffsetFlag(offset)) {
            Vector trueMovement = player.actualMovement.clone().subtract(oldVel);

            Vector vanillaMath = getVanillaMathMovement(trueMovement, 0.1f, player.xRot);
            Vector optifineMath = getOptiFineMathMovement(player, trueMovement, 0.1f, player.xRot);
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

            // for debug purposes
            System.out.println("(DEBUG)"
                    + " OFFSET=" + offset
                    + " VANILLA=" + minVanillaHorizontal
                    + " OPTIFINE=" + minOptiFineHorizontal
                    + " JAVAMATH=" + minJavaMathHorizontal
                    + " LIBGDX=" + minLibGDXHorizontal
                    + " RIVENS=" + minRivensHorizontal
                    + " RIVENSFULL=" + minRivensFullHorizontal
                    + " TAYLOR=" + minTaylorMathHorizontal);

            // TODO: Calculate which version of FastMath the player is using and set it as activeFastMath.
            //  This method below sucks, need to scrap it and re-do it.
            boolean newVanilla = minVanillaHorizontal < minOptiFineHorizontal;
            buffer += newVanilla != isVanillaMath ? 1 : -0.25;

            if (buffer > 5) {
                buffer = 0;
                isVanillaMath = !isVanillaMath;
            }
        }
    }

    public float sin(float f) {
        switch (activeFastMath) {
            case VANILLA_MATH:
                return VanillaMath.sin(f);

            case OPTIFINE_MATH:
                return OptifineFastMath.sin(f);

            case JAVA_MATH:
                return JavaFastMath.sin(f);

            case LIBGDX_MATH:
                return LibGDXFastMath.sin(f);

            case RIVENS_MATH:
                return RivensFastMath.sin(f);

            case RIVENS_FULL_MATH:
                return RivensFullFastMath.sin(f);

            case TAYLOR_MATH:
                return TaylorFastMath.sin(f);

            case LEGACY_MATH:
            default:
                return LegacyFastMath.sin(f);
        }
    }

    public float cos(float f) {
        switch (activeFastMath) {
            case VANILLA_MATH:
                return VanillaMath.cos(f);

            case OPTIFINE_MATH:
                return OptifineFastMath.cos(f);

            case JAVA_MATH:
                return JavaFastMath.cos(f);

            case LIBGDX_MATH:
                return LibGDXFastMath.cos(f);

            case RIVENS_MATH:
                return RivensFastMath.cos(f);

            case RIVENS_FULL_MATH:
                return RivensFullFastMath.cos(f);

            case TAYLOR_MATH:
                return TaylorFastMath.cos(f);

            case LEGACY_MATH:
            default:
                return LegacyFastMath.cos(f);
        }
    }
}
