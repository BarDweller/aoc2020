package org.ozzy.adventofcode.day6;

import org.ozzy.adventofcode.common.FileReader;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.LinkedList;


public class Driver {

    int toBitMap(String answers) {
        if (answers.isEmpty()) return -1;
        else {
            return answers.chars()
                    .boxed()
                    .map(a -> (1 << (a - 'a' + 1)))
                    .reduce((a, b) -> a | b)
                    .get();
        }
    }

    public void part1(Path input) throws Exception {
        LinkedList<Integer> allAnswers = new LinkedList<>(Arrays.asList(-1));
        Files.lines(input)
                .map(this::toBitMap)
                .forEach(a -> {
                    if (a != -1) {
                        if (allAnswers.peekFirst() == -1) {
                            allAnswers.removeFirst();
                            allAnswers.addFirst(a);
                        } else {
                            allAnswers.addFirst(allAnswers.removeFirst() | a);
                        }
                    } else {
                        allAnswers.addFirst(-1);
                    }
                });
        Integer part1 = allAnswers.stream().map(Integer::bitCount).reduce(0, Integer::sum);
        System.out.println("Part 1:" + part1);
    }

    public void part2(Path input) throws Exception {
        LinkedList<Integer> allAnswers = new LinkedList<>(Arrays.asList(-1));
        Files.lines(input)
                .map(this::toBitMap)
                .forEach(a -> {
                    if (a != -1) {
                        if (allAnswers.peekFirst() == -1) {
                            allAnswers.removeFirst();
                            allAnswers.addFirst(a);
                        } else {
                            allAnswers.addFirst(allAnswers.removeFirst() & a);
                        }
                    } else {
                        allAnswers.addFirst(-1);
                    }
                });
        Integer part1 = allAnswers.stream().map(Integer::bitCount).reduce(0, Integer::sum);
        System.out.println("Part 2:" + part1);
    }

    public Driver() throws Exception {
        Path input = FileReader.getPathForClassPathResource("org/ozzy/adventofcode/day6/data.txt");
        part1(input);
        part2(input);
    }

    public static void main(String[] args) throws Exception {
        Driver d = new Driver();
    }
}
