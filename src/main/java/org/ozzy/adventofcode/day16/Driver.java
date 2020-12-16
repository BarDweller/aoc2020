package org.ozzy.adventofcode.day16;

import one.util.streamex.EntryStream;
import one.util.streamex.IntStreamEx;
import one.util.streamex.LongStreamEx;
import one.util.streamex.StreamEx;
import org.ozzy.adventofcode.common.FileReader;

import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Driver {

    private static class Rule {
        String description;
        int lhsLow,lhsHigh;
        int rhsLow,rhsHigh;

        public Rule(String decl){
            String p1[] = decl.split(": ");
            description = p1[0];
            String p2[] = p1[1].split(" or ");
            String lhs[] = p2[0].split("-");
            lhsLow = Integer.parseInt(lhs[0]);
            lhsHigh = Integer.parseInt(lhs[1]);
            String rhs[] = p2[1].split("-");
            rhsLow = Integer.parseInt(rhs[0]);
            rhsHigh = Integer.parseInt(rhs[1]);
        }

        public boolean isValueAcceptable(int value){
            return (value>=lhsLow && value<=lhsHigh) || (value>=rhsLow && value<=rhsHigh);
        }

        public String toString(){
            return description+": "+lhsLow+"-"+lhsHigh+" or "+rhsLow+"-"+rhsHigh;
        }
    }

    private List<Rule> rules;
    private List<Integer> myTicket;
    private List<List<Integer>> otherTickets;


    public void part1and2(Path input) throws Exception {
        rules = new ArrayList<>();
        myTicket = new ArrayList<>();
        otherTickets = new ArrayList<>();

        AtomicInteger phase = new AtomicInteger(0);
        List<String> data = StreamEx.ofLines(input).toList();
        data.forEach(line -> {
            if( ! (line.equals("your ticket:")||line.equals("nearby tickets:")) )  {
                if (line.isEmpty()) {
                    phase.incrementAndGet();
                } else {
                    switch (phase.get()) {
                        case 0:
                            rules.add(new Rule(line));
                            break;
                        case 1:
                            myTicket = StreamEx.of(line.split(",")).map(Integer::parseInt).toList();
                            break;
                        case 2:
                            otherTickets.add(StreamEx.of(line.split(",")).map(Integer::parseInt).toList());
                            break;
                    }
                }
            }
        });

        AtomicInteger errorSum = new AtomicInteger(0);
        int score = StreamEx.of(otherTickets)
                .map(ticket ->IntStreamEx.of(ticket)
                        .filter( value -> rules.stream().noneMatch(rule -> rule.isValueAcceptable(value)))
                        .sum()
                )
                .reduce(0,Integer::sum);

        System.out.println("Part 1: "+score);

        //create list of valid tickets
        List<List<Integer>> validTickets = StreamEx.of(otherTickets).
                filter(ticket ->IntStreamEx.of(ticket).allMatch(
                            value -> rules.stream().anyMatch(rule -> rule.isValueAcceptable(value))
                        )
                )
                .toList();
        //add my own ticket as valid
        validTickets.add(myTicket);

        //assume all tickets have same number of values =), build map of possible columns for each rule offset.
        Map<Integer, List<Integer>> possibleColumns = EntryStream.of(rules)
                //convert rule into list of columns that were valid for the rule.
                //by:
                //iterating over each possible column index
                //converting each ticket to only the value for the current column index
                //then filtering by allMatch using the current rule with the value from the ticket for that column
                //boxed to get us out of the IntStreamEx back to StreamEx<Integer>
                //then toList so our map value is List<Integer>
                .mapValues(rule -> IntStreamEx.range(0, myTicket.size())
                    .filter(i -> validTickets.stream()
                            .map(ticket -> ticket.get(i))
                            .allMatch(rule::isValueAcceptable)
                    )
                    .boxed()
                    .toList()
                )
                //finally, toMap uses the index for the rule in the rules list, as the map index
                //and corresponding possible column list as it's value.
                .toMap();

        //assume that we can resolve the possible columns by iteratively assigning single columns
        //to their targets.. this won't work if the solution requires us to examine multiple potential
        //paths.. (but it doesn't look like the result set is going to be that mean).
        Map<Integer, Integer> resultMap = new HashMap<>();
        boolean done=false;
        while (!done){
            List<Integer> found = new ArrayList<>();
            //hunt for any single possibilities, and lock them in.
            for(int idx=0;idx<possibleColumns.size(); idx++){
                if(possibleColumns.get(idx).size()==1){
                    int lastFound = possibleColumns.get(idx).get(0);
                    resultMap.put(idx, lastFound);
                    found.add(lastFound);
                }
            }
            //remove the ones we just handled
            if(found.size()>0){
                for(int idx=0;idx<possibleColumns.size(); idx++){
                    possibleColumns.get(idx).removeAll(found);
                }
            }
            //we're done when all possibles are now empty.
            done=true;
            for(int idx=0;idx<possibleColumns.size(); idx++){
                done&=possibleColumns.get(idx).size()==0;
            }
        }

        //now double dereference the rule->column->ticketvalue to resolve the final list of actual values,
        //then reduce to create the final product.
        List<Long> resolvedValues = EntryStream.of(rules)
                //drop any rules that don't start "departure"
                .filterValues(i -> i.description.startsWith("departure"))
                //convert the rule, using the rule index, as a key into the resultMap, and that value
                //as an index into the myTicket list, to obtain the double dereferenced result.
                .map(e -> myTicket.get(resultMap.get(e.getKey()))).map(Long::valueOf).toList();
        //reduce the resolved Values via multiply
        long part2 = LongStreamEx.of(resolvedValues).reduce(1,(a,b)->a*b);

        System.out.println("Part 2: "+part2);
    }

    public Driver() throws Exception {
        Path input = FileReader.getPathForClassPathResource("org/ozzy/adventofcode/day16/data.txt");
        part1and2(input);
    }


    public static void main(String[] args) throws Exception {
        long time = System.currentTimeMillis();
        Driver d = new Driver();
        System.out.println("Elapsed : "+(System.currentTimeMillis()-time));
    }
}



