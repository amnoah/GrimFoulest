package ac.grim.grimac.checks.impl.movement.noslowdown;

import ac.grim.grimac.checks.CheckData;
import ac.grim.grimac.checks.type.PostPredictionCheck;
import ac.grim.grimac.player.GrimPlayer;
import ac.grim.grimac.utils.anticheat.update.PredictionComplete;
import com.github.retrooper.packetevents.protocol.player.ClientVersion;

@CheckData(name = "NoSlowdown", setback = 5)
public class NoSlowdown extends PostPredictionCheck {

    // The player sends that they switched items the next tick if they switch
    // from a usable item to another usable item. Affects 1.8 (and most likely 1.7) clients.
    public boolean didSlotChangeLastTick = false;
    public boolean flaggedLastTick = false;
    double offsetToFlag;
    double bestOffset = 1;

    public NoSlowdown(GrimPlayer player) {
        super(player);
    }

    @Override
    public void onPredictionComplete(PredictionComplete predictionComplete) {
        // If the player was using an item for certain, and their predicted velocity had a flipped item
        if (player.packetStateData.slowedByUsingItem) {
            // 1.8 users are not slowed the first tick they use an item, strangely
            if (player.getClientVersion().isOlderThanOrEquals(ClientVersion.V_1_8) && didSlotChangeLastTick) {
                didSlotChangeLastTick = false;
                flaggedLastTick = false;
            }

            if (bestOffset > offsetToFlag) {
                if (flaggedLastTick) {
                    flagAndAlert("", true);
                }
                flaggedLastTick = true;

            } else {
                reward();
                flaggedLastTick = false;
            }
        }

        bestOffset = 1;
    }

    public void handlePredictionAnalysis(double offset) {
        bestOffset = Math.min(bestOffset, offset);
    }

    @Override
    public void reload() {
        super.reload();
        offsetToFlag = getConfig().getDoubleElse("NoSlowdown.threshold", 0.00001);
    }
}
