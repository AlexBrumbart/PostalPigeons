package de.alexbrumbart.postalpigeons.util;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class MailReceptorStorage extends SavedData {
    private final Map<String, BlockPos> positions = new HashMap<>();

    public void setPosition(String name, BlockPos pos) {
        positions.put(name, new BlockPos(pos.getX(), pos.getY(), pos.getZ()));
        setDirty();
    }

    public void removePosition(String name) {
        positions.remove(name);
        setDirty();
    }

    public Set<Map.Entry<String, BlockPos>> getEntries() {
        return positions.entrySet();
    }

    @Override
    public CompoundTag save(CompoundTag tag) {
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

    public static MailReceptorStorage getInstance(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(MailReceptorStorage::load, MailReceptorStorage::new, "mail_receptor_storage");
    }

    private static MailReceptorStorage load(CompoundTag tag) {
        MailReceptorStorage storage = new MailReceptorStorage();

        for (String name : tag.getAllKeys()) {
            CompoundTag entryTag = tag.getCompound(name);
            storage.setPosition(entryTag.getString("name"), new BlockPos(entryTag.getInt("x"), entryTag.getInt("y"), entryTag.getInt("z")));
        }

        return storage;
    }
}
