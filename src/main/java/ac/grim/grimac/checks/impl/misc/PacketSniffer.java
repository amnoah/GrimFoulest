package ac.grim.grimac.checks.impl.misc;

import ac.grim.grimac.checks.impl.aim.processor.AimProcessor;
import ac.grim.grimac.checks.type.PacketCheck;
import ac.grim.grimac.player.GrimPlayer;
import ac.grim.grimac.utils.anticheat.LogUtil;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.handshaking.client.WrapperHandshakingClientHandshake;
import com.github.retrooper.packetevents.wrapper.login.client.WrapperLoginClientEncryptionResponse;
import com.github.retrooper.packetevents.wrapper.login.client.WrapperLoginClientLoginStart;
import com.github.retrooper.packetevents.wrapper.login.client.WrapperLoginClientPluginResponse;
import com.github.retrooper.packetevents.wrapper.play.client.*;
import com.github.retrooper.packetevents.wrapper.play.server.*;
import com.github.retrooper.packetevents.wrapper.status.client.WrapperStatusClientPing;
import com.github.retrooper.packetevents.wrapper.status.client.WrapperStatusClientRequest;
import com.github.retrooper.packetevents.wrapper.status.server.WrapperStatusServerPong;
import com.github.retrooper.packetevents.wrapper.status.server.WrapperStatusServerResponse;
import org.bukkit.Bukkit;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;

// Basic packet sniffer for debugging purposes
@SuppressWarnings("ConstantConditions")
public class PacketSniffer extends PacketCheck {

    public static boolean sniffingIncoming;
    public static boolean sniffingOutgoing;
    public static boolean sniffingFlying;
    public static boolean sniffingWindowConfirmation;
    public static boolean sniffingResourcePackStatus;

    public PacketSniffer(GrimPlayer player) {
        super(player);
    }

