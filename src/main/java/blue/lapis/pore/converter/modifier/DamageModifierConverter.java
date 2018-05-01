package blue.lapis.pore.converter.modifier;

import blue.lapis.pore.converter.type.TypeConverter;
import com.google.common.base.Converter;
import org.bukkit.event.entity.EntityDamageEvent.DamageModifier;
import org.spongepowered.api.event.cause.entity.damage.DamageModifierType;
import org.spongepowered.api.event.cause.entity.damage.DamageModifierTypes;

public class DamageModifierConverter {
    private static final Converter<DamageModifier, DamageModifierType> CONVERTER =
            TypeConverter.builder(DamageModifier.class, DamageModifierType.class)
                    .add(DamageModifier.BASE, DamageModifierTypes.DIFFICULTY)
                    .add(DamageModifier.HARD_HAT, DamageModifierTypes.HARD_HAT)
                    //.add(DamageModifier.BLOCKING, DamageModifierTypes.)
                    .add(DamageModifier.ARMOR, DamageModifierTypes.ARMOR)
                    .add(DamageModifier.RESISTANCE, DamageModifierTypes.NEGATIVE_POTION_EFFECT)
                    .add(DamageModifier.MAGIC, DamageModifierTypes.MAGIC)
                    .add(DamageModifier.ABSORPTION, DamageModifierTypes.ABSORPTION)
                    .build();
    public static DamageModifierType of(DamageModifier type) {
        try {
            return CONVERTER.convert(type);
        } catch (UnsupportedOperationException ex) {
            return DamageModifierTypes.DIFFICULTY;
        }
    }
}
