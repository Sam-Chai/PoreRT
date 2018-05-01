package blue.lapis.pore.mixin.net.minecraft.block;

import net.minecraft.block.BlockButton;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;

@Mixin(BlockButton.class)
public class MixinBlockButton {

//@Inject(method = "onBlockActivated", at = @At(value = "INVOKE", target = "Ljava/lang/Boolean;valueOf(Z)Ljava/lang/Boolean;"))
}
