package my.edu.utar;

import my.edu.utar.data.FileManager;
import my.edu.utar.ui.MainMenu;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        FileManager.initFiles();
        Scanner sc = new Scanner(System.in);
        new MainMenu(sc).show();
        sc.close();
    }
}