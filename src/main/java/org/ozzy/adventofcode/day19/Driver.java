package org.ozzy.adventofcode.day19;

import one.util.streamex.EntryStream;
import one.util.streamex.StreamEx;
import org.ozzy.adventofcode.common.FileReader;

import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Driver {


    public String build(Map<String,List<List<String>>> ruleMap, Map<String,String> cache, String index){
        if(cache.containsKey(index)) return cache.get(index);

        StringBuilder sb = new StringBuilder();
        List<List<String>> current = ruleMap.get(index);
        if(current==null) throw new IllegalStateException("THROW ANOTHER ELF TO THE SHARKS MATEY! " + index);

        if(current.size()==1 && current.get(0).size()==1 && current.get(0).get(0).charAt(0)>='a' && current.get(0).get(0).charAt(0)<='z'){
            cache.put(index, current.get(0).get(0));
            return current.get(0).get(0);
        }

        current.get(0).forEach(n -> sb.append(build(ruleMap, cache, n)));
        if(current.size()>1){
            current.stream().skip(1).forEach(n -> { sb.append("|"); n.forEach( o -> sb.append( build(ruleMap, cache, o) )); } );
        }
        String result = "(?:"+sb.toString()+")";

        cache.put(index,result);
        return result;
    }

    public void part1(Path input) throws Exception {
        List<String> data = StreamEx.ofLines(input).toList();

        List<String> ruleStrings = data.stream().takeWhile(s -> !s.isEmpty()).collect(Collectors.toList());
        List<String> words = data.stream().dropWhile(s -> !s.isEmpty()).skip(1).collect(Collectors.toList());

        Map<String,String> rawRuleMap = StreamEx.of(ruleStrings).toMap(line -> line.split(": ")[0], line  -> line.split(": ")[1]);
        Map<String,List<List<String>>> ruleMap = EntryStream.of(rawRuleMap)
                .mapValues(v -> v.startsWith("\"")?
                                List.of(List.of(v.substring(1,2)))
                                :
                                v.contains(" | ")?
                                        List.of(Arrays.asList(v.split(" \\| ")[0].split(" ")),Arrays.asList(v.split(" \\| ")[1].split(" ")))
                                        :
                                        List.of(Arrays.asList(v.split(" ")))
                        ).toMap();
        String regex = build(ruleMap, new HashMap<>(), "0");

        long count = words.stream().filter(w -> w.matches(regex)).count();

        System.out.println("Part 1: "+count);
    }

    public void part2(Path input) throws Exception {
        List<String> data = StreamEx.ofLines(input).toList();

        List<String> ruleStrings = data.stream().takeWhile(s -> !s.isEmpty()).collect(Collectors.toList());
        List<String> words = data.stream().dropWhile(s -> !s.isEmpty()).skip(1).collect(Collectors.toList());

        Map<String,String> rawRuleMap = StreamEx.of(ruleStrings).toMap(line -> line.split(": ")[0], line  -> line.split(": ")[1]);

        rawRuleMap.put("8","42 | 42 8");
        rawRuleMap.put("11","42  31 | 42 11 31");

        Map<String,List<List<String>>> ruleMap = EntryStream.of(rawRuleMap)
                .mapValues(v -> v.startsWith("\"")?
                        List.of(List.of(v.substring(1,2)))
                        :
                        v.contains(" | ")?
                                List.of(Arrays.asList(v.split(" \\| ")[0].split(" ")),Arrays.asList(v.split(" \\| ")[1].split(" ")))
                                :
                                List.of(Arrays.asList(v.split(" ")))
                ).toMap();

        // "Fix" Rules 8 and 11 into valid regex.
        // We can do this by preseeding our regex cache for those rules with something
        // we provide here, that way the build recursion will just use what we gave instead
        // of trying to figure it out =)
        HashMap<String,String> cache = new HashMap<>();

        // 8 is simple enough, it's either 42 or 42 42 or 42 42 42 etc, which is basically (42)+ in regex.
        String fortyTwo = build(ruleMap, cache, "42");
        cache.put("8","(?:"+fortyTwo+"+)");

        // 11 expands to 42 31, or 42 42 31 31 or 42 42 42 31 31 31 etc
        // means we'd need a regex that only matches even numbers of 2 groups, which feels .. messy
        //
        // we could be smart.. or we could guess the max recursion depth the input ;)
        //
        // If we figure the input won't go more than say 10 layers deep, we can code a static regex that
        // will look for that!!
        String thirtyOne = build(ruleMap, cache, "31");
        StringBuilder sb = new StringBuilder();
        sb.append("(?:");
        for(int depth=1; depth<10; depth++){
            if(depth!=1){
                sb.append("|");
            }
            sb.append("(?:");
            sb.append(String.valueOf(fortyTwo).repeat(depth));
            sb.append(String.valueOf(thirtyOne).repeat(depth));
            sb.append(")");
        }
        sb.append(")");
        cache.put("11",sb.toString());

        //With 8 and 11 suitably corrected, we can allow it to build the main expression =)
        String regex = build(ruleMap, cache, "0");

        //then just do what we did for part1 =)
        long count = words.stream().filter(w -> w.matches(regex)).count();
        System.out.println("Part 2: "+count);
    }

    public Driver() throws Exception {
        Path input = FileReader.getPathForClassPathResource("org/ozzy/adventofcode/day19/data.txt");
        part1(input);
        part2(input);
    }


    public static void main(String[] args) throws Exception {
        long time = System.currentTimeMillis();
        Driver d = new Driver();
        System.out.println("Elapsed : "+(System.currentTimeMillis()-time));
    }
}



