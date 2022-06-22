package ac.grim.grimac.checks.impl.misc;

import ac.grim.grimac.checks.impl.aim.processor.AimProcessor;
import ac.grim.grimac.checks.type.PacketCheck;
import ac.grim.grimac.player.GrimPlayer;
import ac.grim.grimac.utils.anticheat.LogUtil;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.*;
import org.bukkit.Bukkit;

import java.util.Arrays;

// Basic packet sniffer for debugging purposes
@SuppressWarnings("ConstantConditions")
public class PacketSniffer extends PacketCheck {

    public static boolean sniffingIncoming;
    public static boolean sniffingOutgoing;
    public static boolean sniffingFlying;
    public static boolean sniffingWindowConfirmation;

    public PacketSniffer(GrimPlayer player) {
        super(player);
    }

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        if (!sniffingIncoming) {
            return;
        }

        AimProcessor aimProcessor = player.checkManager.getRotationCheck(AimProcessor.class);
        String hSens = String.valueOf((int) Math.round(aimProcessor.sensitivityX * 200));
        String vSens = String.valueOf((int) Math.round(aimProcessor.sensitivityY * 200));

        if (event.getPacketType() == PacketType.Play.Client.ADVANCEMENT_TAB) {
            WrapperPlayClientAdvancementTab packet = new WrapperPlayClientAdvancementTab(event);

            LogUtil.info("(" + Bukkit.getServer().getPlayer(player.playerUUID).getName() + ")"
                    + " [AdvancementTab]"
                    + " ACTION=" + packet.getAction()
                    + ", TAB_ID=" + packet.getTabId());
        }

        if (event.getPacketType() == PacketType.Play.Client.ANIMATION) {
            WrapperPlayClientAnimation packet = new WrapperPlayClientAnimation(event);

            LogUtil.info("(" + Bukkit.getServer().getPlayer(player.playerUUID).getName() + ")"
                    + " [Animation]"
                    + " HAND=" + packet.getHand());
        }

        if (event.getPacketType() == PacketType.Play.Client.CLICK_WINDOW) {
            WrapperPlayClientClickWindow packet = new WrapperPlayClientClickWindow(event);

            LogUtil.info("(" + Bukkit.getServer().getPlayer(player.playerUUID).getName() + ")"
                    + " [ClickWindow]"
                    + " BUTTON=" + packet.getButton()
                    + ", SLOT=" + packet.getSlot()
                    + ", WINDOW_ID=" + packet.getWindowId()
                    + ", CLICK_TYPE=" + packet.getWindowClickType()
                    + ", ITEM_STACK=" + packet.getCarriedItemStack());
        }

        if (event.getPacketType() == PacketType.Play.Client.CREATIVE_INVENTORY_ACTION) {
            WrapperPlayClientCreativeInventoryAction packet = new WrapperPlayClientCreativeInventoryAction(event);

            LogUtil.info("(" + Bukkit.getServer().getPlayer(player.playerUUID).getName() + ")"
                    + " [CreativeInvAction]"
                    + " SLOT=" + packet.getSlot()
                    + ", ITEM_STACK=" + packet.getItemStack());
        }

        if (event.getPacketType() == PacketType.Play.Client.CHAT_COMMAND) {
            WrapperPlayClientChatCommand packet = new WrapperPlayClientChatCommand(event);

            LogUtil.info("(" + Bukkit.getServer().getPlayer(player.playerUUID).getName() + ")"
                    + " [ChatCommand]"
                    + " COMMAND=" + packet.getCommand()
                    + ", SIGN_DATA=" + packet.getMessageSignData());
        }

        if (event.getPacketType() == PacketType.Play.Client.CHAT_MESSAGE) {
            WrapperPlayClientChatMessage packet = new WrapperPlayClientChatMessage(event);

            LogUtil.info("(" + Bukkit.getServer().getPlayer(player.playerUUID).getName() + ")"
                    + " [ChatMessage]"
                    + " MESSAGE=" + packet.getMessage()
                    + (packet.getMessageSignData().isPresent() ? ", SIGN_DATA=" + packet.getMessageSignData().get() : ""));
        }

