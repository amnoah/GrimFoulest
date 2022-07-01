package ac.grim.grimac.checks.impl.combat.aimassist;

import ac.grim.grimac.checks.CheckData;
import ac.grim.grimac.checks.type.RotationCheck;
import ac.grim.grimac.player.GrimPlayer;
import ac.grim.grimac.utils.anticheat.update.RotationUpdate;
import ac.grim.grimac.utils.math.GrimMath;

@CheckData(name = "AimAssist A")
public class AimAssistA extends RotationCheck {

    public double lastDeltaYaw;
    public double lastDeltaPitch;
    public boolean exempt = false;

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

        int yawDecimal = GrimMath.countDecimalPlaces(rotationUpdate.getDeltaYaw());
        int pitchDecimal = GrimMath.countDecimalPlaces(rotationUpdate.getDeltaPitch());

        if (rotationUpdate.getDeltaYaw() > 0.08 && rotationUpdate.getDeltaYaw() == lastDeltaYaw && yawDecimal <= 5) {
            flagAndAlert("YawDiff: " + rotationUpdate.getDeltaYaw() + " Decimal: " + GrimMath.countDecimalPlaces(rotationUpdate.getDeltaYaw()), false);
        }

        if (rotationUpdate.getDeltaPitch() > 0.08 && rotationUpdate.getDeltaPitch() == lastDeltaPitch && pitchDecimal <= 5) {
            flagAndAlert("PitchDiff: " + rotationUpdate.getDeltaPitch() + " Decimal: " + GrimMath.countDecimalPlaces(rotationUpdate.getDeltaPitch()), false);
        }

        lastDeltaYaw = rotationUpdate.getDeltaYaw();
        lastDeltaPitch = rotationUpdate.getDeltaPitch();
    }
}
