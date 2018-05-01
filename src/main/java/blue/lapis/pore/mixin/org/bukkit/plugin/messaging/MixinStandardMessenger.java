package blue.lapis.pore.mixin.org.bukkit.plugin.messaging;

import blue.lapis.pore.Pore;
import blue.lapis.pore.impl.entity.PorePlayer;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.bukkit.plugin.messaging.PluginMessageListenerRegistration;
import org.spongepowered.api.Platform;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.network.ChannelBinding;
import org.spongepowered.api.network.PlayerConnection;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static java.nio.charset.StandardCharsets.*;

@Mixin(targets = "org/bukkit/plugin/messaging/StandardMessenger", remap = false)
public abstract class MixinStandardMessenger {



    @Inject(method = "registerIncomingPluginChannel", at = @At("RETURN"))
    private void onRegisterIncomingPluginChannel(Plugin plugin, String channel, PluginMessageListener listener
            , CallbackInfoReturnable<PluginMessageListenerRegistration> ci){
        ChannelBinding.RawDataChannel rawDataChannel = Sponge.getChannelRegistrar().createRawChannel(Pore.getPlugin(plugin),
                channel);
        rawDataChannel.addListener(Platform.Type.SERVER, (data, connection, side) -> {
            System.out.println("Minix Channel are ready!!!");
            if (connection instanceof PlayerConnection) {
                org.bukkit.entity.Player player = PorePlayer.of(((PlayerConnection) connection).getPlayer());
                byte[] bytedata = data.readBytes(data.available());
                String message = new String(bytedata, UTF_8);
                Pore.getLogger().debug(player.getDisplayName() + ": " + message);
                Pore.getServer().getMessenger().dispatchIncomingMessage(player, channel, bytedata);
            }
        });
    }
}
