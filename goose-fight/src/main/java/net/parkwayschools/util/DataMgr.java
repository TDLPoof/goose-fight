package net.parkwayschools.util;

import java.io.File;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DataMgr {

    //from Tyler & Anish U2 Project Engine
    public static Set<File> getDataListing(String module) {
        return Stream.of(new File("data/"+module).listFiles())
                .filter(file -> !file.isDirectory())
                .collect(Collectors.toSet());
    }
}