        if (event.getPacketType() == PacketType.Play.Client.CHAT_PREVIEW) {
            WrapperPlayClientChatPreview packet = new WrapperPlayClientChatPreview(event);

            LogUtil.info("(" + Bukkit.getServer().getPlayer(player.playerUUID).getName() + ")"
                    + " [ChatPreview]"
                    + " MESSAGE=" + packet.getMessage()
                    + ", QUERY=" + packet.getQuery());
        }

        if (event.getPacketType() == PacketType.Play.Client.CLICK_WINDOW_BUTTON) {
            WrapperPlayClientClickWindowButton packet = new WrapperPlayClientClickWindowButton(event);

            LogUtil.info("(" + Bukkit.getServer().getPlayer(player.playerUUID).getName() + ")"
                    + " [ClickWindowButton]"
                    + " WINDOW_ID=" + packet.getWindowId()
                    + ", BUTTON_ID=" + packet.getButtonId());
        }

        if (event.getPacketType() == PacketType.Play.Client.CLIENT_SETTINGS) {
            WrapperPlayClientSettings packet = new WrapperPlayClientSettings(event);

            LogUtil.info("(" + Bukkit.getServer().getPlayer(player.playerUUID).getName() + ")"
                    + " [Settings]"
                    + " LOCALE=" + packet.getLocale()
                    + ", MAIN_HAND=" + packet.getMainHand()
                    + ", VIEW_DISTANCE=" + packet.getViewDistance()
                    + ", VISIBILITY=" + packet.getVisibility()
                    + ", SKIN_SECTION=" + packet.getVisibleSkinSection()
                    + ", SKIN_SECTION_MASK=" + packet.getVisibleSkinSectionMask());
        }

        if (event.getPacketType() == PacketType.Play.Client.CLIENT_STATUS) {
            WrapperPlayClientClientStatus packet = new WrapperPlayClientClientStatus(event);

            LogUtil.info("(" + Bukkit.getServer().getPlayer(player.playerUUID).getName() + ")"
                    + " [ClientStatus]"
                    + " ACTION=" + packet.getAction());
        }

        if (event.getPacketType() == PacketType.Play.Client.CLOSE_WINDOW) {
            WrapperPlayClientCloseWindow packet = new WrapperPlayClientCloseWindow(event);

            LogUtil.info("(" + Bukkit.getServer().getPlayer(player.playerUUID).getName() + ")"
                    + " [CloseWindow]"
                    + " WINDOW_ID=" + packet.getWindowId());
        }

        if (event.getPacketType() == PacketType.Play.Client.ENTITY_ACTION) {
            WrapperPlayClientEntityAction packet = new WrapperPlayClientEntityAction(event);

            LogUtil.info("(" + Bukkit.getServer().getPlayer(player.playerUUID).getName() + ")"
                    + " [EntityAction]"
                    + " ACTION=" + packet.getAction()
                    + ", ENTITY_ID=" + packet.getEntityId());
        }

        if (event.getPacketType() == PacketType.Play.Client.EDIT_BOOK) {
            WrapperPlayClientEditBook packet = new WrapperPlayClientEditBook(event);

            LogUtil.info("(" + Bukkit.getServer().getPlayer(player.playerUUID).getName() + ")"
                    + " [EditBook]"
                    + " SLOT=" + packet.getSlot()
                    + ", TITLE=" + packet.getTitle()
                    + ", PAGES=" + packet.getPages());
        }

        if (event.getPacketType() == PacketType.Play.Client.GENERATE_STRUCTURE) {
            WrapperPlayClientGenerateStructure packet = new WrapperPlayClientGenerateStructure(event);

            LogUtil.info("(" + Bukkit.getServer().getPlayer(player.playerUUID).getName() + ")"
                    + " [GenerateStructure]"
                    + " LEVELS=" + packet.getLevels()
                    + ", BLOCK_POS=" + packet.getBlockPosition());
        }

        if (event.getPacketType() == PacketType.Play.Client.HELD_ITEM_CHANGE) {
            WrapperPlayClientHeldItemChange packet = new WrapperPlayClientHeldItemChange(event);

            LogUtil.info("(" + Bukkit.getServer().getPlayer(player.playerUUID).getName() + ")"
                    + " [HeldItemChange]"
                    + " SLOT=" + packet.getSlot());
        }

