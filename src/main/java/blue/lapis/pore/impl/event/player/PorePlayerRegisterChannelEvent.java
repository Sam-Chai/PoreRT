package blue.lapis.pore.impl.event.player;

import blue.lapis.pore.event.PoreEvent;
import blue.lapis.pore.impl.entity.PorePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerRegisterChannelEvent;
import org.spongepowered.api.event.network.ChannelRegistrationEvent;

import static com.google.common.base.Preconditions.checkNotNull;

public final class PorePlayerRegisterChannelEvent extends PlayerRegisterChannelEvent implements PoreEvent<ChannelRegistrationEvent.Register> {

    private final ChannelRegistrationEvent.Register handle;

    public PorePlayerRegisterChannelEvent(ChannelRegistrationEvent.Register handle) {
        super(null, null);
        this.handle = checkNotNull(handle, "handle");
    }

    @Override
    public ChannelRegistrationEvent.Register getHandle() {
        return handle;
    }

    @Override
    public String toString() {
        return toStringHelper().toString();
    }

    @Override
    public String getChannel() {
        return handle.getChannel();
    }

    @Override
    public Player getPlayer() {
        Object o = handle.getSource();
        if (o instanceof org.spongepowered.api.entity.living.player.Player){
            return PorePlayer.of((org.spongepowered.api.entity.living.player.Player) o);
        }
        return null;
    }
}
