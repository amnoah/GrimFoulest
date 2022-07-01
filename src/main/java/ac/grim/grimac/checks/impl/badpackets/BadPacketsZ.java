package ac.grim.grimac.checks.impl.badpackets;

import ac.grim.grimac.checks.CheckData;
import ac.grim.grimac.checks.type.PacketCheck;
import ac.grim.grimac.player.GrimPlayer;

@CheckData(name = "BadPackets Z")
public class BadPacketsZ extends PacketCheck {

    public BadPacketsZ(GrimPlayer playerData) {
        super(playerData);
    }
}
