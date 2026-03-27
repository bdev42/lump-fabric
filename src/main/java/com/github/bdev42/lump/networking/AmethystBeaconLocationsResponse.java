package com.github.bdev42.lump.networking;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

import static com.github.bdev42.lump.Lump.identifier;

public record AmethystBeaconLocationsResponse(List<BlockPos> positions) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<AmethystBeaconLocationsResponse> PACKET_ID = new CustomPacketPayload.Type<>(identifier("amethyst_beacon_locations_response"));
    public static final StreamCodec<RegistryFriendlyByteBuf, AmethystBeaconLocationsResponse> PACKET_CODEC = StreamCodec.composite(
            ByteBufCodecs.collection(ArrayList::new, BlockPos.STREAM_CODEC), AmethystBeaconLocationsResponse::positions,
            AmethystBeaconLocationsResponse::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return PACKET_ID;
    }
}
