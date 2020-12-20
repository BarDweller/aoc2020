package org.ozzy.adventofcode.day20;

import one.util.streamex.EntryStream;
import one.util.streamex.StreamEx;
import org.ozzy.adventofcode.common.FileReader;
import org.ozzy.adventofcode.common.Pair;

import javax.swing.text.EditorKit;
import java.lang.reflect.Array;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Driver {

    final int TOP=0;
    final int BOTTOM=1;
    final int LEFT=2;
    final int RIGHT=3;

    private static class Tile {
        String id;
        List<String> data;
        List<String> edges;

        public Tile(String id){
            this.id = id;
            data = new ArrayList<>();
        }
        private short strToShort(String str){
            return Short.parseShort(str.replace('.','0').replace('#','1'),2);
        }
        public void addLine(String line){
            data.add(line);
            if(data.size()==line.length()){
                //we're done.. now calc our keys.
                String topString = data.get(0);
                String bottomString = data.get(data.size()-1);
                String leftString =data.stream().map(a->String.valueOf(a.charAt(0))).collect(Collectors.joining());
                String rightString =  data.stream().map(a->String.valueOf(a.charAt(a.length()-1))).collect(Collectors.joining());

                edges = new ArrayList<>();
                edges.add(topString);
                edges.add(bottomString);
                edges.add(leftString);
                edges.add(rightString);
            }
        }
        public String toString(){
            StringBuilder sb = new StringBuilder();
            sb.append("Tile ");
            sb.append(id);
            sb.append(":\n");
            data.forEach(line -> { sb.append(line); sb.append("\n"); } );
            return sb.toString();
        }
    }

    private static class TileGroup {
        String id;
        List<Tile> variants;
        int currentVariantIdx;
        private Tile rotateTile(Tile source){
            Tile destination = new Tile(source.id);
            for(int i=0; i<source.data.get(0).length(); i++) {
                final AtomicInteger offset = new AtomicInteger(i);
                destination.addLine(new StringBuilder(source.data.stream().map(a -> String.valueOf(a.charAt(offset.get()))).collect(Collectors.joining())).reverse().toString());
            }
            return destination;
        }
        private Tile flipTile(Tile source){
            Tile destination = new Tile(source.id);
            for(int i=0; i<source.data.get(0).length(); i++) {
                destination.addLine(new StringBuilder(source.data.get(i)).reverse().toString());
            }
            return destination;
        }
        public TileGroup(Tile seed){
            currentVariantIdx=0;
            id = seed.id;
            Tile current = seed;
            variants = new ArrayList<>();
            variants.add(current);
            for(int i=0;i<3;i++) {
                current = rotateTile(current);
                variants.add(current);
            }
            current=flipTile(seed);
            variants.add(current);
            for(int i=0;i<3;i++) {
                current = rotateTile(current);
                variants.add(current);
            }
        }
        public Set<String> getAllPossibleEdges(){
            return StreamEx.of(variants).flatMap(t -> t.edges.stream()).toSet();
        }
        public Set<String> getEdgesInDirection(int dir){
            return StreamEx.of(variants).map(t -> t.edges.get(dir)).toSet();
        }
    }

    private boolean seaMonsterIsPresent(List<String> sea, int x, int y){
        //01234567890123456789
        //                  #
        //#    ##    ##    ###
        // #  #  #  #  #  #
        List<Integer> row0 = StreamEx.of(18).toList();
        List<Integer> row1 = StreamEx.of(0,5,6,11,12,17,18,19).toList();
        List<Integer> row2 = StreamEx.of(1,4,7,13,16).toList();
        List<List<Integer>> rows = StreamEx.of(row0,row1,row2).toList();
        for(int ty=0; ty<3; ty++) {
            for(int tx=0; tx<rows.get(ty).size(); tx++) {
                int checky = y+ty;
                int checkx = x+rows.get(ty).get(tx);
                char test = sea.get(checky).charAt(checkx);
                if(test!='#')return false;
            }
        }
        return true;
    }

    LinkedList<String> rotateOcean(List<String> sea){
        LinkedList<String> result = new LinkedList<>();
        for(int i=0; i<sea.get(0).length(); i++) {
            final AtomicInteger offset = new AtomicInteger(i);
            result.add(new StringBuilder(sea.stream().map(a -> String.valueOf(a.charAt(offset.get()))).collect(Collectors.joining())).reverse().toString());
        }
        return result;
    }

    LinkedList<String> flipOcean(List<String> sea){
        LinkedList<String> result = new LinkedList<>();
        for(int i=0; i<sea.get(0).length(); i++) {
            result.add(new StringBuilder(sea.get(i)).reverse().toString());
        }
        return result;
    }


    public void part1and2(Path input) throws Exception {
        List<String> data = StreamEx.ofLines(input).toList();

        //load the tiles into the tiles list.
        LinkedList<Tile> tiles = new LinkedList<>();
        data.forEach( line -> {
                    if(line.isEmpty()) return;
                    if(line.startsWith("Tile ")){
                        line=line.substring("Tile ".length());
                        line=line.substring(0,line.length()-1);
                        tiles.addLast(new Tile(line));
                    }else{
                        tiles.getLast().addLine(line);
                    }
                }
        );

        //build list of tilegroups from tiles
        List<TileGroup> tgs = tiles.stream().map(TileGroup::new).collect(Collectors.toList());

        //build a map of edge, to things that match the edge.
        Map<String, List<Map.Entry<String, String>>> edgeToMatches = StreamEx.of(tgs).flatMap(tg -> StreamEx.of(tg.getAllPossibleEdges()).zipWith(StreamEx.of(Collections.nCopies(tg.getAllPossibleEdges().size(), tg.id)))).groupingBy(Map.Entry::getKey);
        //use that to build a map of how many tiles matched each edge
        Map<String,Long> matchedEdges = StreamEx.of(tgs).map(tg -> new Pair<>(tg.id, tg.getAllPossibleEdges().stream()
                .map(edge -> edgeToMatches.get(edge).stream()
                        .filter(e -> !e.getValue().equals(tg.id))
                        .count()
                )
                .reduce(0L, Long::sum)
        )
        ).toMap( pair -> pair.a, pair -> pair.b);
        //and lastly, build a map from tileid, to tilegroup
        Map<String,TileGroup> tileGroupsById = StreamEx.of(tgs).toMap(t -> t.id, Function.identity());

        //corners will be the ones with only 4 viable edges.. (map edges will have 6, and center pieces 8, because we don't know which
        //way up things go yet, so everything is doubled)
        List<Long> cornerIds = EntryStream.of(matchedEdges).filterValues(val -> val==4).map( e -> Long.parseLong(e.getKey())).toList();

        //part1
        Long score = cornerIds.stream().reduce(1L, (a,b) -> (a*b));
        System.out.println("Part 1: "+score);

        //arbitrarily pick one as the top left.. we have no way to know which way round the puzzle goes.
        TileGroup topLeft = tileGroupsById.get(String.valueOf(cornerIds.get(0)));

        System.out.println("Top Left : "+topLeft.id);
        //having decided this corner is top left, now find which of it's variants has no matches up or left.
        //there will be at least 2, because we don't know which way up the pieces go yet.
        List<Integer> variants = EntryStream.of(topLeft.variants)
                .filterValues(tile -> edgeToMatches.get(tile.edges.get(TOP)).stream().allMatch(e -> e.getValue().equals(topLeft.id)))
                .filterValues(tile -> edgeToMatches.get(tile.edges.get(LEFT)).stream().allMatch(e -> e.getValue().equals(topLeft.id)))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
        //2nd variant makes the grid look like the sample.. tho really there's no 'correct' answer yet.
        //but having the output "look like" the example is great validation ;)
        topLeft.currentVariantIdx = variants.get(1);

        //assume we're building a square.
        int edgesize = Double.valueOf(Math.sqrt(matchedEdges.size())).intValue();
        System.out.println("Assuming "+ edgesize+"*"+edgesize);

        Tile[][] grid = new Tile[edgesize][edgesize];
        grid[0][0] = topLeft.variants.get(topLeft.currentVariantIdx);

        //iterate each location in the grid, and find something with matching edges.
        //based on the numbers of possible matches for each edge seen in the maps above, it would
        //appear each edge is unique that we don't need to resolve choices for pieces, only 1
        //tile will ever match to any other as required..
        //we're matching first using tilegroups that allow any orientation/direction, then
        //focusing down to the correct variant from each selected group, using the edge from
        //above, or left (depending on row).
        for(int y=0;y<edgesize; y++){
            for(int x=0;x<edgesize; x++){
                //skip topleft =)
                if(x==0 && y==0)continue;
                if(y==0){
                    //only match to the left.. there's nothing above us =)
                    final Tile last = grid[x-1][y];
                    String edgeToFind = last.edges.get(RIGHT);
                    String matchedId = edgeToMatches.get(edgeToFind).stream().filter(e -> (!e.getValue().equals(last.id))).map(Map.Entry::getValue).findFirst().get();
                    //we know the id of the new tile, now we need to select which variant.
                    TileGroup next = tileGroupsById.get(matchedId);
                    int nextidx = EntryStream.of(next.variants)
                            .filterValues(tile -> tile.edges.get(LEFT).equals(edgeToFind))
                            .limit(1)
                            .map(Map.Entry::getKey)
                            .findFirst()
                            .get();
                    grid[x][y]=next.variants.get(nextidx);
                }else{
                    //only match above, we could also match to the left, but it seems not to be needed.
                    final Tile last = grid[x][y-1];
                    String edgeToFind = last.edges.get(BOTTOM);
                    String matchedId = edgeToMatches.get(edgeToFind).stream().filter(e -> (!e.getValue().equals(last.id))).map(Map.Entry::getValue).findFirst().get();
                    //we know the id of the new tile, now we need to select which variant.
                    TileGroup next = tileGroupsById.get(matchedId);
                    int nextidx = EntryStream.of(next.variants)
                            .filterValues(tile -> tile.edges.get(TOP).equals(edgeToFind))
                            .limit(1)
                            .map(Map.Entry::getKey)
                            .findFirst()
                            .get();
                    grid[x][y]=next.variants.get(nextidx);
                }

            }
        }

        //comvert grid into ocean =) (uncomment sysout's to see pretty ocean to verify against example)
        LinkedList<String> sea = new LinkedList<>();
        for(int y=0;y<edgesize;y++){
            for(int row=1; row<grid[0][0].data.size()-1; row++) {
                StringBuffer thisLine = new StringBuffer();
                for (int x = 0; x < edgesize; x++) {
                    String line = grid[x][y].data.get(row);
                    line = line.substring(1,line.length()-1);
                    //System.out.print(line);
                    thisLine.append(line);
                }
                sea.addLast(thisLine.toString());
                //System.out.println();
            }
        }

        System.out.println("Ocean is "+sea.size()+" * "+sea.get(0).length());

        int hashesInSeaMonster = 15;
        int seaMonsterCount = 0;
        //assume we got everything right enough that we will actually find seamonsters.
        while(seaMonsterCount == 0) {
            for (int r = 0; r < 4; r++) {
                int seaMonsterWidth = 20;
                int seaMonsterHeight = 3;
                for (int y = 0; y < sea.size() - seaMonsterHeight; y++) {
                    for (int x = 0; x < sea.get(0).length() - seaMonsterWidth; x++) {
                        if (seaMonsterIsPresent(sea, x, y)) {
                            seaMonsterCount++;
                        }
                    }
                }
                if (seaMonsterCount > 0) {
                    //assume when we find seamonsters, that this is the 'right' orientation/direction
                    //for the ocean.. and bug out here.
                    System.out.println("Found " + seaMonsterCount + " monsters");
                    break;
                }
                sea = rotateOcean(sea);
            }
            if(seaMonsterCount>0){
                break;
            }
            sea = flipOcean(sea);
        }

        //part2 calc, total hashes - (seamonsters found * hashes per seamonster)
        long count = sea.stream().flatMap(line -> line.chars().boxed().filter(i -> i=='#')).count();
        count -= (seaMonsterCount * hashesInSeaMonster);

        System.out.println("Part 2: "+count);
    }


    public Driver() throws Exception {
        Path input = FileReader.getPathForClassPathResource("org/ozzy/adventofcode/day20/data.txt");
        part1and2(input);
    }


    public static void main(String[] args) throws Exception {
        long time = System.currentTimeMillis();
        Driver d = new Driver();
        System.out.println("Elapsed : "+(System.currentTimeMillis()-time));
    }
}





