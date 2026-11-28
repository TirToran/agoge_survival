package net.torantyr.groundwork_sling.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;
import net.torantyr.groundwork_sling.ModCommonConfig;

import java.util.function.Supplier;

public class SetFireChargesPacket {
    private final boolean enabled;

    public SetFireChargesPacket(boolean enabled) {
        this.enabled = enabled;
    }

    public static void encode(SetFireChargesPacket msg, FriendlyByteBuf buf) {
        buf.writeBoolean(msg.enabled);
    }

    public static SetFireChargesPacket decode(FriendlyByteBuf buf) {
        return new SetFireChargesPacket(buf.readBoolean());
    }

    public static void handle(SetFireChargesPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer sender = ctx.get().getSender();
            if (sender == null || !sender.hasPermissions(2)) {
                return; // not an operator: ignore completely
            }
            ModCommonConfig.FIRE_CHARGES_ALLOWED.set(msg.enabled);
            ModCommonConfig.FIRE_CHARGES_ALLOWED.save();
            ModNetwork.CHANNEL.send(PacketDistributor.ALL.noArg(), new FireChargesSyncPacket(msg.enabled));
        });
        ctx.get().setPacketHandled(true);
    }
}