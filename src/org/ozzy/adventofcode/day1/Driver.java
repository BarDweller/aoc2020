package org.ozzy.adventofcode.day1;

import org.ozzy.adventofcode.common.FileReader;

import java.nio.file.Path;
import java.util.List;

public class Driver {

    Integer findDoubleMatch(List<Integer> values, int target){
        return values.stream().filter(a -> (values.contains(target-a))).findFirst().get();
    }

    Integer findTripleMatch(List<Integer> values, int target){
        return values.stream().filter(a -> findDoubleMatch(values, 2020-a)!=null).findFirst().get();
    }

    Driver() throws Exception{
        Path input = FileReader.getPathForClassPathResource("org/ozzy/adventofcode/day1/data.txt");
        List<Integer> numbers = FileReader.getFileAsSortedListOfInt(input);

        //part1
        Integer value = findDoubleMatch(numbers, 2020);
        System.out.println("Result "+(2020-value)+" "+value+" -> "+((2020-value)*value));

        //part2 (not brilliant, as it doesn't save the double match located during the triple match)
        Integer s1 = findTripleMatch(numbers,2020);
        Integer s2 = findDoubleMatch(numbers, 2020-s1);
        Integer s3 = 2020-s1-s2;
        System.out.println("Result "+s1+" "+s2+" "+s3+" -> "+(s1*s2*s3));
    }

    public static void main(String []args) throws Exception {
        Driver d = new Driver();
    }
}
