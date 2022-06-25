package ac.grim.grimac.checks.impl.badpackets;

import ac.grim.grimac.checks.CheckData;
import ac.grim.grimac.checks.type.PacketCheck;
import ac.grim.grimac.player.GrimPlayer;

@CheckData(name = "BadPackets H")
public class BadPacketsH extends PacketCheck {

    public BadPacketsH(GrimPlayer playerData) {
        super(playerData);
    }
}
