package blue.lapis.pore.converter.type.entity.player;

import blue.lapis.pore.converter.type.TypeConverter;
import com.google.common.base.Converter;
import org.bukkit.inventory.EquipmentSlot;
import org.spongepowered.api.data.type.HandType;
import org.spongepowered.api.data.type.HandTypes;

public final class HandTypeConverter {

    private static final Converter<EquipmentSlot, HandType> CONVERTER =
            TypeConverter.builder(EquipmentSlot.class, HandType.class)
            .add(EquipmentSlot.HAND, HandTypes.MAIN_HAND)
            .add(EquipmentSlot.OFF_HAND, HandTypes.OFF_HAND)
            .build();

    public static HandType of(EquipmentSlot handType){
        return CONVERTER.convert(handType);
    }

    public static EquipmentSlot of(HandType handType){
        return CONVERTER.reverse().convert(handType);
    }
}
