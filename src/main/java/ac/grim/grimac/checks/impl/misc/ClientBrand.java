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
            Pattern.compile("vanilla"),
            Pattern.compile("fabric"),
            Pattern.compile("^lunarclient:[a-z\\d]{7}"),
            Pattern.compile("Feather Fabric"),
            Pattern.compile("fml,forge"),
            Pattern.compile("LiteLoader"),
            Pattern.compile("\u0007vanilla"),
            Pattern.compile("\u0007fml,forge"),
            Pattern.compile("\u0007LiteLoader"),
            Pattern.compile("\u0007fabric")
    );

    public static final List<Pattern> HACKED_BRANDS = Arrays.asList(
            Pattern.compile("\nLunar-Client"),
            Pattern.compile("Vanilla"),
            Pattern.compile("\u0007Vanilla"),
            Pattern.compile("Synergy"),
            Pattern.compile("\u0007Synergy"),
            Pattern.compile("Created By "),
            Pattern.compile("\u0007Created By ")
    );

    public static final List<Pattern> IGNORED_REGISTERS = Collections.emptyList();

    public static final List<Pattern> HACKED_REGISTERS = Arrays.asList(
            Pattern.compile("Lunar-Client")
    );

    public static final List<Pattern> HACKED_CHANNELS = Arrays.asList(
            Pattern.compile("LOLIMAHCKER"),
            Pattern.compile("CPS_BAN_THIS_NIGGER"),
            Pattern.compile("EROUAXWASHERE"),
            Pattern.compile("#unbanearwax"),
            Pattern.compile("1946203560"),
            Pattern.compile("cock"),
            Pattern.compile("lmaohax"),
            Pattern.compile("reach"),
            Pattern.compile("gg"),
            Pattern.compile("customGuiOpenBspkrs"),
            Pattern.compile("0SO1Lk2KASxzsd"),
            Pattern.compile("MCnetHandler"),
            Pattern.compile("n"),
            Pattern.compile("BLC|M"),
            Pattern.compile("XDSMKDKFDKSDAKDFkEJF")
    );

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

                // Remove Velocity's brand suffix.
                data = data.replace(" (Velocity)", "");

                // Sets the player's brand.
                brand = data;

                // Kicks players with hacked brands.
                if (isInPatternList(HACKED_BRANDS, brand)) {
                    event.setCancelled(true);
                    player.kick("Hacked Brand", "BRAND=" + brand,
                            "Internal Exception: java.io.IOException: An existing connection was forcibly closed by the remote host");
                    return;
                }

                // Prints warnings for players with unknown brands.
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

                // Kicks players with hacked registered data.
                if (isInPatternList(HACKED_REGISTERS, data)) {
                    event.setCancelled(true);
                    player.kick("Hacked Register Data", "BRAND=" + brand,
                            "Internal Exception: java.io.IOException: An existing connection was forcibly closed by the remote host");
                    return;
                }

                // Prints warnings for players with unknown registered data.
                if (!isInPatternList(IGNORED_REGISTERS, data)) {
                    // TODO: Sync with prefix & shit
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "grim sendalert &b[Grim] &f"
                            + player.user.getProfile().getName() + " &7registered unknown data: &f" + data);
                }

            } else {
                // Kicks Crystalware players.
                if (channelName.startsWith("CRYSTAL|")) {
                    event.setCancelled(true);
                    player.kick("Hacked Channel Data", "CHANNEL=" + channelName,
                            "Internal Exception: java.io.IOException: An existing connection was forcibly closed by the remote host");
                    return;
                }

                // Kicks players with hacked channel data.
                if (isInPatternList(HACKED_CHANNELS, channelName)) {
                    event.setCancelled(true);
                    player.kick("Hacked Channel Data", "CHANNEL=" + channelName,
                            "Internal Exception: java.io.IOException: An existing connection was forcibly closed by the remote host");
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
