package com.github.bdev42.lump.networking;

import static com.github.bdev42.lump.Lump.identifier;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record AmethystBeaconLocationsRequest(int chunkX, int chunkZ, int radius) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<AmethystBeaconLocationsRequest> PACKET_ID = new CustomPacketPayload.Type<>(identifier("amethyst_beacon_locations_request"));
    public static final StreamCodec<RegistryFriendlyByteBuf, AmethystBeaconLocationsRequest> PACKET_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, AmethystBeaconLocationsRequest::chunkX,
            ByteBufCodecs.INT, AmethystBeaconLocationsRequest::chunkZ,
            ByteBufCodecs.INT, AmethystBeaconLocationsRequest::radius,
            AmethystBeaconLocationsRequest::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return PACKET_ID;
    }
}
