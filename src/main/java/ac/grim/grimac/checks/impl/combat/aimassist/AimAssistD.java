package ac.grim.grimac.checks.impl.combat.aimassist;

import ac.grim.grimac.checks.CheckData;
import ac.grim.grimac.checks.type.RotationCheck;
import ac.grim.grimac.player.GrimPlayer;
import ac.grim.grimac.utils.anticheat.update.RotationUpdate;

@CheckData(name = "AimAssist D")
public class AimAssistD extends RotationCheck {

    public AimAssistD(GrimPlayer playerData) {
        super(playerData);
    }

    @Override
    public void process(RotationUpdate rotationUpdate) {
        double absDeltaYaw = Math.abs(rotationUpdate.getDeltaYaw());

        if (absDeltaYaw >= 1.0 && absDeltaYaw % 0.1 == 0.0) {
            if (absDeltaYaw % 1.0 == 0.0
                    || absDeltaYaw % 10.0 == 0.0
                    || absDeltaYaw % 30.0 == 0.0) {
                flagAndAlert("YAW=" + absDeltaYaw, false);
            }
        }

        double absDeltaPitch = Math.abs(rotationUpdate.getDeltaPitch());

        if (absDeltaPitch >= 1.0 && absDeltaPitch % 0.1 == 0.0) {
            if (absDeltaPitch % 1.0 == 0.0
                    || absDeltaPitch % 10.0 == 0.0
                    || absDeltaPitch % 30.0 == 0.0) {
                flagAndAlert("PITCH=" + absDeltaPitch, false);
            }
        }
    }
}
