package ac.grim.grimac.checks.impl.world.scaffold;

import ac.grim.grimac.checks.CheckData;
import ac.grim.grimac.checks.type.BlockPlaceCheck;
import ac.grim.grimac.player.GrimPlayer;
import ac.grim.grimac.utils.anticheat.update.BlockPlace;
import ac.grim.grimac.utils.collisions.datatypes.SimpleCollisionBox;
import ac.grim.grimac.utils.math.VectorUtils;
import com.github.retrooper.packetevents.protocol.player.GameMode;
import com.github.retrooper.packetevents.protocol.world.states.type.StateTypes;
import com.github.retrooper.packetevents.util.Vector3i;
import org.bukkit.util.Vector;

// Detects illegal block placements at impossible distances
// Previously named: FarPlace
@CheckData(name = "Scaffold D")
public class ScaffoldD extends BlockPlaceCheck {

    public ScaffoldD(GrimPlayer player) {
        super(player);
    }

    @Override
    public void onBlockPlace(BlockPlace place) {
        Vector3i blockPos = place.getPlacedAgainstBlockLocation();

        // Ignores the scaffolding block
        if (place.getMaterial() == StateTypes.SCAFFOLDING) {
            return;
        }

        double min = Double.MAX_VALUE;

        for (double d : player.getPossibleEyeHeights()) {
            SimpleCollisionBox box = new SimpleCollisionBox(blockPos);
            Vector eyes = new Vector(player.x, player.y + d, player.z);
            Vector best = VectorUtils.cutBoxToVector(eyes, box);
            min = Math.min(min, eyes.distanceSquared(best));
        }

        // getPickRange() determines this?
        double maxReach = player.gamemode == GameMode.CREATIVE ? 6.0 : 4.5D;
        double threshold = player.getMovementThreshold();
        maxReach += Math.hypot(threshold, threshold);

        if (min > maxReach * maxReach) {
            flagAndAlert("", false);
            place.resync();
        }
    }
}
