package ac.grim.grimac.checks.impl.badpackets;

import ac.grim.grimac.checks.CheckData;
import ac.grim.grimac.checks.type.PacketCheck;
import ac.grim.grimac.player.GrimPlayer;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.player.GameMode;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerAbilities;

// Detects sending invalid ABILITIES packets
@CheckData(name = "BadPackets I")
public class BadPacketsI extends PacketCheck {

    public BadPacketsI(GrimPlayer player) {
        super(player);
    }

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        if (event.getPacketType() == PacketType.Play.Client.PLAYER_ABILITIES) {
            WrapperPlayClientPlayerAbilities packet = new WrapperPlayClientPlayerAbilities(event);
            boolean isFlying = packet.isFlying();
            boolean isInGodMode = packet.isInGodMode().get();
            boolean isFlightAllowed = packet.isFlightAllowed().get();
            boolean isInCreative = packet.isInCreativeMode().get();

            // Detects sending invalid data for every ability.
            if (isFlying == player.isFlying && isInGodMode == player.isInGodMode
                    && isFlightAllowed == player.isFlightAllowed && isInCreative == player.isInCreative
                    && !isFlying && !isInGodMode && !isFlightAllowed && !isInCreative) {
                player.kick(getCheckName(), event, "Abilities (All)");
                return;
            }

            // Detects sending invalid Flight Allowed data.
            if (isFlying && !player.isFlightAllowed) {
                player.kick(getCheckName(), event, "Abilities (Flight Allowed)");
                return;
            }

            if (player.gamemode != GameMode.CREATIVE) {
                // Detects sending invalid God Mode data.
                if (isInGodMode != player.isInGodMode) {
                    player.kick(getCheckName(), event, "Abilities (God Mode)");
                    return;
                }

                // Detects sending invalid Flying data.
                if (isFlying && !player.isFlying) {
                    player.kick(getCheckName(), event, "Abilities (Flying)");
                    return;
                }

                // Detects sending invalid Creative data.
                if (isInCreative && !player.isInCreative) {
                    player.kick(getCheckName(), event, "Abilities (Creative)");
                }
            }
        }
    }
}
