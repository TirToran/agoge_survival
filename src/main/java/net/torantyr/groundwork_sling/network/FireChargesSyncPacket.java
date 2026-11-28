package net.torantyr.groundwork_sling.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;
import net.torantyr.groundwork_sling.SlingRules;

import java.util.function.Supplier;

public class FireChargesSyncPacket {
    private final boolean enabled;

    public FireChargesSyncPacket(boolean enabled) {
        this.enabled = enabled;
    }

    public static void encode(FireChargesSyncPacket msg, FriendlyByteBuf buf) {
        buf.writeBoolean(msg.enabled);
    }

    public static FireChargesSyncPacket decode(FriendlyByteBuf buf) {
        return new FireChargesSyncPacket(buf.readBoolean());
    }

    public static void handle(FireChargesSyncPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() ->
                DistExecutor.unsafeRunWhenOn(Dist.CLIENT,
                        () -> () -> SlingRules.setSyncedFireCharges(msg.enabled)));
        ctx.get().setPacketHandled(true);
    }
}
