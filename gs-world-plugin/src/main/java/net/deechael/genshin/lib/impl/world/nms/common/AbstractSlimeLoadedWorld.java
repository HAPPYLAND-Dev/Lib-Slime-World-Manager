package net.deechael.genshin.lib.impl.world.nms.common;

import com.flowpowered.nbt.CompoundTag;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import net.deechael.genshin.lib.impl.world.nms.NmsUtil;
import net.deechael.genshin.lib.open.world.SlimeChunk;
import net.deechael.genshin.lib.open.world.SlimeLoadedWorld;
import net.deechael.genshin.lib.open.world.SlimeWorld;
import net.deechael.genshin.lib.open.world.exception.WorldAlreadyExistsException;
import net.deechael.genshin.lib.open.world.loader.SlimeLoader;
import net.deechael.genshin.lib.open.world.property.SlimePropertyMap;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public abstract class AbstractSlimeLoadedWorld implements SlimeLoadedWorld {

    protected SlimeLoader loader;
    protected byte version;
    protected final String name;
    protected final Long2ObjectOpenHashMap<SlimeChunk> chunks;

    protected final CompoundTag extraData;
    protected final SlimePropertyMap propertyMap;
    protected final boolean readOnly;
    private final boolean lock;

    protected final Long2ObjectOpenHashMap<List<CompoundTag>> entities;

    private final Object chunkAccessLock = new Object();

    protected AbstractSlimeLoadedWorld(byte version, SlimeLoader loader, String name,
                                       Long2ObjectOpenHashMap<SlimeChunk> chunks, CompoundTag extraData, SlimePropertyMap propertyMap,
                                       boolean readOnly, boolean lock, Long2ObjectOpenHashMap<List<CompoundTag>> entities) {
        this.version = version;
        this.loader = loader;
        this.name = name;
        this.chunks = chunks;
        this.extraData = extraData;
        this.propertyMap = propertyMap;
        this.readOnly = readOnly;
        this.lock = lock;
        this.entities = entities;
    }

    @Override
    public SlimeWorld clone(String worldName) {
        try {
            return clone(worldName, null);
        } catch (WorldAlreadyExistsException | IOException ignored) {
            return null; // Never going to happen
        }
    }

    @Override
    public SlimeWorld clone(String worldName, SlimeLoader loader) throws WorldAlreadyExistsException, IOException {
        return clone(worldName, loader, true);
    }

    @Override
    public SlimeWorld clone(String worldName, SlimeLoader loader, boolean lock) throws WorldAlreadyExistsException, IOException {
        if (name.equals(worldName)) {
            throw new IllegalArgumentException("The clone world cannot have the same name as the original world!");
        }

        if (worldName == null) {
            throw new IllegalArgumentException("The world name cannot be null!");
        }

        if (loader != null) {
            if (loader.worldExists(worldName)) {
                throw new WorldAlreadyExistsException(worldName);
            }
        }

        SlimeLoadedWorld world = createSlimeWorld(worldName, loader, lock);

        if (loader != null) {
            loader.saveWorld(worldName, world.serialize().join(), lock);
        }

        return world;
    }

    public abstract SlimeLoadedWorld createSlimeWorld(String worldName, SlimeLoader loader, boolean lock);

    public Long2ObjectOpenHashMap<List<CompoundTag>> getEntities() {
        return entities;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public SlimeLoader getLoader() {
        return loader;
    }

    @Override
    public void setLoader(SlimeLoader loader) {
        this.loader = loader;
    }

    @Override
    public CompoundTag getExtraData() {
        return extraData;
    }

    @Override
    public SlimePropertyMap getPropertyMap() {
        return propertyMap;
    }

    @Override
    public byte getVersion() {
        return version;
    }

    @Override
    public void updateVersion(byte version) {
        this.version = version;
    }

    @Override
    public Collection<CompoundTag> getWorldMaps() {
        return Collections.emptyList(); // This doesn't actually do anything at this moment
    }

    @Override
    public boolean isLocked() {
        return lock;
    }

    @Override
    public boolean isReadOnly() {
        return readOnly;
    }

    // Chunk methods
    @Override
    public SlimeChunk getChunk(int x, int z) {
        synchronized (chunkAccessLock) {
            return chunks.get(getIndex(x, z));
        }
    }

    @Override
    public Map<Long, SlimeChunk> getChunks() {
        return this.chunks;
    }

    @Override
    public void updateChunk(SlimeChunk chunk) {
        synchronized (chunkAccessLock) {
            this.chunks.put(getIndex(chunk.getX(), chunk.getZ()), chunk);
        }
    }

    public static long getIndex(int x, int z) {
        return NmsUtil.asLong(x, z);
    }

}
