package org.example.services;

import org.springframework.stereotype.Component;

@Component("frenchGreetingService")
public class FrenchGreetingService implements IGreetingService {

    @Override
    public void greet() {
        System.out.println("Bonjour!");
    }
}
