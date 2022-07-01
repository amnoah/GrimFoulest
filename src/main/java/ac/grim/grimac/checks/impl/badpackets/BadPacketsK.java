package ac.grim.grimac.checks.impl.badpackets;

import ac.grim.grimac.checks.CheckData;
import ac.grim.grimac.checks.type.PacketCheck;
import ac.grim.grimac.player.GrimPlayer;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientClientStatus;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerFlying;

// Detects sending invalid PERFORM_RESPAWN packets
@CheckData(name = "BadPackets K")
public class BadPacketsK extends PacketCheck {

    public boolean sentRespawn;

    public BadPacketsK(GrimPlayer player) {
        super(player);
    }

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        if (event.getPacketType() == PacketType.Play.Client.CLIENT_STATUS) {
            WrapperPlayClientClientStatus packet = new WrapperPlayClientClientStatus(event);

            if (packet.getAction() == WrapperPlayClientClientStatus.Action.PERFORM_RESPAWN) {
                if (!player.compensatedEntities.getSelf().isDead) {
                    player.kick(getCheckName(), event, "Respawn (Impossible)");
                }

                if (sentRespawn) {
                    player.kick(getCheckName(), event, "Respawn (Sent)");
                }

                sentRespawn = true;
            }

        } else if (WrapperPlayClientPlayerFlying.isFlying(event.getPacketType())) {
            sentRespawn = false;
        }
    }
}
