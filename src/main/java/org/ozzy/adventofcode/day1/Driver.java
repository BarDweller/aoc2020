package org.ozzy.adventofcode.day1;

import org.ozzy.adventofcode.common.FileReader;
import org.ozzy.adventofcode.common.Pair;
import org.ozzy.adventofcode.common.Triple;

import java.nio.file.Path;
import java.util.List;
import java.util.Objects;


public class Driver {

    Pair<Integer,Integer> findPair(List<Integer> values, int target){
        return values.stream()
                .map(a -> values.contains(target-a)? new Pair<>(a, target - a):null )
                .filter(Objects::nonNull)
                .findFirst()
                .get();
    }

    Triple<Integer,Integer,Integer> findTriple(List<Integer> values, int target){
        return values.stream()
                .map(i -> new Triple<>(findPair(values, 2020 - i), i))
                .filter(t -> (t.a+t.b+t.c)==target)
                .findFirst()
                .get();
    }

    Integer findDoubleMatch(List<Integer> values, int target){
        return values.stream()
                .filter(a -> (values.contains(target-a)))
                .findFirst()
                .get();
    }

    Integer findTripleMatch(List<Integer> values, int target){
        return values.stream()
                .filter(a -> findDoubleMatch(values, target-a)!=null)
                .findFirst()
                .get();
    }

    Driver() throws Exception{
        Path input = FileReader.getPathForClassPathResource("org/ozzy/adventofcode/day1/data.txt");
        List<Integer> numbers = FileReader.getFileAsSortedListOfInt(input);

        //part1
        Integer value = findDoubleMatch(numbers, 2020);
        System.out.println("Result (Part1) "+(2020-value)+" "+value+" -> "+((2020-value)*value));

        //part2 (not brilliant, as it doesn't save the double match located during the triple match)
        Integer s1 = findTripleMatch(numbers,2020);
        Integer s2 = findDoubleMatch(numbers, 2020-s1);
        Integer s3 = 2020-s1-s2;
        System.out.println("Result (Part2) "+s1+" "+s2+" "+s3+" -> "+(s1*s2*s3));

        //part1 redone using Pair
        Pair<Integer,Integer> p1 = findPair(numbers, 2020);
        System.out.println("Result (Part1) "+p1.a+" "+p1.b+" -> "+(p1.a*p1.b));

        //part2 redone using Triple (better as it doesn't need to redo the double match)
        Triple<Integer,Integer,Integer> p2 = findTriple(numbers, 2020);
        System.out.println("Result (Part2) "+p2.a+" "+p2.b+" "+p2.c+" -> "+(p2.a*p2.b*p2.c));
    }

    public static void main(String []args) throws Exception {
        Driver d = new Driver();
    }
}
