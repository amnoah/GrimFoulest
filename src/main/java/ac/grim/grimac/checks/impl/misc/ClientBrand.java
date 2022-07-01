package ac.grim.grimac.checks.impl.misc;

import ac.grim.grimac.checks.type.PacketCheck;
import ac.grim.grimac.player.GrimPlayer;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.resources.ResourceLocation;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPluginMessage;
import lombok.Getter;
import org.bukkit.Bukkit;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

public class ClientBrand extends PacketCheck {

    public static final List<String> IGNORED_BRANDS = Arrays.asList(
            "vanilla",
            "fabric",
            "lunarclient:[a-z\\d]{7}",
            "Feather Fabric",
            "fml,forge",
            "\tfml,forge",
            "LiteLoader",
            "\u0007vanilla",
            "\u0007fml,forge",
            "\u0007LiteLoader",
            "\u0007fabric"
    );

    public static final List<String> HACKED_BRANDS = Arrays.asList(
            "\nLunar-Client",
            "Vanilla",
            "\u0007Vanilla",
            "Synergy",
            "\u0007Synergy",
            "Created By ",
            "\u0007Created By ",
            "\u0003FML", // Forge Spoof
            "\u0003LMC", // LabyMod Spoof (Need to verify)
            "PLC18", // PvPLounge Client Spoof (Need to verify)
            "\u0002CB", // CheatBreaker Spoof (Need to verify)
            "Geyser" // Geyser Spoof (Need to verify)
    );

    public static final List<String> IGNORED_REGISTERS = Arrays.asList();

    public static final List<String> HACKED_REGISTERS = Arrays.asList(
            "Lunar-Client"
    );

    public static final List<String> HACKED_CHANNELS = Arrays.asList(
            "LOLIMAHCKER",
            "CPS_BAN_THIS_NIGGER",
            "EROUAXWASHERE",
            "#unbanearwax",
            "1946203560",
            "cock",
            "lmaohax",
            "reach",
            "gg",
            "customGuiOpenBspkrs",
            "0SO1Lk2KASxzsd",
            "MCnetHandler",
            "n",
            "BLC|M",
            "XDSMKDKFDKSDAKDFkEJF"
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

                if (data.contains("FML|HS")) {
                    return;
                }

                // Remove Velocity's brand suffix.
                data = data.replace(" (Velocity)", "");

                // Sets the player's brand.
                brand = data;

                // Kicks players with hacked brands.
                if (HACKED_BRANDS.contains(data)) {
                    player.kick("Hacked Brand", event, "BRAND=" + data);
                    return;
                }

                // Prints warnings for players with unknown brands.
                if (!IGNORED_BRANDS.contains(data)) {
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "grim sendalert &b[Grim] &f"
                            + player.user.getProfile().getName() + " &7sent an unknown brand: &f" + data);

                    try {
                        BufferedWriter writer = new BufferedWriter(new FileWriter("unknown-brand.txt"));
                        writer.write(data);
                        writer.close();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }

            } else if (channelName.equalsIgnoreCase("minecraft:register")
                    || channelName.equals("REGISTER")) {
                String data = new String(packet.getData(), StandardCharsets.UTF_8);

                if (data.equals("")) {
                    data = "Empty";
                }

                // Kicks players with hacked registered data.
                if (HACKED_REGISTERS.contains(data)) {
                    player.kick("Hacked Register Data", event, "BRAND=" + brand);
                    return;
                }

                // Prints warnings for players with unknown registered data.
                if (!IGNORED_REGISTERS.contains(data)) {
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "grim sendalert &b[Grim] &f"
                            + player.user.getProfile().getName() + " &7registered unknown data: &f" + data);

                    try {
                        BufferedWriter writer = new BufferedWriter(new FileWriter("unknown-data.txt"));
                        writer.write(data);
                        writer.close();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }

            } else {
                // Kicks Crystalware players.
                if (channelName.startsWith("CRYSTAL|")) {
                    player.kick("Hacked Channel Data", event, "CHANNEL=" + channelName);
                    return;
                }

                // Kicks players with hacked channel data.
                if (HACKED_CHANNELS.contains(channelName)) {
                    player.kick("Hacked Channel Data", event, "CHANNEL=" + channelName);
                }
            }
        }
    }
}
