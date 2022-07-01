package ac.grim.grimac.checks.impl.badpackets;

import ac.grim.grimac.checks.CheckData;
import ac.grim.grimac.checks.type.PacketCheck;
import ac.grim.grimac.player.GrimPlayer;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.player.GameMode;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientSpectate;
import org.bukkit.Bukkit;

// Detects sending invalid SPECTATE packets
@CheckData(name = "BadPackets C")
public class BadPacketsC extends PacketCheck {

    public BadPacketsC(GrimPlayer player) {
        super(player);
    }

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        if (event.getPacketType() == PacketType.Play.Client.SPECTATE) {
            WrapperPlayClientSpectate packet = new WrapperPlayClientSpectate(event);

            // Detects spectating invalid targets.
            if (Bukkit.getServer().getPlayer(packet.getTargetUUID()) == null) {
                player.kick(getCheckName(), event, "Invalid Target");
            }

            // Detects sending packets while not being in spectator mode.
            if (player.gamemode != GameMode.SPECTATOR) {
                player.kick(getCheckName(), event, "Not in Spectator");
            }
        }
    }
}