        if (event.getPacketType() == PacketType.Play.Client.INTERACT_ENTITY) {
            WrapperPlayClientInteractEntity packet = new WrapperPlayClientInteractEntity(event);

            LogUtil.info("(" + Bukkit.getServer().getPlayer(player.playerUUID).getName() + ")"
                    + " [InteractEntity]"
                    + " ACTION=" + packet.getAction()
                    + ", ENTITY_ID=" + packet.getEntityId()
                    + (packet.getTarget().isPresent() ? ", TARGET=" + packet.getTarget().get() : ""));
        }

        if (event.getPacketType() == PacketType.Play.Client.KEEP_ALIVE) {
            WrapperPlayClientKeepAlive packet = new WrapperPlayClientKeepAlive(event);

            LogUtil.info("(" + Bukkit.getServer().getPlayer(player.playerUUID).getName() + ")"
                    + " [KeepAlive]"
                    + " ID=" + packet.getId());
        }

        if (event.getPacketType() == PacketType.Play.Client.LOCK_DIFFICULTY) {
            LogUtil.info("(" + Bukkit.getServer().getPlayer(player.playerUUID).getName() + ")"
                    + " [LockDifficulty]");
        }

        if (event.getPacketType() == PacketType.Play.Client.NAME_ITEM) {
            WrapperPlayClientNameItem packet = new WrapperPlayClientNameItem(event);

            LogUtil.info("(" + Bukkit.getServer().getPlayer(player.playerUUID).getName() + ")"
                    + " [NameItem]"
                    + " ITEM_NAME=" + packet.getItemName());
        }

        // TODO: Grim doesn't log right-clicking the air with an item (fuck you)
        if (event.getPacketType() == PacketType.Play.Client.PLAYER_BLOCK_PLACEMENT) {
            WrapperPlayClientPlayerBlockPlacement packet = new WrapperPlayClientPlayerBlockPlacement(event);

            LogUtil.info("(" + Bukkit.getServer().getPlayer(player.playerUUID).getName() + ")"
                    + " [BlockPlace]"
                    + " SEQUENCE=" + packet.getSequence()
                    + ", HAND=" + packet.getHand()
                    + ", FACE=" + packet.getFace()
                    + ", CURSOR_POS=" + packet.getCursorPosition()
                    + ", BLOCK_POS=" + packet.getBlockPosition()
                    + (packet.getInsideBlock().isPresent() ? ", INSIDE_BLOCK=" + packet.getInsideBlock().get() : "")
                    + (packet.getItemStack().isPresent() ? ", ITEM_STACK=" + packet.getItemStack().get() : ""));
        }

        if (event.getPacketType() == PacketType.Play.Client.PLAYER_DIGGING) {
            WrapperPlayClientPlayerDigging packet = new WrapperPlayClientPlayerDigging(event);

            LogUtil.info("(" + Bukkit.getServer().getPlayer(player.playerUUID).getName() + ")"
                    + " [BlockDig]"
                    + " ACTION=" + packet.getAction()
                    + ", SEQUENCE=" + packet.getSequence()
                    + ", FACE=" + packet.getFace()
                    + ", BLOCK_FACE=" + packet.getBlockFace()
                    + ", BLOCK_POS=" + packet.getBlockPosition());
        }

        if (event.getPacketType() == PacketType.Play.Client.PLAYER_ABILITIES) {
            WrapperPlayClientPlayerAbilities packet = new WrapperPlayClientPlayerAbilities(event);

            LogUtil.info("(" + Bukkit.getServer().getPlayer(player.playerUUID).getName() + ")"
                    + " [Abilities]"
                    + " FLYING=" + packet.isFlying()
                    + (packet.getFlySpeed().isPresent() ? ", FLY_SPEED=" + packet.getFlySpeed().get() : "")
                    + (packet.getWalkSpeed().isPresent() ? ", WALK_SPEED=" + packet.getWalkSpeed().get() : "")
                    + (packet.isInCreativeMode().isPresent() ? ", CREATIVE=" + packet.isInCreativeMode().get() : "")
                    + (packet.isInGodMode().isPresent() ? ", GOD_MODE=" + packet.isInGodMode().get() : "")
                    + (packet.isFlightAllowed().isPresent() ? ", FLIGHT_ALLOWED=" + packet.isFlightAllowed().get() : "")
            );
        }

