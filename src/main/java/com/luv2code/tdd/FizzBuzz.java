package com.luv2code.tdd;

public class FizzBuzz {
    public static String compute(int value) {
        StringBuilder sb = new StringBuilder();
        if(isDividableBy3(value)) {
            sb.append("Fizz");
        }
        if(isDividableBy5(value)) {
            sb.append("Buzz");
        }
        if(sb.isEmpty()) {
            sb.append(value);
        }
        return sb.toString();
    }

//    public static String compute(int value) {
//        return (isDividableBy3(value) && isDividableBy5(value)) ?
//                "FizzBuzz" :
//                isDividableBy3(value) ?
//                        "Fizz" :
//                        isDividableBy5(value) ?
//                                "Buzz" :
//                                String.valueOf(value);
//    }

    private static boolean isDividableBy3(int value) {
        return value % 3 == 0;
    }

    private static boolean isDividableBy5(int value) {
        return value % 5 == 0;
    }
}
