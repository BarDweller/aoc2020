package org.ozzy.adventofcode.day23;

import one.util.streamex.StreamEx;
import org.ozzy.adventofcode.common.FileReader;

import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

public class Driver {

    private class Circle {

        private class Node {
            Integer value;
            Node next;
            Node(Integer i){ this.value=i; }
        }

        Node firstNode;
        Node currentNode;
        Map<Integer,Node> nodeLookup = new HashMap<>();
        Integer maxValue;

        private void addNode(Integer n){
            if(currentNode == null){
                currentNode = new Node(n);
                firstNode = currentNode;
            }else{
                currentNode.next = new Node(n);
                currentNode = currentNode.next;
            }
            nodeLookup.put(currentNode.value,currentNode);
        }

        public Circle(String seed){
            seed.chars().boxed().map(i -> i-'0').forEach(this::addNode);
            //assume 1-9 present in input
            maxValue = 9;
            currentNode.next = firstNode;
            currentNode = firstNode;
        }

        public Circle(String seed, int max){
            seed.chars().boxed().map(i -> i-'0').forEach(this::addNode);
            //assume 1-9 present in input
            for(int i=10; i<=max; i++){
                addNode(i);
            }
            maxValue = max;
            currentNode.next = firstNode;
            currentNode = firstNode;
        }

        public String getPart1Key(){
            Node n = nodeLookup.get(1).next;
            StringBuilder sb = new StringBuilder();
            while(n.value!=1){ sb.append(n.value); n=n.next; }
            return sb.toString();
        }

        public Long getPart2Key(){
            Node oneCup = nodeLookup.get(1);
            return Long.valueOf(oneCup.next.value) * Long.valueOf(oneCup.next.next.value);
        }

        public void doMove(){
            //for reference ;)
            // (c)  (c.n)  (c.n.n)  (c.n.n.n)  (c.n.n.n.n)
            //        (r)    (r.n)    (r.n.n)    (r.n.n.n)

            //remove 3 cups after currentNode
            Node removed = currentNode.next;
            currentNode.next = removed.next.next.next;
            removed.next.next.next = null;

            //find destination.
            int destination = currentNode.value - 1;
            if(destination<1){
                destination=maxValue;
            }
            //test removed cups for destination
            boolean ok = false;
            while(!ok) {
                ok=true;
                Node n = removed;
                while (n != null) {
                    if(n.value == destination){
                        destination -= 1;
                        if(destination<1){
                            destination=maxValue;
                        }
                        ok=false;
                    }
                    n = n.next;
                }
            }
            //insert removed cups after destination
            Node after = nodeLookup.get(destination);
            Node afterDestination = after.next;
            after.next = removed;
            removed.next.next.next = afterDestination;

            //set the new current cup.
            currentNode = currentNode.next;
        }
    }


    public void part1(String input) throws Exception {
        Circle c = new Circle(input);
        for(int i=0; i<100; i++) {
            c.doMove();
        }
        System.out.println("Part 1: "+c.getPart1Key());
    }


    public void part2(String input) throws Exception {
        Circle c = new Circle(input,1000000);
        for(int i=0; i<10000000; i++) {
            c.doMove();
        }
        System.out.println("Part 2: "+c.getPart2Key());
    }


    public Driver() throws Exception {
        String testdata = "389125467";
        String data = "562893147";
        String input = data;
        part1(input);
        part2(input);
    }


    public static void main(String[] args) throws Exception {
        long time = System.currentTimeMillis();
        Driver d = new Driver();
        System.out.println("Elapsed : "+(System.currentTimeMillis()-time));
    }
}