        if (event.getPacketType() == PacketType.Play.Client.PLAYER_FLYING) {
            if (!sniffingFlying) {
                return;
            }

            WrapperPlayClientPlayerFlying packet = new WrapperPlayClientPlayerFlying(event);

            LogUtil.info("(" + Bukkit.getServer().getPlayer(player.playerUUID).getName() + ")"
                    + " [Flying]"
                    + " LOCATION=" + packet.getLocation()
                    + ", ON_GROUND=" + packet.isOnGround());
        }

        if (event.getPacketType() == PacketType.Play.Client.PLAYER_POSITION) {
            WrapperPlayClientPlayerPosition packet = new WrapperPlayClientPlayerPosition(event);

            LogUtil.info("(" + Bukkit.getServer().getPlayer(player.playerUUID).getName() + ")"
                    + " [Position]"
                    + " LOCATION=" + packet.getLocation()
                    + ", ON_GROUND=" + packet.isOnGround());
        }

        if (event.getPacketType() == PacketType.Play.Client.PLAYER_POSITION_AND_ROTATION) {
            WrapperPlayClientPlayerPositionAndRotation packet = new WrapperPlayClientPlayerPositionAndRotation(event);

            LogUtil.info("(" + Bukkit.getServer().getPlayer(player.playerUUID).getName() + ")"
                    + " [Position-Rotation]"
                    + " LOCATION=" + packet.getLocation()
                    + ", ON_GROUND=" + packet.isOnGround()
                    + ", H_SENS=" + hSens
                    + ", V_SENS=" + vSens);
        }

        if (event.getPacketType() == PacketType.Play.Client.PLAYER_ROTATION) {
            WrapperPlayClientPlayerRotation packet = new WrapperPlayClientPlayerRotation(event);

            LogUtil.info("(" + Bukkit.getServer().getPlayer(player.playerUUID).getName() + ")"
                    + " [Rotation]"
                    + " LOCATION=" + packet.getLocation()
                    + ", ON_GROUND=" + packet.isOnGround()
                    + ", H_SENS=" + hSens
                    + ", V_SENS=" + vSens);
        }

        if (event.getPacketType() == PacketType.Play.Client.PICK_ITEM) {
            WrapperPlayClientPickItem packet = new WrapperPlayClientPickItem(event);

            LogUtil.info("(" + Bukkit.getServer().getPlayer(player.playerUUID).getName() + ")"
                    + " [PickItem]"
                    + " SLOT=" + packet.getSlot());
        }

        if (event.getPacketType() == PacketType.Play.Client.PLUGIN_MESSAGE) {
            WrapperPlayClientPluginMessage packet = new WrapperPlayClientPluginMessage(event);

            LogUtil.info("(" + Bukkit.getServer().getPlayer(player.playerUUID).getName() + ")"
                    + " [PluginMessage]"
                    + " CHANNEL=" + packet.getChannelName()
                    + ", DATA=" + Arrays.toString(packet.getData()));
        }

        if (event.getPacketType() == PacketType.Play.Client.PONG) {
            WrapperPlayClientPong packet = new WrapperPlayClientPong(event);

            LogUtil.info("(" + Bukkit.getServer().getPlayer(player.playerUUID).getName() + ")"
                    + " [Pong]"
                    + " ID=" + packet.getId());
        }

        if (event.getPacketType() == PacketType.Play.Client.QUERY_BLOCK_NBT) {
            WrapperPlayClientQueryBlockNBT packet = new WrapperPlayClientQueryBlockNBT(event);

            LogUtil.info("(" + Bukkit.getServer().getPlayer(player.playerUUID).getName() + ")"
                    + " [QueryBlockNBT]"
                    + " ID=" + packet.getTransactionId()
                    + ", BLOCK_POS=" + packet.getBlockPosition());
        }

        if (event.getPacketType() == PacketType.Play.Client.QUERY_ENTITY_NBT) {
            WrapperPlayClientQueryEntityNBT packet = new WrapperPlayClientQueryEntityNBT(event);

            LogUtil.info("(" + Bukkit.getServer().getPlayer(player.playerUUID).getName() + ")"
                    + " [QueryEntityNBT]"
                    + " ID=" + packet.getTransactionId()
                    + ", ENTITY_ID=" + packet.getEntityId());
        }

