package org.ozzy.adventofcode.common;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

public class FileReader {

    public static Path getPathForClassPathResource(String resourceName) throws URISyntaxException {
        return Paths.get(ClassLoader.getSystemResource(resourceName).toURI());
    }


    public static List<String> getFileAsListOfString(Path input) throws IOException {
        return Files.lines(input).collect(Collectors.toList());
    }

    public static List<String> getFileAsSortedListOfString(Path input) throws IOException {
        return Files.lines(input).sorted().collect(Collectors.toList());
    }

    public static List<Integer> getFileAsListOfInt(Path input) throws IOException {
        return Files.lines(input).map(a->Integer.parseInt(a)).collect(Collectors.toList());
    }

    public static List<Integer> getFileAsSortedListOfInt(Path input) throws IOException {
        return Files.lines(input).map(a->Integer.parseInt(a)).sorted().collect(Collectors.toList());
    }



}
