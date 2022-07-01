// This file was designed and is an original check for GrimAC
// Copyright (C) 2021 DefineOutside
//
// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program.  If not, see <http://www.gnu.org/licenses/>.
package ac.grim.grimac.checks.impl.combat.reach;

import ac.grim.grimac.checks.CheckData;
import ac.grim.grimac.checks.type.PacketCheck;
import ac.grim.grimac.player.GrimPlayer;
import ac.grim.grimac.utils.collisions.datatypes.SimpleCollisionBox;
import ac.grim.grimac.utils.data.packetentity.PacketEntity;
import ac.grim.grimac.utils.math.VectorUtils;
import ac.grim.grimac.utils.nmsutil.ReachUtils;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.entity.type.EntityType;
import com.github.retrooper.packetevents.protocol.entity.type.EntityTypes;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.player.ClientVersion;
import com.github.retrooper.packetevents.protocol.player.GameMode;
import com.github.retrooper.packetevents.util.Vector3d;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientInteractEntity;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerFlying;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

// Detects attacking entities at impossible distances
// Note: You may not copy the check unless you are licensed under GPL.
@CheckData(name = "Reach A", setback = 10)
public class ReachA extends PacketCheck {

    public static final List<EntityType> blacklisted = Arrays.asList(EntityTypes.BOAT, EntityTypes.CHEST_BOAT, EntityTypes.SHULKER);
    // Concurrent to support weird entity trackers
    public final ConcurrentLinkedQueue<Integer> playerAttackQueue = new ConcurrentLinkedQueue<>();
    public boolean cancelImpossibleHits;
    public double threshold;
    public double cancelBuffer; // For the next 4 hits after using reach, we aggressively cancel reach

    public ReachA(GrimPlayer player) {
        super(player);
    }

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        if (!player.disableGrim && event.getPacketType() == PacketType.Play.Client.INTERACT_ENTITY) {
            WrapperPlayClientInteractEntity action = new WrapperPlayClientInteractEntity(event);

            // Don't let the player teleport to bypass reach
            if (player.getSetbackTeleportUtil().shouldBlockMovement()) {
                event.setCancelled(true);
                return;
            }

            PacketEntity entity = player.compensatedEntities.entityMap.get(action.getEntityId());

            // Stop people from freezing transactions before an entity spawns to bypass reach
            if (entity == null) {
                // Only cancel if and only if we are tracking this entity
                // This is because we don't track paintings.
                if (player.compensatedEntities.serverPositionsMap.containsKey(action.getEntityId())) {
                    event.setCancelled(true);
                }
                return;
            }

            // Ignores players in Creative
            if (player.gamemode == GameMode.CREATIVE) {
                return;
            }

            // Ignores players in vehicles
            if (player.compensatedEntities.getSelf().inVehicle()) {
                return;
            }

            // Ignores targets riding an entity (?)
            if (entity.riding != null) {
                return;
            }

            playerAttackQueue.add(action.getEntityId()); // Queue for next tick for very precise check

            if (cancelImpossibleHits && isKnownInvalid(entity)) {
                event.setCancelled(true);
            }
        }

