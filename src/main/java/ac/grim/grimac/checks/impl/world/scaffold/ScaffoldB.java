package ac.grim.grimac.checks.impl.world.scaffold;

import ac.grim.grimac.checks.CheckData;
import ac.grim.grimac.checks.type.BlockPlaceCheck;
import ac.grim.grimac.player.GrimPlayer;
import ac.grim.grimac.utils.anticheat.update.BlockPlace;
import ac.grim.grimac.utils.collisions.RayTraceUtil;
import com.github.retrooper.packetevents.protocol.world.states.type.StateTypes;

// Detects illegal block placements using ray-tracing
// Previously named: RotationPlace
@CheckData(name = "Scaffold B")
public class ScaffoldB extends BlockPlaceCheck {

    public double flagBuffer = 0; // If the player flags once, force them to play legit, or we will cancel the tick before.

    public ScaffoldB(GrimPlayer player) {
        super(player);
    }

    @Override
    public void onBlockPlace(BlockPlace place) {
        // Ignores the scaffolding block
        if (place.getMaterial() == StateTypes.SCAFFOLDING) {
            return;
        }

        if (flagBuffer > 0 && !RayTraceUtil.didRayTraceHit(player, place.getPlacedAgainstBlockLocation())) {
            flagAndAlert("Pre-Flying", false);
            place.resync();
        }
    }

    // Use post flying because it has the correct rotation, and can't false easily.
    @Override
    public void onPostFlyingBlockPlace(BlockPlace place) {
        // Ignores the scaffolding block
        if (place.getMaterial() == StateTypes.SCAFFOLDING) {
            return;
        }

        // Ray-trace to try and hit the target block
        boolean hit = RayTraceUtil.didRayTraceHit(player, place.getPlacedAgainstBlockLocation());

        // Note: This can false with rapidly moving yaw in 1.8+ clients
        if (!hit) {
            flagBuffer = 1;
            flagAndAlert("Post-Flying", false);
        } else {
            flagBuffer = Math.max(0, flagBuffer - 0.1);
        }
    }
}
