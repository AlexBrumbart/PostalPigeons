package de.alexbrumbart.postalpigeons.util;

import de.alexbrumbart.postalpigeons.rendering.PigeonCoopScreen;
import de.alexbrumbart.postalpigeons.util.packets.SBCoopSyncPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class MailReceptorStorage extends SavedData {
    private static final Map<ResourceKey<Level>, MailReceptorStorage> clientStorageMap = new HashMap<>();

    private final Map<String, BlockPos> positions = new HashMap<>();
    private long version = 0;

    /***** Serverside handling *****/

    public void setPosition(String name, BlockPos pos) {
        positions.put(name, new BlockPos(pos.getX(), pos.getY(), pos.getZ()));
        version++;
        setDirty();
    }

    public void removePosition(String name) {
        positions.remove(name);
        version++;
        setDirty();
    }

    @Override
    public CompoundTag save(CompoundTag tag) {
        tag.putLong("version", version);

        for (var entry : getEntries()) {
            CompoundTag entryTag = new CompoundTag();
            entryTag.putString("name", entry.getKey());
            entryTag.putInt("x", entry.getValue().getX());
            entryTag.putInt("y", entry.getValue().getY());
            entryTag.putInt("z", entry.getValue().getZ());

            tag.put(entry.toString(), entryTag);
        }

        return tag;
    }

    /***** Common handling *****/

    @Nullable
    public BlockPos getPosition(String name) {
        return positions.get(name);
    }

    public Set<Map.Entry<String, BlockPos>> getEntries() {
        return positions.entrySet();
    }

    public long getVersion() {
        return version;
    }

    /***** Client handling *****/

    public void refreshClientPositions(CompoundTag tag) {
        positions.clear();

        long versionCache = tag.getLong("version");
        tag.remove("version");

        for (String name : tag.getAllKeys()) {
            CompoundTag entryTag = tag.getCompound(name);
            setPosition(entryTag.getString("name"), new BlockPos(entryTag.getInt("x"), entryTag.getInt("y"), entryTag.getInt("z")));
        }

        version = versionCache;

        // Updates cached entries
        if (Minecraft.getInstance().screen instanceof PigeonCoopScreen coopScreen)
            coopScreen.updateEntries();
    }

    /***** Instance management *****/

    public static MailReceptorStorage getInstance(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(MailReceptorStorage::load, MailReceptorStorage::new, "mail_receptor_storage");
    }

    public static MailReceptorStorage getClientInstance(ResourceKey<Level> dimension) {
        MailReceptorStorage storage = getClientInstanceUnsynced(dimension);
        NetworkHandler.INSTANCE.sendToServer(new SBCoopSyncPacket(storage.version, dimension));

        return storage;
    }

    public static MailReceptorStorage getClientInstanceUnsynced(ResourceKey<Level> dimension) {
        clientStorageMap.computeIfAbsent(dimension, dim -> new MailReceptorStorage());

        return clientStorageMap.get(dimension);
    }

    private static MailReceptorStorage load(CompoundTag tag) {
        MailReceptorStorage storage = new MailReceptorStorage();

        long versionCache = tag.getLong("version");
        tag.remove("version");

        for (String name : tag.getAllKeys()) {
            CompoundTag entryTag = tag.getCompound(name);
            storage.setPosition(entryTag.getString("name"), new BlockPos(entryTag.getInt("x"), entryTag.getInt("y"), entryTag.getInt("z")));
        }

        storage.version = versionCache;

        return storage;
    }
}
