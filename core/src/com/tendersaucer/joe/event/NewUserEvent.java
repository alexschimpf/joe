package com.tendersaucer.joe.event;

import com.tendersaucer.joe.event.listeners.INewUserListener;

/**
 * Created by Alex on 8/1/2016.
 */
public class NewUserEvent extends Event<INewUserListener> {

    @Override
    public void notify(INewUserListener listener) {
        listener.onNewUser();
    }
}
