package ac.grim.grimac.utils.data;

import com.github.retrooper.packetevents.util.Vector3d;
import org.bukkit.Location;
import org.bukkit.util.Vector;

public class SetbackLocationVelocity {
    public final Location position;
    final Vector velocity;

    public SetbackLocationVelocity(Vector3d vector3d) {
        position = new Location(null, vector3d.getX(), vector3d.getY(), vector3d.getZ());
        velocity = null;
    }

    public SetbackLocationVelocity(Vector3d vector3d, Vector velocity) {
        position = new Location(null, vector3d.getX(), vector3d.getY(), vector3d.getZ());
        this.velocity = velocity;
    }
}
