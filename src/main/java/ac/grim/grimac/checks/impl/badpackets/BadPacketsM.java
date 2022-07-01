package ac.grim.grimac.checks.impl.badpackets;

import ac.grim.grimac.checks.CheckData;
import ac.grim.grimac.checks.type.PacketCheck;
import ac.grim.grimac.player.GrimPlayer;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.player.DiggingAction;
import com.github.retrooper.packetevents.protocol.world.BlockFace;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerDigging;

// Detects sending invalid PLAYER_DIGGING packets
@CheckData(name = "BadPackets M")
public class BadPacketsM extends PacketCheck {

    public boolean sentStart;
    public boolean sentFinished;
    public boolean sentCancelled;

    public BadPacketsM(GrimPlayer player) {
        super(player);
    }

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        if (event.getPacketType() == PacketType.Play.Client.PLAYER_DIGGING) {
            WrapperPlayClientPlayerDigging packet = new WrapperPlayClientPlayerDigging(event);

            // Detects sending two START_DIGGING packets in a row.
            if (packet.getAction() == DiggingAction.START_DIGGING) {
                if (sentStart) {
                    player.kick(getCheckName(), event, "Sent Start");
                }

                sentStart = true;
                sentCancelled = false;
                sentFinished = false;
            }

            // Detects sending two CANCELLED_DIGGING packets in a row.
            if (packet.getAction() == DiggingAction.CANCELLED_DIGGING) {
                if (sentCancelled) {
                    player.kick(getCheckName(), event, "Sent Cancelled");
                }

                sentCancelled = true;
                sentStart = false;
                sentFinished = false;
            }

            // Detects sending two FINISHED_DIGGING packets in a row.
            if (packet.getAction() == DiggingAction.FINISHED_DIGGING) {
                if (sentFinished) {
                    player.kick(getCheckName(), event, "Sent Finished");
                }

                sentFinished = true;
                sentStart = false;
                sentCancelled = false;
            }

            // Detects sending invalid RELEASE_USE_ITEM packets.
            if (packet.getAction() == DiggingAction.RELEASE_USE_ITEM) {
                if (packet.getFace() != BlockFace.DOWN
                        || packet.getBlockPosition().getX() != 0
                        || packet.getBlockPosition().getY() != 0
                        || packet.getBlockPosition().getZ() != 0) {
                    player.kick(getCheckName(), event, "RELEASE_USE_ITEM");
                }
            }
        }
    }
}
