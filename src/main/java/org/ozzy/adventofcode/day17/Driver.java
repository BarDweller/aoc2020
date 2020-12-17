package org.ozzy.adventofcode.day17;

import one.util.streamex.EntryStream;
import one.util.streamex.IntStreamEx;
import one.util.streamex.LongStreamEx;
import one.util.streamex.StreamEx;
import org.ozzy.adventofcode.common.FileReader;

import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class Driver {

    private static class Cube {
        int x,y,z,w;
        public Cube(int x, int y, int z, int w){
            this.x=x; this.y=y; this.z=z; this.w=w;
        }

        public Cube(int x, int y, int z){
            this.x=x; this.y=y; this.z=z; this.w=0;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Cube cube = (Cube) o;
            return x == cube.x &&
                    y == cube.y &&
                    z == cube.z &&
                    w == cube.w;
        }

        @Override
        public int hashCode() {
            return Objects.hash(x, y, z, w);
        }

    }

    boolean willBecomeActivePt1(Cube start, Set<Cube> activeCubes) {
        int activeNeighbours = 0;

        for (int z = start.z - 1; z <= start.z + 1; z++) {
            for (int x = start.x - 1; x <= start.x + 1; x++) {
                for (int y = start.y - 1; y <= start.y + 1; y++) {
                    Cube test = new Cube(x, y, z);
                    //skip self =)
                    if (start.equals(test)) continue;
                    activeNeighbours += activeCubes.contains(test) ? 1 : 0;
                    if (activeNeighbours > 3) break;
                }
                if (activeNeighbours > 3) break;
            }
            if (activeNeighbours > 3) break;
        }

        if(activeCubes.contains(start)){
            return activeNeighbours==2 || activeNeighbours==3;
        }else{
            return activeNeighbours==3;
        }
    }

    Set<Cube> doPhasePt1(Set<Cube> activeCubes){
        Set<Cube> tested = new HashSet<>();
        Set<Cube> result = new HashSet<>();

        for(Cube start : activeCubes){
            for(int z=start.z-1; z<=start.z+1; z++) {
                for (int x = start.x - 1; x <= start.x + 1; x++) {
                    for (int y = start.y - 1; y <= start.y + 1; y++) {
                        Cube toTest = new Cube(x, y, z);
                        if (!tested.contains(toTest)) {
                            tested.add(toTest);
                            if (willBecomeActivePt1(toTest, activeCubes)) {
                                result.add(toTest);
                            }
                        }
                    }
                }
            }
        }

        return result;
    }

    boolean willBecomeActivePt2(Cube start, Set<Cube> activeCubes) {
        int activeNeighbours = 0;

        for(int w=start.w-1; w<=start.w+1; w++) {
            for (int z = start.z - 1; z <= start.z + 1; z++) {
                for (int x = start.x - 1; x <= start.x + 1; x++) {
                    for (int y = start.y - 1; y <= start.y + 1; y++) {
                        Cube test = new Cube(x, y, z, w);
                        //skip self =)
                        if (start.equals(test)) continue;
                        activeNeighbours += activeCubes.contains(test) ? 1 : 0;
                        if (activeNeighbours > 3) break;
                    }
                    if (activeNeighbours > 3) break;
                }
                if (activeNeighbours > 3) break;
            }
            if (activeNeighbours > 3) break;
        }

        if(activeCubes.contains(start)){
            return activeNeighbours==2 || activeNeighbours==3;
        }else{
            return activeNeighbours==3;
        }
    }

    Set<Cube> doPhasePt2(Set<Cube> activeCubes){
        Set<Cube> tested = new HashSet<>();
        Set<Cube> result = new HashSet<>();

        for(Cube start : activeCubes){
            for(int w=start.w-1; w<=start.w+1; w++) {
                for (int z = start.z - 1; z <= start.z + 1; z++) {
                    for (int x = start.x - 1; x <= start.x + 1; x++) {
                        for (int y = start.y - 1; y <= start.y + 1; y++) {
                            Cube toTest = new Cube(x, y, z, w);
                            if (!tested.contains(toTest)) {
                                tested.add(toTest);
                                if (willBecomeActivePt2(toTest, activeCubes)) {
                                    result.add(toTest);
                                }
                            }
                        }
                    }
                }
            }
        }

        return result;
    }

    public void part1(Path input) throws Exception {
        List<String> data = StreamEx.ofLines(input).toList();

        Set<Cube> activeCubes = EntryStream.of(data)
                //for each row, stream the chars in the row as a stream of ints, filter to active, map to cubes.
                .flatMapKeyValue((y,row) -> EntryStream.of( StreamEx.of(row.chars().boxed()).toList() )
                                                   .filterValues(x->x=='#')
                                                   .mapKeyValue((x,value)->new Cube(x,y,0)))
                .toSet();


        for(int i=0; i<6; i++) {
            activeCubes = doPhasePt1(activeCubes);
        }
        System.out.println("Part 1: "+activeCubes.size());
    }

    public void part2(Path input) throws Exception {
        List<String> data = StreamEx.ofLines(input).toList();

        Set<Cube> activeCubes = EntryStream.of(data)
                //for each row, stream the chars in the row as a stream of ints, filter to active, map to cubes.
                .flatMapKeyValue((y,row) -> EntryStream.of( StreamEx.of(row.chars().boxed()).toList() )
                        .filterValues(x->x=='#')
                        .mapKeyValue((x,value)->new Cube(x,y,0)))
                .toSet();


        for(int i=0; i<6; i++) {
            activeCubes = doPhasePt2(activeCubes);
        }
        System.out.println("Part 2: "+activeCubes.size());
    }

    public Driver() throws Exception {
        Path input = FileReader.getPathForClassPathResource("org/ozzy/adventofcode/day17/data.txt");
        part1(input);
        part2(input);
    }


    public static void main(String[] args) throws Exception {
        long time = System.currentTimeMillis();
        Driver d = new Driver();
        System.out.println("Elapsed : "+(System.currentTimeMillis()-time));
    }
}



