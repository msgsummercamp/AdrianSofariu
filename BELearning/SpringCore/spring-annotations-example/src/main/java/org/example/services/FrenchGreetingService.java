package org.example.services;

import org.springframework.stereotype.Service;

@Service("frenchGreetingService")
public class FrenchGreetingService implements IGreetingService {

    @Override
    public void greet() {
        System.out.println("Bonjour!");
    }
}
