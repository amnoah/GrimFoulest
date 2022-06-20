package ac.grim.grimac.utils.data;

import com.github.retrooper.packetevents.protocol.entity.type.EntityType;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

@Data
@RequiredArgsConstructor
public class TrackerData {
    double x, y, z;
    float xRot, yRot;
    @NonNull
    EntityType entityType;
    int lastTransactionHung;
    int legacyPointEightMountedUpon;

    public TrackerData(double x, double y, double z, float xRot, float yRot,
                       @NotNull EntityType entityType, int lastTransactionHung) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.xRot = xRot;
        this.yRot = yRot;
        this.entityType = entityType;
        this.lastTransactionHung = lastTransactionHung;
    }
}
