package org.ozzy.adventofcode.day7;

import one.util.streamex.StreamEx;
import org.ozzy.adventofcode.common.FileReader;
import org.ozzy.adventofcode.common.Pair;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Driver {

    private static class Bag {
        final String desc;
        final Set<Pair<Integer, String>> holds;
        public Bag(String bagDesc){
            holds = new HashSet<>();
            String[] p = bagDesc.substring(0,bagDesc.length()-1).replace(" bags contain", ",").split(", ");
            desc = p[0];
            for(int i=1; i<p.length; i++){
                String current = p[i].replace(" bags", "").replace(" bag","");
                if("no other".equals(current)){
                    break;
                }else{
                    holds.add(new Pair<>(Integer.valueOf(current.substring(0, 1)), current.substring(2)));
                }
            }
        }
        public String getName(){
            return desc;
        }
        public Integer getNestedCount(Map<String, Bag> bags){
            AtomicInteger result = new AtomicInteger(0);
            holds.stream().flatMap(a -> Collections.nCopies(a.a, a.b).stream())
                    .peek(a -> result.incrementAndGet())
                    .map(bags::get)
                    .filter(Objects::nonNull)
                    .map(a -> a.getNestedCount(bags))
                    .forEach(result::addAndGet);
            return result.get();
        }
        public Set<String> getAllNestedBagTypes(Map<String, Bag> bags){
            Set<String> result = new HashSet<>();
            result.addAll(holds.stream()
                    .map(a -> a.b)
                    .peek(result::add)
                    .map(bags::get)
                    .filter(Objects::nonNull)
                    .flatMap(a -> a.getAllNestedBagTypes(bags).stream())
                    .collect(Collectors.toSet()));
            return result;
        }
    }

    public void part1(Path input) throws Exception {
        Map<String,Bag> bags = Files.lines(input).map(Bag::new).collect(Collectors.toMap(Bag::getName, Function.identity()));
        System.out.println("part 1: "+bags.keySet().stream().map(bags::get).filter(a -> a.getAllNestedBagTypes(bags).contains("shiny gold")).count());
    }

    public void part2(Path input) throws Exception {
        Map<String,Bag> bags = Files.lines(input).map(Bag::new).collect(Collectors.toMap(Bag::getName, Function.identity()));
        System.out.println("part 2: "+bags.get("shiny gold").getNestedCount(bags));
    }

    public Driver() throws Exception {
        Path input = FileReader.getPathForClassPathResource("org/ozzy/adventofcode/day7/data.txt");
        part1(input);
        part2(input);
    }

    public static void main(String[] args) throws Exception {
        Driver d = new Driver();
    }
}
