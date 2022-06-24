package ac.grim.grimac.checks.impl.pingspoof;

import ac.grim.grimac.GrimAPI;
import ac.grim.grimac.checks.CheckData;
import ac.grim.grimac.checks.type.PacketCheck;
import ac.grim.grimac.player.GrimPlayer;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientKeepAlive;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientResourcePackStatus;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerKeepAlive;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerResourcePackSend;
import net.kyori.adventure.text.Component;

// Detects PingSpoof using the ResourcePackStatus packet
@CheckData(name = "PingSpoof G")
public class PingSpoofG extends PacketCheck {

    private long pingPacketTime;
    private long keepAliveTime;
    private long keepAliveID;

    private int pingPacketDiff = -1;
    private int lastPingPacketDiff = -1;
    private int keepAliveDiff = -1;
    private int lastKeepAliveDiff = -1;

    private boolean clientBlocksPacket = false;
    public static boolean serverSendsResourcePacks = GrimAPI.INSTANCE.getConfigManager().getConfig()
            .getBooleanElse("using-custom-resource-packs", false);

    public PingSpoofG(GrimPlayer player) {
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
            pingPacketTime = System.nanoTime();
            keepAliveTime = System.nanoTime();
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
            // Never spoofed using hacked clients.
            if (packet.getHash().equals("")
                    && packet.getResult() == WrapperPlayClientResourcePackStatus.Result.FAILED_DOWNLOAD
                    || packet.getResult() == WrapperPlayClientResourcePackStatus.Result.ACCEPTED
                    || packet.getResult() == WrapperPlayClientResourcePackStatus.Result.DECLINED) {
                pingPacketDiff = (int) (System.nanoTime() - pingPacketTime) / 1000000;
                checkPing();
                lastPingPacketDiff = pingPacketDiff;
            }

        } else if (event.getPacketType() == PacketType.Play.Client.KEEP_ALIVE) {
            WrapperPlayClientKeepAlive packet = new WrapperPlayClientKeepAlive(event);

            // Calculate ping via the KeepAlive packet.
            // Packet that's spoofed in PingSpoof modules.
            if (packet.getId() == keepAliveID) {
                keepAliveDiff = (int) (System.nanoTime() - keepAliveTime) / 1000000;
                checkPing();
                lastKeepAliveDiff = keepAliveDiff;
            }
        }
    }

    public void checkPing() {
        if (pingPacketDiff != -1 && lastPingPacketDiff != -1
                && lastKeepAliveDiff != -1 && keepAliveDiff != -1) {
            clientBlocksPacket = false;

            // If there's a difference between the pings sent, the player is spoofing their ping.
            if (Math.abs(pingPacketDiff - keepAliveDiff) > 10) {
                if (Math.abs(lastPingPacketDiff - keepAliveDiff) > 10) {
                    flagAndAlert("REAL=" + pingPacketDiff + " FAKE=" + keepAliveDiff, false);
                }
            }

        } else if (pingPacketDiff == -1 && keepAliveDiff != -1 && lastKeepAliveDiff != -1) {
            clientBlocksPacket = true;
        }
    }
}
