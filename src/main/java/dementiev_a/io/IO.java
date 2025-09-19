package dementiev_a.io;

import java.util.Scanner;

public class IO {
    private static final Scanner scanner = new Scanner(System.in);

    public static String readLine(String message) {
        System.out.println(message);
        return scanner.nextLine();
    }

    public static void print(String message) {
        System.out.println(message);
    }

    public static void printError(String message) {
        System.err.println(message);
    }
}