        if (event.getPacketType() == PacketType.Play.Client.RESOURCE_PACK_STATUS) {
            WrapperPlayClientResourcePackStatus packet = new WrapperPlayClientResourcePackStatus(event);

            LogUtil.info("(" + Bukkit.getServer().getPlayer(player.playerUUID).getName() + ")"
                    + " [ResourcePackStatus]"
                    + " HASH=" + packet.getHash()
                    + ", RESULT=" + packet.getResult());
        }

        if (event.getPacketType() == PacketType.Play.Client.STEER_VEHICLE) {
            WrapperPlayClientSteerVehicle packet = new WrapperPlayClientSteerVehicle(event);

            LogUtil.info("(" + Bukkit.getServer().getPlayer(player.playerUUID).getName() + ")"
                    + " [SteerVehicle]"
                    + " FORWARD=" + packet.getForward()
                    + ", SIDEWAYS=" + packet.getSideways()
                    + ", FLAGS=" + packet.getFlags()
                    + ", JUMP=" + packet.isJump()
                    + ", UNMOUNT=" + packet.isUnmount());
        }

        if (event.getPacketType() == PacketType.Play.Client.SPECTATE) {
            WrapperPlayClientSpectate packet = new WrapperPlayClientSpectate(event);

            LogUtil.info("(" + Bukkit.getServer().getPlayer(player.playerUUID).getName() + ")"
                    + " [Spectate]"
                    + " TARGET=" + packet.getTargetUUID());
        }

        if (event.getPacketType() == PacketType.Play.Client.SELECT_TRADE) {
            WrapperPlayClientSelectTrade packet = new WrapperPlayClientSelectTrade(event);

            LogUtil.info("(" + Bukkit.getServer().getPlayer(player.playerUUID).getName() + ")"
                    + " [SelectTrade]"
                    + " SLOT=" + packet.getSlot());
        }

        if (event.getPacketType() == PacketType.Play.Client.SET_DIFFICULTY) {
            WrapperPlayClientSetDifficulty packet = new WrapperPlayClientSetDifficulty(event);

            LogUtil.info("(" + Bukkit.getServer().getPlayer(player.playerUUID).getName() + ")"
                    + " [SetDifficulty]"
                    + " DIFFICULTY=" + packet.getDifficulty());
        }

        if (event.getPacketType() == PacketType.Play.Client.SET_BEACON_EFFECT) {
            WrapperPlayClientSetBeaconEffect packet = new WrapperPlayClientSetBeaconEffect(event);

            LogUtil.info("(" + Bukkit.getServer().getPlayer(player.playerUUID).getName() + ")"
                    + " [SetBeaconEffect]"
                    + " PRIMARY=" + packet.getPrimaryEffect()
                    + ", SECONDARY=" + packet.getSecondaryEffect());
        }

        if (event.getPacketType() == PacketType.Play.Client.SET_DISPLAYED_RECIPE) {
            WrapperPlayClientSetDisplayedRecipe packet = new WrapperPlayClientSetDisplayedRecipe(event);

            LogUtil.info("(" + Bukkit.getServer().getPlayer(player.playerUUID).getName() + ")"
                    + " [SetDisplayedRecipe]"
                    + " RECIPE=" + packet.getRecipe());
        }

        if (event.getPacketType() == PacketType.Play.Client.SET_RECIPE_BOOK_STATE) {
            WrapperPlayClientSetRecipeBookState packet = new WrapperPlayClientSetRecipeBookState(event);

            LogUtil.info("(" + Bukkit.getServer().getPlayer(player.playerUUID).getName() + ")"
                    + " [SetRecipeBookState]"
                    + " BOOK_TYPE=" + packet.getBookType());
        }

        if (event.getPacketType() == PacketType.Play.Client.STEER_BOAT) {
            WrapperPlayClientSteerBoat packet = new WrapperPlayClientSteerBoat(event);

            LogUtil.info("(" + Bukkit.getServer().getPlayer(player.playerUUID).getName() + ")"
                    + " [SteerBoat]"
                    + " LEFT_PADDLE=" + packet.isLeftPaddleTurning()
                    + ", RIGHT_PADDLE=" + packet.isRightPaddleTurning());
        }

