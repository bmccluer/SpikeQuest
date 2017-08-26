package com.brendanmccluer.spikequest.desktop;

import com.badlogic.gdx.Gdx;

import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Scanner;
import java.util.concurrent.Exchanger;
import java.util.logging.FileHandler;

/**
 * Created by brend on 8/5/2017.
 */

public class JavaCode {
    public static void main (String[] arg) {
        try {
            Scanner scanner = new Scanner(Gdx.files.internal("jsonSkins/Uiskin").read());
            //string = scanner.nextLine();
        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }
}
