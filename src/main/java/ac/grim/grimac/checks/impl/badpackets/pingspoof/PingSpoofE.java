package ac.grim.grimac.checks.impl.badpackets.pingspoof;

import ac.grim.grimac.checks.CheckData;
import ac.grim.grimac.checks.type.PacketCheck;
import ac.grim.grimac.player.GrimPlayer;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientWindowConfirmation;

// Detects modifying Transaction packets
@CheckData(name = "PingSpoof E")
public class PingSpoofE extends PacketCheck {

    private int lastID;

    public PingSpoofE(GrimPlayer player) {
        super(player);
    }

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        if (event.getPacketType() == PacketType.Play.Client.WINDOW_CONFIRMATION) {
            WrapperPlayClientWindowConfirmation packet = new WrapperPlayClientWindowConfirmation(event);
            int diff = packet.getActionId() - lastID;

            if (Math.abs(diff) >= 7 && lastID != 0 && Math.abs(packet.getActionId()) >= 30) {
                player.kick(getCheckName(), event, "MODIFY (DIFF=" + diff + ", ID=" + packet.getActionId() + ")");
                return;
            }

            lastID = packet.getActionId();
        }
    }
}
