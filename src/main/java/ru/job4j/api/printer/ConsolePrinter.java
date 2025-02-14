package ru.job4j.api.printer;

public class ConsolePrinter implements Printer {

    @Override
    public void print(String text) {
        System.out.println(text);
    }
}
