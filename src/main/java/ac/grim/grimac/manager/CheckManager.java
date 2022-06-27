package ac.grim.grimac.manager;

import ac.grim.grimac.checks.Check;
import ac.grim.grimac.checks.impl.badpackets.*;
import ac.grim.grimac.checks.impl.badpackets.packetorder.*;
import ac.grim.grimac.checks.impl.badpackets.pingspoof.*;
import ac.grim.grimac.checks.impl.combat.aimassist.AimAssistA;
import ac.grim.grimac.checks.impl.combat.aimassist.AimAssistB;
import ac.grim.grimac.checks.impl.combat.aimassist.AimAssistC;
import ac.grim.grimac.checks.impl.combat.aimassist.AimAssistD;
import ac.grim.grimac.checks.impl.combat.aimassist.processor.AimProcessor;
import ac.grim.grimac.checks.impl.combat.aimassist.processor.Cinematic;
import ac.grim.grimac.checks.impl.combat.autoblock.AutoBlockA;
import ac.grim.grimac.checks.impl.combat.autoheal.*;
import ac.grim.grimac.checks.impl.combat.reach.ReachA;
import ac.grim.grimac.checks.impl.combat.reach.ReachB;
import ac.grim.grimac.checks.impl.combat.velocity.VelocityA;
import ac.grim.grimac.checks.impl.combat.velocity.VelocityB;
import ac.grim.grimac.checks.impl.inventory.*;
import ac.grim.grimac.checks.impl.misc.ClientBrand;
import ac.grim.grimac.checks.impl.misc.PacketSniffer;
import ac.grim.grimac.checks.impl.movement.PredictionRunner;
import ac.grim.grimac.checks.impl.movement.SetbackBlocker;
import ac.grim.grimac.checks.impl.movement.VehiclePredictionRunner;
import ac.grim.grimac.checks.impl.movement.entitycontrol.EntityControl;
import ac.grim.grimac.checks.impl.movement.groundspoof.GroundSpoofA;
import ac.grim.grimac.checks.impl.movement.groundspoof.GroundSpoofB;
import ac.grim.grimac.checks.impl.movement.noslowdown.NoSlowdown;
import ac.grim.grimac.checks.impl.movement.prediction.DebugHandler;
import ac.grim.grimac.checks.impl.movement.prediction.Phase;
import ac.grim.grimac.checks.impl.movement.prediction.Simulation;
import ac.grim.grimac.checks.impl.movement.timer.Timer;
import ac.grim.grimac.checks.impl.movement.timer.VehicleTimer;
import ac.grim.grimac.checks.impl.world.fastbreak.FastBreak;
import ac.grim.grimac.checks.impl.world.scaffold.*;
import ac.grim.grimac.checks.type.*;
import ac.grim.grimac.events.packets.PacketChangeGameState;
import ac.grim.grimac.events.packets.PacketEntityReplication;
import ac.grim.grimac.events.packets.PacketPlayerAbilities;
import ac.grim.grimac.events.packets.PacketWorldBorder;
import ac.grim.grimac.player.GrimPlayer;
import ac.grim.grimac.predictionengine.GhostBlockDetector;
import ac.grim.grimac.predictionengine.SneakingEstimator;
import ac.grim.grimac.utils.anticheat.update.*;
import ac.grim.grimac.utils.latency.CompensatedCooldown;
import ac.grim.grimac.utils.latency.CompensatedFireworks;
import ac.grim.grimac.utils.latency.CompensatedInventory;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.google.common.collect.ClassToInstanceMap;
import com.google.common.collect.ImmutableClassToInstanceMap;

public class CheckManager {

    public final ClassToInstanceMap<Check> allChecks;
    final ClassToInstanceMap<PacketCheck> packetChecks;
    final ClassToInstanceMap<PositionCheck> positionCheck;
    final ClassToInstanceMap<RotationCheck> rotationCheck;
    final ClassToInstanceMap<VehicleCheck> vehicleCheck;
    final ClassToInstanceMap<PacketCheck> timerCheck;
    final ClassToInstanceMap<BlockPlaceCheck> blockPlaceCheck;
    final ClassToInstanceMap<PostPredictionCheck> postPredictionCheck;

