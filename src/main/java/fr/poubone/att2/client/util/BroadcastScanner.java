package fr.poubone.att2.client.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.item.ItemStack;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

public class BroadcastScanner {
    private static final Map<UUID, Long> seen = new HashMap<>();
    private static final long LIFESPAN_MS = 2000;

    public static void tick(MinecraftClient client) {
        if (client.world == null || client.player == null) return;

        long now = System.currentTimeMillis();

        for (Entity entity : client.world.getEntities()) {
            if (!(entity instanceof ArmorStandEntity stand)) continue;
            if (stand.getCustomName() == null) continue;

            String name = stand.getCustomName().getString();
            if (!name.equals("[BROADCAST_TAG]")) continue;

            UUID id = stand.getUuid();
            if (seen.containsKey(id)) continue;

            ItemStack stack = stand.getMainHandStack();
            if (!stack.isEmpty()) {
                ChatItemUtils.sendItemInChat(stack);
                seen.put(id, now);
            }
        }

        Iterator<Map.Entry<UUID, Long>> it = seen.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<UUID, Long> entry = it.next();
            if (now - entry.getValue() > LIFESPAN_MS) {
                it.remove();
            }
        }
    }
}
