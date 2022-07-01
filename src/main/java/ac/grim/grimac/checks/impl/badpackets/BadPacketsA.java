package ac.grim.grimac.checks.impl.badpackets;

import ac.grim.grimac.checks.CheckData;
import ac.grim.grimac.checks.type.PacketCheck;
import ac.grim.grimac.player.GrimPlayer;
import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.manager.server.ServerVersion;
import com.github.retrooper.packetevents.protocol.packettype.PacketTypeCommon;
import com.github.retrooper.packetevents.protocol.player.ClientVersion;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerFlying;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;

import static com.github.retrooper.packetevents.protocol.packettype.PacketType.Play.Client.*;

// Detects sending "post" packets
@CheckData(name = "BadPackets A")
public class BadPacketsA extends PacketCheck {

    public final ArrayDeque<PacketTypeCommon> postPackets = new ArrayDeque<>();
    // Due to 1.9+ missing the idle packet, we must queue flags
    // 1.8 clients will have the same logic for simplicity, although it's not needed
    public final List<String> flags = new ArrayList<>();
    public boolean sentFlying = false;

    public BadPacketsA(GrimPlayer playerData) {
        super(playerData);
    }

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        if (event.getPacketType() == INTERACT_ENTITY) {
            return;
        }

        if (WrapperPlayClientPlayerFlying.isFlying(event.getPacketType())) {
            // Don't count teleports or duplicates as movements
            if (player.packetStateData.lastPacketWasTeleport
                    || player.packetStateData.lastPacketWasOnePointSeventeenDuplicate) {
                return;
            }

            if (!flags.isEmpty()) {
                // Okay, the user might be cheating, let's double-check
                // 1.8 clients have the idle packet, and this shouldn't false on 1.8 clients
                // 1.9+ clients have predictions, which will determine if hidden tick skipping occurred
                if (player.isTickingReliablyFor(3)) {
                    for (String flag : flags) {
                        player.kick(getCheckName(), event, flag + " (" + player.getClientVersion().name() + ")");
                        return;
                    }
                }

                flags.clear();
            }

            postPackets.clear();
            sentFlying = true;

        } else {
            PacketTypeCommon packetType = event.getPacketType();

            if (WINDOW_CONFIRMATION.equals(packetType) || PONG.equals(packetType)) {
                if (sentFlying && !postPackets.isEmpty()) {
                    flags.add(postPackets.getFirst().toString());
                }

                postPackets.clear();
                sentFlying = false;

            } else if (PLAYER_ABILITIES.equals(packetType) || INTERACT_ENTITY.equals(packetType)
                    || PLAYER_BLOCK_PLACEMENT.equals(packetType) || USE_ITEM.equals(packetType)
                    || PLAYER_DIGGING.equals(packetType)) {
                if (sentFlying) {
                    postPackets.add(event.getPacketType());
                }

            } else if (CLICK_WINDOW.equals(packetType)
                    && player.getClientVersion().isOlderThan(ClientVersion.V_1_15)) {
                // Why do 1.15+ players send the click window packet whenever? This doesn't make sense.
                if (sentFlying) {
                    postPackets.add(event.getPacketType());
                }

            } else if (ANIMATION.equals(packetType)
                    && (player.getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_9) // ViaVersion delays animations for 1.8 clients
                    || PacketEvents.getAPI().getServerManager().getVersion().isOlderThanOrEquals(ServerVersion.V_1_8_8)) // when on 1.9+ servers
                    && player.getClientVersion().isOlderThanOrEquals(ClientVersion.V_1_15)) { // Unsure what mojang did in 1.15, but animations no longer work
                if (sentFlying) {
                    postPackets.add(event.getPacketType());
                }

            } else if (ENTITY_ACTION.equals(packetType) // ViaRewind sends START_FALL_FLYING packets async for 1.8 clients on 1.9+ servers
                    && ((player.getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_9) // ViaRewind doesn't 1.9 players
                    || PacketEvents.getAPI().getServerManager().getVersion().isOlderThanOrEquals(ServerVersion.V_1_8_8)))) { // No elytras
                if (sentFlying) {
                    postPackets.add(event.getPacketType());
                }
            }
        }
    }
}
