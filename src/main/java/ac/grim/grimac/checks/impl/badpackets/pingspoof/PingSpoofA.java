package ac.grim.grimac.checks.impl.badpackets.pingspoof;

import ac.grim.grimac.GrimAPI;
import ac.grim.grimac.checks.CheckData;
import ac.grim.grimac.checks.type.PacketCheck;
import ac.grim.grimac.player.GrimPlayer;
import ac.grim.grimac.utils.lists.EvictingList;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.world.Location;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientKeepAlive;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerFlying;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientResourcePackStatus;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerKeepAlive;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerResourcePackSend;
import net.kyori.adventure.text.Component;

// Detects PingSpoof using the ResourcePackStatus packet
@CheckData(name = "PingSpoof A")
public class PingSpoofA extends PacketCheck {

    public static boolean serverSendsResourcePacks = GrimAPI.INSTANCE.getConfigManager().getConfig()
            .getBooleanElse("using-custom-resource-packs", false);
    private long updateLocationTime;
    private Location lastLocation;
    private long pingPacketTime;
    private long keepAliveTime;
    private long keepAliveID;
    private boolean clientBlocksPacket = false;
    private final EvictingList<Long> pingPacketDiffs = new EvictingList<>(5);
    private final EvictingList<Long> keepAliveDiffs = new EvictingList<>(5);

    public PingSpoofA(GrimPlayer player) {
        super(player);
    }

    @Override
    public void onPacketSend(PacketSendEvent event) {
        // Skip check if server sends custom resource packs.
        if (serverSendsResourcePacks) {
            return;
        }

        if (event.getPacketType() == PacketType.Play.Server.KEEP_ALIVE) {
            WrapperPlayServerKeepAlive packet = new WrapperPlayServerKeepAlive(event);

            // Sends the ping packet to the player.
            // A blank URL is used for clients blocking the packet.
            player.user.sendPacket(new WrapperPlayServerResourcePackSend((!clientBlocksPacket ? "level://" : ""), "", false, Component.empty()));

            // Keeps track of when the pings were sent, along with the KeepAlive ID.
            pingPacketTime = System.currentTimeMillis();
            keepAliveTime = System.currentTimeMillis();
            keepAliveID = packet.getId();
        }
    }

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        // Skip check if server sends custom resource packs.
        if (serverSendsResourcePacks) {
            return;
        }

        if (event.getPacketType() == PacketType.Play.Client.RESOURCE_PACK_STATUS) {
            WrapperPlayClientResourcePackStatus packet = new WrapperPlayClientResourcePackStatus(event);

            // Calculate ping via the ResourcePackStatus packet.
            if (packet.getHash().equals("")
                    && packet.getResult() == WrapperPlayClientResourcePackStatus.Result.FAILED_DOWNLOAD
                    || packet.getResult() == WrapperPlayClientResourcePackStatus.Result.ACCEPTED
                    || packet.getResult() == WrapperPlayClientResourcePackStatus.Result.DECLINED) {
                pingPacketDiffs.add(System.currentTimeMillis() - pingPacketTime);
                checkPing();
            }

        } else if (event.getPacketType() == PacketType.Play.Client.KEEP_ALIVE) {
            WrapperPlayClientKeepAlive packet = new WrapperPlayClientKeepAlive(event);

            // Calculate ping via the KeepAlive packet.
            if (packet.getId() == keepAliveID) {
                keepAliveDiffs.add(System.currentTimeMillis() - keepAliveTime);
                checkPing();
            }

        } else if (WrapperPlayClientPlayerFlying.isFlying(event.getPacketType())) {
            WrapperPlayClientPlayerFlying packet = new WrapperPlayClientPlayerFlying(event);

            // Keeps track of player movement.
            if (packet.hasPositionChanged() && packet.getLocation() != lastLocation) {
                lastLocation = packet.getLocation();
                updateLocationTime = System.currentTimeMillis();
            }
        }
    }

    public void checkPing() {
        if (keepAliveDiffs.size() != keepAliveDiffs.getMaxSize()) {
            return;
        }

        if (pingPacketDiffs.size() == pingPacketDiffs.getMaxSize()) {
            clientBlocksPacket = false;
            long locationDiff = System.currentTimeMillis() - updateLocationTime;

            // Ignores players who aren't moving.
            if (locationDiff > 100) {
                return;
            }

            int count = 0;
            for (Long diff : pingPacketDiffs) {
                if (Math.abs(diff - keepAliveDiffs.getLast()) > 10) {
                    count++;

                    // If there's a difference between the pings sent, the player is spoofing their ping.
                    if (count >= 5) {
                        player.kick(getCheckName(), "STREAK=" + count + " REAL=" + diff
                                + " FAKE=" + keepAliveDiffs.getLast(), "Your connection is unstable.");
                    }
                }
            }

        } else {
            clientBlocksPacket = true;
        }
    }
}
