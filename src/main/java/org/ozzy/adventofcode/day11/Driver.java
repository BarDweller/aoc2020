package org.ozzy.adventofcode.day11;

import one.util.streamex.StreamEx;
import org.ozzy.adventofcode.common.FileReader;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class Driver {

    public static class CharGrid {
        String data;
        int maxX;
        int maxY;
        public CharGrid(List<String> source){
            maxX=source.get(0).length(); //assume even lines
            maxY=source.size();
            StringBuilder d = new StringBuilder(maxX * maxY);
            source.forEach(d::append);
            data = d.toString();
        }
        char get(int x, int y){
            if(x<0 || x>=maxX || y<0 || y>=maxY) return '.';
            return data.charAt((y*maxX)+x);
        }
        long occupiedAdjacentCount(final int x, final int y) {
            int[][] offsets = {{-1, -1}, {-1, 0}, {-1, 1}, {0, -1}, {0, 1}, {1, 1}, {1, 0}, {1, -1}};
            final Character occupied = '#';
            return Arrays.stream(offsets)
                    .peek(a -> {
                                    a[0] = x + a[0];
                                    a[1] = y + a[1];
                    })
                    .map(a -> get(a[0], a[1]))
                    .filter(a -> a.equals(occupied))
                    .count();
        }

        long occupiedDirectionalCount(final int x, final int y) {
            int[][] offsets = {{-1, -1}, {-1, 0}, {-1, 1}, {0, -1}, {0, 1}, {1, 1}, {1, 0}, {1, -1}};
            final Character occupied = '#';
            long count=0;
            for(int[] offset : offsets){
                int newx=x+offset[0];
                int newy=y+offset[1];
                while(newx>=0 && newx<maxX && newy>=0 && newy<maxY){
                    char found=get(newx,newy);
                    if(found != '.'){
                        if(found=='#'){count++;}
                        break;
                    }
                    newx=newx+offset[0];
                    newy=newy+offset[1];
                }
            }
            return count;
        }

        void applyPart1Rules(){
            StringBuilder newData = new StringBuilder();
            for(int y=0; y<maxY; y++){
                for(int x=0; x<maxX; x++){
                    char c = get(x,y);
                    long count = occupiedAdjacentCount(x,y);
                    switch(c){
                        case '.' : newData.append('.'); break;
                        case 'L' : newData.append(count==0?'#':'L'); break;
                        case '#' : newData.append(count>3?'L':'#'); break;
                    }
                }
            }
            this.data = newData.toString();
        }

        void applyPart2Rules(){
            StringBuilder newData = new StringBuilder();
            for(int y=0; y<maxY; y++){
                for(int x=0; x<maxX; x++){
                    char c = get(x,y);
                    long count = occupiedDirectionalCount(x,y);
                    switch(c){
                        case '.' : newData.append('.'); break;
                        case 'L' : newData.append(count==0?'#':'L'); break;
                        case '#' : newData.append(count>4?'L':'#'); break;
                    }
                }
            }
            this.data = newData.toString();
        }

        void dumpGrid(){
            for(int y=0; y<maxY; y++) {
                StringBuilder line=new StringBuilder();
                for (int x = 0; x < maxX; x++) {
                    line.append(get(x,y));
                }
                System.out.println(line.toString());
            }
            System.out.println();
        }

        String getDataString(){
            return data;
        }

        void runUntilStablePart1(){
            String last = "";
            while(!data.equals(last)){
                last = getDataString();
                applyPart1Rules();
                //dumpGrid();
            }
        }

        void runUntilStablePart2(){
            String last = "";
            while(!data.equals(last)){
                last = getDataString();
                applyPart2Rules();
                //dumpGrid();
            }
        }

        public long countOccupied(){
            return data.chars().filter(a->a=='#').count();
        }
    }

    public void part1(Path input) throws Exception {
        CharGrid cg = new CharGrid(StreamEx.ofLines(input).toList());
        cg.runUntilStablePart1();

        System.out.println("Part 1:"+cg.countOccupied());
    }

    public void part2(Path input) throws Exception {
        CharGrid cg = new CharGrid(StreamEx.ofLines(input).toList());
        cg.runUntilStablePart2();

        System.out.println("Part 2:"+cg.countOccupied());
    }

    public Driver() throws Exception {
        Path input = FileReader.getPathForClassPathResource("org/ozzy/adventofcode/day11/data.txt");
        part1(input);
        part2(input);
    }

    public static void main(String[] args) throws Exception {
        Driver d = new Driver();
    }
}
