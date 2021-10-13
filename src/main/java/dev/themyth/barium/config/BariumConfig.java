package dev.themyth.barium.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import oshi.util.tuples.Triplet;

import java.io.File;
import java.util.List;

public class BariumConfig implements ConfigData {
    public static String[] ignoredMods= {""};

    public static long msBetweenUpdates = -1;
}
