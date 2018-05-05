package blue.lapis.pore.mixin.org.apache.commons.lang3;

import blue.lapis.pore.Pore;
import org.apache.commons.lang3.NotImplementedException;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = NotImplementedException.class, remap = false)
public class MixinNotImplementedException {

    @Inject(method = "<init>", at = @At("RETURN"))
    private void onInit(String message, CallbackInfoReturnable<NotImplementedException> callbackInfoReturnable){
        if (message.equalsIgnoreCase("TODO")){
            Pore.catchTodo(Thread.currentThread().getStackTrace()[2].getClassName());
        }
    }
}
