package net.deechael.genshin.lib.open.world.property.type;

import com.flowpowered.nbt.ByteTag;
import com.flowpowered.nbt.CompoundMap;
import com.flowpowered.nbt.Tag;
import net.deechael.genshin.lib.open.world.property.SlimeProperty;

import java.util.function.Function;

public class SlimePropertyBoolean extends SlimeProperty<Boolean> {

    public SlimePropertyBoolean(String nbtName, Boolean defaultValue) {
        super(nbtName, defaultValue);
    }

    public SlimePropertyBoolean(String nbtName, Boolean defaultValue, Function<Boolean, Boolean> validator) {
        super(nbtName, defaultValue, validator);
    }

    @Override
    protected void writeValue(CompoundMap compound, Boolean value) {
        compound.put(getNbtName(), new ByteTag(getNbtName(), (byte) (value ? 1 : 0)));
    }

    @Override
    protected Boolean readValue(Tag<?> compoundTag) {
        return compoundTag.getAsByteTag()
                .map((value) -> value.getValue() == 1)
                .orElse(getDefaultValue());
    }
}
