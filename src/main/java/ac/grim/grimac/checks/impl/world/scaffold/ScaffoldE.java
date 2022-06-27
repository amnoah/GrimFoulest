package ac.grim.grimac.checks.impl.world.scaffold;

import ac.grim.grimac.checks.CheckData;
import ac.grim.grimac.checks.type.BlockPlaceCheck;
import ac.grim.grimac.player.GrimPlayer;
import ac.grim.grimac.utils.anticheat.update.BlockPlace;
import ac.grim.grimac.utils.nmsutil.Materials;
import com.github.retrooper.packetevents.protocol.world.states.type.StateType;
import com.github.retrooper.packetevents.util.Vector3i;

// Detects illegal block placements on air and liquids
// Previously named: AirLiquidPlace
@CheckData(name = "Scaffold E")
public class ScaffoldE extends BlockPlaceCheck {

    public ScaffoldE(GrimPlayer player) {
        super(player);
    }

    @Override
    public void onBlockPlace(BlockPlace place) {
        Vector3i blockPos = place.getPlacedAgainstBlockLocation();
        StateType placeAgainst = player.compensatedWorld.getStateTypeAt(blockPos.getX(), blockPos.getY(), blockPos.getZ());

        if (placeAgainst.isAir() || Materials.isNoPlaceLiquid(placeAgainst)) {
            flagAndAlert("", false);
            place.resync();
        }
    }
}
