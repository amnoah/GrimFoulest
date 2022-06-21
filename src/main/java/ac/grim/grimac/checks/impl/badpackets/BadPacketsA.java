package ac.grim.grimac.checks.impl.badpackets;

import ac.grim.grimac.checks.CheckData;
import ac.grim.grimac.checks.type.PacketCheck;
import ac.grim.grimac.player.GrimPlayer;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.player.DiggingAction;
import com.github.retrooper.packetevents.protocol.player.GameMode;
import com.github.retrooper.packetevents.protocol.world.BlockFace;
import com.github.retrooper.packetevents.wrapper.play.client.*;

@CheckData(name = "BadPackets A")
public class BadPacketsA extends PacketCheck {

    public BadPacketsA(GrimPlayer player) {
        super(player);
    }

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        // Invalid Block Place
        if (event.getPacketType() == PacketType.Play.Client.PLAYER_BLOCK_PLACEMENT) {
            WrapperPlayClientPlayerBlockPlacement packet = new WrapperPlayClientPlayerBlockPlacement(event);

            // TODO: May fail on lily-pads for 1.9+ players
            if (packet.getFace() == BlockFace.UP
                    && packet.getBlockPosition().getX() == 0.0
                    && packet.getBlockPosition().getY() == 0.0
                    && packet.getBlockPosition().getZ() == 0.0) {
                flagAndAlert("Block Place", false);
                return;
            }
        }

        // Invalid Use Item
        if (event.getPacketType() == PacketType.Play.Client.PLAYER_DIGGING) {
            WrapperPlayClientPlayerDigging packet = new WrapperPlayClientPlayerDigging(event);

            if (packet.getAction() == DiggingAction.RELEASE_USE_ITEM) {
                if (packet.getFace() != BlockFace.DOWN
                        || packet.getBlockPosition().getX() != 0
                        || packet.getBlockPosition().getY() != 0
                        || packet.getBlockPosition().getZ() != 0) {
                    flagAndAlert("Use Item", false);
                }
            }
        }

        // Invalid Spectate
        if (event.getPacketType() == PacketType.Play.Client.SPECTATE) {
            if (player.gamemode != GameMode.SPECTATOR) {
                flagAndAlert("Spectate", false);
            }
        }

        // Invalid Interact Entity
        if (event.getPacketType() == PacketType.Play.Client.INTERACT_ENTITY) {
            WrapperPlayClientInteractEntity packet = new WrapperPlayClientInteractEntity(event);

            if (packet.getEntityId() == player.entityID) {
                flagAndAlert("Self Interact", false);
            }
        }

        // Invalid Steer Vehicle
        if (event.getPacketType() == PacketType.Play.Client.STEER_VEHICLE) {
            WrapperPlayClientSteerVehicle packet = new WrapperPlayClientSteerVehicle(event);
            float forwards = Math.abs(packet.getForward());
            float sideways = Math.abs(packet.getSideways());

            if (!player.compensatedEntities.getSelf().inVehicle()) {
                flagAndAlert("Not in Vehicle", false);
            }

            if (forwards > 0.98f || sideways > 0.98f) {
                flagAndAlert("Vehicle Speed", false);
            }
        }

        // Invalid Tab Complete
        if (event.getPacketType() == PacketType.Play.Client.TAB_COMPLETE) {
            WrapperPlayClientTabComplete packet = new WrapperPlayClientTabComplete(event);
            String message = packet.getText();

            if (message.equals("")) {
                flagAndAlert("Tab Complete", false);
                return;
            }
        }

        // Invalid Creative Slot
        if (event.getPacketType() == PacketType.Play.Client.CREATIVE_INVENTORY_ACTION) {
            // Could be transaction split
            if (player.gamemode != GameMode.CREATIVE) {
                flagAndAlert("Creative Slot", false);
            }
        }

