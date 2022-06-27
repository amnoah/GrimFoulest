package ac.grim.grimac.checks.impl.combat.aimassist;

import ac.grim.grimac.checks.CheckData;
import ac.grim.grimac.checks.type.RotationCheck;
import ac.grim.grimac.player.GrimPlayer;
import ac.grim.grimac.utils.anticheat.update.RotationUpdate;
import ac.grim.grimac.utils.data.HeadRotation;
import ac.grim.grimac.utils.math.GrimMath;

// Detects Baritone
@CheckData(name = "Baritone")
public class AimAssistD extends RotationCheck {

    private float lastPitchDifference;
    private int verbose;

    public AimAssistD(GrimPlayer playerData) {
        super(playerData);
    }

    @Override
    public void process(RotationUpdate rotationUpdate) {
        HeadRotation from = rotationUpdate.getFrom();
        HeadRotation to = rotationUpdate.getTo();
        float deltaPitch = Math.abs(to.getPitch() - from.getPitch());

        // Baritone works with small degrees, limit to 1 degrees to pick up on baritone slightly moving aim to bypass anticheats
        if (rotationUpdate.getDeltaYaw() == 0 && deltaPitch > 0 && deltaPitch < 1 && Math.abs(to.getPitch()) != 90.0f) {
            long gcd = GrimMath.getGcd((long) (deltaPitch * GrimMath.EXPANDER), (long) (lastPitchDifference * GrimMath.EXPANDER));

            if (gcd < 131072L) {
                verbose = Math.min(verbose + 1, 20);

                if (verbose > 9) {
                    flagAndAlert("GCD: " + gcd, false);
                    verbose = 0;
                }
            } else {
                verbose = Math.max(0, verbose - 1);
            }
        }

        lastPitchDifference = deltaPitch;
    }
}
