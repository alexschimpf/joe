package com.tendersaucer.joe.event;

/**
 * Created by Alex on 8/1/2016.
 */
public class NewUserEvent extends Event<com.tendersaucer.joe.event.listeners.INewUserEventListener> {

    @Override
    public void notify(com.tendersaucer.joe.event.listeners.INewUserEventListener listener) {
        listener.onNewUser();
    }
}
