package org.ozzy.adventofcode.day15;

import one.util.streamex.EntryStream;
import one.util.streamex.IntStreamEx;
import one.util.streamex.LongStreamEx;
import one.util.streamex.StreamEx;
import org.ozzy.adventofcode.common.FileReader;

import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class Driver {


    private long getLast(List<String> data, int max){
        //map from value to last index seen
        Map<Long,Integer> lastLookup = new HashMap<>();
        //value last 'spoken'
        AtomicLong lastNumber = new AtomicLong(1);

        //populate initial numbers into map & lastNumber
        EntryStream.of(data.get(0).split(","))
                .mapValues(Long::parseLong)
                .peekValues(lastNumber::set)
                .forKeyValue((k,v)->lastLookup.put(v,k+1));

        //Stream from index after initial values, to target value.
        IntStreamEx.range(lastLookup.size()+1,max+1).forEach(i -> {
            long last = lastNumber.get();
            long lastIdx = lastLookup.getOrDefault(last,0);
            lastLookup.put(last,i-1);
            lastNumber.set( lastIdx==0 ? 0 : i -1 - lastIdx);
        });

        return lastNumber.get();
    }


    public void part1(Path input) throws Exception {
        List<String> data = StreamEx.ofLines(input).toList();
        System.out.println("Part 1: "+getLast(data,2020));
    }

    public void part2(Path input) throws Exception {
        List<String> data = StreamEx.ofLines(input).toList();
        System.out.println("Part 2: "+getLast(data,30000000));
    }

    public Driver() throws Exception {
        Path input = FileReader.getPathForClassPathResource("org/ozzy/adventofcode/day15/data.txt");

        part1(input);
        part2(input);
    }

    public static void main(String[] args) throws Exception {
        long time = System.currentTimeMillis();
        Driver d = new Driver();
        System.out.println("Elapsed : "+(System.currentTimeMillis()-time));
    }
}



