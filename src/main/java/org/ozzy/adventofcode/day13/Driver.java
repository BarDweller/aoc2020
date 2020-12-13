package org.ozzy.adventofcode.day13;

import one.util.streamex.EntryStream;
import one.util.streamex.IntStreamEx;
import one.util.streamex.StreamEx;
import org.ozzy.adventofcode.common.FileReader;
import org.ozzy.adventofcode.common.Pair;

import javax.xml.crypto.dsig.keyinfo.KeyValue;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

public class Driver {

    public List<Integer> busesForTimestamp(long timestamp, List<Integer> possibleBuses){
        return StreamEx.of(possibleBuses).filter(a -> timestamp % a ==0).toList();
    }

    public long imod(long a, long b){
        BigInteger bia = BigInteger.valueOf(a);
        BigInteger bib = BigInteger.valueOf(b);
        return bia.modInverse(bib).longValue();
    }

    public void part1(Path input) throws Exception {
        List<String> data = StreamEx.ofLines(input).toList();
        long startStamp = Integer.parseInt(data.get(0));
        long timeStamp = startStamp;
        List<Integer> possibleBuses = StreamEx.of(data.get(1).split(",")).filter(a->!a.equals("x")).map(Integer::parseInt).toList();
        while(busesForTimestamp(timeStamp,possibleBuses).size()==0){
            timeStamp++;
        }
        long result = busesForTimestamp(timeStamp,possibleBuses).get(0) * (timeStamp - startStamp);
        System.out.println("Part 1: "+result);
    }

    public void part2(Path input) throws Exception {
        List<String> data = StreamEx.ofLines(input).toList();
        List<Integer> possibleBuses = StreamEx.of(data.get(1).split(",")).map(a->a.equals("x")?"0":a).map(Integer::parseInt).toList();
        List<Map.Entry<Integer, Integer>> validBusesWithDeltas = EntryStream.of(possibleBuses)
                .filterValues(a -> a != 0)
                .toList();

        // combination of https://www.freecodecamp.org/news/how-to-implement-the-chinese-remainder-theorem-in-java-db88a3f1ffe0/
        // and https://www.youtube.com/watch?v=zIFehsBHB8o
        long product = StreamEx.of(validBusesWithDeltas).mapToLong(Map.Entry::getValue).reduce((a,b) -> a*b).getAsLong();
        long sum = StreamEx.of(validBusesWithDeltas)
                // crt solves t for (t % bi = offset)
                // we have (t+offset)%bi = 0
                //      eg, given 7,13,x,31...
                //          (ts+0)%7  = 0
                //          (ts+1)%13 = 0
                //          (ts+3)%31 = 0
                // reorg for (t%bi=offset), to get new remainders to use during crt
                // t% 7 ==  0 %  7 =  0 ==  0
                // t%13 == -1 % 13 = -1 == 12 (can't use negative remainder, add a back to it to make it a positive)
                // t%31 == -3 % 31 = -3 == 28 (can't use negative remainder, add a back to it to make it a positive)
                //              (Ni = N/ni)            * (Xi = imod( Ni, busid) )                     * (Bi = ((-1 * busoffset)%busid)+busid)
                .mapToLong(a -> (product/a.getValue()) * imod( product/a.getValue(), a.getValue()) * (((-1 * a.getKey())%a.getValue())+a.getValue()))
                .sum();
        System.out.println("Part 2: "+(sum%product));
    }

    public Driver() throws Exception {
        Path input = FileReader.getPathForClassPathResource("org/ozzy/adventofcode/day13/testdata.txt");

        part1(input);
        part2(input);
    }

    public static void main(String[] args) throws Exception {
        long time = System.currentTimeMillis();
        Driver d = new Driver();
        System.out.println("Elapsed : "+(System.currentTimeMillis()-time));
    }
}


