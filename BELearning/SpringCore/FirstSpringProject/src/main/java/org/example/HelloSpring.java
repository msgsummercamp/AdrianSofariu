package org.example;

public class HelloSpring {
    private String message;

    public void setMessage(String message) {
        this.message = message;
    }

    public void sayHello() {
        System.out.println("Your message is: " + message);
    }
}
