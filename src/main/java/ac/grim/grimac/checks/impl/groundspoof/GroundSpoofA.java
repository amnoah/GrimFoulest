package ac.grim.grimac.checks.impl.groundspoof;

import ac.grim.grimac.checks.CheckData;
import ac.grim.grimac.checks.type.PostPredictionCheck;
import ac.grim.grimac.player.GrimPlayer;
import ac.grim.grimac.utils.anticheat.update.PredictionComplete;
import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.manager.server.ServerVersion;
import com.github.retrooper.packetevents.protocol.player.GameMode;

@CheckData(name = "GroundSpoof A", setback = 10, decay = 0.01)
public class GroundSpoofA extends PostPredictionCheck {

    public GroundSpoofA(GrimPlayer player) {
        super(player);
    }

    @Override
    public void onPredictionComplete(PredictionComplete predictionComplete) {
        // Exemptions
        // Don't check players in spectator
        if (PacketEvents.getAPI().getServerManager().getVersion().isNewerThanOrEquals(ServerVersion.V_1_8)
                && player.gamemode == GameMode.SPECTATOR) {
            return;
        }

        // And don't check this long list of ground exemptions
        if (player.exemptOnGround()) {
            return;
        }

        // Don't check if the player was on a ghost block
        if (player.getSetbackTeleportUtil().blockOffsets) {
            return;
        }

        // ViaVersion sends wrong ground status... (doesn't matter but is annoying)
        if (player.packetStateData.lastPacketWasTeleport) {
            return;
        }

        if (player.clientClaimsLastOnGround != player.onGround) {
            flagAndAlert("Spoofing " + player.clientClaimsLastOnGround, true);
            player.checkManager.getNoFall().flipPlayerGroundStatus = true;
        }
    }
}
