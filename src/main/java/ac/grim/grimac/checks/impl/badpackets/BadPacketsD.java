package ac.grim.grimac.checks.impl.badpackets;

import ac.grim.grimac.checks.CheckData;
import ac.grim.grimac.checks.type.PacketCheck;
import ac.grim.grimac.player.GrimPlayer;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientEntityAction;

// Detects sending two Sneak packets with the same state
@CheckData(name = "BadPackets D")
public class BadPacketsD extends PacketCheck {

    boolean wasTeleport;
    boolean lastSneaking;

    public BadPacketsD(GrimPlayer player) {
        super(player);
    }

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        wasTeleport = player.packetStateData.lastPacketWasTeleport || wasTeleport;

        if (event.getPacketType() == PacketType.Play.Client.ENTITY_ACTION) {
            WrapperPlayClientEntityAction packet = new WrapperPlayClientEntityAction(event);

            if (packet.getAction() == WrapperPlayClientEntityAction.Action.START_SNEAKING) {
                if (lastSneaking && !wasTeleport) {
                    player.kick(getCheckName(), event, "Sneaking (Start)");
                } else {
                    lastSneaking = true;
                }

            } else if (packet.getAction() == WrapperPlayClientEntityAction.Action.STOP_SNEAKING) {
                if (!lastSneaking && !wasTeleport) {
                    player.kick(getCheckName(), event, "Sneaking (Stop)");
                } else {
                    lastSneaking = false;
                }
            }
        }
    }
}
