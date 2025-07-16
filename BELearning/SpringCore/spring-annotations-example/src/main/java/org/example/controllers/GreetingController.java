package org.example.controllers;

import org.example.services.IGreetingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class GreetingController {

    private final IGreetingService greetingService;

    /**
     * Constructs a GreetingController with the specified IGreetingService.
     * This constructor will prioritize the FrenchGreetingService when multiple implementations are available.
     *
     * @param greetingService the IGreetingService implementation to use, prioritizing FrenchGreetingService in case of ambiguity
     */
    @Autowired
    public GreetingController(@Qualifier("frenchGreetingService") IGreetingService greetingService){
        this.greetingService = greetingService;
    }

    public void sayGreeting(){
        greetingService.greet();
    }
}