    @Override
    public void onPacketSend(PacketSendEvent event) {
        if (!sniffingOutgoing) {
            return;
        }

        if (event.getPacketType() == PacketType.Play.Server.ACKNOWLEDGE_BLOCK_CHANGES) {
            WrapperPlayServerAcknowledgeBlockChanges packet = new WrapperPlayServerAcknowledgeBlockChanges(event);

            LogUtil.info("[SERVER]"
                    + " [ACKNOWLEDGE_BLOCK_CHANGES]"
                    + " SEQUENCE=" + packet.getSequence());

        } else if (event.getPacketType() == PacketType.Play.Server.ACKNOWLEDGE_PLAYER_DIGGING) {
            WrapperPlayServerAcknowledgePlayerDigging packet = new WrapperPlayServerAcknowledgePlayerDigging(event);

            LogUtil.info("[SERVER]"
                    + " [ACKNOWLEDGE_PLAYER_DIGGING]"
                    + " SUCCESSFUL=" + packet.isSuccessful()
                    + ", ACTION=" + packet.getAction()
                    + ", BLOCK_ID=" + packet.getBlockId()
                    + ", BLOCK_POS=" + packet.getBlockPosition());

        } else if (event.getPacketType() == PacketType.Play.Server.ATTACH_ENTITY) {
            WrapperPlayServerAttachEntity packet = new WrapperPlayServerAttachEntity(event);

            LogUtil.info("[SERVER]"
                    + " [ATTACH_ENTITY]"
                    + " LEASH=" + packet.isLeash()
                    + ", ATTACHED_ID=" + packet.getAttachedId()
                    + ", HOLDING_ID=" + packet.getHoldingId());

        } else if (event.getPacketType() == PacketType.Play.Server.BLOCK_ACTION) {
            WrapperPlayServerBlockAction packet = new WrapperPlayServerBlockAction(event);

            LogUtil.info("[SERVER]"
                    + " [BLOCK_ACTION]"
                    + " ACTION_ID=" + packet.getActionId()
                    + ", ACTION_DATA=" + packet.getActionData()
                    + ", BLOCK_TYPE=" + packet.getBlockType()
                    + ", BLOCK_TYPE_ID=" + packet.getBlockTypeId()
                    + ", BLOCK_POS=" + packet.getBlockPosition());

        } else if (event.getPacketType() == PacketType.Play.Server.BLOCK_BREAK_ANIMATION) {
            WrapperPlayServerBlockBreakAnimation packet = new WrapperPlayServerBlockBreakAnimation(event);

            LogUtil.info("[SERVER]"
                    + " [BLOCK_BREAK_ANIMATION]"
                    + " ENTITY_ID=" + packet.getEntityId()
                    + ", DESTROY_STAGE=" + packet.getDestroyStage()
                    + ", BLOCK_POS=" + packet.getBlockPosition());

        } else if (event.getPacketType() == PacketType.Play.Server.BLOCK_CHANGE) {
            WrapperPlayServerBlockChange packet = new WrapperPlayServerBlockChange(event);

            LogUtil.info("[SERVER]"
                    + " [BLOCK_CHANGE]"
                    + " BLOCK_ID=" + packet.getBlockId()
                    + ", BLOCK_STATE=" + packet.getBlockState()
                    + ", BLOCK_POS=" + packet.getBlockPosition());

        } else if (event.getPacketType() == PacketType.Play.Server.CAMERA) {
            WrapperPlayServerCamera packet = new WrapperPlayServerCamera(event);

            LogUtil.info("[SERVER]"
                    + " [CAMERA]"
                    + " CAMERA_ID=" + packet.getCameraId());

        } else if (event.getPacketType() == PacketType.Play.Server.CHAT_MESSAGE) {
            WrapperPlayServerChatMessage packet = new WrapperPlayServerChatMessage(event);

            // TODO: Print optional data
            LogUtil.info("[SERVER]"
                    + " [CHAT_MESSAGE]"
                    + " CHAT_TYPE=" + packet.getType()
                    + ", CHAT_CONTENT=" + packet.getChatContent()
                    + ", SENDER=" + packet.getSender());

        } else if (event.getPacketType() == PacketType.Play.Server.CLOSE_WINDOW) {
            WrapperPlayServerCloseWindow packet = new WrapperPlayServerCloseWindow(event);

            LogUtil.info("[SERVER]"
                    + " [CLOSE_WINDOW]"
                    + " WINDOW_ID=" + packet.getWindowId());

        } else if (event.getPacketType() == PacketType.Play.Server.CHAT_PREVIEW_PACKET) {
            WrapperPlayServerChatPreview packet = new WrapperPlayServerChatPreview(event);

            LogUtil.info("[SERVER]"
                    + " [CHAT_PREVIEW]"
                    + " MESSAGE=" + packet.getMessage()
                    + ", QUERY_ID=" + packet.getQueryID());

        } else if (event.getPacketType() == PacketType.Play.Server.CHANGE_GAME_STATE) {
            WrapperPlayServerChangeGameState packet = new WrapperPlayServerChangeGameState(event);

            LogUtil.info("[SERVER]"
                    + " [CHAT_PREVIEW]"
                    + " REASON=" + packet.getReason()
                    + ", VALUE=" + packet.getValue());

        } else if (event.getPacketType() == PacketType.Play.Server.CLEAR_TITLES) {
            WrapperPlayServerClearTitles packet = new WrapperPlayServerClearTitles(event);

            LogUtil.info("[SERVER]"
                    + " [CLEAR_TITLES]"
                    + " RESET=" + packet.getReset());

        } else if (event.getPacketType() == PacketType.Play.Server.COLLECT_ITEM) {
            WrapperPlayServerCollectItem packet = new WrapperPlayServerCollectItem(event);

            LogUtil.info("[SERVER]"
                    + " [COLLECT_ITEM]"
                    + " ITEM_ID=" + packet.getCollectedEntityId()
                    + ", COLLECTOR_ID=" + packet.getCollectorEntityId()
                    + ", ITEM_COUNT=" + packet.getPickupItemCount());

        } else if (event.getPacketType() == PacketType.Play.Server.DECLARE_RECIPES) {
            WrapperPlayServerDeclareRecipes packet = new WrapperPlayServerDeclareRecipes(event);

            LogUtil.info("[SERVER]"
                    + " [DECLARE_RECIPES]"
                    + " RECIPES=" + Arrays.toString(packet.getRecipes()));

        } else if (event.getPacketType() == PacketType.Play.Server.DISCONNECT) {
            WrapperPlayServerDisconnect packet = new WrapperPlayServerDisconnect(event);

            LogUtil.info("[SERVER]"
                    + " [DISCONNECT]"
                    + " REASON=" + packet.getReason());

        } else if (event.getPacketType() == PacketType.Play.Server.DISCONNECT) {
            WrapperPlayServerDisconnect packet = new WrapperPlayServerDisconnect(event);

            LogUtil.info("[SERVER]"
                    + " [DISCONNECT]"
                    + " REASON=" + packet.getReason());

//        } else if (event.getPacketType() == PacketType.Play.Server.DESTROY_ENTITIES) {
//            WrapperPlayServerDestroyEntities packet = new WrapperPlayServerDestroyEntities(event);
//
//            LogUtil.info("[SERVER]"
//                    + " [DESTROY_ENTITIES]"
//                    + " ENTITY_IDS=" + Arrays.toString(packet.getEntityIds()));

        } else if (event.getPacketType() == PacketType.Play.Server.DISPLAY_CHAT_PREVIEW) {
            WrapperPlayServerSetDisplayChatPreview packet = new WrapperPlayServerSetDisplayChatPreview(event);

            LogUtil.info("[SERVER]"
                    + " [DISPLAY_CHAT_PREVIEW]"
                    + " STATE=" + packet.isChatPreviewDisplay());

        } else if (event.getPacketType() == PacketType.Play.Server.DISPLAY_SCOREBOARD) {
            WrapperPlayServerDisplayScoreboard packet = new WrapperPlayServerDisplayScoreboard(event);

            LogUtil.info("[SERVER]"
                    + " [DISPLAY_SCOREBOARD]"
                    + " POSITION=" + packet.getPosition()
                    + ", SCORE_NAME=" + packet.getScoreName());

        } else if (event.getPacketType() == PacketType.Play.Server.ENTITY_ANIMATION) {
            WrapperPlayServerEntityAnimation packet = new WrapperPlayServerEntityAnimation(event);

            LogUtil.info("[SERVER]"
                    + " [ENTITY_ANIMATION]"
                    + " ENTITY_ID=" + packet.getEntityId()
                    + ", TYPE=" + packet.getType());

        } else if (event.getPacketType() == PacketType.Play.Server.ENTITY_EFFECT) {
            WrapperPlayServerEntityEffect packet = new WrapperPlayServerEntityEffect(event);

            LogUtil.info("[SERVER]"
                    + " [ENTITY_EFFECT]"
                    + " ENTITY_ID=" + packet.getEntityId()
                    + ", AMBIENT=" + packet.isAmbient()
                    + ", SHOW_ICON=" + packet.isShowIcon()
                    + ", VISIBLE=" + packet.isVisible()
                    + ", AMPLIFIER=" + packet.getEffectAmplifier()
                    + ", DURATION=" + packet.getEffectDurationTicks()
                    + ", FACTOR_DATA=" + packet.getFactorData()
                    + ", POTION_TYPE=" + packet.getPotionType());

        } else if (event.getPacketType() == PacketType.Play.Server.ENTITY_EQUIPMENT) {
            WrapperPlayServerEntityEquipment packet = new WrapperPlayServerEntityEquipment(event);

            LogUtil.info("[SERVER]"
                    + " [ENTITY_EQUIPMENT]"
                    + " ENTITY_ID=" + packet.getEntityId()
                    + ", EQUIPMENT=" + packet.getEquipment());

        } else if (event.getPacketType() == PacketType.Play.Server.ENTITY_HEAD_LOOK) {
            WrapperPlayServerEntityHeadLook packet = new WrapperPlayServerEntityHeadLook(event);

            LogUtil.info("[SERVER]"
                    + " [ENTITY_HEAD_LOOK]"
                    + " ENTITY_ID=" + packet.getEntityId()
                    + ", YAW=" + packet.getHeadYaw());

        } else if (event.getPacketType() == PacketType.Play.Server.ENTITY_MOVEMENT) {
            WrapperPlayServerEntityMovement packet = new WrapperPlayServerEntityMovement(event);

            LogUtil.info("[SERVER]"
                    + " [ENTITY_MOVEMENT]"
                    + " ENTITY_ID=" + packet.getEntityId());

        } else if (event.getPacketType() == PacketType.Play.Server.ENTITY_RELATIVE_MOVE) {
            WrapperPlayServerEntityRelativeMove packet = new WrapperPlayServerEntityRelativeMove(event);

            LogUtil.info("[SERVER]"
                    + " [ENTITY_RELATIVE_MOVE]"
                    + " ENTITY_ID=" + packet.getEntityId()
                    + ", ON_GROUND=" + packet.isOnGround()
                    + ", DELTA_X=" + packet.getDeltaX()
                    + ", DELTA_Y=" + packet.getDeltaY()
                    + ", DELTA_Z=" + packet.getDeltaZ());

        } else if (event.getPacketType() == PacketType.Play.Server.ENTITY_RELATIVE_MOVE_AND_ROTATION) {
            WrapperPlayServerEntityRelativeMoveAndRotation packet = new WrapperPlayServerEntityRelativeMoveAndRotation(event);

            LogUtil.info("[SERVER]"
                    + " [ENTITY_RELATIVE_MOVE_AND_ROTATION]"
                    + " ENTITY_ID=" + packet.getEntityId()
                    + ", ON_GROUND=" + packet.isOnGround()
                    + ", DELTA_X=" + packet.getDeltaX()
                    + ", DELTA_Y=" + packet.getDeltaY()
                    + ", DELTA_Z=" + packet.getDeltaZ()
                    + ", YAW=" + packet.getYaw()
                    + ", PITCH=" + packet.getPitch());

        } else if (event.getPacketType() == PacketType.Play.Server.ENTITY_SOUND_EFFECT) {
            WrapperPlayServerEntitySoundEffect packet = new WrapperPlayServerEntitySoundEffect(event);

            LogUtil.info("[SERVER]"
                    + " [ENTITY_SOUND_EFFECT]"
                    + " ENTITY_ID=" + packet.getEntityId()
                    + ", PITCH=" + packet.getPitch()
                    + ", SOUND_CATEGORY=" + packet.getSoundCategory()
                    + ", SOUND_ID=" + packet.getSoundId()
                    + ", VOLUME=" + packet.getVolume()
                    + ", PITCH=" + packet.getPitch());

        } else if (event.getPacketType() == PacketType.Play.Server.ENTITY_STATUS) {
            WrapperPlayServerEntityStatus packet = new WrapperPlayServerEntityStatus(event);

            LogUtil.info("[SERVER]"
                    + " [ENTITY_STATUS]"
                    + " ENTITY_ID=" + packet.getEntityId()
                    + ", STATUS=" + packet.getStatus());

        } else if (event.getPacketType() == PacketType.Play.Server.FACE_PLAYER) {
            WrapperPlayServerFacePlayer packet = new WrapperPlayServerFacePlayer(event);

            LogUtil.info("[SERVER]"
                    + " [ENTITY_STATUS]"
                    + " TARGET=" + packet.getTargetEntity()
                    + " AIM_UNIT=" + packet.getAimUnit()
                    + ", TARGET_POS=" + packet.getTargetPosition());

        } else if (event.getPacketType() == PacketType.Play.Server.HELD_ITEM_CHANGE) {
            WrapperPlayServerHeldItemChange packet = new WrapperPlayServerHeldItemChange(event);

            LogUtil.info("[SERVER]"
                    + " [HELD_ITEM_CHANGE]"
                    + " SLOT=" + packet.getSlot());

        } else if (event.getPacketType() == PacketType.Play.Server.KEEP_ALIVE) {
            WrapperPlayServerKeepAlive packet = new WrapperPlayServerKeepAlive(event);

            LogUtil.info("[SERVER]"
                    + " [KEEP_ALIVE]"
                    + " ID=" + packet.getId());

        } else if (event.getPacketType() == PacketType.Play.Server.MULTI_BLOCK_CHANGE) {
            WrapperPlayServerMultiBlockChange packet = new WrapperPlayServerMultiBlockChange(event);

            LogUtil.info("[SERVER]"
                    + " [MULTI_BLOCK_CHANGE]"
                    + " BLOCKS=" + Arrays.toString(packet.getBlocks())
                    + ", CHUNK_POS=" + packet.getChunkPosition()
                    + ", TRUST_EDGES=" + packet.getTrustEdges());

        } else if (event.getPacketType() == PacketType.Play.Server.NBT_QUERY_RESPONSE) {
            WrapperPlayServerNBTQueryResponse packet = new WrapperPlayServerNBTQueryResponse(event);

            LogUtil.info("[SERVER]"
                    + " [NBT_QUERY_RESPONSE]"
                    + " ID=" + packet.getTransactionId()
                    + ", TAG=" + packet.getTag());

        } else if (event.getPacketType() == PacketType.Play.Server.OPEN_BOOK) {
            WrapperPlayServerOpenBook packet = new WrapperPlayServerOpenBook(event);

            LogUtil.info("[SERVER]"
                    + " [OPEN_BOOK]"
                    + " HAND=" + packet.getHand());

        } else if (event.getPacketType() == PacketType.Play.Server.OPEN_HORSE_WINDOW) {
            WrapperPlayServerOpenHorseWindow packet = new WrapperPlayServerOpenHorseWindow(event);

            LogUtil.info("[SERVER]"
                    + " [OPEN_HORSE_WINDOW]"
                    + " ENTITY_ID=" + packet.getEntityId()
                    + ", WINDOW_ID=" + packet.getWindowId()
                    + ", SLOT_COUNT=" + packet.getSlotCount());

        } else if (event.getPacketType() == PacketType.Play.Server.OPEN_SIGN_EDITOR) {
            WrapperPlayServerOpenSignEditor packet = new WrapperPlayServerOpenSignEditor(event);

            LogUtil.info("[SERVER]"
                    + " [OPEN_SIGN_EDITOR]"
                    + " POSITION=" + packet.getPosition());

        } else if (event.getPacketType() == PacketType.Play.Server.OPEN_WINDOW) {
            WrapperPlayServerOpenWindow packet = new WrapperPlayServerOpenWindow(event);

            LogUtil.info("[SERVER]"
                    + " [OPEN_WINDOW]"
                    + " USING_PROVIDED=" + packet.isUseProvidedWindowTitle()
                    + ", TITLE=" + packet.getTitle()
                    + ", TYPE=" + packet.getType()
                    + ", CONTAINER_ID=" + packet.getContainerId()
                    + ", HORSE_ID=" + packet.getHorseId()
                    + ", LEGACY_TYPE=" + packet.getLegacyType()
                    + ", LEGACY_SLOTS=" + packet.getLegacySlots());

        } else if (event.getPacketType() == PacketType.Play.Server.PLAYER_POSITION_AND_LOOK) {
            WrapperPlayServerPlayerPositionAndLook packet = new WrapperPlayServerPlayerPositionAndLook(event);

            LogUtil.info("[SERVER]"
                    + " [PLAYER_POSITION_AND_LOOK]"
                    + " DISMOUNT=" + packet.isDismountVehicle()
                    + ", X=" + packet.getX()
                    + ", Y=" + packet.getY()
                    + ", Z=" + packet.getZ()
                    + ", YAW=" + packet.getYaw()
                    + ", PITCH=" + packet.getPitch()
                    + ", RELATIVE_FLAGS=" + packet.getRelativeFlags()
                    + ", RELATIVE_MASK=" + packet.getRelativeMask()
                    + ", TELEPORT_ID=" + packet.getTeleportId());

        } else if (event.getPacketType() == PacketType.Play.Server.PLAYER_ABILITIES) {
            WrapperPlayServerPlayerAbilities packet = new WrapperPlayServerPlayerAbilities(event);

            LogUtil.info("[SERVER]"
                    + " [PLAYER_ABILITIES]"
                    + " CREATIVE=" + packet.isInCreativeMode()
                    + ", GOD_MODE=" + packet.isInGodMode()
                    + ", FLIGHT_ALLOWED=" + packet.isFlightAllowed()
                    + ", FLYING=" + packet.isFlying()
                    + ", FLY_SPEED=" + packet.getFlySpeed()
                    + ", FOV_MODIFIER=" + packet.getFOVModifier());

        } else if (event.getPacketType() == PacketType.Play.Server.PLUGIN_MESSAGE) {
            WrapperPlayServerPluginMessage packet = new WrapperPlayServerPluginMessage(event);

            LogUtil.info("[SERVER]"
                    + " [PLUGIN_MESSAGE]"
                    + " CHANNEL_NAME=" + packet.getChannelName()
                    + ", DATA=" + new String(packet.getData(), StandardCharsets.UTF_8));

        } else if (event.getPacketType() == PacketType.Play.Server.PARTICLE) {
            WrapperPlayServerParticle packet = new WrapperPlayServerParticle(event);

            LogUtil.info("[SERVER]"
                    + " [PARTICLE]"
                    + " LONG_DISTANCE=" + packet.isLongDistance()
                    + ", LEGACY_DATA=" + Arrays.toString(packet.getLegacyData())
                    + ", PARTICLE_DATA=" + packet.getParticleData()
                    + ", PARTICLE=" + packet.getParticle()
                    + ", POSITION=" + packet.getPosition()
                    + ", OFFSET=" + packet.getOffset()
                    + ", PARTICLE_COUNT=" + packet.getParticleCount());

        } else if (event.getPacketType() == PacketType.Play.Server.PING) {
            WrapperPlayServerPing packet = new WrapperPlayServerPing(event);

            LogUtil.info("[SERVER]"
                    + " [PING]"
                    + " ID=" + packet.getId());

        } else if (event.getPacketType() == PacketType.Play.Server.REMOVE_ENTITY_EFFECT) {
            WrapperPlayServerRemoveEntityEffect packet = new WrapperPlayServerRemoveEntityEffect(event);

            LogUtil.info("[SERVER]"
                    + " [REMOVE_ENTITY_EFFECT]"
                    + " ENTITY_ID=" + packet.getEntityId()
                    + ", POTION_TYPE=" + packet.getPotionType());

        } else if (event.getPacketType() == PacketType.Play.Server.RESOURCE_PACK_SEND) {
            WrapperPlayServerResourcePackSend packet = new WrapperPlayServerResourcePackSend(event);

            LogUtil.info("[SERVER]"
                    + " [RESOURCE_PACK_SEND]"
                    + " REQUIRED=" + packet.isRequired()
                    + ", HASH=" + packet.getHash()
                    + ", URL=" + packet.getUrl()
                    + ", PROMPT=" + packet.getPrompt());

        } else if (event.getPacketType() == PacketType.Play.Server.RESPAWN) {
            WrapperPlayServerRespawn packet = new WrapperPlayServerRespawn(event);

            LogUtil.info("[SERVER]"
                    + " [RESPAWN]"
                    + " KEEPING_DATA=" + packet.isKeepingAllPlayerData()
                    + ", WORLD_NAME=" + packet.getWorldName());

        } else if (event.getPacketType() == PacketType.Play.Server.SCOREBOARD_OBJECTIVE) {
            WrapperPlayServerScoreboardObjective packet = new WrapperPlayServerScoreboardObjective(event);

            LogUtil.info("[SERVER]"
                    + " [SCOREBOARD_OBJECTIVE]"
                    + " NAME=" + packet.getName()
                    + ", MODE=" + packet.getMode()
                    + ", DISPLAY_NAME=" + packet.getDisplayName()
                    + ", RENDER_TYPE=" + packet.getRenderType());

        } else if (event.getPacketType() == PacketType.Play.Server.SERVER_DATA) {
            WrapperPlayServerServerData packet = new WrapperPlayServerServerData(event);

            LogUtil.info("[SERVER]"
                    + " [SERVER_DATA]"
                    + " PREVIEWS_CHAT=" + packet.isPreviewsChat());
        }
    }

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        if (!sniffingIncoming) {
            return;
        }

