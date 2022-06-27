package ac.grim.grimac.checks.impl.combat.aimassist;

import ac.grim.grimac.checks.CheckData;
import ac.grim.grimac.checks.type.RotationCheck;
import ac.grim.grimac.player.GrimPlayer;
import ac.grim.grimac.utils.anticheat.update.RotationUpdate;
import ac.grim.grimac.utils.math.GrimMath;

@CheckData(name = "AimAssist D")
public class AimAssistD extends RotationCheck {

    public AimAssistD(GrimPlayer playerData) {
        super(playerData);
    }

    @Override
    public void process(RotationUpdate rotationUpdate) {
        float absDeltaYaw = Math.abs(rotationUpdate.getDeltaYaw());

        if (absDeltaYaw >= 1.0f && absDeltaYaw % 0.1f == 0.0f) {
            if (absDeltaYaw % 1.0f == 0.0f
                    || absDeltaYaw % 10.0f == 0.0f
                    || absDeltaYaw % 30.0f == 0.0f) {
                flagAndAlert("YAW=" + absDeltaYaw, false);
            }
        }

        float absDeltaPitch = Math.abs(rotationUpdate.getDeltaPitch());

        if (absDeltaPitch >= 1.0f && absDeltaPitch % 0.1f == 0.0f) {
            if (absDeltaPitch % 1.0f == 0.0f
                    || absDeltaPitch % 10.0f == 0.0f
                    || absDeltaPitch % 30.0f == 0.0f) {
                flagAndAlert("PITCH=" + absDeltaPitch, false);
            }
        }
    }
}
