package ac.grim.grimac.checks.impl.combat;

import ac.grim.grimac.checks.CheckData;
import ac.grim.grimac.checks.type.PacketCheck;
import ac.grim.grimac.player.GrimPlayer;

@CheckData(name = "Hitbox")
public class Hitbox extends PacketCheck {

    public Hitbox(GrimPlayer playerData) {
        super(playerData);
    }
}
