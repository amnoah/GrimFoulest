package ac.grim.grimac.utils.anticheat;

import ac.grim.grimac.GrimAPI;
import lombok.experimental.UtilityClass;

import java.util.logging.Logger;

@UtilityClass
public class LogUtil {
    public void info(String info) {
        getLogger().info(info);
    }

    public void warn(String warn) {
        getLogger().info(warn);
    }

    public void error(String error) {
        getLogger().info(error);
    }

    public Logger getLogger() {
        return GrimAPI.INSTANCE.getPlugin().getLogger();
    }
}
