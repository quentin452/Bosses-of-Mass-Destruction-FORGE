package com.cerbon.bosses_of_mass_destruction.packet;

import com.cerbon.bosses_of_mass_destruction.packet.custom.*;
import com.cerbon.bosses_of_mass_destruction.packet.custom.multipart_entities.MultipartEntityInteractionC2SPacket;
import com.cerbon.bosses_of_mass_destruction.util.BMDConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.ChannelBuilder;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.SimpleChannel;

public class BMDPacketHandler{
    private static final int PROTOCOL_VERSION = 1;
    public static SimpleChannel INSTANCE;

    private static int packetId = 0;
    private static int id(){
        return packetId++;
    }

    public static void register() {
        SimpleChannel net = ChannelBuilder.named(new ResourceLocation(BMDConstants.MOD_ID, "packets"))
                .networkProtocolVersion(PROTOCOL_VERSION)
                .clientAcceptedVersions((s, v) -> true)
                .serverAcceptedVersions((s, v) -> true)
                .simpleChannel();

        INSTANCE = net;

        net.messageBuilder(ChargedEnderPearlS2CPacket.class, id(), NetworkDirection.PLAY_TO_CLIENT)
                .decoder(ChargedEnderPearlS2CPacket::new)
                .encoder(ChargedEnderPearlS2CPacket::write)
                .consumerMainThread((chargedEnderPearlS2CPacket, context) -> chargedEnderPearlS2CPacket.handle(() -> context))
                .add();

        net.messageBuilder(SendDeltaMovementS2CPacket.class, id(), NetworkDirection.PLAY_TO_CLIENT)
                .decoder(SendDeltaMovementS2CPacket::new)
                .encoder(SendDeltaMovementS2CPacket::write)
                .consumerMainThread((sendDeltaMovementS2CPacket, context) -> sendDeltaMovementS2CPacket.handle(() -> context))
                .add();

        net.messageBuilder(PlaceS2CPacket.class, id(), NetworkDirection.PLAY_TO_CLIENT)
                .decoder(PlaceS2CPacket::new)
                .encoder(PlaceS2CPacket::write)
                .consumerMainThread((placeS2CPacket, context) -> placeS2CPacket.handle(() -> context))
                .add();

        net.messageBuilder(SpikeS2CPacket.class, id(), NetworkDirection.PLAY_TO_CLIENT)
                .decoder(SpikeS2CPacket::new)
                .encoder(SpikeS2CPacket::write)
                .consumerMainThread((spikeS2CPacket, context) -> spikeS2CPacket.handle(() -> context))
                .add();

        net.messageBuilder(HealS2CPacket.class, id(), NetworkDirection.PLAY_TO_CLIENT)
                .decoder(HealS2CPacket::new)
                .encoder(HealS2CPacket::write)
                .consumerMainThread((healS2CPacket, context) -> healS2CPacket.handle(() -> context))
                .add();

        net.messageBuilder(BlindnessS2CPacket.class, id(), NetworkDirection.PLAY_TO_CLIENT)
                .decoder(BlindnessS2CPacket::new)
                .encoder(BlindnessS2CPacket::write)
                .consumerMainThread((blindnessS2CPacket, context) -> blindnessS2CPacket.handle(() -> context))
                .add();

        net.messageBuilder(ChangeHitboxS2CPacket.class, id(), NetworkDirection.PLAY_TO_CLIENT)
                .decoder(ChangeHitboxS2CPacket::new)
                .encoder(ChangeHitboxS2CPacket::write)
                .consumerMainThread((changeHitboxS2CPacket, context) -> changeHitboxS2CPacket.handle(() -> context))
                .add();

        net.messageBuilder(SendParticleS2CPacket.class, id(), NetworkDirection.PLAY_TO_CLIENT)
                .decoder(SendParticleS2CPacket::new)
                .encoder(SendParticleS2CPacket::write)
                .consumerMainThread((sendParticleS2CPacket, context) -> sendParticleS2CPacket.handle(() -> context))
                .add();

        net.messageBuilder(SendVec3S2CPacket.class, id(), NetworkDirection.PLAY_TO_CLIENT)
                .decoder(SendVec3S2CPacket::new)
                .encoder(SendVec3S2CPacket::write)
                .consumerMainThread((sendVec3S2CPacket, context) -> sendVec3S2CPacket.handle(() -> context))
                .add();

        net.messageBuilder(ObsidilithReviveS2CPacket.class, id(), NetworkDirection.PLAY_TO_CLIENT)
                .decoder(ObsidilithReviveS2CPacket::new)
                .encoder(ObsidilithReviveS2CPacket::write)
                .consumerMainThread((obsidilithReviveS2CPacket, context) -> obsidilithReviveS2CPacket.handle(() -> context))
                .add();

        net.messageBuilder(VoidBlossomReviveS2CPacket.class, id(), NetworkDirection.PLAY_TO_CLIENT)
                .decoder(VoidBlossomReviveS2CPacket::new)
                .encoder(VoidBlossomReviveS2CPacket::write)
                .consumerMainThread((voidBlossomReviveS2CPacket, context) -> voidBlossomReviveS2CPacket.handle(() -> context))
                .add();

        net.messageBuilder(MultipartEntityInteractionC2SPacket.class, id(), NetworkDirection.PLAY_TO_SERVER)
                .decoder(MultipartEntityInteractionC2SPacket::new)
                .encoder(MultipartEntityInteractionC2SPacket::write)
                .consumerMainThread((multipartEntityInteractionC2SPacket, context) -> multipartEntityInteractionC2SPacket.handle(() -> context))
                .add();
    }

    public static <MSG> void sendToServer(MSG message) {
        INSTANCE.send(message, PacketDistributor.SERVER.noArg());
    }

    public static <MSG> void sendToPlayer(MSG message, ServerPlayer player){
        INSTANCE.send(message, PacketDistributor.PLAYER.with(player));
    }

    public static <MSG> void sendToAllPlayersTrackingChunk(MSG message, ServerLevel level, Vec3 pos){
        INSTANCE.send(message, PacketDistributor.TRACKING_CHUNK.with(level.getChunkAt(BlockPos.containing(pos))));
    }
}
