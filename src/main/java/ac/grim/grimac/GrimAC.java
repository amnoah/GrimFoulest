package ac.grim.grimac;

import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public final class GrimAC extends JavaPlugin {

    public static Plugin instance;

    @Override
    public void onLoad() {
        instance = this;
        GrimAPI.INSTANCE.load(this);
    }

    @Override
    public void onDisable() {
        instance = this;
        GrimAPI.INSTANCE.stop(this);
    }

    @Override
    public void onEnable() {
        instance = this;
        GrimAPI.INSTANCE.start(this);
    }
}
