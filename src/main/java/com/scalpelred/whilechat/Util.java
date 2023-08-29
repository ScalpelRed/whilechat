package com.scalpelred.whilechat;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class Util {

    public static String[] readAllLines(String filePath) throws FileNotFoundException {
        ArrayList<String> lines = new ArrayList<>();
        Scanner scanner = new Scanner(new File(System.getProperty("user.dir") + "/" + filePath));

        while (scanner.hasNextLine()) lines.add(scanner.nextLine());

        return lines.toArray(new String[0]);
    }
}
