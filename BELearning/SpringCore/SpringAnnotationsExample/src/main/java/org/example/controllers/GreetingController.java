package org.example.controllers;

import org.example.services.IGreetingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class GreetingController {

    private final IGreetingService greetingService;

    // This constructor will prioritize the FrenchGreetingService, when both English and French services are available.
    @Autowired
    public GreetingController(@Qualifier("frenchGreetingService") IGreetingService greetingService){
        this.greetingService = greetingService;
    }

    public void sayGreeting(){
        greetingService.greet();
    }
}
