package ac.grim.grimac.checks.impl.world.scaffold;

import ac.grim.grimac.checks.CheckData;
import ac.grim.grimac.checks.type.BlockPlaceCheck;
import ac.grim.grimac.player.GrimPlayer;
import ac.grim.grimac.utils.anticheat.update.BlockPlace;
import com.github.retrooper.packetevents.protocol.item.ItemStack;
import com.github.retrooper.packetevents.protocol.item.type.ItemTypes;
import com.github.retrooper.packetevents.protocol.world.BlockFace;

@CheckData(name = "Scaffold F")
public class ScaffoldF extends BlockPlaceCheck {

    public ScaffoldF(GrimPlayer player) {
        super(player);
    }

    @Override
    public void onBlockPlace(BlockPlace place) {
        BlockFace face = place.getFace();

        if (face != BlockFace.OTHER) {
            ItemStack itemStack = place.getItemStack();

            if (itemStack.getType() != ItemTypes.AIR) {
                float x = place.getBlockPosition().x;
                float y = place.getBlockPosition().y;
                float z = place.getBlockPosition().z;

                if (x > 1.0 || y > 1.0 || z > 1.0) {
                    flagAndAlert("X=" + x + " Y=" + y + " Z=" + z, false);
                }
            }
        }
    }
}
