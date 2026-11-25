package tfar.classicbar.network;

import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import tfar.classicbar.ClassicBar;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Syncs saturation (vanilla only syncs it when it hits 0) and exhaustion (vanilla never syncs it).
 *
 * <p>Listeners are attached in {@link ClassicBar}'s constructor; every send is inside a
 * {@link ServerPlayer} check so the client side is a no-op.
 */
public final class SyncHandler {

    private static final SyncHandler INSTANCE = new SyncHandler();

    public static SyncHandler instance() {
        return INSTANCE;
    }

    private SyncHandler() {}

    private final Map<UUID, Float> lastSaturationLevels = new HashMap<>();
    private final Map<UUID, Float> lastExhaustionLevels = new HashMap<>();

    public void onPlayerTick(PlayerTickEvent.Post event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        sync(player);
    }

    private void sync(ServerPlayer player) {
        UUID uuid = player.getUUID();

        float saturationLevel = player.getFoodData().getSaturationLevel();
        Float lastSaturationLevel = lastSaturationLevels.get(uuid);
        if (lastSaturationLevel == null || lastSaturationLevel != saturationLevel) {
            PacketDistributor.sendToPlayer(player, new Message.SaturationSync(saturationLevel));
            lastSaturationLevels.put(uuid, saturationLevel);
        }

        float exhaustionLevel = player.getFoodData().getExhaustionLevel();
        Float lastExhaustionLevel = lastExhaustionLevels.get(uuid);
        if (lastExhaustionLevel == null || Math.abs(lastExhaustionLevel - exhaustionLevel) >= 0.01f) {
            PacketDistributor.sendToPlayer(player, new Message.ExhaustionSync(exhaustionLevel));
            lastExhaustionLevels.put(uuid, exhaustionLevel);
        }
    }

    public void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        UUID uuid = player.getUUID();
        lastSaturationLevels.remove(uuid);
        lastExhaustionLevels.remove(uuid);
    }
}
