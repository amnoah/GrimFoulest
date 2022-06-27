package ac.grim.grimac.checks.impl.movement.groundspoof;

import ac.grim.grimac.checks.CheckData;
import ac.grim.grimac.checks.type.PostPredictionCheck;
import ac.grim.grimac.player.GrimPlayer;
import ac.grim.grimac.utils.anticheat.MessageUtil;
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
        // Player is in spectator
        if (PacketEvents.getAPI().getServerManager().getVersion().isNewerThanOrEquals(ServerVersion.V_1_8)
                && player.gamemode == GameMode.SPECTATOR) {
            return;
        }

        // Player is exempt on ground
        if (player.exemptOnGround()) {
            return;
        }

        // Player was on a ghost block
        if (player.getSetbackTeleportUtil().blockOffsets) {
            return;
        }

        // ViaVersion sends wrong ground status... (doesn't matter but is annoying)
        if (player.packetStateData.lastPacketWasTeleport) {
            return;
        }

        if (player.clientClaimsLastOnGround != player.onGround) {
            flagAndAlert( "(" + MessageUtil.toCamelCase(Boolean.toString(player.clientClaimsLastOnGround)) + ")", true);
            player.checkManager.getNoFall().flipPlayerGroundStatus = true;
        }
    }
}
