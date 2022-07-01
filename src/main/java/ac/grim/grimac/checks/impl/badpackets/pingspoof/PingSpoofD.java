package ac.grim.grimac.checks.impl.badpackets.pingspoof;

import ac.grim.grimac.checks.CheckData;
import ac.grim.grimac.checks.type.PacketCheck;
import ac.grim.grimac.player.GrimPlayer;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerFlying;

// Detects cancelling KeepAlive and Transaction
@CheckData(name = "PingSpoof D")
public class PingSpoofD extends PacketCheck {

    public int flyingCount;
    public int transactionCount;
    public long lastReset;

    public PingSpoofD(GrimPlayer player) {
        super(player);
    }

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        if (WrapperPlayClientPlayerFlying.isFlying(event.getPacketType())) {
            ++flyingCount;

        } else if (event.getPacketType() == PacketType.Play.Client.WINDOW_CONFIRMATION) {
            ++transactionCount;

        } else if (event.getPacketType() == PacketType.Play.Client.KEEP_ALIVE) {
            transactionCount = 0;
            flyingCount = 0;
            lastReset = System.currentTimeMillis();
        }

        long timeSinceReset = System.currentTimeMillis() - lastReset;
        if (timeSinceReset == System.currentTimeMillis()) {
            return;
        }

        double countRatio = ((double) (flyingCount == 0 ? 1 : flyingCount)
                / (transactionCount == 0 ? 1 : transactionCount));

        // We can't track our targeted ratio (0.5 and below) because
        // Grim sends Transaction packets on every teleport.
        // So when a player gets repeatedly setback, this false flags.
        if (flyingCount >= 20 && countRatio > 3.5) {
            player.kick(getCheckName(), event, "(Ratio) FLYING=" + flyingCount + " TRANS=" + transactionCount
                    + " RESET=" + timeSinceReset + " RATIO=" + countRatio);

        } else if (flyingCount >= 40 && timeSinceReset >= 3000) {
            player.kick(getCheckName(), event, "(Count) FLYING=" + flyingCount + " TRANS=" + transactionCount
                    + " RESET=" + timeSinceReset + " RATIO=" + countRatio);
        }
    }
}
