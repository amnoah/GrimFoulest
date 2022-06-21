package ac.grim.grimac.utils.anticheat;

import ac.grim.grimac.GrimAPI;
import lombok.experimental.UtilityClass;
import org.bukkit.ChatColor;

@UtilityClass
public class MessageUtil {
    // & to paragraph symbol
    public String format(String string) {
        string = string.replace("%prefix%", GrimAPI.INSTANCE.getConfigManager().getConfig().getStringElse("prefix", "&bGrim &8»"));
        return ChatColor.translateAlternateColorCodes('&', string);
    }

    public static String toCamelCase(String s){
        String[] parts = s.split("_");
        StringBuilder camelCaseString = new StringBuilder();

        for (String part : parts){
            camelCaseString.append(toProperCase(part));
        }

        return camelCaseString.toString();
    }

    public static String toProperCase(String s) {
        return s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase();
    }
}
