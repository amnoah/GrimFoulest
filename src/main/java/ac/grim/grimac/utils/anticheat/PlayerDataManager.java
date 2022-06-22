package ac.grim.grimac.utils.anticheat;

import ac.grim.grimac.player.GrimPlayer;
import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.protocol.player.User;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerDataManager {

    private final ConcurrentHashMap<User, GrimPlayer> playerDataMap = new ConcurrentHashMap<>();

    public GrimPlayer getPlayer(Player player) {
        if (player == null) {
            return null;
        }

        // Is it safe to interact with this, or is this internal PacketEvents code?
        User user = PacketEvents.getAPI().getPlayerManager().getUser(player);
        return playerDataMap.get(user);
    }

    @Nullable
    public GrimPlayer getPlayer(User player) {
        return playerDataMap.get(player);
    }

    public void addPlayer(User user, GrimPlayer player) {
        playerDataMap.put(user, player);
    }

    public void remove(User player) {
        playerDataMap.remove(player);
    }

    public Collection<GrimPlayer> getEntries() {
        return playerDataMap.values();
    }

    public int size() {
        return playerDataMap.size();
    }
}
