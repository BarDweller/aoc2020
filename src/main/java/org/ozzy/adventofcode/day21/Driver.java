package org.ozzy.adventofcode.day21;

import one.util.streamex.EntryStream;
import one.util.streamex.StreamEx;
import org.ozzy.adventofcode.common.FileReader;
import org.ozzy.adventofcode.common.Pair;

import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Driver {

    public void part1and2(Path input) throws Exception {
        List<String> data = StreamEx.ofLines(input).toList();

        //load all the data into typed stuff to avoid string handling =)
        List<Pair<Set<String>, Set<String>>> all = StreamEx.of(data)
                .map( line -> line.substring(0,line.length()-1).split(" \\(contains ") )
                .map( p -> new Pair<>(StreamEx.of(p[0].split(" ")).toSet()
                                    , StreamEx.of(p[1].split(", ")).toSet()))
                .toList();

        //build up the total sets
        Set<String> allIngredients = all.stream().flatMap(pair -> pair.a.stream()).collect(Collectors.toSet());
        Set<String> allAllergens = all.stream().flatMap(pair -> pair.b.stream()).collect(Collectors.toSet());
        Map<String,Integer> counts = all.stream().flatMap(pair -> pair.a.stream()).collect(Collectors.groupingBy(Function.identity(), Collectors.summingInt(i -> 1)));

        //suspect everything!!
        Map<String, Set<String>> potentials = StreamEx.of(allAllergens)
                .map(allergen -> new Pair<String,Set<String>>(allergen, new HashSet<>(allIngredients)))
                .toMap(p -> p.a, p->p.b);
        //cull to actually viable =)
        all.forEach( p -> p.b.forEach( allergen -> allIngredients.stream().filter(candidate -> !p.a.contains(candidate)).forEach(candidate -> potentials.get(allergen).remove(candidate))) );

        //count the impossibles
        Set<String> allPossibles = StreamEx.of(potentials.values()).flatMap(Collection::stream).toSet();
        Integer impossibleCount = StreamEx.of(allIngredients).filter(ingredient -> !allPossibles.contains(ingredient)).map(counts::get).reduce(0, Integer::sum);

        System.out.println("Part 1: "+impossibleCount);

        //day 16 part2 redux
        //sort possibles map by length of value list, shortest first.
        Map<String, String> resultMap = new HashMap<>();
        boolean done=false;
        while (!done){
            List<String> found = new ArrayList<>();
            //hunt for any single possibilities, and lock them in.
            EntryStream.of(potentials)
                    .filterValues(v->v.size()==1)
                    .mapKeyValue((k,v) -> new Pair<>(k, v.stream().findFirst().get()))
                    .forEach(p -> { found.add(p.b); resultMap.put(p.a, p.b); });
            //remove the ones we just handled
            EntryStream.of(potentials).forKeyValue((k,v)->v.removeAll(found));
            //we're done when all possibles are now empty.
            done = EntryStream.of(potentials).allMatch((k,v) -> v.size()==0);
        }

        TreeMap<String, String> sortedMap = new TreeMap<>(resultMap);
        String list = String.join(",", sortedMap.values());

        System.out.println("Part 2: "+list);
    }


    public Driver() throws Exception {
        Path input = FileReader.getPathForClassPathResource("org/ozzy/adventofcode/day21/data.txt");
        part1and2(input);
    }


    public static void main(String[] args) throws Exception {
        long time = System.currentTimeMillis();
        Driver d = new Driver();
        System.out.println("Elapsed : "+(System.currentTimeMillis()-time));
    }
}





