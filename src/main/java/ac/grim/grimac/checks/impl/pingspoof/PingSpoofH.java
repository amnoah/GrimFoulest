package ac.grim.grimac.checks.impl.pingspoof;

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

    private final EvictingList<Long> changeFlyingList = new EvictingList<>(40);
    private final EvictingList<Long> changeTransactionList = new EvictingList<>(20);
    private final EvictingList<Integer> flyingCountList = new EvictingList<>(5);
    private final EvictingList<Integer> transactionCountList = new EvictingList<>(5);
    private long lastFlying;
    private long lastTransaction;
    private double lastAverageFlyingTimePercent;
    private double lastAverageTransactionTimePercent;
    private int flyingCount;
    private int transactionCount;

    public PingSpoofH(GrimPlayer player) {
        super(player);
    }

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        if (WrapperPlayClientPlayerFlying.isFlying(event.getPacketType())) {
            flyingCount++;
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
            transactionCount++;
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
                boolean inTransactionRange = transactionCount >= 20 && transactionCount <= 25;
                boolean lastInTransactionRange = transactionCountList.getLast() >= 20 && transactionCountList.getLast() <= 25;

                if (flyingCount > 41 && flyingCountList.getLast() > 41
                        && inTransactionRange && lastInTransactionRange) {
                    flagAndAlert("(High Count F) " + "[FLYING] " + lastAverageFlyingTimePercent + "% (" + flyingCount + ") "
                            + " [TRANS] " + lastAverageTransactionTimePercent + "% (" + transactionCount + ")", false);
                }

                if (!player.user.getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_9)) {
                    if (flyingCount < 41 && flyingCountList.getLast() < 41 && flyingCount != 0
                            && inTransactionRange && lastInTransactionRange) {
                        flagAndAlert("(Low Count F) " + "[FLYING] " + lastAverageFlyingTimePercent + "% (" + flyingCount + ") "
                                + " [TRANS] " + lastAverageTransactionTimePercent + "% (" + transactionCount + ")", false);
                    }
                }

                if (flyingCount == 0 && transactionCount != 0) {
                    flagAndAlert("(Zero Count F) " + "[FLYING] " + lastAverageFlyingTimePercent + "% (" + flyingCount + ") "
                            + " [TRANS] " + lastAverageTransactionTimePercent + "% (" + transactionCount + ")", false);
                }

                if (transactionCountList.size() - 2 >= 0) {
                    if (transactionCount > 25 && transactionCountList.getLast() > 25
                            && transactionCountList.get(transactionCountList.size() - 2) > 25
                            && flyingCount == 41 && flyingCountList.getLast() == 41) {
                        flagAndAlert("(High Count T) " + "[FLYING] " + lastAverageFlyingTimePercent + "% (" + flyingCount + ") "
                                + " [TRANS] " + lastAverageTransactionTimePercent + "% (" + transactionCount + ")", false);
                    }
                }

                if (transactionCount < 20 && transactionCountList.getLast() < 20 && transactionCount != 0
                        && flyingCount == 41 && flyingCountList.getLast() == 41) {
                    flagAndAlert("(Low Count T) " + "[FLYING] " + lastAverageFlyingTimePercent + "% (" + flyingCount + ") "
                            + " [TRANS] " + lastAverageTransactionTimePercent + "% (" + transactionCount + ")", false);
                }

                if (transactionCount == 0 && flyingCount != 0) {
                    flagAndAlert("(Zero Count T) " + "[FLYING] " + lastAverageFlyingTimePercent + "% (" + flyingCount + ") "
                            + " [TRANS] " + lastAverageTransactionTimePercent + "% (" + transactionCount + ")", false);
                }
            }

//            System.out.println("[FLYING] " + lastAverageFlyingTimePercent + "% (" + flyingCount + ") "
//                    + " [TRANS] " + lastAverageTransactionTimePercent + "% (" + transactionCount + ")");

            flyingCountList.add(flyingCount);
            transactionCountList.add(transactionCount);
            flyingCount = 0;
            transactionCount = 0;
        }
    }
}
