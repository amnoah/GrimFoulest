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

// Mother of all BadPackets checks
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

            // TODO: May fail on lily-pads for 1.9+ players; need to test
            if (packet.getFace() == BlockFace.UP
                    && packet.getBlockPosition().getX() == 0.0
                    && packet.getBlockPosition().getY() == 0.0
                    && packet.getBlockPosition().getZ() == 0.0) {
                player.kick(getCheckName(), event, "BLOCK_PLACE");
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
                    player.kick(getCheckName(), event, "USE_ITEM");
                    return;
                }
            }
        }

        // Invalid Spectate
        if (event.getPacketType() == PacketType.Play.Client.SPECTATE) {
            if (player.gamemode != GameMode.SPECTATOR) {
                player.kick(getCheckName(), event, "SPECTATE");
                return;
            }
        }

        // Invalid Interact Entity
        if (event.getPacketType() == PacketType.Play.Client.INTERACT_ENTITY) {
            WrapperPlayClientInteractEntity packet = new WrapperPlayClientInteractEntity(event);

            if (packet.getEntityId() == player.entityID) {
                player.kick(getCheckName(), event, "Self Interact");
                return;
            }

            if (player.compensatedEntities.getEntity(packet.getEntityId()) == null) {
                player.kick(getCheckName(), event, "Invalid Entity");
                return;
            }
        }

        // Invalid Steer Vehicle
        if (event.getPacketType() == PacketType.Play.Client.STEER_VEHICLE) {
            WrapperPlayClientSteerVehicle packet = new WrapperPlayClientSteerVehicle(event);
            float forwards = Math.abs(packet.getForward());
            float sideways = Math.abs(packet.getSideways());

            if (!player.compensatedEntities.getSelf().inVehicle()) {
                player.kick(getCheckName(), event, "Not In Vehicle");
                return;
            }

            if (forwards > 0.9800000190734863 || sideways > 0.9800000190734863) {
                player.kick(getCheckName(), event, "Vehicle Steer");
                return;
            }
        }

        // Invalid Tab Complete
        if (event.getPacketType() == PacketType.Play.Client.TAB_COMPLETE) {
            WrapperPlayClientTabComplete packet = new WrapperPlayClientTabComplete(event);
            String message = packet.getText();

            if (message.equals("")) {
                player.kick(getCheckName(), event, "TAB_COMPLETE");
                return;
            }
        }

        // Invalid Creative Slot
        if (event.getPacketType() == PacketType.Play.Client.CREATIVE_INVENTORY_ACTION) {
            if (player.gamemode != GameMode.CREATIVE) {
                player.kick(getCheckName(), event, "Not in Creative");
                return;
            }
        }

        // Invalid Window Click
        if (event.getPacketType() == PacketType.Play.Client.CLICK_WINDOW) {
            WrapperPlayClientClickWindow packet = new WrapperPlayClientClickWindow(event);

            // Player Inventory
            if (packet.getWindowId() == 0) {
                if (packet.getSlot() > 44 || (packet.getSlot() != -999 && packet.getSlot() < 0)) {
                    player.kick(getCheckName(), event, "Window Click (SLOT=" + packet.getSlot() + ")");
                    return;
                }
            }

            switch (packet.getPacketId()) {
                case 0:
                    if (packet.getButton() != 0 && packet.getButton() != 1) {
                        player.kick(getCheckName(), event, "Window Click (0)");
                        return;
                    }
                    break;

                case 1:
                    if (packet.getButton() != 0 && packet.getButton() != 1) {
                        player.kick(getCheckName(), event, "Window Click (1)");
                        return;
                    }
                    break;

                case 2:
                    if (packet.getButton() != 0 && packet.getButton() != 1 && packet.getButton() != 2
                            && packet.getButton() != 8 && packet.getButton() != 40) {
                        player.kick(getCheckName(), event, "Window Click (2)");
                        return;
                    }
                    break;

                case 3:
                    if (packet.getButton() != 2) {
                        player.kick(getCheckName(), event, "Window Click (3)");
                        return;
                    }
                    break;

                case 4:
                    if (packet.getButton() != 0 && packet.getButton() != 1) {
                        player.kick(getCheckName(), event, "Window Click (4)");
                        return;
                    }
                    break;

                case 5:
                    if (packet.getButton() != 0 && packet.getButton() != 1 && packet.getButton() != 2
                            && packet.getButton() != 4 && packet.getButton() != 5 && packet.getButton() != 6
                            && packet.getButton() != 8 && packet.getButton() != 9 && packet.getButton() != 10) {
                        player.kick(getCheckName(), event, "Window Click (5)");
                        return;
                    }
                    break;

                case 6:
                    if (packet.getButton() != 0) {
                        player.kick(getCheckName(), event, "Window Click (6)");
                        return;
                    }
                    break;
            }
        }

        // Invalid Entity Action
        if (event.getPacketType() == PacketType.Play.Client.ENTITY_ACTION) {
            WrapperPlayClientEntityAction packet = new WrapperPlayClientEntityAction(event);

            if (packet.getAction() == WrapperPlayClientEntityAction.Action.LEAVE_BED && !player.isInBed) {
                player.kick(getCheckName(), event, "Entity Action (LEAVE_BED)");
                return;
            }

            if (packet.getAction() == WrapperPlayClientEntityAction.Action.OPEN_HORSE_INVENTORY
                    && !player.compensatedEntities.getSelf().inVehicle()) {
                player.kick(getCheckName(), event, "Entity Action (HORSE_INVENTORY)");
                return;
            }

            if (packet.getAction() == WrapperPlayClientEntityAction.Action.START_JUMPING_WITH_HORSE
                    && !player.compensatedEntities.getSelf().inVehicle()) {
                player.kick(getCheckName(), event, "Entity Action (START_HORSE_JUMP)");
                return;
            }

            if (packet.getAction() == WrapperPlayClientEntityAction.Action.STOP_JUMPING_WITH_HORSE
                    && !player.compensatedEntities.getSelf().inVehicle()) {
                player.kick(getCheckName(), event, "Entity Action (STOP_HORSE_JUMP)");
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
                player.kick(getCheckName(), event, "Abilities (All)");
                return;
            }

            if (isFlying && !player.isFlightAllowed) {
                player.kick(getCheckName(), event, "Abilities (Flight Allowed)");
                return;
            }

            if (player.gamemode != GameMode.CREATIVE) {
                if (isInGodMode != player.isInGodMode) {
                    player.kick(getCheckName(), event, "Abilities (God Mode)");
                    return;
                }

                if (isFlying && !player.isFlying) {
                    player.kick(getCheckName(), event, "Abilities (Flying)");
                    return;
                }

                if (isInCreative && !player.isInCreative) {
                    player.kick(getCheckName(), event, "Abilities (Creative)");
                }
            }
        }
    }
}
