package org.ozzy.adventofcode.day10;

import one.util.streamex.StreamEx;
import org.ozzy.adventofcode.common.FileReader;

import java.nio.file.Path;
import java.util.*;

public class Driver {

    long findChains(int seatValue, List<Integer> adapters, Map<String, Long> cache) {
        //shortcut =)
        Long cacheHit = cache.get(seatValue+":"+adapters.toString());
        if(cacheHit!=null) return cacheHit;

        long total = 0;
        if(adapters.size()==1){
            //terminal case
            total = adapters.get(0)-seatValue < 4 ? 1 : 0;
        }else {
            //leaf case
            List<Integer> clone = new ArrayList<>(adapters);
            while (!clone.isEmpty() && clone.get(0) - seatValue < 4) {
                //evaluate branch.
                total += findChains(clone.remove(0), clone, cache);
            }
        }

        cache.put(seatValue+":"+adapters.toString(), total);
        return total;
    }

    long scoreChain(List<Integer> adapters){
        long ones = StreamEx.ofSubLists(adapters,2,1)
                .filter(a -> (a.get(1) - a.get(0)) == 1)
                .count();

        long threes = StreamEx.ofSubLists(adapters,2,1)
                .filter(a -> (a.get(1) - a.get(0)) == 3)
                .count();

        return ones * threes;
    }

    public void part1(Path input) throws Exception {
        List<Integer> adapters = StreamEx.ofLines(input).map(Integer::parseInt).toList();
        adapters.add(0);
        adapters.sort(Integer::compareTo);
        adapters.add(adapters.get(adapters.size()-1)+3);

        System.out.println("Part 1: "+scoreChain(adapters));
    }

    public void part2(Path input) throws Exception {
        List<Integer> adapters = StreamEx.ofLines(input).map(Integer::parseInt).toList();
        adapters.sort(Integer::compareTo);
        adapters.add(adapters.get(adapters.size()-1)+3);

        long countChains = findChains(0,adapters, new HashMap<>());
        System.out.println("Part 2: "+countChains);
    }

    public Driver() throws Exception {
        Path input = FileReader.getPathForClassPathResource("org/ozzy/adventofcode/day10/data.txt");
        part1(input);
        part2(input);
    }

    public static void main(String[] args) throws Exception {
        Driver d = new Driver();
    }
}
