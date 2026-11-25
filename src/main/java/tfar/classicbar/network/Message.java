package tfar.classicbar.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.registration.NetworkRegistry;
import tfar.classicbar.ClassicBar;

/**
 * Server → client food-data sync.
 *
 * <p>Vanilla only syncs saturation when it reaches zero and never syncs exhaustion at all,
 * so the bar would sit stale. The 1.20.1 port fixed that with a Forge SimpleChannel; on
 * 1.21.1 the equivalent is one {@link CustomPacketPayload} per value.
 */
public final class Message {

    /** Network protocol version; bump whenever a payload shape changes. */
    public static final String NETWORK_VERSION = "1";

    /** True when the connected server also runs Classic Bar, so it can send us sync data. */
    public static volatile boolean presentOnServer;

    private Message() {}

    /** Called on the main thread right after the client joins a world. */
    public static void onLoggedIn(net.minecraft.network.Connection connection) {
        presentOnServer = NetworkRegistry.hasChannel(connection, null, SaturationSync.TYPE.id());
    }

    public static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(ClassicBar.MODID, path);
    }

    static Player sidedPlayer(IPayloadContext context) {
        return context.player();
    }

    // ---------------------------------------------------------------- saturation

    public record SaturationSync(float saturation) implements CustomPacketPayload {
        public static final Type<SaturationSync> TYPE = new Type<>(Message.id("saturation_sync"));
        public static final StreamCodec<FriendlyByteBuf, SaturationSync> CODEC = StreamCodec.of(
                (buf, msg) -> buf.writeFloat(msg.saturation),
                buf -> new SaturationSync(buf.readFloat()));

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }

        public static void handle(SaturationSync msg, IPayloadContext context) {
            Player player = sidedPlayer(context);
            if (player != null) player.getFoodData().setSaturation(msg.saturation);
        }
    }

    // ---------------------------------------------------------------- exhaustion

    public record ExhaustionSync(float exhaustion) implements CustomPacketPayload {
        public static final Type<ExhaustionSync> TYPE = new Type<>(Message.id("exhaustion_sync"));
        public static final StreamCodec<FriendlyByteBuf, ExhaustionSync> CODEC = StreamCodec.of(
                (buf, msg) -> buf.writeFloat(msg.exhaustion),
                buf -> new ExhaustionSync(buf.readFloat()));

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }

        public static void handle(ExhaustionSync msg, IPayloadContext context) {
            Player player = sidedPlayer(context);
            if (player != null) player.getFoodData().setExhaustion(msg.exhaustion);
        }
    }

    /** Sends the current food values to one player. */
    public static void syncTo(net.minecraft.server.level.ServerPlayer player) {
        PacketDistributor.sendToPlayer(player, new SaturationSync(player.getFoodData().getSaturationLevel()),
                new ExhaustionSync(player.getFoodData().getExhaustionLevel()));
    }
}
