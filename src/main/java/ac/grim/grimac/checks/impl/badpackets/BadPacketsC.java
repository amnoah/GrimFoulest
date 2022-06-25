package ac.grim.grimac.checks.impl.badpackets;

import ac.grim.grimac.checks.CheckData;
import ac.grim.grimac.checks.type.PacketCheck;
import ac.grim.grimac.player.GrimPlayer;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientEntityAction;

// Detects sending two Sprint packets with the same state
@CheckData(name = "BadPackets C")
public class BadPacketsC extends PacketCheck {

    boolean thanksMojang; // Support 1.14+ clients starting on either true or false sprinting, we don't know
    public boolean lastSprinting;

    public BadPacketsC(GrimPlayer player) {
        super(player);
    }

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        if (event.getPacketType() == PacketType.Play.Client.ENTITY_ACTION) {
            WrapperPlayClientEntityAction packet = new WrapperPlayClientEntityAction(event);

            if (packet.getAction() == WrapperPlayClientEntityAction.Action.START_SPRINTING) {
                if (lastSprinting) {
                    if (!thanksMojang) {
                        thanksMojang = true;
                        return;
                    }

                    event.setCancelled(true);
                    player.kick(getCheckName(), "START_SPRINTING", "You are sending too many packets!");
                    return;
                }

                lastSprinting = true;

            } else if (packet.getAction() == WrapperPlayClientEntityAction.Action.STOP_SPRINTING) {
                if (!lastSprinting) {
                    if (!thanksMojang) {
                        thanksMojang = true;
                        return;
                    }

                    event.setCancelled(true);
                    player.kick(getCheckName(), "STOP_SPRINTING", "You are sending too many packets!");
                    return;
                }

                lastSprinting = false;
            }
        }
    }
}
