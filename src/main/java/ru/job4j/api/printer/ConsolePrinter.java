package ru.job4j.api.printer;

import org.springframework.stereotype.Component;

@Component
public class ConsolePrinter implements Printer {

    @Override
    public void print(String text) {
        System.out.println(text);
    }
}
