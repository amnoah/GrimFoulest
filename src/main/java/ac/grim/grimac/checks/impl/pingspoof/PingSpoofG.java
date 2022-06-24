package ac.grim.grimac.checks.impl.pingspoof;

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

import java.util.Objects;

// Detects PingSpoof using the ResourcePackStatus packet
@CheckData(name = "PingSpoof G")
public class PingSpoofG extends PacketCheck {

    private long resourcePackTime;
    private long keepAliveTime;
    private long keepAliveID;
    private int resourcePackPing = -1;
    private int keepAlivePing = -1;
    boolean clientBlocksPacket = false;

    public PingSpoofG(GrimPlayer player) {
        super(player);
    }

    @Override
    public void onPacketSend(PacketSendEvent event) {
        if (event.getPacketType() == PacketType.Play.Server.KEEP_ALIVE) {
            WrapperPlayServerKeepAlive packet = new WrapperPlayServerKeepAlive(event);

            // Sends the ping packet to the player.
            // Compensates for clients blocking the level:// URL by sending a blank URL.
            if (!clientBlocksPacket) {
                player.user.sendPacket(new WrapperPlayServerResourcePackSend("level://", "", false, Component.empty()));
            } else {
                player.user.sendPacket(new WrapperPlayServerResourcePackSend("", "", false, Component.empty()));
            }

            // Keeps track of when the pings were sent, along with the KeepAlive ID.
            resourcePackTime = System.nanoTime();
            keepAliveTime = System.nanoTime();
            keepAliveID = packet.getId();
        }
    }

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        if (event.getPacketType() == PacketType.Play.Client.RESOURCE_PACK_STATUS) {
            WrapperPlayClientResourcePackStatus packet = new WrapperPlayClientResourcePackStatus(event);

            // Calculate ping via the ResourcePackStatus packet.
            // Never spoofed using hacked clients.
            if (packet.getHash().equals("")
                    && packet.getResult() == WrapperPlayClientResourcePackStatus.Result.FAILED_DOWNLOAD
                    || packet.getResult() == WrapperPlayClientResourcePackStatus.Result.ACCEPTED
                    || packet.getResult() == WrapperPlayClientResourcePackStatus.Result.DECLINED) {
                resourcePackPing = (int) (System.nanoTime() - resourcePackTime) / 1000000;
                checkPing();
            }

        } else if (event.getPacketType() == PacketType.Play.Client.KEEP_ALIVE) {
            WrapperPlayClientKeepAlive packet = new WrapperPlayClientKeepAlive(event);

            // Calculate ping via the KeepAlive packet.
            // Packet that's spoofed in PingSpoof modules.
            if (packet.getId() == keepAliveID) {
                keepAlivePing = (int) (System.nanoTime() - keepAliveTime) / 1000000;
                checkPing();
            }
        }
    }

    public void checkPing() {
        // If there's a difference between the two pings sent, the player is spoofing their ping.
        if (resourcePackPing != -1 && keepAlivePing != -1) {
            clientBlocksPacket = false;

            if (Math.abs(resourcePackPing - keepAlivePing) > 10) {
                flagAndAlert("REAL=" + resourcePackPing + " FAKE=" + keepAlivePing, false);
            }

        } else if (resourcePackPing == -1 && keepAlivePing != -1) {
            clientBlocksPacket = true;
        }
    }
}
