package ac.grim.grimac.manager;

import ac.grim.grimac.GrimAPI;
import ac.grim.grimac.manager.init.Initable;
import ac.grim.grimac.player.GrimPlayer;
import ac.grim.grimac.utils.anticheat.LogUtil;
import ac.grim.grimac.utils.math.GrimMath;
import club.minnced.discord.webhook.WebhookClient;
import club.minnced.discord.webhook.send.WebhookEmbed;
import club.minnced.discord.webhook.send.WebhookEmbedBuilder;
import io.github.retrooper.packetevents.util.SpigotReflectionUtil;

import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DiscordManager implements Initable {
    private static WebhookClient client;

    @Override
    public void start() {
        try {
            if (!GrimAPI.INSTANCE.getConfigManager().getConfig().getBooleanElse("enabled", false)) {
                return;
            }

            client = WebhookClient.withUrl(GrimAPI.INSTANCE.getConfigManager().getConfig().getStringElse("webhook", ""));
            if (client.getUrl().isEmpty()) {
                LogUtil.warn("Discord webhook is empty, disabling Discord alerts");
                client = null;
                return;
            }

            client.setTimeout(15000); // Requests expire after 15 seconds

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendAlert(GrimPlayer player, String verbose, String checkName, String violations) {
        if (client != null) {
            String tps = String.format("%.2f", SpigotReflectionUtil.getTPS());
            String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            String formattedPing = String.valueOf(GrimMath.floor(player.getTransactionPing() / 1e6));
            String formattedVer = player.getClientVersion().getReleaseName();

            String content = "**Player**\n" + (player.bukkitPlayer != null ? player.bukkitPlayer.getName() : player.user.getProfile().getName())
                    + "\n**Check**\n" + checkName
                    + "\n**Violations**\n " + violations
                    + "\n**Client Version**\n" + formattedVer
                    + "\n**Ping**\n" + formattedPing
                    + "\n**TPS**\n" + tps;

            WebhookEmbedBuilder embed = new WebhookEmbedBuilder()
                    .setImageUrl("https://i.stack.imgur.com/Fzh0w.png") // Constant width
                    .setColor(Color.YELLOW.getRGB())
                    // Discord caches this for around 24 hours, this is abuse of neither CraftHead nor discord
                    .setThumbnailUrl("https://crafthead.net/avatar/" + player.user.getProfile().getUUID())
                    .setTitle(new WebhookEmbed.EmbedTitle("**Grim Alert**", null))
                    .setDescription(content)
                    .setFooter(new WebhookEmbed.EmbedFooter(time, "https://grim.ac/images/grim.png"));

            if (!verbose.isEmpty()) {
                embed.addField(new WebhookEmbed.EmbedField(true, "Verbose", verbose));
            }

            sendWebhookEmbed(embed);
        }
    }

    public void sendWebhookEmbed(WebhookEmbedBuilder embed) {
        try {
            client.send(embed.build());
        } catch (Exception ignored) {
        }
    }
}
