package net.torantyr.groundwork_sling.network;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import net.torantyr.groundwork_sling.groundwork_sling;

public class ModNetwork {
    private static final String PROTOCOL = "1";

    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(groundwork_sling.MODID, "main"),
            () -> PROTOCOL,
            PROTOCOL::equals,
            PROTOCOL::equals);

    public static void register() {
        int id = 0;
        CHANNEL.registerMessage(id++, SetFireChargesPacket.class,
                SetFireChargesPacket::encode, SetFireChargesPacket::decode, SetFireChargesPacket::handle);
        CHANNEL.registerMessage(id++, FireChargesSyncPacket.class,
                FireChargesSyncPacket::encode, FireChargesSyncPacket::decode, FireChargesSyncPacket::handle);
    }
}