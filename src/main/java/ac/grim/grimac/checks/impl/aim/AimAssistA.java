package ac.grim.grimac.checks.impl.aim;

import ac.grim.grimac.checks.CheckData;
import ac.grim.grimac.checks.type.RotationCheck;
import ac.grim.grimac.player.GrimPlayer;
import ac.grim.grimac.utils.anticheat.update.RotationUpdate;
import ac.grim.grimac.utils.math.GrimMath;

@CheckData(name = "AimAssist A")
public class AimAssistA extends RotationCheck {

    public double lastYawDiff;
    public double lastPitchDiff;
    boolean exempt = false;

    public AimAssistA(GrimPlayer playerData) {
        super(playerData);
    }

    @Override
    public void process(RotationUpdate rotationUpdate) {
        if (player.packetStateData.lastPacketWasTeleport || player.compensatedEntities.getSelf().getRiding() != null) {
            exempt = true;
            return;
        }

        if (exempt) { // Exempt for a tick on teleport
            exempt = false;
            return;
        }

        double yawDiff = Math.abs(rotationUpdate.getTo().getYaw() - rotationUpdate.getFrom().getYaw());
        double pitchDiff = Math.abs(rotationUpdate.getTo().getPitch() - rotationUpdate.getFrom().getPitch());
        int yawDecimal = GrimMath.countDecimalPlaces(yawDiff);
        int pitchDecimal = GrimMath.countDecimalPlaces(pitchDiff);

        if (yawDiff > 0.08 && yawDiff == lastYawDiff && yawDecimal <= 7) {
            flagAndAlert("YawDiff: " + yawDiff + " Decimal: " + GrimMath.countDecimalPlaces(yawDiff), false);
        }

        if (pitchDiff > 0.08 && pitchDiff == lastPitchDiff && pitchDecimal <= 7) {
            flagAndAlert("PitchDiff: " + pitchDiff + " Decimal: " + GrimMath.countDecimalPlaces(pitchDiff), false);
        }

        lastYawDiff = yawDiff;
        lastPitchDiff = pitchDiff;
    }
}
