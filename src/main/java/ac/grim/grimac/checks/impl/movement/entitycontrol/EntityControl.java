package ac.grim.grimac.checks.impl.movement.entitycontrol;

import ac.grim.grimac.checks.CheckData;
import ac.grim.grimac.checks.type.PostPredictionCheck;
import ac.grim.grimac.player.GrimPlayer;

@CheckData(name = "EntityControl")
public class EntityControl extends PostPredictionCheck {

    public EntityControl(GrimPlayer player) {
        super(player);
    }

    public void rewardPlayer() {
        reward();
    }
}
