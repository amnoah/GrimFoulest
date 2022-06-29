package ac.grim.grimac.checks;

import ac.grim.grimac.AbstractCheck;
import ac.grim.grimac.GrimAPI;
import ac.grim.grimac.events.FlagEvent;
import ac.grim.grimac.player.GrimPlayer;
import github.scarsz.configuralize.DynamicConfig;
import lombok.Getter;
import org.bukkit.Bukkit;

@Getter
public class Check implements AbstractCheck {

    protected final GrimPlayer player;
    public double violations;
    private double decay;
    private double setbackVL;
    private String checkName;
    private String configName;
    private String alternativeName;

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
            alternativeName = checkData.alternativeName();
        }

        reload();
    }

    public void flagAndAlert(String verbose, boolean setback) {
        // Returns if the player is being kicked.
        if (player.isKicking) {
            return;
        }

        if (flag()) {
            alert(verbose);

            if (setback) {
                setbackIfAboveSetbackVL();
            }
        }
    }

    public final boolean flag() {
        // Avoid calling event if disabled
        if (player.disableGrim) {
            return false;
        }

        // Returns false if the player is being kicked.
        if (player.isKicking) {
            return false;
        }

        FlagEvent event = new FlagEvent(player, this);
        Bukkit.getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            return false;
        }

        player.punishmentManager.handleViolation(this);
        violations++;
        return true;
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