    public CheckManager(GrimPlayer player) {
        // Include post checks in the packet check too
        packetChecks = new ImmutableClassToInstanceMap.Builder<PacketCheck>()
                .put(PacketSniffer.class, new PacketSniffer(player))

                .put(ReachA.class, new ReachA(player))
                .put(ReachB.class, new ReachB(player))

                .put(PacketEntityReplication.class, new PacketEntityReplication(player))
                .put(PacketChangeGameState.class, new PacketChangeGameState(player))

                .put(VelocityB.class, new VelocityB(player))
                .put(VelocityA.class, new VelocityA(player))

                .put(CompensatedInventory.class, new CompensatedInventory(player))
                .put(PacketPlayerAbilities.class, new PacketPlayerAbilities(player))
                .put(PacketWorldBorder.class, new PacketWorldBorder(player))
                .put(ClientBrand.class, new ClientBrand(player))

                .put(GroundSpoofB.class, new GroundSpoofB(player))

                .put(BadPacketsA.class, new BadPacketsA(player))
                .put(BadPacketsC.class, new BadPacketsC(player))
                .put(BadPacketsD.class, new BadPacketsD(player))
                .put(BadPacketsE.class, new BadPacketsE(player))
                .put(BadPacketsF.class, new BadPacketsF(player))
                .put(BadPacketsG.class, new BadPacketsG(player))
                .put(BadPacketsH.class, new BadPacketsH(player))
                .put(BadPacketsI.class, new BadPacketsI(player))
                .put(BadPacketsJ.class, new BadPacketsJ(player))
                .put(BadPacketsL.class, new BadPacketsL(player))
                .put(BadPacketsM.class, new BadPacketsM(player))
                .put(BadPacketsN.class, new BadPacketsN(player))

                .put(AutoBlockA.class, new AutoBlockA(player))

                .put(PacketOrderA.class, new PacketOrderA(player))
                .put(PacketOrderB.class, new PacketOrderB(player))
                .put(PacketOrderC.class, new PacketOrderC(player))
                .put(PacketOrderD.class, new PacketOrderD(player))
                .put(PacketOrderE.class, new PacketOrderE(player))

                .put(AutoHealA.class, new AutoHealA(player))
                .put(AutoHealB.class, new AutoHealB(player))
                .put(AutoHealC.class, new AutoHealC(player))
                .put(AutoHealD.class, new AutoHealD(player))
                .put(AutoHealE.class, new AutoHealE(player))
                .put(AutoHealF.class, new AutoHealF(player))

                .put(InventoryA.class, new InventoryA(player))
                .put(InventoryB.class, new InventoryB(player))
                .put(InventoryC.class, new InventoryC(player))
                .put(InventoryD.class, new InventoryD(player))
                .put(InventoryE.class, new InventoryE(player))

                .put(PingSpoofA.class, new PingSpoofA(player))
                .put(PingSpoofB.class, new PingSpoofB(player))
                .put(PingSpoofC.class, new PingSpoofC(player))
                .put(PingSpoofD.class, new PingSpoofD(player))
                .put(PingSpoofE.class, new PingSpoofE(player))
                .put(PingSpoofF.class, new PingSpoofF(player))
                .put(PingSpoofG.class, new PingSpoofG(player))
                .put(PingSpoofH.class, new PingSpoofH(player))
                .put(PingSpoofI.class, new PingSpoofI(player))
                .put(PingSpoofJ.class, new PingSpoofJ(player))

                .put(FastBreak.class, new FastBreak(player))

                .put(SetbackBlocker.class, new SetbackBlocker(player)) // Must be last class otherwise we can't check while blocking packets
                .build();

        positionCheck = new ImmutableClassToInstanceMap.Builder<PositionCheck>()
                .put(PredictionRunner.class, new PredictionRunner(player))
                .put(CompensatedCooldown.class, new CompensatedCooldown(player))
                .build();

        rotationCheck = new ImmutableClassToInstanceMap.Builder<RotationCheck>()
                .put(AimProcessor.class, new AimProcessor(player))
                .put(Cinematic.class, new Cinematic(player))
                .put(AimAssistA.class, new AimAssistA(player))
                .put(AimAssistB.class, new AimAssistB(player))
                .put(AimAssistC.class, new AimAssistC(player))
                .put(AimAssistD.class, new AimAssistD(player))
                .build();

        vehicleCheck = new ImmutableClassToInstanceMap.Builder<VehicleCheck>()
                .put(VehiclePredictionRunner.class, new VehiclePredictionRunner(player))
                .build();

        postPredictionCheck = new ImmutableClassToInstanceMap.Builder<PostPredictionCheck>()
                .put(GhostBlockDetector.class, new GhostBlockDetector(player))
                .put(Phase.class, new Phase(player))
                .put(GroundSpoofA.class, new GroundSpoofA(player))
                .put(Simulation.class, new Simulation(player))
                .put(DebugHandler.class, new DebugHandler(player))
                .put(EntityControl.class, new EntityControl(player))
                .put(NoSlowdown.class, new NoSlowdown(player))
                .put(SetbackTeleportUtil.class, new SetbackTeleportUtil(player)) // Avoid teleporting to new position, update safe pos last
                .put(CompensatedFireworks.class, player.compensatedFireworks)
                .put(SneakingEstimator.class, new SneakingEstimator(player))
                .put(LastInstanceManager.class, new LastInstanceManager(player))
                .build();

        blockPlaceCheck = new ImmutableClassToInstanceMap.Builder<BlockPlaceCheck>()
                .put(ScaffoldA.class, new ScaffoldA(player))
                .put(ScaffoldB.class, new ScaffoldB(player))
                .put(ScaffoldC.class, new ScaffoldC(player))
                .put(ScaffoldD.class, new ScaffoldD(player))
                .put(ScaffoldE.class, new ScaffoldE(player))
                .build();

        timerCheck = new ImmutableClassToInstanceMap.Builder<PacketCheck>()
                .put(Timer.class, new Timer(player))
                .put(VehicleTimer.class, new VehicleTimer(player))
                .build();

        allChecks = new ImmutableClassToInstanceMap.Builder<Check>()
                .putAll(packetChecks)
                .putAll(positionCheck)
                .putAll(rotationCheck)
                .putAll(vehicleCheck)
                .putAll(postPredictionCheck)
                .putAll(blockPlaceCheck)
                .putAll(timerCheck)
                .build();
    }

