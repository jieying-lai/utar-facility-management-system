package my.edu.utar.data;

import my.edu.utar.ui.MainMenu;

import java.util.Scanner;

/**
 * Main.java
 * Entry point of the UTAR Smart Campus Management System.
 * Run this class to start the program.
 */
public class Main {

    public static void main(String[] args) {
        // Initialize data files on startup
        FileManager.initFiles();

        // Start the main menu
        Scanner sc = new Scanner(System.in);
        new MainMenu(sc).show();
        sc.close();
    }
}