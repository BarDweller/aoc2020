package org.ozzy.adventofcode.day9;

import one.util.streamex.EntryStream;
import one.util.streamex.IntStreamEx;
import one.util.streamex.StreamEx;
import org.ozzy.adventofcode.common.FileReader;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class Driver {

    public long part1(Path input) throws Exception {
        List<Long> lines = Files.lines(input).mapToLong(Long::parseLong).boxed().collect(Collectors.toList());
        int preamble = lines.get(0).intValue();
        lines.remove(0);
        long bad = EntryStream.of(lines)
                .skip(preamble)
                .filterKeyValue( (idx,a) -> ! StreamEx.of(lines.subList(idx - preamble, idx))
                        .cross(lines.subList(idx - preamble, idx))
                        .mapKeyValue(Long::sum)
                        .toList()
                        .contains(a)
                )
                .mapKeyValue( (idx, a) -> a)
                .findFirst()
                .get();
        System.out.println("Part 1: "+bad);
        return bad;
    }

    public void part2(Path input, long bad) throws Exception {
        List<Long> lines = Files.lines(input).mapToLong(Long::parseLong).boxed().collect(Collectors.toList());
        lines.remove(0);

        List<Long> range = IntStreamEx.range(2,50)
                .boxed()
                .map ( i -> StreamEx.ofSubLists(lines,i,1)
                        .filter( a -> StreamEx.of(a).reduce((long) 0, Long::sum) == bad).findFirst() )
                .filter(Optional::isPresent)
                .map(Optional::get)
                .findFirst()
                .get();


        long result = range.stream().min(Long::compare).get() + range.stream().max(Long::compare).get();

        System.out.println("Part 2: "+result);
    }

    public Driver() throws Exception {
        Path input = FileReader.getPathForClassPathResource("org/ozzy/adventofcode/day9/data.txt");
        long bad = part1(input);
        part2(input, bad);
    }

    public static void main(String[] args) throws Exception {
        Driver d = new Driver();
    }
}
