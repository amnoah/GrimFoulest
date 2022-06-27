package ac.grim.grimac.checks.impl.combat.reach;

import ac.grim.grimac.checks.CheckData;
import ac.grim.grimac.checks.type.PacketCheck;
import ac.grim.grimac.player.GrimPlayer;

// Detects attacking entities outside their hitbox
@CheckData(name = "Reach B")
public class ReachB extends PacketCheck {

    public ReachB(GrimPlayer playerData) {
        super(playerData);
    }
}
