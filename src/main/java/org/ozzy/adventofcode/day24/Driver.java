package org.ozzy.adventofcode.day24;

import one.util.streamex.EntryStream;
import one.util.streamex.StreamEx;
import org.ozzy.adventofcode.common.FileReader;
import org.ozzy.adventofcode.common.MapArray;
import org.ozzy.adventofcode.common.Pair;

import java.nio.file.Path;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Driver {

    enum Color {BLACK, WHITE}
    MapArray<Color> grid = new MapArray<>();

    private Color flip(Color c){
        if(c == Color.WHITE){
            return Color.BLACK;
        }else{
            return Color.WHITE;
        }
    }

    private void apply(int x, int y, MapArray<Color> newgrid){
        //copy the old value over..
        newgrid.put(x,y,grid.getOrDefault(x,y, Color.WHITE));
        //now figure out if it flips.
        int count=0;
        for(Pair<Integer,Integer> neighbour : StreamEx.of(new Pair<>(0,-1),
                                                          new Pair<>(1, -1),
                                                          new Pair<>(-1,1),
                                                          new Pair<>(0,1),
                                                          new Pair<>(1,0),
                                                          new Pair<>(-1, 0)).toList()) {
            count += grid.getOrDefault(x+neighbour.a, y+neighbour.b, Color.WHITE) == Color.BLACK ? 1 : 0;
            if(count>2)break; //no need to count past 2 =)
        }
        //flip according to number of black cells adjacent.
        if(grid.getOrDefault(x, y, Color.WHITE) == Color.BLACK){
            if(count==0 || count>2){
                newgrid.put(x,y,Color.WHITE);
            }
        }else{
            if(count==2){
                newgrid.put(x,y,Color.BLACK);
            }
        }
    }

    private void applyRules(MapArray.Coords c, MapArray<Color> newgrid, Set<Pair<Integer, Integer>> done){
        //evaluate current cell, and neighbours..
        for(Pair<Integer,Integer> neighbour : StreamEx.of(new Pair<>(0, 0),
                                                          new Pair<>(0,-1),
                                                          new Pair<>(1, -1),
                                                          new Pair<>(-1,1),
                                                          new Pair<>(0,1),
                                                          new Pair<>(1,0),
                                                          new Pair<>(-1, 0)).toList()) {
            //no need to evaluate cells we already handled.
            Pair<Integer,Integer> hash = new Pair<>(c.x+neighbour.a, c.y+neighbour.b);
            if(!done.contains(hash)) {
                done.add(hash);
                //do this cell.
                apply(hash.a, hash.b, newgrid);
            }
        }
    }


    public void part1andpart2(Path input) throws Exception {
        List<String> data = StreamEx.ofLines(input).toList();

        //Meh.. Java seemingly has no way to stream Regex token matches
        String regex = "(sw|se|nw|ne|w|e)";
        Pattern p = Pattern.compile(regex);
        for(String s: data){
            int x=0; int y=0;
            Matcher m = p.matcher(s);
            while(m.find()) {
                switch (m.group(1)) {
                    case "sw": { y--; break; }
                    case "se": { x++; y--; break; }
                    case "nw": { x--; y++; break; }
                    case "ne": { y++; break; }
                    case "e" : { x++; break; }
                    case "w" : { x--; break; }
                }
            }
            grid.put(x,y,flip(grid.getOrDefault(x,y,Color.WHITE)));
        }

        long blackTileCount = StreamEx.of(grid.values()).filter(c -> c==Color.BLACK).count();
        System.out.println("Part 1: "+blackTileCount);

        for(int i=1; i<=100; i++){
            MapArray<Color> newgrid = new MapArray<>();
            Set<Pair<Integer,Integer>> cache = new HashSet<>();
            grid.forEach((k,v) -> applyRules(k,newgrid,cache));
            grid = newgrid;
        }

        blackTileCount = StreamEx.of(grid.values()).filter(c -> c==Color.BLACK).count();
        System.out.println("Part 2: "+blackTileCount);
    }


    public Driver() throws Exception {
        Path input = FileReader.getPathForClassPathResource("org/ozzy/adventofcode/day24/data.txt");
        part1andpart2(input);
    }


    public static void main(String[] args) throws Exception {
        long time = System.currentTimeMillis();
        Driver d = new Driver();
        System.out.println("Elapsed : "+(System.currentTimeMillis()-time));
    }
}