        if (event.getPacketType() == PacketType.Play.Client.TAB_COMPLETE) {
            WrapperPlayClientTabComplete packet = new WrapperPlayClientTabComplete(event);

            LogUtil.info("(" + Bukkit.getServer().getPlayer(player.playerUUID).getName() + ")"
                    + " [TabComplete]"
                    + " TEXT=" + packet.getText()
                    + (packet.getTransactionId().isPresent() ? ", ID=" + packet.getTransactionId().getAsInt() : "")
                    + (packet.getBlockPosition().isPresent() ? ", BLOCK_POS=" + packet.getBlockPosition().get() : ""));
        }

        if (event.getPacketType() == PacketType.Play.Client.TELEPORT_CONFIRM) {
            WrapperPlayClientTeleportConfirm packet = new WrapperPlayClientTeleportConfirm(event);

            LogUtil.info("(" + Bukkit.getServer().getPlayer(player.playerUUID).getName() + ")"
                    + " [TeleportConfirm]"
                    + " ID=" + packet.getTeleportId());
        }

        if (event.getPacketType() == PacketType.Play.Client.USE_ITEM) {
            WrapperPlayClientUseItem packet = new WrapperPlayClientUseItem(event);

            LogUtil.info("(" + Bukkit.getServer().getPlayer(player.playerUUID).getName() + ")"
                    + " [UseItem]"
                    + " HAND=" + packet.getHand()
                    + ", SEQUENCE=" + packet.getSequence());
        }

        if (event.getPacketType() == PacketType.Play.Client.UPDATE_COMMAND_BLOCK) {
            WrapperPlayClientUpdateCommandBlock packet = new WrapperPlayClientUpdateCommandBlock(event);

            LogUtil.info("(" + Bukkit.getServer().getPlayer(player.playerUUID).getName() + ")"
                    + " [UpdateCommandBlock]"
                    + " AUTOMATIC=" + packet.isAutomatic()
                    + ", CONDITIONAL=" + packet.isConditional()
                    + ", TRACK_OUTPUT=" + packet.isDoesTrackOutput()
                    + ", COMMAND=" + packet.getCommand()
                    + ", FLAGS=" + packet.getFlags()
                    + ", MODE=" + packet.getMode()
                    + ", POSITION=" + packet.getPosition());
        }

        if (event.getPacketType() == PacketType.Play.Client.UPDATE_COMMAND_BLOCK_MINECART) {
            WrapperPlayClientUpdateCommandBlockMinecart packet = new WrapperPlayClientUpdateCommandBlockMinecart(event);

            LogUtil.info("(" + Bukkit.getServer().getPlayer(player.playerUUID).getName() + ")"
                    + " [UpdateCommandBlock]"
                    + " ENTITY_ID=" + packet.getEntityId()
                    + ", TRACK_OUTPUT=" + packet.isTrackOutput()
                    + ", COMMAND=" + packet.getCommand());
        }

        if (event.getPacketType() == PacketType.Play.Client.UPDATE_SIGN) {
            WrapperPlayClientUpdateSign packet = new WrapperPlayClientUpdateSign(event);

            LogUtil.info("(" + Bukkit.getServer().getPlayer(player.playerUUID).getName() + ")"
                    + " [UpdateSign]"
                    + " LINES=" + Arrays.toString(packet.getTextLines())
                    + ", BLOCK_POS=" + packet.getBlockPosition());
        }

        if (event.getPacketType() == PacketType.Play.Client.WINDOW_CONFIRMATION) {
            if (!sniffingWindowConfirmation) {
                return;
            }

            WrapperPlayClientWindowConfirmation packet = new WrapperPlayClientWindowConfirmation(event);

            LogUtil.info("(" + Bukkit.getServer().getPlayer(player.playerUUID).getName() + ")"
                    + " [WindowConfirmation]"
                    + " WINDOW_ID=" + packet.getWindowId()
                    + ", ACTION_ID=" + packet.getActionId()
                    + ", ACCEPTED=" + packet.isAccepted());
        }
    }
}
