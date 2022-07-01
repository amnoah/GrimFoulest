package ac.grim.grimac.checks.impl.combat.autoblock;

import ac.grim.grimac.checks.CheckData;
import ac.grim.grimac.checks.type.PacketCheck;
import ac.grim.grimac.player.GrimPlayer;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.player.DiggingAction;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerDigging;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerFlying;

// Detects sending BLOCK_PLACEMENT and RELEASE_USE_ITEM in the same tick
@CheckData(name = "AutoBlock B")
public class AutoBlockB extends PacketCheck {

    public boolean sent;

    public AutoBlockB(GrimPlayer player) {
        super(player);
    }

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        if (event.getPacketType() == PacketType.Play.Client.PLAYER_BLOCK_PLACEMENT) {
            sent = true;

        } else if (WrapperPlayClientPlayerFlying.isFlying(event.getPacketType())) {
            sent = false;

        } else if (event.getPacketType() == PacketType.Play.Client.PLAYER_DIGGING) {
            WrapperPlayClientPlayerDigging packet = new WrapperPlayClientPlayerDigging(event);

            if (sent && packet.getAction() == DiggingAction.RELEASE_USE_ITEM
                    && !player.packetStateData.lastPacketWasTeleport
                    && !player.compensatedEntities.getSelf().inVehicle()) {
                flagAndAlert("", false);
            }
        }
    }
}