    @SuppressWarnings("unchecked")
    public <T extends PositionCheck> T getPositionCheck(Class<T> check) {
        return (T) positionCheck.get(check);
    }

    @SuppressWarnings("unchecked")
    public <T extends RotationCheck> T getRotationCheck(Class<T> check) {
        return (T) rotationCheck.get(check);
    }

    @SuppressWarnings("unchecked")
    public <T extends VehicleCheck> T getVehicleCheck(Class<T> check) {
        return (T) vehicleCheck.get(check);
    }

    public void onPrePredictionReceivePacket(PacketReceiveEvent packet) {
        timerCheck.values().forEach(check -> check.onPacketReceive(packet));
    }

    public void onPacketReceive(PacketReceiveEvent packet) {
        packetChecks.values().forEach(packetCheck -> packetCheck.onPacketReceive(packet));
    }

    public void onPacketSend(PacketSendEvent packet) {
        timerCheck.values().forEach(check -> check.onPacketSend(packet));
        packetChecks.values().forEach(packetCheck -> packetCheck.onPacketSend(packet));
    }

    public void onPositionUpdate(PositionUpdate position) {
        positionCheck.values().forEach(positionCheck -> positionCheck.onPositionUpdate(position));
        // Allow the reach check to listen to filtered position packets
        packetChecks.values().forEach(packetCheck -> packetCheck.onPositionUpdate(position));
    }

    public void onRotationUpdate(RotationUpdate rotation) {
        rotationCheck.values().forEach(rotationCheck -> rotationCheck.process(rotation));
    }

    public void onVehiclePositionUpdate(VehiclePositionUpdate update) {
        vehicleCheck.values().forEach(vehicleCheck -> vehicleCheck.process(update));
    }

    public void onPredictionFinish(PredictionComplete complete) {
        postPredictionCheck.values().forEach(predictionCheck -> predictionCheck.onPredictionComplete(complete));
    }

    public void onBlockPlace(BlockPlace place) {
        blockPlaceCheck.values().forEach(check -> check.onBlockPlace(place));
    }

    public void onPostFlyingBlockPlace(BlockPlace place) {
        blockPlaceCheck.values().forEach(check -> check.onPostFlyingBlockPlace(place));
    }

    public VelocityB getExplosionHandler() {
        return getPacketCheck(VelocityB.class);
    }

    @SuppressWarnings("unchecked")
    public <T extends PacketCheck> T getPacketCheck(Class<T> check) {
        return (T) packetChecks.get(check);
    }

    public PacketEntityReplication getEntityReplication() {
        return getPacketCheck(PacketEntityReplication.class);
    }

    public GroundSpoofB getNoFall() {
        return getPacketCheck(GroundSpoofB.class);
    }

    public VelocityA getKnockbackHandler() {
        return getPacketCheck(VelocityA.class);
    }

    public CompensatedCooldown getCompensatedCooldown() {
        return getPositionCheck(CompensatedCooldown.class);
    }

    public NoSlowdown getNoSlow() {
        return getPostPredictionCheck(NoSlowdown.class);
    }

    public SetbackTeleportUtil getSetbackUtil() {
        return getPostPredictionCheck(SetbackTeleportUtil.class);
    }

    public DebugHandler getDebugHandler() {
        return getPostPredictionCheck(DebugHandler.class);
    }

    public Simulation getOffsetHandler() {
        return getPostPredictionCheck(Simulation.class);
    }

    @SuppressWarnings("unchecked")
    public <T extends PostPredictionCheck> T getPostPredictionCheck(Class<T> check) {
        return (T) postPredictionCheck.get(check);
    }
}
