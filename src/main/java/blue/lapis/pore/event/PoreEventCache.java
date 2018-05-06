package blue.lapis.pore.event;

import org.bukkit.event.Event;

import java.util.HashMap;

public class PoreEventCache {
    private HashMap<Class<?>, Event> eventCache = new HashMap<>();

    public PoreEvent<?> getLastEvent(Class<?> eventClass){
        Event event = eventCache.get(eventClass);
        PoreEvent<?> poreEvent = null;
        if (event instanceof PoreEvent<?>){
            poreEvent = (PoreEvent<?>) event;
        }
        return poreEvent;
    }

    void addEventToCache(Class<?> eventClass, Event event){
        eventCache.put(eventClass, event);
    }
}
