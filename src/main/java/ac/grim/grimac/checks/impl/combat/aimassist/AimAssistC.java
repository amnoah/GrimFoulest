package ac.grim.grimac.checks.impl.combat.aimassist;

import ac.grim.grimac.checks.CheckData;
import ac.grim.grimac.checks.type.RotationCheck;
import ac.grim.grimac.player.GrimPlayer;
import ac.grim.grimac.utils.anticheat.update.RotationUpdate;

// Based on Kauri AimA,
// I also discovered this flaw before open source Kauri, but did not want to open source its detection.
// It works on clients who % 360 their rotation.
@CheckData(name = "AimAssist C", decay = 0.005)
public class AimAssistC extends RotationCheck {

    float lastDeltaYaw;

    public AimAssistC(GrimPlayer playerData) {
        super(playerData);
    }

    @Override
    public void process(RotationUpdate rotationUpdate) {
        if (player.packetStateData.lastPacketWasTeleport) {
            return;
        }

        if (player.xRot < 360 && player.xRot > -360
                && Math.abs(rotationUpdate.getDeltaYaw()) > 320
                && Math.abs(lastDeltaYaw) < 30) {
            flagAndAlert("", false);
        } else {
            reward();
        }

        lastDeltaYaw = rotationUpdate.getDeltaYaw();
    }
}
