package ac.grim.grimac.checks;

import ac.grim.grimac.GrimAPI;
import ac.grim.grimac.player.GrimPlayer;
import ac.grim.grimac.utils.events.FlagEvent;
import github.scarsz.configuralize.DynamicConfig;
import lombok.Getter;
import org.bukkit.Bukkit;

// Class from https://github.com/Tecnio/AntiCheatBase/blob/master/src/main/java/me/tecnio/anticheat/check/Check.java
@Getter
public class Check<T> {
    protected final GrimPlayer player;

    public double violations;
    public double decay;
    public double setbackVL;

    private String checkName;
    private String configName;
    private String alernativeName;

    public Check(GrimPlayer player) {
        this.player = player;

        Class<?> checkClass = getClass();

        if (checkClass.isAnnotationPresent(CheckData.class)) {
            CheckData checkData = checkClass.getAnnotation(CheckData.class);
            checkName = checkData.name();
            configName = checkData.configName();
            // Fall back to check name
            if (configName.equals("DEFAULT")) {
                configName = checkName;
            }
            decay = checkData.decay();
            setbackVL = checkData.setback();
            alernativeName = checkData.alternativeName();
        }

        reload();
    }

    public void flagAndAlert(String verbose) {
        if (flag()) {
            alert(verbose);
        }
    }

    public void flagAndAlert() {
        flagAndAlert("");
    }

    public final boolean flag() {
        if (player.disableGrim) {
            return false; // Avoid calling event if disabled
        }

        FlagEvent event = new FlagEvent(this);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            return false;
        }

        player.punishmentManager.handleViolation(this);

        violations++;
        return true;
    }

    public final void flagWithSetback() {
        if (flag()) {
            setbackIfAboveSetbackVL();
        }
    }

    public final void reward() {
        violations = Math.max(0, violations - decay);
    }

    public void reload() {
        decay = getConfig().getDoubleElse(configName + ".decay", decay);
        setbackVL = getConfig().getDoubleElse(configName + ".setbackvl", setbackVL);

        if (setbackVL == -1) {
            setbackVL = Double.MAX_VALUE;
        }
    }

    public void alert(String verbose) {
        player.punishmentManager.handleAlert(player, verbose, this);
    }

    public DynamicConfig getConfig() {
        return GrimAPI.INSTANCE.getConfigManager().getConfig();
    }

    public void setbackIfAboveSetbackVL() {
        if (getViolations() > setbackVL) {
            player.getSetbackTeleportUtil().executeViolationSetback();
        }
    }

    public String formatOffset(double offset) {
        return offset > 0.001 ? String.format("%.5f", offset) : String.format("%.2E", offset);
    }
}

