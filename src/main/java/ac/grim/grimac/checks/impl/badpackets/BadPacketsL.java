package ac.grim.grimac.checks.impl.badpackets;

import ac.grim.grimac.checks.CheckData;
import ac.grim.grimac.checks.type.PacketCheck;
import ac.grim.grimac.player.GrimPlayer;

@CheckData(name = "BadPacketsL")
public class BadPacketsL extends PacketCheck {

    public BadPacketsL(GrimPlayer playerData) {
        super(playerData);
    }
}
