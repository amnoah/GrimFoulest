package ac.grim.grimac.utils.data;

import com.github.retrooper.packetevents.protocol.entity.type.EntityType;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class TrackerData {
    double x, y, z;
    float xRot, yRot;
    @NonNull
    EntityType entityType;
    int lastTransactionHung;
    int legacyPointEightMountedUpon;
}
