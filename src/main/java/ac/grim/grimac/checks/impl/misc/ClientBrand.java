package ac.grim.grimac.checks.impl.misc;

import ac.grim.grimac.checks.type.PacketCheck;
import ac.grim.grimac.player.GrimPlayer;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.resources.ResourceLocation;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPluginMessage;
import lombok.Getter;
import org.bukkit.Bukkit;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

public class ClientBrand extends PacketCheck {

    public static final List<Pattern> IGNORED_BRANDS = Arrays.asList(
            Pattern.compile("^vanilla$"),
            Pattern.compile("^fabric$"),
            Pattern.compile("^lunarclient:[a-z\\d]{7}"),
            Pattern.compile("^Feather Fabric$")
    );

    public static final List<Pattern> IGNORED_REGISTERS = Collections.emptyList();

    @Getter
    String brand = "vanilla";

    public ClientBrand(GrimPlayer player) {
        super(player);
    }

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        if (event.getPacketType() == PacketType.Play.Client.PLUGIN_MESSAGE) {
            WrapperPlayClientPluginMessage packet = new WrapperPlayClientPluginMessage(event);
            Object channelObject = packet.getChannelName();
            String channelName;

            if (channelObject instanceof String) {
                channelName = (String) channelObject;
            } else {
                ResourceLocation resourceLocation = (ResourceLocation) channelObject;
                channelName = resourceLocation.getNamespace() + ":" + resourceLocation.getKey();
            }

            if (channelName.equalsIgnoreCase("minecraft:brand") || channelName.equals("MC|Brand")) {
                String data = new String(packet.getData(), StandardCharsets.UTF_8);

                if (data.equals("")) {
                    data = "Empty";
                }

                // Removes Velocity's brand suffix
                data = data.replace(" (Velocity)", "");

                // Prints unknown brands to console & online players with permission
                if (!isInPatternList(IGNORED_BRANDS, brand)) {
                    // TODO: Sync with prefix & shit
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "grim sendalert &b[Grim] &f"
                            + player.user.getProfile().getName() + " &7sent an unknown brand: &f" + data);
                }

            } else if (channelName.equalsIgnoreCase("minecraft:register") || channelName.equals("REGISTER")) {
                String data = new String(packet.getData(), StandardCharsets.UTF_8);

                if (data.equals("")) {
                    data = "Empty";
                }

                if (!isInPatternList(IGNORED_REGISTERS, data)) {
                    // TODO: Sync with prefix & shit
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "grim sendalert &b[Grim] &f"
                            + player.user.getProfile().getName() + " &7registered unknown data: &f" + data);
                }
            }
        }
    }

    public boolean isInPatternList(List<Pattern> patternList, String text) {
        for (Pattern pattern : patternList) {
            if (pattern.matcher(text).find()) {
                return true;
            }
        }

        return false;
    }
}
