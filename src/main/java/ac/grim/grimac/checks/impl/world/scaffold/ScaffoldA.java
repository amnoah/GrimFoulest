package ac.grim.grimac.checks.impl.world.scaffold;

import ac.grim.grimac.checks.CheckData;
import ac.grim.grimac.checks.type.BlockPlaceCheck;
import ac.grim.grimac.player.GrimPlayer;
import ac.grim.grimac.utils.anticheat.update.BlockPlace;
import ac.grim.grimac.utils.nmsutil.Materials;
import com.github.retrooper.packetevents.protocol.world.states.type.StateTypes;
import com.github.retrooper.packetevents.util.Vector3f;

// Detects illegal block placements using the cursor position
// Previously named: FabricatedPlace
@CheckData(name = "Scaffold A")
public class ScaffoldA extends BlockPlaceCheck {

    public ScaffoldA(GrimPlayer player) {
        super(player);
    }

    @Override
    public void onBlockPlace(BlockPlace place) {
        Vector3f cursor = place.getCursor();

        if (cursor == null) {
            return;
        }

        double allowed = Materials.isShapeExceedsCube(place.getPlacedAgainstMaterial())
                || place.getPlacedAgainstMaterial() == StateTypes.LECTERN ? 1.5 : 1;
        double minAllowed = 1 - allowed;

        if (cursor.getX() < minAllowed || cursor.getY() < minAllowed || cursor.getZ() < minAllowed
                || cursor.getX() > allowed || cursor.getY() > allowed || cursor.getZ() > allowed) {
            flagAndAlert("", false);
            place.resync();
        }
    }
}
