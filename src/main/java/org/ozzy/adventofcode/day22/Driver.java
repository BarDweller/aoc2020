package org.ozzy.adventofcode.day22;

import one.util.streamex.EntryStream;
import one.util.streamex.StreamEx;
import org.ozzy.adventofcode.common.FileReader;
import org.ozzy.adventofcode.common.Pair;

import javax.rmi.ssl.SslRMIClientSocketFactory;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Driver {

    private void doRound(LinkedList<Integer> one, LinkedList<Integer> two){
        Integer a = one.removeFirst();
        Integer b = two.removeFirst();
        if(a>b) {
            one.addLast(a);
            one.addLast(b);
        }else{
            two.addLast(b);
            two.addLast(a);
        }
    }

    public void part1(Path input) throws Exception {
        List<String> data = StreamEx.ofLines(input).toList();

        LinkedList<Integer> onedeck = null;
        LinkedList<Integer> twodeck = null;

        for(String d: data){
            if(d.equals("Player 1:")){
                onedeck = new LinkedList<>();
                continue;
            }
            if(d.equals("Player 2:")){
                twodeck = new LinkedList<>();
                continue;
            }
            if(d.isEmpty()) continue;
            if(twodeck==null){
                onedeck.addLast(Integer.parseInt(d));
            }else{
                twodeck.addLast(Integer.parseInt(d));
            }
        }

        while( !onedeck.isEmpty() && !twodeck.isEmpty()) {
            doRound(onedeck,twodeck);
        }

        LinkedList<Integer> winner = onedeck.isEmpty() ? twodeck : onedeck;
        long score = 0;
        int index = 1;
        while(!winner.isEmpty()){
            score += (index * winner.removeLast());
            index++;
        }

        System.out.println("Part 1: "+score);
    }

    private int recursiveCombat(LinkedList<Integer> one, LinkedList<Integer> two, Set<String> history) {
        while( !one.isEmpty() && !two.isEmpty()) {
            if (history.contains("ONE" + one.toString() + "TWO" + two.toString())) {
                return 1;
            }
            history.add("ONE" + one.toString() + "TWO" + two.toString());

            Integer a = one.removeFirst();
            Integer b = two.removeFirst();
            if (one.size() >= a && two.size() >= b) {
                //recurse
                LinkedList<Integer> nextOneDeck = new LinkedList<>(one.subList(0, Math.min(one.size(), a)));
                LinkedList<Integer> nextTwoDeck = new LinkedList<>(two.subList(0, Math.min(two.size(), b)));
                if(recursiveCombat(nextOneDeck, nextTwoDeck, new HashSet<>()) == 1){
                    one.addLast(a);
                    one.addLast(b);
                }else{
                    two.addLast(b);
                    two.addLast(a);
                }
            } else {
                if (a > b) {
                    one.addLast(a);
                    one.addLast(b);
                } else {
                    two.addLast(b);
                    two.addLast(a);
                }
            }
        }
        return one.isEmpty() ? 2 : 1;
    }

    public void part2(Path input) throws Exception {
        List<String> data = StreamEx.ofLines(input).toList();

        LinkedList<Integer> onedeck = null;
        LinkedList<Integer> twodeck = null;

        for (String d : data) {
            if (d.equals("Player 1:")) {
                onedeck = new LinkedList<>();
                continue;
            }
            if (d.equals("Player 2:")) {
                twodeck = new LinkedList<>();
                continue;
            }
            if (d.isEmpty()) continue;
            if (twodeck == null) {
                onedeck.addLast(Integer.parseInt(d));
            } else {
                twodeck.addLast(Integer.parseInt(d));
            }
        }

        int winnerIdx = recursiveCombat(onedeck,twodeck,new HashSet<>());

        LinkedList<Integer> winner = onedeck.isEmpty() ? twodeck : onedeck;
        long score = 0;
        int index = 1;
        while(!winner.isEmpty()){
            score += (index * winner.removeLast());
            index++;
        }

        System.out.println("Part 2: "+score);
    }


    public Driver() throws Exception {
        Path input = FileReader.getPathForClassPathResource("org/ozzy/adventofcode/day22/data.txt");
        part1(input);

        part2(input);
    }


    public static void main(String[] args) throws Exception {
        long time = System.currentTimeMillis();
        Driver d = new Driver();
        System.out.println("Elapsed : "+(System.currentTimeMillis()-time));
    }
}








