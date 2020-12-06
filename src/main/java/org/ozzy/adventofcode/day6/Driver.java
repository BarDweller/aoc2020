package org.ozzy.adventofcode.day6;

import one.util.streamex.MoreCollectors;
import one.util.streamex.StreamEx;
import org.ozzy.adventofcode.common.FileReader;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.stream.Collectors;


public class Driver {

    Integer toBitMap(String answers) {
        if (answers.isEmpty()) return null;
        else {
            return answers.chars()
                    .boxed()
                    .map(a -> (1 << (a - 'a' + 1)))
                    .reduce((a, b) -> a | b)
                    .get();
        }
    }

    public void part1b(Path input) throws Exception {
        Integer part1 = StreamEx.of(Files.lines(input).map(this::toBitMap))
                .collapse( (a,b) -> a != null,
                        MoreCollectors.filtering(Objects::nonNull, Collectors.reducing(0, (a,b) -> a | b)))
                .map(Integer::bitCount)
                .reduce(0, Integer::sum);

        System.out.println("Part 1:" + part1);
    }

    public void part2b(Path input) throws Exception {
        Integer part2 = StreamEx.of(Files.lines(input).map(this::toBitMap))
                .collapse( (a,b) -> a != null,
                        MoreCollectors.filtering(Objects::nonNull, Collectors.reducing(Integer.MAX_VALUE, (a,b) -> a & b)))
                .map(Integer::bitCount)
                .reduce(0, Integer::sum);

        System.out.println("Part 2:" + part2);
    }

    public Driver() throws Exception {
        Path input = FileReader.getPathForClassPathResource("org/ozzy/adventofcode/day6/data.txt");

        part1b(input);
        part2b(input);
    }

    public static void main(String[] args) throws Exception {
        Driver d = new Driver();
    }
}