        AimProcessor aimProcessor = player.checkManager.getRotationCheck(AimProcessor.class);
        String hSens = String.valueOf((int) Math.round(aimProcessor.sensitivityX * 200));
        String vSens = String.valueOf((int) Math.round(aimProcessor.sensitivityY * 200));

        if (event.getPacketType() == PacketType.Handshaking.Client.HANDSHAKE) {
            WrapperHandshakingClientHandshake packet = new WrapperHandshakingClientHandshake(event);

            LogUtil.info("[HANDSHAKE]"
                    + " ADDRESS=" + packet.getServerAddress()
                    + ", CLIENT_VERSION=" + packet.getClientVersion()
                    + ", PROTOCOL_VERSION=" + packet.getProtocolVersion()
                    + ", NEXT_CONNECTION_STATE=" + packet.getNextConnectionState()
                    + ", SERVER_PORT=" + packet.getServerPort());

        } else if (event.getPacketType() == PacketType.Login.Client.LOGIN_START) {
            WrapperLoginClientLoginStart packet = new WrapperLoginClientLoginStart(event);

            LogUtil.info("[LOGIN_START]"
                    + " USERNAME=" + packet.getUsername()
                    + ", SIGNATURE_DATA=" + packet.getSignatureData());

        } else if (event.getPacketType() == PacketType.Login.Client.LOGIN_PLUGIN_RESPONSE) {
            WrapperLoginClientPluginResponse packet = new WrapperLoginClientPluginResponse(event);

            LogUtil.info("[PLUGIN_RESPONSE]"
                    + " SUCCESSFUL=" + packet.isSuccessful()
                    + ", SIGNATURE_DATA=" + new String(packet.getData(), StandardCharsets.UTF_8)
                    + ", MESSAGE_ID=" + packet.getMessageId());

        } else if (event.getPacketType() == PacketType.Login.Client.ENCRYPTION_RESPONSE) {
            WrapperLoginClientEncryptionResponse packet = new WrapperLoginClientEncryptionResponse(event);

            LogUtil.info("[ENCRYPTION_RESPONSE]"
                    + " PACKET_ID=" + packet.getPacketId());

        } else if (event.getPacketType() == PacketType.Status.Client.PING) {
            WrapperStatusClientPing packet = new WrapperStatusClientPing(event);

            LogUtil.info("[CLIENT_PING]"
                    + " TIME=" + packet.getTime());

        } else if (event.getPacketType() == PacketType.Play.Client.ADVANCEMENT_TAB) {
            WrapperPlayClientAdvancementTab packet = new WrapperPlayClientAdvancementTab(event);

            LogUtil.info("[CLIENT]"
                    + " [ADVANCEMENT_TAB]"
                    + " ACTION=" + packet.getAction()
                    + ", TAB_ID=" + packet.getTabId());

        } else if (event.getPacketType() == PacketType.Play.Client.ANIMATION) {
            WrapperPlayClientAnimation packet = new WrapperPlayClientAnimation(event);

            LogUtil.info("[CLIENT]"
                    + " [ANIMATION]"
                    + " HAND=" + packet.getHand());

        } else if (event.getPacketType() == PacketType.Play.Client.CLICK_WINDOW) {
            WrapperPlayClientClickWindow packet = new WrapperPlayClientClickWindow(event);

            LogUtil.info("[CLIENT]"
                    + " [CLICK_WINDOW]"
                    + " BUTTON=" + packet.getButton()
                    + ", SLOT=" + packet.getSlot()
                    + ", WINDOW_ID=" + packet.getWindowId()
                    + ", CLICK_TYPE=" + packet.getWindowClickType()
                    + ", ITEM_STACK=" + packet.getCarriedItemStack());

        } else if (event.getPacketType() == PacketType.Play.Client.CREATIVE_INVENTORY_ACTION) {
            WrapperPlayClientCreativeInventoryAction packet = new WrapperPlayClientCreativeInventoryAction(event);

            LogUtil.info("[CLIENT]"
                    + " [CREATIVE_INVENTORY_ACTION]"
                    + " SLOT=" + packet.getSlot()
                    + ", ITEM_STACK=" + packet.getItemStack());

        } else if (event.getPacketType() == PacketType.Play.Client.CHAT_COMMAND) {
            WrapperPlayClientChatCommand packet = new WrapperPlayClientChatCommand(event);

            LogUtil.info("[CLIENT]"
                    + " [CHAT_COMMAND]"
                    + " COMMAND=" + packet.getCommand()
                    + ", SIGN_DATA=" + packet.getMessageSignData());

        } else if (event.getPacketType() == PacketType.Play.Client.CHAT_MESSAGE) {
            WrapperPlayClientChatMessage packet = new WrapperPlayClientChatMessage(event);

            LogUtil.info("[CLIENT]"
                    + " [CHAT_MESSAGE]"
                    + " MESSAGE=" + packet.getMessage()
                    + (packet.getMessageSignData().isPresent() ? ", SIGN_DATA=" + packet.getMessageSignData().get() : ""));

        } else if (event.getPacketType() == PacketType.Play.Client.CHAT_PREVIEW) {
            WrapperPlayClientChatPreview packet = new WrapperPlayClientChatPreview(event);

            LogUtil.info("[CLIENT]"
                    + " [CHAT_PREVIEW]"
                    + " MESSAGE=" + packet.getMessage()
                    + ", QUERY=" + packet.getQuery());

        } else if (event.getPacketType() == PacketType.Play.Client.CLICK_WINDOW_BUTTON) {
            WrapperPlayClientClickWindowButton packet = new WrapperPlayClientClickWindowButton(event);

            LogUtil.info("[CLIENT]"
                    + " [CLICK_WINDOW_BUTTON]"
                    + " WINDOW_ID=" + packet.getWindowId()
                    + ", BUTTON_ID=" + packet.getButtonId());

        } else if (event.getPacketType() == PacketType.Play.Client.CLIENT_SETTINGS) {
            WrapperPlayClientSettings packet = new WrapperPlayClientSettings(event);

            LogUtil.info("[CLIENT]"
                    + " [CLIENT_SETTINGS]"
                    + " LOCALE=" + packet.getLocale()
                    + ", MAIN_HAND=" + packet.getMainHand()
                    + ", VIEW_DISTANCE=" + packet.getViewDistance()
                    + ", VISIBILITY=" + packet.getVisibility()
                    + ", SKIN_SECTION=" + packet.getVisibleSkinSection().getMask());

        } else if (event.getPacketType() == PacketType.Play.Client.CLIENT_STATUS) {
            WrapperPlayClientClientStatus packet = new WrapperPlayClientClientStatus(event);

            LogUtil.info("[CLIENT]"
                    + " [CLIENT_STATUS]"
                    + " ACTION=" + packet.getAction());

        } else if (event.getPacketType() == PacketType.Play.Client.CLOSE_WINDOW) {
            WrapperPlayClientCloseWindow packet = new WrapperPlayClientCloseWindow(event);

            LogUtil.info("[CLIENT]"
                    + " [CLOSE_WINDOW]"
                    + " WINDOW_ID=" + packet.getWindowId());

        } else if (event.getPacketType() == PacketType.Play.Client.ENTITY_ACTION) {
            WrapperPlayClientEntityAction packet = new WrapperPlayClientEntityAction(event);

            LogUtil.info("[CLIENT]"
                    + " [ENTITY_ACTION]"
                    + " ACTION=" + packet.getAction()
                    + ", ENTITY_ID=" + packet.getEntityId());

        } else if (event.getPacketType() == PacketType.Play.Client.EDIT_BOOK) {
            WrapperPlayClientEditBook packet = new WrapperPlayClientEditBook(event);

            LogUtil.info("[CLIENT]"
                    + " [EDIT_BOOK]"
                    + " SLOT=" + packet.getSlot()
                    + ", TITLE=" + packet.getTitle()
                    + ", PAGES=" + packet.getPages());

        } else if (event.getPacketType() == PacketType.Play.Client.GENERATE_STRUCTURE) {
            WrapperPlayClientGenerateStructure packet = new WrapperPlayClientGenerateStructure(event);

            LogUtil.info("[CLIENT]"
                    + " [GENERATE_STRUCTURE]"
                    + " LEVELS=" + packet.getLevels()
                    + ", BLOCK_POS=" + packet.getBlockPosition());

        } else if (event.getPacketType() == PacketType.Play.Client.HELD_ITEM_CHANGE) {
            WrapperPlayClientHeldItemChange packet = new WrapperPlayClientHeldItemChange(event);

            LogUtil.info("[CLIENT]"
                    + " [HELD_ITEM_CHANGE]"
                    + " SLOT=" + packet.getSlot());

        } else if (event.getPacketType() == PacketType.Play.Client.INTERACT_ENTITY) {
            WrapperPlayClientInteractEntity packet = new WrapperPlayClientInteractEntity(event);

            LogUtil.info("[CLIENT]"
                    + " [INTERACT_ENTITY]"
                    + " ACTION=" + packet.getAction()
                    + ", ENTITY_ID=" + packet.getEntityId()
                    + (packet.getTarget().isPresent() ? ", TARGET=" + packet.getTarget().get() : ""));

        } else if (event.getPacketType() == PacketType.Play.Client.KEEP_ALIVE) {
            WrapperPlayClientKeepAlive packet = new WrapperPlayClientKeepAlive(event);

            LogUtil.info("[CLIENT]"
                    + " [KEEP_ALIVE]"
                    + " ID=" + packet.getId());

        } else if (event.getPacketType() == PacketType.Play.Client.LOCK_DIFFICULTY) {
            LogUtil.info("[CLIENT]"
                    + " [LOCK_DIFFICULTY]");

        } else if (event.getPacketType() == PacketType.Play.Client.NAME_ITEM) {
            WrapperPlayClientNameItem packet = new WrapperPlayClientNameItem(event);

            LogUtil.info("[CLIENT]"
                    + " [NAME_ITEM]"
                    + " ITEM_NAME=" + packet.getItemName());

        } else if (event.getPacketType() == PacketType.Play.Client.PLAYER_BLOCK_PLACEMENT) {
            WrapperPlayClientPlayerBlockPlacement packet = new WrapperPlayClientPlayerBlockPlacement(event);

            // TODO: Grim doesn't log right-clicking the air with an item (fuck you)
            LogUtil.info("[CLIENT]"
                    + " [BLOCK_PLACEMENT]"
                    + " SEQUENCE=" + packet.getSequence()
                    + ", HAND=" + packet.getHand()
                    + ", FACE=" + packet.getFace()
                    + ", CURSOR_POS=" + packet.getCursorPosition()
                    + ", BLOCK_POS=" + packet.getBlockPosition()
                    + (packet.getInsideBlock().isPresent() ? ", INSIDE_BLOCK=" + packet.getInsideBlock().get() : "")
                    + (packet.getItemStack().isPresent() ? ", ITEM_STACK=" + packet.getItemStack().get() : ""));

        } else if (event.getPacketType() == PacketType.Play.Client.PLAYER_DIGGING) {
            WrapperPlayClientPlayerDigging packet = new WrapperPlayClientPlayerDigging(event);

            LogUtil.info("[CLIENT]"
                    + " [DIGGING]"
                    + " ACTION=" + packet.getAction()
                    + ", SEQUENCE=" + packet.getSequence()
                    + ", FACE=" + packet.getFace()
                    + ", BLOCK_FACE=" + packet.getBlockFace()
                    + ", BLOCK_POS=" + packet.getBlockPosition());

        } else if (event.getPacketType() == PacketType.Play.Client.PLAYER_ABILITIES) {
            WrapperPlayClientPlayerAbilities packet = new WrapperPlayClientPlayerAbilities(event);

            LogUtil.info("[CLIENT]"
                    + " [ABILITIES]"
                    + " FLYING=" + packet.isFlying()
                    + (packet.getFlySpeed().isPresent() ? ", FLY_SPEED=" + packet.getFlySpeed().get() : "")
                    + (packet.getWalkSpeed().isPresent() ? ", WALK_SPEED=" + packet.getWalkSpeed().get() : "")
                    + (packet.isInCreativeMode().isPresent() ? ", CREATIVE=" + packet.isInCreativeMode().get() : "")
                    + (packet.isInGodMode().isPresent() ? ", GOD_MODE=" + packet.isInGodMode().get() : "")
                    + (packet.isFlightAllowed().isPresent() ? ", FLIGHT_ALLOWED=" + packet.isFlightAllowed().get() : "")
            );

        } else if (event.getPacketType() == PacketType.Play.Client.PLAYER_FLYING) {
            if (!sniffingFlying) {
                return;
            }

            WrapperPlayClientPlayerFlying packet = new WrapperPlayClientPlayerFlying(event);

            LogUtil.info("[CLIENT]"
                    + " [FLYING]"
                    + " LOCATION=" + packet.getLocation()
                    + ", ON_GROUND=" + packet.isOnGround());

        } else if (event.getPacketType() == PacketType.Play.Client.PLAYER_POSITION) {
            WrapperPlayClientPlayerPosition packet = new WrapperPlayClientPlayerPosition(event);

            LogUtil.info("[CLIENT]"
                    + " [POSITION]"
                    + " LOCATION=" + packet.getLocation()
                    + ", ON_GROUND=" + packet.isOnGround());

        } else if (event.getPacketType() == PacketType.Play.Client.PLAYER_POSITION_AND_ROTATION) {
            WrapperPlayClientPlayerPositionAndRotation packet = new WrapperPlayClientPlayerPositionAndRotation(event);

            LogUtil.info("[CLIENT]"
                    + " [POSITION_AND_ROTATION]"
                    + " LOCATION=" + packet.getLocation()
                    + ", ON_GROUND=" + packet.isOnGround()
                    + ", H_SENS=" + hSens
                    + ", V_SENS=" + vSens);

        } else if (event.getPacketType() == PacketType.Play.Client.PLAYER_ROTATION) {
            WrapperPlayClientPlayerRotation packet = new WrapperPlayClientPlayerRotation(event);

            LogUtil.info("[CLIENT]"
                    + " [ROTATION]"
                    + " LOCATION=" + packet.getLocation()
                    + ", ON_GROUND=" + packet.isOnGround()
                    + ", H_SENS=" + hSens
                    + ", V_SENS=" + vSens);

        } else if (event.getPacketType() == PacketType.Play.Client.PICK_ITEM) {
            WrapperPlayClientPickItem packet = new WrapperPlayClientPickItem(event);

            LogUtil.info("[CLIENT]"
                    + " [PICK_ITEM]"
                    + " SLOT=" + packet.getSlot());

        } else if (event.getPacketType() == PacketType.Play.Client.PLUGIN_MESSAGE) {
            WrapperPlayClientPluginMessage packet = new WrapperPlayClientPluginMessage(event);

            LogUtil.info("[CLIENT]"
                    + " [PLUGIN_MESSAGE]"
                    + " CHANNEL=" + packet.getChannelName()
                    + ", DATA=" + new String(packet.getData(), StandardCharsets.UTF_8));

        } else if (event.getPacketType() == PacketType.Play.Client.PONG) {
            WrapperPlayClientPong packet = new WrapperPlayClientPong(event);

            LogUtil.info("[CLIENT]"
                    + " [PONG]"
                    + " ID=" + packet.getId());

        } else if (event.getPacketType() == PacketType.Play.Client.QUERY_BLOCK_NBT) {
            WrapperPlayClientQueryBlockNBT packet = new WrapperPlayClientQueryBlockNBT(event);

            LogUtil.info("[CLIENT]"
                    + " [QUERY_BLOCK_NBT]"
                    + " ID=" + packet.getTransactionId()
                    + ", BLOCK_POS=" + packet.getBlockPosition());

        } else if (event.getPacketType() == PacketType.Play.Client.QUERY_ENTITY_NBT) {
            WrapperPlayClientQueryEntityNBT packet = new WrapperPlayClientQueryEntityNBT(event);

            LogUtil.info("[CLIENT]"
                    + " [QUERY_ENTITY_NBT]"
                    + " ID=" + packet.getTransactionId()
                    + ", ENTITY_ID=" + packet.getEntityId());

        } else if (event.getPacketType() == PacketType.Play.Client.RESOURCE_PACK_STATUS) {
            WrapperPlayClientResourcePackStatus packet = new WrapperPlayClientResourcePackStatus(event);

            LogUtil.info("[CLIENT]"
                    + " [RESOURCE_PACK_STATUS]"
                    + " HASH=" + packet.getHash()
                    + ", RESULT=" + packet.getResult());

        } else if (event.getPacketType() == PacketType.Play.Client.STEER_VEHICLE) {
            WrapperPlayClientSteerVehicle packet = new WrapperPlayClientSteerVehicle(event);

            LogUtil.info("[CLIENT]"
                    + " [STEER_VEHICLE]"
                    + " FORWARD=" + packet.getForward()
                    + ", SIDEWAYS=" + packet.getSideways()
                    + ", FLAGS=" + packet.getFlags()
                    + ", JUMP=" + packet.isJump()
                    + ", UNMOUNT=" + packet.isUnmount());

        } else if (event.getPacketType() == PacketType.Play.Client.SPECTATE) {
            WrapperPlayClientSpectate packet = new WrapperPlayClientSpectate(event);

            LogUtil.info("[CLIENT]"
                    + " [SPECTATE]"
                    + " TARGET=" + packet.getTargetUUID());

        } else if (event.getPacketType() == PacketType.Play.Client.SELECT_TRADE) {
            WrapperPlayClientSelectTrade packet = new WrapperPlayClientSelectTrade(event);

            LogUtil.info("[CLIENT]"
                    + " [SELECT_TRADE]"
                    + " SLOT=" + packet.getSlot());

        } else if (event.getPacketType() == PacketType.Play.Client.SET_DIFFICULTY) {
            WrapperPlayClientSetDifficulty packet = new WrapperPlayClientSetDifficulty(event);

            LogUtil.info("[CLIENT]"
                    + " [SET_DIFFICULTY]"
                    + " DIFFICULTY=" + packet.getDifficulty());

        } else if (event.getPacketType() == PacketType.Play.Client.SET_BEACON_EFFECT) {
            WrapperPlayClientSetBeaconEffect packet = new WrapperPlayClientSetBeaconEffect(event);

            LogUtil.info("[CLIENT]"
                    + " [SET_BEACON_EFFECT]"
                    + " PRIMARY=" + packet.getPrimaryEffect()
                    + ", SECONDARY=" + packet.getSecondaryEffect());

        } else if (event.getPacketType() == PacketType.Play.Client.SET_DISPLAYED_RECIPE) {
            WrapperPlayClientSetDisplayedRecipe packet = new WrapperPlayClientSetDisplayedRecipe(event);

            LogUtil.info("[CLIENT]"
                    + " [SET_DISPLAYED_RECIPE]"
                    + " RECIPE=" + packet.getRecipe());

        } else if (event.getPacketType() == PacketType.Play.Client.SET_RECIPE_BOOK_STATE) {
            WrapperPlayClientSetRecipeBookState packet = new WrapperPlayClientSetRecipeBookState(event);

            LogUtil.info("[CLIENT]"
                    + " [SET_RECIPE_BOOK_STATE]"
                    + " BOOK_TYPE=" + packet.getBookType());

        } else if (event.getPacketType() == PacketType.Play.Client.STEER_BOAT) {
            WrapperPlayClientSteerBoat packet = new WrapperPlayClientSteerBoat(event);

            LogUtil.info("[CLIENT]"
                    + " [STEER_BOAT]"
                    + " LEFT_PADDLE=" + packet.isLeftPaddleTurning()
                    + ", RIGHT_PADDLE=" + packet.isRightPaddleTurning());

        } else if (event.getPacketType() == PacketType.Play.Client.TAB_COMPLETE) {
            WrapperPlayClientTabComplete packet = new WrapperPlayClientTabComplete(event);

            LogUtil.info("[CLIENT]"
                    + " [TAB_COMPLETE]"
                    + " TEXT=" + packet.getText()
                    + (packet.getTransactionId().isPresent() ? ", ID=" + packet.getTransactionId().getAsInt() : "")
                    + (packet.getBlockPosition().isPresent() ? ", BLOCK_POS=" + packet.getBlockPosition().get() : ""));

        } else if (event.getPacketType() == PacketType.Play.Client.TELEPORT_CONFIRM) {
            WrapperPlayClientTeleportConfirm packet = new WrapperPlayClientTeleportConfirm(event);

            LogUtil.info("[CLIENT]"
                    + " [TELEPORT_CONFIRM]"
                    + " ID=" + packet.getTeleportId());

        } else if (event.getPacketType() == PacketType.Play.Client.USE_ITEM) {
            WrapperPlayClientUseItem packet = new WrapperPlayClientUseItem(event);

            LogUtil.info("[CLIENT]"
                    + " [USE_ITEM]"
                    + " HAND=" + packet.getHand()
                    + ", SEQUENCE=" + packet.getSequence());

        } else if (event.getPacketType() == PacketType.Play.Client.UPDATE_COMMAND_BLOCK) {
            WrapperPlayClientUpdateCommandBlock packet = new WrapperPlayClientUpdateCommandBlock(event);

            LogUtil.info("[CLIENT]"
                    + " [UPDATE_COMMAND_BLOCK]"
                    + " AUTOMATIC=" + packet.isAutomatic()
                    + ", CONDITIONAL=" + packet.isConditional()
                    + ", TRACK_OUTPUT=" + packet.isDoesTrackOutput()
                    + ", COMMAND=" + packet.getCommand()
                    + ", FLAGS=" + packet.getFlags()
                    + ", MODE=" + packet.getMode()
                    + ", POSITION=" + packet.getPosition());

        } else if (event.getPacketType() == PacketType.Play.Client.UPDATE_COMMAND_BLOCK_MINECART) {
            WrapperPlayClientUpdateCommandBlockMinecart packet = new WrapperPlayClientUpdateCommandBlockMinecart(event);

            LogUtil.info("[CLIENT]"
                    + " [UPDATE_COMMAND_BLOCK_MINECART]"
                    + " ENTITY_ID=" + packet.getEntityId()
                    + ", TRACK_OUTPUT=" + packet.isTrackOutput()
                    + ", COMMAND=" + packet.getCommand());

        } else if (event.getPacketType() == PacketType.Play.Client.UPDATE_SIGN) {
            WrapperPlayClientUpdateSign packet = new WrapperPlayClientUpdateSign(event);

            LogUtil.info("[CLIENT]"
                    + " [UPDATE_SIGN]"
                    + " LINES=" + Arrays.toString(packet.getTextLines())
                    + ", BLOCK_POS=" + packet.getBlockPosition());

        } else if (event.getPacketType() == PacketType.Play.Client.WINDOW_CONFIRMATION) {
            if (!sniffingWindowConfirmation) {
                return;
            }

            WrapperPlayClientWindowConfirmation packet = new WrapperPlayClientWindowConfirmation(event);

            LogUtil.info("[CLIENT]"
                    + " [WINDOW_CONFIRMATION]"
                    + " WINDOW_ID=" + packet.getWindowId()
                    + ", ACTION_ID=" + packet.getActionId()
                    + ", ACCEPTED=" + packet.isAccepted());

        } else {
            System.out.println(event.getPacketType());
        }
    }
}
