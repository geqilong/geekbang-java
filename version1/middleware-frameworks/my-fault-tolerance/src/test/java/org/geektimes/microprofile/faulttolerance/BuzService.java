package org.geektimes.microprofile.faulttolerance;

import org.eclipse.microprofile.faulttolerance.CircuitBreaker;
import org.eclipse.microprofile.faulttolerance.Fallback;
import org.eclipse.microprofile.faulttolerance.Timeout;

@CircuitBreaker
public interface BuzService {
    @Timeout(value = 50)
    @Fallback(fallbackMethod = "fallbackTask")
    void task();
}
