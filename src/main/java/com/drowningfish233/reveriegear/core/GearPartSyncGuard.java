package com.drowningfish233.reveriegear.core;

import net.minecraft.resources.ResourceLocation;
import net.silentchaos512.gear.gear.part.PartInstance;
import net.silentchaos512.gear.setup.SgRegistries;

import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public final class GearPartSyncGuard {

    private static final Set<ResourceLocation> KNOWN_PARTS = ConcurrentHashMap.newKeySet();
    private static final ThreadLocal<Boolean> FORCE_PRESERVE = ThreadLocal.withInitial(() -> false);

    private GearPartSyncGuard() {
    }

    public static void beginForce() {
        FORCE_PRESERVE.set(true);
    }

    public static void endForce() {
        FORCE_PRESERVE.remove();
    }

    public static void snapshot(Collection<ResourceLocation> ids) {
        if (ids == null || ids.isEmpty()) {
            return;
        }
        try {
            KNOWN_PARTS.addAll(ids);
        } catch (ConcurrentModificationException ignored) {
        }
    }

    public static boolean preserve(PartInstance part) {
        if (part == null) {
            return false;
        }
        if (Boolean.TRUE.equals(FORCE_PRESERVE.get())) {
            return true;
        }
        if (SgRegistries.PART.get(part.getId()) != null) {
            KNOWN_PARTS.add(part.getId());
            return true;
        }
        if (SgRegistries.PART.isReloading()) {
            return true;
        }
        if (SgRegistries.PART.keySet().isEmpty()) {
            return true;
        }
        return KNOWN_PARTS.contains(part.getId());
    }
}
