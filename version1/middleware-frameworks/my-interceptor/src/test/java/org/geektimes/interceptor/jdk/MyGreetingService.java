package org.geektimes.interceptor.jdk;


import org.eclipse.microprofile.faulttolerance.Bulkhead;

@Bulkhead(value = 1)
public class MyGreetingService implements GreetingService {

    @Override
    public String greet(String words) {
        String message = "Yo!" + words;
        System.out.println(message);
        return message;
    }

}
