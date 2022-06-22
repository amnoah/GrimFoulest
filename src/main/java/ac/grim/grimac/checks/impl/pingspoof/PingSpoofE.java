package ac.grim.grimac.checks.impl.pingspoof;

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

            if (Math.abs(packet.getActionId() - lastID) >= 3 && lastID != 0) {
                event.setCancelled(true);
                player.kick(getCheckName(), "MODIFY (ID=" + packet.getActionId() + ", LAST=" + lastID + ")");
                return;
            }

            lastID = packet.getActionId();
        }
    }
}
