package com.cerbon.bosses_of_mass_destruction.packet;

import com.cerbon.bosses_of_mass_destruction.packet.custom.ChargedEnderPearlS2CPacket;
import com.cerbon.bosses_of_mass_destruction.packet.custom.PlaceS2CPacket;
import com.cerbon.bosses_of_mass_destruction.packet.custom.SendDeltaMovementS2CPacket;
import com.cerbon.bosses_of_mass_destruction.packet.custom.multipart_entities.MultipartEntityAttackC2SPacket;
import com.cerbon.bosses_of_mass_destruction.packet.custom.multipart_entities.MultipartEntityInteractC2SPacket;
import com.cerbon.bosses_of_mass_destruction.util.BMDConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

public class BMDPacketHandler {
    private static final String PROTOCOL_VERSION = "1";
    public static SimpleChannel INSTANCE;

    private static int packetId = 0;
    private static int id(){
        return packetId++;
    }

    public static void register() {
        SimpleChannel net = NetworkRegistry.ChannelBuilder.named(new ResourceLocation(BMDConstants.MOD_ID, "packets"))
                .networkProtocolVersion(() -> PROTOCOL_VERSION)
                .clientAcceptedVersions(s -> true)
                .serverAcceptedVersions(s -> true)
                .simpleChannel();

        INSTANCE = net;

        net.messageBuilder(ChargedEnderPearlS2CPacket.class, id(), NetworkDirection.PLAY_TO_CLIENT)
                .decoder(ChargedEnderPearlS2CPacket::new)
                .encoder(ChargedEnderPearlS2CPacket::write)
                .consumerMainThread(ChargedEnderPearlS2CPacket::handle)
                .add();

        net.messageBuilder(SendDeltaMovementS2CPacket.class, id(), NetworkDirection.PLAY_TO_CLIENT)
                .decoder(SendDeltaMovementS2CPacket::new)
                .encoder(SendDeltaMovementS2CPacket::write)
                .consumerMainThread(SendDeltaMovementS2CPacket::handle)
                .add();

        net.messageBuilder(PlaceS2CPacket.class, id(), NetworkDirection.PLAY_TO_CLIENT)
                .decoder(PlaceS2CPacket::new)
                .encoder(PlaceS2CPacket::write)
                .consumerMainThread(PlaceS2CPacket::handle)
                .add();

        net.messageBuilder(MultipartEntityAttackC2SPacket.class, id(), NetworkDirection.PLAY_TO_SERVER)
                .decoder(MultipartEntityAttackC2SPacket::new)
                .encoder(MultipartEntityAttackC2SPacket::write)
                .consumerMainThread(MultipartEntityAttackC2SPacket::handle)
                .add();

        net.messageBuilder(MultipartEntityInteractC2SPacket.class, id(), NetworkDirection.PLAY_TO_SERVER)
                .decoder(MultipartEntityInteractC2SPacket::new)
                .encoder(MultipartEntityInteractC2SPacket::write)
                .consumerMainThread(MultipartEntityInteractC2SPacket::handle)
                .add();
    }

    public static <MSG> void sendToServer(MSG message) {
        INSTANCE.sendToServer(message);
    }

    public static <MSG> void sendToPlayer(MSG message, ServerPlayer player){
        INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), message);
    }

    public static <MSG> void sendToAllPlayersTrackingChunk(MSG message, ServerLevel level, Vec3 pos){
        INSTANCE.send(PacketDistributor.TRACKING_CHUNK.with(() -> level.getChunkAt(BlockPos.containing(pos))), message);
    }
}
