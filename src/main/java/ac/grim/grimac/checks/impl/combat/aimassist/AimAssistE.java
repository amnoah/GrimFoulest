package ac.grim.grimac.checks.impl.combat.aimassist;

import ac.grim.grimac.checks.CheckData;
import ac.grim.grimac.checks.type.RotationCheck;
import ac.grim.grimac.player.GrimPlayer;
import ac.grim.grimac.utils.anticheat.update.RotationUpdate;

import java.util.Objects;

@CheckData(name = "AimAssist E")
public class AimAssistE extends RotationCheck {

    public double lastDeltaYaw;

    public AimAssistE(GrimPlayer playerData) {
        super(playerData);
    }

    @Override
    public void process(RotationUpdate rotationUpdate) {
        double absDeltaYaw = Math.abs(rotationUpdate.getDeltaYaw());
        long round;

        if (absDeltaYaw > 1.0 && (round = Math.round(absDeltaYaw)) == absDeltaYaw) {
            if (Objects.equals(absDeltaYaw, lastDeltaYaw)) {
                flagAndAlert("YAW=" + lastDeltaYaw, false);
            }

            lastDeltaYaw = round;

        } else {
            lastDeltaYaw = 0.0;
        }
    }
}
