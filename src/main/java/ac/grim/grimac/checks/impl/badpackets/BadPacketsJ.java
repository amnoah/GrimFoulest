package ac.grim.grimac.checks.impl.badpackets;

import ac.grim.grimac.checks.CheckData;
import ac.grim.grimac.checks.type.PacketCheck;
import ac.grim.grimac.player.GrimPlayer;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientEntityAction;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerFlying;

// Detects sending invalid ENTITY_ACTION packets
@CheckData(name = "BadPackets J")
public class BadPacketsJ extends PacketCheck {

    public boolean sentLeaveBed;
    public boolean sentStartSprinting;
    public boolean sentStopSprinting;
    public boolean sentStartSneaking;
    public boolean sentStopSneaking;
    public boolean sentStartHorseJump;
    public boolean sentStopHorseJump;
    public boolean thanksMojang; // Support 1.14+ clients starting on either true or false sprinting, we don't know
    public boolean wasTeleport;

    public BadPacketsJ(GrimPlayer player) {
        super(player);
    }

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        wasTeleport = player.packetStateData.lastPacketWasTeleport || wasTeleport;

        if (event.getPacketType() == PacketType.Play.Client.ENTITY_ACTION) {
            WrapperPlayClientEntityAction packet = new WrapperPlayClientEntityAction(event);

            if (packet.getAction() == WrapperPlayClientEntityAction.Action.LEAVE_BED) {
                if (!player.isInBed) {
                    player.kick(getCheckName(), event, "Leave Bed (Impossible)");
                }

                if (sentLeaveBed) {
                    player.kick(getCheckName(), event, "Leave Bed (Sent)");
                }

                sentLeaveBed = true;

            } else if (packet.getAction() == WrapperPlayClientEntityAction.Action.START_SPRINTING) {
                if (sentStartSprinting) {
                    if (!thanksMojang) {
                        thanksMojang = true;
                        return;
                    }

                    player.kick(getCheckName(), event, "Sprinting (Start)");
                }

                sentStartSprinting = true;
                sentStopSprinting = false;

            } else if (packet.getAction() == WrapperPlayClientEntityAction.Action.STOP_SPRINTING) {
                if (sentStopSprinting) {
                    if (!thanksMojang) {
                        thanksMojang = true;
                        return;
                    }

                    player.kick(getCheckName(), event, "Sprinting (Stop)");
                }

                sentStopSprinting = true;
                sentStartSprinting = false;

            } else if (packet.getAction() == WrapperPlayClientEntityAction.Action.START_SNEAKING) {
                if (sentStartSneaking && !wasTeleport) {
                    player.kick(getCheckName(), event, "Sneaking (Start)");
                }

                sentStartSneaking = true;
                sentStopSneaking = false;

            } else if (packet.getAction() == WrapperPlayClientEntityAction.Action.STOP_SPRINTING) {
                if (sentStopSneaking && !wasTeleport) {
                    player.kick(getCheckName(), event, "Sneaking (Stop)");
                }

                sentStopSneaking = true;
                sentStartSneaking = false;

            } else if (packet.getAction() == WrapperPlayClientEntityAction.Action.START_JUMPING_WITH_HORSE) {
                if (!player.compensatedEntities.getSelf().inVehicle()) {
                    player.kick(getCheckName(), event, "Start Horse Jump (Vehicle)");
                }

                if (sentStartHorseJump) {
                    player.kick(getCheckName(), event, "Horse Jump (Start)");
                }

                sentStartHorseJump = true;
                sentStopHorseJump = false;

            } else if (packet.getAction() == WrapperPlayClientEntityAction.Action.STOP_JUMPING_WITH_HORSE) {
                if (!player.compensatedEntities.getSelf().inVehicle()) {
                    player.kick(getCheckName(), event, "Stop Horse Jump (Vehicle)");
                }

                if (sentStopHorseJump) {
                    player.kick(getCheckName(), event, "Horse Jump (Stop)");
                }

                sentStopHorseJump = true;
                sentStartHorseJump = false;

            } else if (packet.getAction() == WrapperPlayClientEntityAction.Action.OPEN_HORSE_INVENTORY) {
                if (!player.compensatedEntities.getSelf().inVehicle()) {
                    player.kick(getCheckName(), event, "Horse Inventory (Vehicle)");
                }
            }

        } else if (WrapperPlayClientPlayerFlying.isFlying(event.getPacketType())) {
            sentLeaveBed = false;
        }
    }
}
