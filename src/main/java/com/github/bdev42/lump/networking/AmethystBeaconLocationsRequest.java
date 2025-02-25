package com.github.bdev42.lump.networking;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;

import static com.github.bdev42.lump.Lump.identifier;

public record AmethystBeaconLocationsRequest(int chunkX, int chunkZ, int radius) implements CustomPayload {
    public static final CustomPayload.Id<AmethystBeaconLocationsRequest> PACKET_ID = new CustomPayload.Id<>(identifier("amethyst_beacon_locations_request"));
    public static final PacketCodec<RegistryByteBuf, AmethystBeaconLocationsRequest> PACKET_CODEC = PacketCodec.tuple(
            PacketCodecs.INTEGER, AmethystBeaconLocationsRequest::chunkX,
            PacketCodecs.INTEGER, AmethystBeaconLocationsRequest::chunkZ,
            PacketCodecs.INTEGER, AmethystBeaconLocationsRequest::radius,
            AmethystBeaconLocationsRequest::new
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return PACKET_ID;
    }
}
