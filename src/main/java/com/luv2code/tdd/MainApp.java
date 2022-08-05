package com.luv2code.tdd;

import java.util.stream.IntStream;

public class MainApp {

    public static void main(String[] args) {
        IntStream.rangeClosed(1, 100).mapToObj(FizzBuzz::compute).forEach(System.out::println);
    }
}
