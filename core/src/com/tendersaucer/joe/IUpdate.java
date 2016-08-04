package com.tendersaucer.joe;

/**
 * Interface for updated objects
 * <p/>
 * Created by Alex on 4/8/2016.
 */
public interface IUpdate {

    /**
     * Updates the object, returning true when complete
     *
     * @return true when no longer needs updated
     */
    boolean update();
}
