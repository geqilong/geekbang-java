package org.geektimes.projects.user.function;

import java.util.function.Function;

/**
 * A function interface for action with {@link Throwable}
 *
 * @see Function
 * @see Throwable
 */
public interface ThrowableAction {

    /**
     * Executes the action
     *
     * @throws Throwable if met with error
     */
    void execute() throws Throwable;

    /**
     * Executes {@link ThrowableAction}
     *
     * @param action {@link ThrowableAction}
     * @throws RuntimeException wrap {@link Exception} to {@link RuntimeException}
     */
    static void execute(ThrowableAction action) throws RuntimeException {
        try {
            action.execute();
        } catch (Throwable throwable) {
            throw new RuntimeException(throwable);
        }
    }
}
