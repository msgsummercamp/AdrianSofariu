package org.example.services;

import org.springframework.stereotype.Service;

@Service("englishGreetingService")
public class EnglishGreetingService implements IGreetingService{

    @Override
    public void greet() {
        System.out.println("Hello!");
    }
}
