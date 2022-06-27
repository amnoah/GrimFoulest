package ac.grim.grimac.checks.impl.combat.aimassist;

import ac.grim.grimac.checks.CheckData;
import ac.grim.grimac.checks.type.RotationCheck;
import ac.grim.grimac.player.GrimPlayer;
import ac.grim.grimac.utils.anticheat.update.RotationUpdate;

@CheckData(name = "AimAssist F")
public class AimAssistF extends RotationCheck {

    public AimAssistF(GrimPlayer playerData) {
        super(playerData);
    }

    @Override
    public void process(RotationUpdate rotationUpdate) {
        float absDeltaYaw = Math.abs(rotationUpdate.getDeltaYaw());
        float absDeltaPitch = Math.abs(rotationUpdate.getDeltaPitch());

        if (absDeltaYaw > 0.0f && absDeltaYaw < 0.01 && absDeltaPitch > 0.2) {
            flagAndAlert("YAW=" + absDeltaYaw + " PITCH=" + absDeltaPitch, false);
        } else {
            violations -= Math.min(violations + 2.5, 0.05);
        }
    }
}
