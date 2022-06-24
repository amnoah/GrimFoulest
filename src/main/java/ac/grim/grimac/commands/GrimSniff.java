package ac.grim.grimac.commands;

import ac.grim.grimac.checks.impl.misc.PacketSniffer;
import ac.grim.grimac.utils.anticheat.MessageUtil;
import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Subcommand;
import org.bukkit.command.CommandSender;

@CommandAlias("grim|grimac")
public class GrimSniff extends BaseCommand {

    @Subcommand("sniff")
    @CommandPermission("grim.sniff")
    public void onReload(CommandSender sender, String value) {
        switch (value.toLowerCase()) {
            case "in":
                PacketSniffer.sniffingIncoming = true;
                sender.sendMessage(MessageUtil.format("&aNow sniffing all incoming packets."));
                break;

            case "out":
                PacketSniffer.sniffingOutgoing = true;
                sender.sendMessage(MessageUtil.format("&aNow sniffing all outgoing packets."));
                break;

            case "none":
                PacketSniffer.sniffingIncoming = false;
                PacketSniffer.sniffingOutgoing = false;
                sender.sendMessage(MessageUtil.format("&cPacket sniffing has been disabled."));
                break;

            case "windowconfirmation":
                PacketSniffer.sniffingWindowConfirmation = !PacketSniffer.sniffingWindowConfirmation;
                sender.sendMessage(MessageUtil.format("&eSniffing WindowConfirmation: &f" + PacketSniffer.sniffingWindowConfirmation));
                break;

            case "flying":
                PacketSniffer.sniffingFlying = !PacketSniffer.sniffingFlying;
                sender.sendMessage(MessageUtil.format("&eSniffing Flying: &f" + PacketSniffer.sniffingFlying));
                break;

            case "resourcepack":
                PacketSniffer.sniffingFlying = !PacketSniffer.sniffingResourcePack;
                sender.sendMessage(MessageUtil.format("&eSniffing ResourcePack: &f" + PacketSniffer.sniffingResourcePack));
                break;

            default:
                sender.sendMessage(MessageUtil.format("&cUsage: <packet-name/in/out/none>"));
                break;
        }
    }
}
