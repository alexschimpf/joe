package com.tendersaucer.joe.event;

/**
 * Created by Alex on 5/5/2016.
 */
public class PlayerInteractionEvent extends Event<com.tendersaucer.joe.event.listeners.IPlayerInteractionListener> {

    @Override
    public void notify(com.tendersaucer.joe.event.listeners.IPlayerInteractionListener listener) {
        listener.onPlayerInteraction();
    }
}
