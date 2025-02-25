package com.github.bdev42.lump.networking;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.List;

import static com.github.bdev42.lump.Lump.identifier;

public record AmethystBeaconLocationsResponse(List<BlockPos> positions) implements CustomPayload {
    public static final CustomPayload.Id<AmethystBeaconLocationsResponse> PACKET_ID = new CustomPayload.Id<>(identifier("amethyst_beacon_locations_response"));
    public static final PacketCodec<RegistryByteBuf, AmethystBeaconLocationsResponse> PACKET_CODEC = PacketCodec.tuple(
            PacketCodecs.collection(ArrayList::new, BlockPos.PACKET_CODEC), AmethystBeaconLocationsResponse::positions,
            AmethystBeaconLocationsResponse::new
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return PACKET_ID;
    }
}
