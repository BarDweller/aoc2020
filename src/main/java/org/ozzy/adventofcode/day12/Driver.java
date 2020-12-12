package org.ozzy.adventofcode.day12;

import one.util.streamex.EntryStream;
import one.util.streamex.IntStreamEx;
import one.util.streamex.StreamEx;
import org.ozzy.adventofcode.common.FileReader;
import org.ozzy.adventofcode.common.Pair;

import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class Driver {

    final Pair<Integer,Integer> N = new Pair<>(0, +1);
    final Pair<Integer,Integer> S = new Pair<>(0, -1);
    final Pair<Integer,Integer> E = new Pair<>(+1, 0);
    final Pair<Integer,Integer> W = new Pair<>(-1, 0);

    final Map<Integer,Pair<Integer,Integer>> deltaMap = EntryStream.of('N',N,'S',S,'E',E,'W',W)
            .mapKeys(Integer::valueOf)
            .toMap();
    final Map<Integer,Integer> angleMap = EntryStream.of('N',0,'S',180,'E',90,'W',270)
            .mapKeys(Integer::valueOf)
            .toMap();

    final Map<Integer,Integer> directionMap = EntryStream.of(
            0,'N',
            90,'E',
            180,'S',
            270,'W',
            360,'N',
            450,'E',
            540,'S')
            .mapValues(Integer::valueOf)
            .append( EntryStream.of( //note: StreamEx.of only allows up to 10 pairs, so append ;)
            -90,'W',
            -180,'S',
            -270,'E',
            -360,'N',
            -450,'W',
            -540,'S'
            ).mapValues(Integer::valueOf))
            .mapKeys(Integer::valueOf)
            .toMap();

    void updateHeading(final AtomicInteger direction, int angle){
        direction.set( directionMap.get( angleMap.get(direction.get()) + angle ) );
    }

    void rotateWaypointRight(Pair<Integer,Integer> waypoint){
        int tempa = waypoint.a;
        waypoint.a = waypoint.b;
        waypoint.b = -tempa;
    }
    void rotateWaypointLeft(Pair<Integer,Integer> waypoint){
        int tempa = waypoint.a;
        int tempb = waypoint.b;
        waypoint.a = -tempb;
        waypoint.b = tempa;
    }

    void updateWaypoint(int angle, Pair<Integer,Integer> waypoint){
        int count = Math.abs(angle)/90;
        if(angle>0) IntStreamEx.range(count).forEach(a -> rotateWaypointRight(waypoint));
        else IntStreamEx.range(count).forEach(a -> rotateWaypointLeft(waypoint));
    }

    void mutate1(final String input, final AtomicInteger direction, Pair<Integer,Integer> coords){
        Pair<Integer,Integer> delta=null;
        int multiple = Integer.parseInt(input.substring(1));
        switch(input.charAt(0)){
            case 'R' : updateHeading(direction, multiple); break;
            case 'L' : updateHeading(direction, -multiple); break;
            case 'F' : delta = deltaMap.get(direction.get()); break;
            case 'N' :
            case 'S' :
            case 'E' :
            case 'W' : delta = deltaMap.get((int)input.charAt(0)); break;
            default: throw new IllegalArgumentException("Unknown line "+input);
        }
        if(delta!=null){
            delta = new Pair<>(delta.a * multiple, delta.b * multiple);
            coords.a = coords.a + delta.a;
            coords.b = coords.b + delta.b;
        }
        //System.out.println(input + " " + coords);
    }

    void mutate2(final String input, Pair<Integer,Integer> coords, Pair<Integer,Integer> waypoint){
        Pair<Integer,Integer> delta;
        int multiple = Integer.parseInt(input.substring(1));

        switch(input.charAt(0)){
            case 'R' : updateWaypoint(multiple, waypoint); break;
            case 'L' : updateWaypoint(-multiple, waypoint); break;
            case 'F' :
                delta = waypoint;
                delta = new Pair<>(delta.a * multiple, delta.b * multiple);
                coords.a = coords.a + delta.a;
                coords.b = coords.b + delta.b;
            break;
            case 'N' :
            case 'S' :
            case 'E' :
            case 'W' :
                delta = deltaMap.get((int)input.charAt(0));
                delta = new Pair<>(delta.a * multiple, delta.b * multiple);
                waypoint.a = waypoint.a + delta.a;
                waypoint.b = waypoint.b + delta.b;
            break;
            default: throw new IllegalArgumentException("Unknown line "+input);
        }
        //System.out.println(input + " " + coords+" "+waypoint);
    }

    int manhattanDistance(Pair<Integer, Integer> test){
        return Math.abs(test.a) + Math.abs(test.b);
    }

    public void part1(Path input) throws Exception {
        final AtomicInteger direction = new AtomicInteger('E');
        final Pair<Integer,Integer> coords = new Pair<>(0,0);
        StreamEx.ofLines(input).forEach(a -> mutate1(a, direction, coords));

        System.out.println("Part 1:"+manhattanDistance(coords));
    }

    public void part2(Path input) throws Exception {
        final Pair<Integer,Integer> coords = new Pair<>(0,0);
        final Pair<Integer,Integer> waypoint = new Pair<>(10,1);

        StreamEx.ofLines(input).forEach(a -> mutate2(a, coords, waypoint));

        System.out.println("Part 2:"+manhattanDistance(coords));
    }

    public Driver() throws Exception {
        Path input = FileReader.getPathForClassPathResource("org/ozzy/adventofcode/day12/data.txt");

        //make sure we don't have any surprise angles that are not 90' multiples.
        if(StreamEx.ofLines(input)
                .filter(a -> a.startsWith("L") || a.startsWith("R"))
                .map(a -> Integer.parseInt(a.substring(1)))
                .distinct()
                .anyMatch(a -> !angleMap.containsValue(a))) {
            throw new IllegalStateException("Error : list contains angles not mulitples of 90.");
        }

        part1(input);
        part2(input);
    }

    public static void main(String[] args) throws Exception {
        long time = System.currentTimeMillis();
        Driver d = new Driver();
        System.out.println("Elapsed : "+(System.currentTimeMillis()-time));
    }
}
