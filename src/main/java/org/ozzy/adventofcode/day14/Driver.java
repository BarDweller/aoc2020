package org.ozzy.adventofcode.day14;

import one.util.streamex.EntryStream;
import one.util.streamex.LongStreamEx;
import one.util.streamex.StreamEx;
import org.ozzy.adventofcode.common.FileReader;

import java.math.BigInteger;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Driver {



    public void part1(Path input) throws Exception {
        List<String> data = StreamEx.ofLines(input).toList();

        Map<Long,Long> memory = new HashMap<>();

        long andMask=Long.MAX_VALUE;
        long orMask=0L;

        for(String line: data) {
            String[] parts = line.split(" = ");
            if(parts[0].equals("mask")) {
                String maskString = parts[1];
                String andString = maskString.chars()
                        .map(a -> a == '0' ? '0' : '1')
                        .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                        .toString();
                String orString = maskString.chars()
                        .map(a -> a == '1' ? '1' : '0')
                        .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                        .toString();

                andMask = Long.parseLong(andString, 2);
                orMask = Long.parseLong(orString, 2);
            }else{
                Long mem = Long.parseLong(parts[0].substring(4,parts[0].length()-1));
                Long arg = Long.parseLong(parts[1]);
                arg&=andMask;
                arg|=orMask;
                memory.put(mem,arg);
            }
        }

        long sum = LongStreamEx.of(memory.values()).sum();

        System.out.println("Part 1: "+sum);
    }

    //expand a masked address into all possible addresses.
    private List<Long> buildAddresses(String mask){
        if(mask.contains("X")){
            int idx = mask.indexOf('X');
            List<Long> result = new ArrayList<>();
            result.addAll(buildAddresses(mask.substring(0,idx)+"0"+mask.substring(idx+1)));
            result.addAll(buildAddresses(mask.substring(0,idx)+"1"+mask.substring(idx+1)));
            return result;
        }else{
            return List.of(Long.parseLong(mask,2));
        }
    }

    public void part2(Path input) throws Exception {
        List<String> data = StreamEx.ofLines(input).toList();

        Map<Long,Long> memory = new HashMap<>();

        long orMask=0;
        String maskString="";
        for(String line: data) {
            String[] parts = line.split(" = ");
            if(parts[0].equals("mask")) {
                maskString = parts[1];
                String orString = maskString.chars()
                        .map(a -> a == '1' ? '1' : '0')
                        .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                        .toString();
                orMask = Long.parseLong(orString, 2);
            }else{
                Long arg = Long.parseLong(parts[1]);
                Long mem = Long.parseLong(parts[0].substring(4,parts[0].length()-1));
                //add in the mask bits
                String memString = Long.toBinaryString(mem|orMask);
                //pad to mask length
                memString = String.join("", Collections.nCopies(maskString.length() - memString.length(), "0")) + memString;
                //blend X's from mask into memString
                memString = StreamEx.of(memString.chars().boxed())
                        .zipWith(maskString.chars().boxed(), (a,b) -> b=='X'?'X':a)
                        .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                        .toString();
                //generate all addresses from memString, and set to arg.
                buildAddresses(memString).forEach(a -> memory.put(a,arg));
            }
        }

        long sum = LongStreamEx.of(memory.values()).sum();
        System.out.println("Part 2: "+sum);
    }

    public Driver() throws Exception {
        Path input = FileReader.getPathForClassPathResource("org/ozzy/adventofcode/day14/data.txt");

        part1(input);
        part2(input);
    }

    public static void main(String[] args) throws Exception {
        long time = System.currentTimeMillis();
        Driver d = new Driver();
        System.out.println("Elapsed : "+(System.currentTimeMillis()-time));
    }
}