        if (WrapperPlayClientPlayerFlying.isFlying(event.getPacketType())) {
            // Teleports don't interpolate, duplicate 1.17 packets don't interpolate
            if (player.packetStateData.lastPacketWasTeleport
                    || player.packetStateData.lastPacketWasOnePointSeventeenDuplicate) {
                return;
            }

            tickFlying();
        }
    }

    // This method finds the most optimal point at which the user should be aiming at
    // and then measures the distance between the player's eyes and this target point
    //
    // It will not cancel every invalid attack but should cancel 3.05+ or so in real-time
    // Let the post look check measure the distance, as it will always return equal or higher
    // than this method.  If this method flags, the other method WILL flag.
    //
    // Meaning that the other check should be the only one that flags.
    private boolean isKnownInvalid(PacketEntity reachEntity) {
        boolean giveMovementThresholdLenience = player.packetStateData.didLastMovementIncludePosition
                || player.getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_9);

        // If the entity doesn't exist, or if it is exempt, or if it is dead
        if ((blacklisted.contains(reachEntity.type) || !reachEntity.isLivingEntity())
                && reachEntity.type != EntityTypes.END_CRYSTAL) {
            return false;
        }

        // Ignores players in Creative
        if (player.gamemode == GameMode.CREATIVE) {
            return false;
        }

        // Ignores players in vehicles
        if (player.compensatedEntities.getSelf().inVehicle()) {
            return false;
        }

        double lowest = 6;

        // Filter out what we assume to be cheats
        if (cancelBuffer != 0) {
            return checkReach(reachEntity, true) != null; // If they flagged
        } else {
            // Don't allow blatant cheats to get first hit
            for (double eyes : player.getPossibleEyeHeights()) {
                SimpleCollisionBox targetBox = reachEntity.getPossibleCollisionBoxes();

                if (reachEntity.type == EntityTypes.END_CRYSTAL) {
                    targetBox = new SimpleCollisionBox(reachEntity.desyncClientPos.subtract(1, 0, 1),
                            reachEntity.desyncClientPos.add(1, 2, 1));
                }

                Vector from = new Vector(player.x, player.y + eyes, player.z);
                Vector closestPoint = VectorUtils.cutBoxToVector(from, targetBox);
                lowest = Math.min(lowest, closestPoint.distance(from));
            }
        }

        return lowest > 3 + (giveMovementThresholdLenience ? player.getMovementThreshold() : 0);
    }

    private void tickFlying() {
        Integer attackQueue = playerAttackQueue.poll();

        while (attackQueue != null) {
            PacketEntity reachEntity = player.compensatedEntities.entityMap.get(attackQueue);

            if (reachEntity != null) {
                String result = checkReach(reachEntity, false);

                if (result != null) {
                    flagAndAlert(result, false);
                }
            }

            attackQueue = playerAttackQueue.poll();
        }
    }

    private String checkReach(PacketEntity reachEntity, boolean isPrediction) {
        SimpleCollisionBox targetBox = reachEntity.getPossibleCollisionBoxes();

        if (reachEntity.type == EntityTypes.END_CRYSTAL) { // Hardcode end crystal box
            targetBox = new SimpleCollisionBox(reachEntity.desyncClientPos.subtract(1, 0, 1), reachEntity.desyncClientPos.add(1, 2, 1));
        }

        // 1.7 and 1.8 players get a bit of extra hitbox (this is why you should use 1.8 on cross version servers)
        // Yes, this is vanilla and not uncertainty.  All reach checks have this or they are wrong.
        if (player.getClientVersion().isOlderThan(ClientVersion.V_1_9)) {
            targetBox.expand(0.1f);
        }

        targetBox.expand(threshold);

        // This is better than adding to the reach, as 0.03 can cause a player to miss their target
        // Adds some more than 0.03 uncertainty in some cases, but a good trade off for simplicity
        //
        // Just give the uncertainty on 1.9+ clients as we have no way of knowing whether they had 0.03 movement
        if (!player.packetStateData.didLastLastMovementIncludePosition || player.getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_9)) {
            targetBox.expand(player.getMovementThreshold());
        }

        Vector3d from = new Vector3d(player.lastX, player.lastY, player.lastZ);
        double minDistance = Double.MAX_VALUE;

        // https://bugs.mojang.com/browse/MC-67665
        List<Vector> possibleLookDirs = new ArrayList<>(Collections.singletonList(ReachUtils.getLook(player, player.xRot, player.yRot)));

        // If we are a tick behind, we don't know their next look so don't bother doing this
        if (!isPrediction) {
            possibleLookDirs.add(ReachUtils.getLook(player, player.lastXRot, player.yRot));

            // 1.9+ players could be a tick behind because we don't get skipped ticks
            if (player.getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_9)) {
                possibleLookDirs.add(ReachUtils.getLook(player, player.lastXRot, player.lastYRot));
            }

            // 1.7 players do not have any of these issues! They are always on the latest look vector
            if (player.getClientVersion().isOlderThan(ClientVersion.V_1_8)) {
                possibleLookDirs = Collections.singletonList(ReachUtils.getLook(player, player.xRot, player.yRot));
            }
        }

        for (Vector lookVec : possibleLookDirs) {
            for (double eye : player.getPossibleEyeHeights()) {
                Vector eyePos = new Vector(from.getX(), from.getY() + eye, from.getZ());
                Vector endReachPos = eyePos.clone().add(new Vector(lookVec.getX() * 6, lookVec.getY() * 6, lookVec.getZ() * 6));
                Vector intercept = ReachUtils.calculateIntercept(targetBox, eyePos, endReachPos).getFirst();

                if (ReachUtils.isVecInside(targetBox, eyePos)) {
                    minDistance = 0;
                    break;
                }

                if (intercept != null) {
                    minDistance = Math.min(eyePos.distance(intercept), minDistance);
                }
            }
        }

        // if the entity is not exempt and the entity is alive
        if ((!blacklisted.contains(reachEntity.type) && reachEntity.isLivingEntity())
                || reachEntity.type == EntityTypes.END_CRYSTAL) {
            if (minDistance == Double.MAX_VALUE) {
                cancelBuffer = 1;
                player.checkManager.getPacketCheck(ReachB.class).flagAndAlert("", false);

            } else if (minDistance > 3) {
                cancelBuffer = 1;
                return "Blocks: " + String.format("%.5f", minDistance);

            } else {
                cancelBuffer = Math.max(0, cancelBuffer - 0.25);
            }
        }

        return null;
    }

    @Override
    public void reload() {
        super.reload();
        cancelImpossibleHits = getConfig().getBooleanElse("Reach.block-impossible-hits", true);
        threshold = getConfig().getDoubleElse("Reach.threshold", 0.0005);
    }
}