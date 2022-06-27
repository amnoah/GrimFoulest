package ac.grim.grimac.checks.impl.combat.aimassist.processor;

import ac.grim.grimac.checks.type.RotationCheck;
import ac.grim.grimac.player.GrimPlayer;
import ac.grim.grimac.utils.anticheat.update.RotationUpdate;
import ac.grim.grimac.utils.data.HeadRotation;
import ac.grim.grimac.utils.lists.RunningMode;
import ac.grim.grimac.utils.math.GrimMath;
import lombok.Getter;

// From OverFlow V2 AntiCheat, modified from o(n^2) to best case o(1) worst case o(n) time.
public class AimProcessor extends RotationCheck {

    private final RunningMode<Double> yawSamples = new RunningMode<>(50);
    private final RunningMode<Double> pitchSamples = new RunningMode<>(50);
    @Getter
    public double sensitivityX, sensitivityY, deltaX, deltaY;
    private float lastDeltaYaw, lastDeltaPitch;

    public AimProcessor(GrimPlayer playerData) {
        super(playerData);
    }

    private static double yawToF2(double yawDelta) {
        return yawDelta / .15;
    }

    private static double pitchToF3(double pitchDelta) {
        int b0 = pitchDelta >= 0 ? 1 : -1; //Checking for inverted mouse.
        return pitchDelta / .15 / b0;
    }

    private static double getSensitivityFromPitchGCD(double gcd) {
        double stepOne = pitchToF3(gcd) / 8;
        double stepTwo = Math.cbrt(stepOne);
        double stepThree = stepTwo - .2f;
        return stepThree / .6f;
    }

    private static double getSensitivityFromYawGCD(double gcd) {
        double stepOne = yawToF2(gcd) / 8;
        double stepTwo = Math.cbrt(stepOne);
        double stepThree = stepTwo - .2f;
        return stepThree / .6f;
    }

    @Override
    public void process(RotationUpdate rotationUpdate) {
        rotationUpdate.setProcessor(this);

        HeadRotation from = rotationUpdate.getFrom();
        HeadRotation to = rotationUpdate.getTo();
        float deltaYaw = Math.abs(to.getYaw() - from.getYaw());
        float deltaPitch = Math.abs(to.getPitch() - from.getPitch());
        double gcdYaw = GrimMath.getGcd((long) (deltaYaw * GrimMath.EXPANDER), (long) (lastDeltaYaw * GrimMath.EXPANDER));
        double gcdPitch = GrimMath.getGcd((long) (deltaPitch * GrimMath.EXPANDER), (long) (lastDeltaPitch * GrimMath.EXPANDER));
        double dividedYawGcd = gcdYaw / GrimMath.EXPANDER;
        double dividedPitchGcd = gcdPitch / GrimMath.EXPANDER;

        if (gcdYaw > 90000 && gcdYaw < 2E7 && dividedYawGcd > 0.01f && deltaYaw < 8) {
            yawSamples.add(dividedYawGcd);
        }

        if (gcdPitch > 90000 && gcdPitch < 2E7 && deltaPitch < 8) {
            pitchSamples.add(dividedPitchGcd);
        }

        double modeYaw = 0.0;
        double modePitch = 0.0;

        if (pitchSamples.size() > 5 && yawSamples.size() > 5) {
            modeYaw = yawSamples.getMode();
            modePitch = pitchSamples.getMode();
        }

        double deltaX = deltaYaw / modeYaw;
        double deltaY = deltaPitch / modePitch;
        double sensitivityX = getSensitivityFromYawGCD(modeYaw);
        double sensitivityY = getSensitivityFromPitchGCD(modePitch);

        rotationUpdate.setSensitivityX(sensitivityX);
        rotationUpdate.setSensitivityY(sensitivityY);

        this.deltaX = deltaX;
        this.deltaY = deltaY;
        this.sensitivityX = sensitivityX;
        this.sensitivityY = sensitivityY;
        lastDeltaYaw = deltaYaw;
        lastDeltaPitch = deltaPitch;
    }
}