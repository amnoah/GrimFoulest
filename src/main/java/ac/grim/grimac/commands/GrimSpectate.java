package ac.grim.grimac.commands;

import ac.grim.grimac.GrimAPI;
import ac.grim.grimac.player.GrimPlayer;
import ac.grim.grimac.utils.anticheat.MessageUtil;
import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.bukkit.contexts.OnlinePlayer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.GameMode;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandAlias("grim|grimac")
public class GrimSpectate extends BaseCommand {

    @Subcommand("spectate")
    @CommandPermission("grim.spectate")
    @CommandCompletion("@players")
    public void onSpectate(CommandSender sender, OnlinePlayer target) {
        if (!(sender instanceof Player)) {
            return;
        }

        Player player = (Player) sender;

        if (target == null) {
            String message = GrimAPI.INSTANCE.getConfigManager().getConfig().getStringElse("player-not-this-server", "%prefix% &cPlayer isn't on this server!");
            sender.sendMessage(MessageUtil.format(message));
            return;
        }

        //hide player from tab list
        if (GrimAPI.INSTANCE.getSpectateManager().enable(player)) {
            GrimPlayer grimPlayer = GrimAPI.INSTANCE.getPlayerDataManager().getPlayer(player);
            if (grimPlayer != null) {
                String message = GrimAPI.INSTANCE.getConfigManager().getConfig().getStringElse("spectate-return", "\n%prefix% &fClick here to return to previous location\n");
                grimPlayer.user.sendMessage(
                        LegacyComponentSerializer.legacy('&')
                                .deserialize(MessageUtil.format(message))
                                .clickEvent(ClickEvent.clickEvent(ClickEvent.Action.RUN_COMMAND, "/grim stopspectating"))
                                .hoverEvent(HoverEvent.showText(Component.text("/grim stopspectating")))
                );
            }
        }

        player.setGameMode(GameMode.SPECTATOR);
        player.teleport(target.getPlayer());
    }


}