        // Invalid Window Click
        if (event.getPacketType() == PacketType.Play.Client.CLICK_WINDOW) {
            WrapperPlayClientClickWindow packet = new WrapperPlayClientClickWindow(event);

            switch (packet.getPacketId()) {
                case 0:
                    if (packet.getButton() != 0 && packet.getButton() != 1) {
                        flagAndAlert("Window Click (0)", false);
                        return;
                    }
                    break;

                case 1:
                    if (packet.getButton() != 0 && packet.getButton() != 1) {
                        flagAndAlert("Window Click (1)", false);
                        return;
                    }
                    break;

                case 2:
                    if (packet.getButton() != 0 && packet.getButton() != 1 && packet.getButton() != 2
                            && packet.getButton() != 8 && packet.getButton() != 40) {
                        flagAndAlert("Window Click (2)", false);
                        return;
                    }
                    break;

                case 3:
                    if (packet.getButton() != 2) {
                        flagAndAlert("Window Click (3)", false);
                        return;
                    }
                    break;

                case 4:
                    if (packet.getButton() != 0 && packet.getButton() != 1) {
                        flagAndAlert("Window Click (4)", false);
                        return;
                    }
                    break;

                case 5:
                    if (packet.getButton() != 0 && packet.getButton() != 1 && packet.getButton() != 2
                            && packet.getButton() != 4 && packet.getButton() != 5 && packet.getButton() != 6
                            && packet.getButton() != 8 && packet.getButton() != 9 && packet.getButton() != 10) {
                        flagAndAlert("Window Click (5)", false);
                        return;
                    }
                    break;

                case 6:
                    if (packet.getButton() != 0) {
                        flagAndAlert("Window Click (6)", false);
                        return;
                    }
                    break;
            }
        }

        // Invalid Entity Action
        if (event.getPacketType() == PacketType.Play.Client.ENTITY_ACTION) {
            WrapperPlayClientEntityAction packet = new WrapperPlayClientEntityAction(event);

            if (packet.getAction() == WrapperPlayClientEntityAction.Action.LEAVE_BED && !player.isInBed) {
                flagAndAlert("Entity Action (Sleeping)", false);
                return;
            }

            if (packet.getAction() == WrapperPlayClientEntityAction.Action.OPEN_HORSE_INVENTORY
                    && !player.compensatedEntities.getSelf().inVehicle()) {
                flagAndAlert("Entity Action (Horse Inventory)", false);
                return;
            }

            if (packet.getAction() == WrapperPlayClientEntityAction.Action.START_JUMPING_WITH_HORSE
                    && !player.compensatedEntities.getSelf().inVehicle()) {
                flagAndAlert("Entity Action (Horse Start Jump)", false);
                return;
            }

            if (packet.getAction() == WrapperPlayClientEntityAction.Action.STOP_JUMPING_WITH_HORSE
                    && !player.compensatedEntities.getSelf().inVehicle()) {
                flagAndAlert("Entity Action (Horse Stop Jump)", false);
                return;
            }

            if (packet.getAction() == WrapperPlayClientEntityAction.Action.STOP_JUMPING_WITH_HORSE
                    && !player.compensatedEntities.getSelf().inVehicle()) {
                flagAndAlert("Entity Action (Horse Stop Jump)", false);
                return;
            }
        }

        // Invalid Abilities
        if (event.getPacketType() == PacketType.Play.Client.PLAYER_ABILITIES) {
            WrapperPlayClientPlayerAbilities packet = new WrapperPlayClientPlayerAbilities(event);
            boolean isFlying = packet.isFlying();
            boolean isInGodMode = packet.isInGodMode().get();
            boolean isFlightAllowed = packet.isFlightAllowed().get();
            boolean isInCreative = packet.isInCreativeMode().get();

            if (isFlying == player.isFlying && isInGodMode == player.isInGodMode
                    && isFlightAllowed == player.isFlightAllowed && isInCreative == player.isInCreative
                    && !isFlying && !isInGodMode && !isFlightAllowed && !isInCreative) {
                flagAndAlert("Abilities (All)", false);
                return;
            }

            if (isFlying && !player.isFlightAllowed) {
                flagAndAlert("Abilities (Flight Allowed)", false);
                return;
            }

            if (player.gamemode != GameMode.CREATIVE) {
                if (isInGodMode != player.isInGodMode) {
                    flagAndAlert("Abilities (GodMode)", false);
                    return;
                }

                if (isFlying && !player.isFlying) {
                    flagAndAlert("Abilities (Flying)", false);
                    return;
                }

                if (isInCreative && !player.isInCreative) {
                    flagAndAlert("Abilities (Creative)", false);
                }
            }
        }
    }
}
