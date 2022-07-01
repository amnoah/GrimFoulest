package ac.grim.grimac.checks.impl.combat.aimassist.processor;

import ac.grim.grimac.checks.type.RotationCheck;
import ac.grim.grimac.player.GrimPlayer;
import ac.grim.grimac.utils.anticheat.update.RotationUpdate;
import ac.grim.grimac.utils.math.GraphUtil;

import java.util.ArrayList;
import java.util.List;

public class Cinematic extends RotationCheck {

    public final List<Double> yawSamples = new ArrayList<>(20);
    public final List<Double> pitchSamples = new ArrayList<>(20);
    public long lastSmooth = 0;
    public long lastHighRate = 0;
    public double lastDeltaYaw = 0.0;
    public double lastDeltaPitch = 0.0;

    public Cinematic(GrimPlayer playerData) {
        super(playerData);
    }

    @Override
    public void process(RotationUpdate rotationUpdate) {
        long now = System.currentTimeMillis();
        double deltaYaw = rotationUpdate.getDeltaYaw();
        double deltaPitch = rotationUpdate.getDeltaPitch();
        double differenceYaw = Math.abs(deltaYaw - lastDeltaYaw);
        double differencePitch = Math.abs(deltaPitch - lastDeltaPitch);
        double joltYaw = Math.abs(differenceYaw - deltaYaw);
        double joltPitch = Math.abs(differencePitch - deltaPitch);
        boolean cinematic = (now - lastHighRate > 250L) || now - lastSmooth < 9000L;

        if (joltYaw > 1.0 && joltPitch > 1.0) {
            lastHighRate = now;
        }

        if (deltaYaw > 0.0 && deltaPitch > 0.0) {
            yawSamples.add(deltaYaw);
            pitchSamples.add(deltaPitch);
        }

        if (yawSamples.size() == 20 && pitchSamples.size() == 20) {
            // Get the cerberus/positive graph of the sample-lists
            GraphUtil.GraphResult resultsYaw = GraphUtil.getGraphNoString(yawSamples);
            GraphUtil.GraphResult resultsPitch = GraphUtil.getGraphNoString(pitchSamples);

            // Negative values
            int negativesYaw = resultsYaw.getNegatives();
            int negativesPitch = resultsPitch.getNegatives();

            // Positive values
            int positivesYaw = resultsYaw.getPositives();
            int positivesPitch = resultsPitch.getPositives();

            // Cinematic camera usually does this on *most* speeds and is accurate for the most part.
            if (positivesYaw > negativesYaw || positivesPitch > negativesPitch) {
                lastSmooth = now;
            }

            yawSamples.clear();
            pitchSamples.clear();
        }

        rotationUpdate.setCinematic(cinematic);

        lastDeltaYaw = deltaYaw;
        lastDeltaPitch = deltaPitch;
    }
}
