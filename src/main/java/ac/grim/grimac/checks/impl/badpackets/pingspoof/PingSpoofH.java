package ac.grim.grimac.checks.impl.badpackets.pingspoof;

import ac.grim.grimac.checks.CheckData;
import ac.grim.grimac.checks.type.PacketCheck;
import ac.grim.grimac.player.GrimPlayer;
import ac.grim.grimac.utils.lists.EvictingList;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.player.ClientVersion;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerFlying;

// Detects delaying packets, timed on KeepAlive
@CheckData(name = "PingSpoof H")
public class PingSpoofH extends PacketCheck {

    public final EvictingList<Long> changeFlyingList = new EvictingList<>(40);
    public final EvictingList<Long> changeTransactionList = new EvictingList<>(20);
    public final EvictingList<Integer> flyingCountList = new EvictingList<>(5);
    public final EvictingList<Integer> transactionCountList = new EvictingList<>(5);
    public long lastFlying;
    public long lastTransaction;
    public double lastAverageFlyingTimePercent;
    public double lastAverageTransactionTimePercent;
    public int flyingCount;
    public int transactionCount;

    public PingSpoofH(GrimPlayer player) {
        super(player);
    }

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        if (WrapperPlayClientPlayerFlying.isFlying(event.getPacketType())) {
            ++flyingCount;
            long timeBetweenFlying = System.nanoTime() - lastFlying;
            lastFlying = System.nanoTime();
            changeFlyingList.add(timeBetweenFlying);

            // Gets the average time between Flying packets
            if (changeFlyingList.size() == changeFlyingList.getMaxSize()) {
                long lastAverageFlyingTime = (long) changeFlyingList.stream().mapToDouble(d -> d).average().orElse(0.0);
                lastAverageFlyingTimePercent = ((lastAverageFlyingTime * 100.0) / 50000000) - 100;
                lastAverageFlyingTimePercent = Math.round(lastAverageFlyingTimePercent * 1000.0) / 1000.0;
                changeFlyingList.clear();
            }

        } else if (event.getPacketType() == PacketType.Play.Client.WINDOW_CONFIRMATION) {
            ++transactionCount;
            long timeBetweenTransaction = System.nanoTime() - lastTransaction;
            lastTransaction = System.nanoTime();
            changeTransactionList.add(timeBetweenTransaction);

            // Gets the average time between Transaction packets
            if (changeTransactionList.size() == changeTransactionList.getMaxSize()) {
                long lastAverageTransactionTime = (long) changeTransactionList.stream().mapToDouble(d -> d).average().orElse(0.0);
                lastAverageTransactionTimePercent = ((lastAverageTransactionTime * 100.0) / 100000000) - 100;
                lastAverageTransactionTimePercent = Math.round(lastAverageTransactionTimePercent * 1000.0) / 1000.0;
                changeTransactionList.clear();
            }

        } else if (event.getPacketType() == PacketType.Play.Client.KEEP_ALIVE) {
            if (!transactionCountList.isEmpty() && !flyingCountList.isEmpty()
                    && (flyingCount != 0 && transactionCount != 0)) {
                boolean inTransactionRange = Math.abs(lastAverageTransactionTimePercent) <= 5.0
                        && transactionCount >= 20 && transactionCount <= 25;
                boolean lastInTransactionRange = Math.abs(lastAverageTransactionTimePercent) <= 5.0
                        && transactionCountList.getLast() >= 20 && transactionCountList.getLast() <= 25;

                // Detects sending higher amount of Flying packets per KeepAlive
                if (flyingCount > 42 && flyingCountList.getLast() > 42
                        && inTransactionRange && lastInTransactionRange) {
                    flagAndAlert("(High Count F) " + "[FLYING] " + lastAverageFlyingTimePercent + "% (" + flyingCount + ") "
                            + " [TRANS] " + lastAverageTransactionTimePercent + "% (" + transactionCount + ")", false);
                }

                // Detects sending lower amount of Flying packets per KeepAlive
                if (!player.user.getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_9)) {
                    if (flyingCount < 37 && flyingCountList.getLast() < 37 && flyingCount != 0
                            && inTransactionRange && lastInTransactionRange) {
                        flagAndAlert("(Low Count F) " + "[FLYING] " + lastAverageFlyingTimePercent + "% (" + flyingCount + ") "
                                + " [TRANS] " + lastAverageTransactionTimePercent + "% (" + transactionCount + ")", false);
                    }
                }

                // Detects sending zero Flying packets per KeepAlive
                if (flyingCount == 0 && transactionCount != 0) {
                    flagAndAlert("(Zero Count F) " + "[FLYING] " + lastAverageFlyingTimePercent + "% (" + flyingCount + ") "
                            + " [TRANS] " + lastAverageTransactionTimePercent + "% (" + transactionCount + ")", false);
                }

                // Detects sending lower amount of Transaction packets per KeepAlive
                if (transactionCount < 20 && transactionCountList.getLast() < 20 && transactionCount != 0
                        && flyingCount == 41 && flyingCountList.getLast() == 41) {
                    flagAndAlert("(Low Count T) " + "[FLYING] " + lastAverageFlyingTimePercent + "% (" + flyingCount + ") "
                            + " [TRANS] " + lastAverageTransactionTimePercent + "% (" + transactionCount + ")", false);
                }

                // Detects sending zero Transaction packets per KeepAlive
                if (transactionCount == 0 && flyingCount != 0) {
                    flagAndAlert("(Zero Count T) " + "[FLYING] " + lastAverageFlyingTimePercent + "% (" + flyingCount + ") "
                            + " [TRANS] " + lastAverageTransactionTimePercent + "% (" + transactionCount + ")", false);
                }
            }

            flyingCountList.add(flyingCount);
            transactionCountList.add(transactionCount);
            flyingCount = 0;
            transactionCount = 0;
        }
    }
}
