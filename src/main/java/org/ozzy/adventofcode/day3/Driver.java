package org.ozzy.adventofcode.day3;

import org.ozzy.adventofcode.common.AppendableMapArray;
import org.ozzy.adventofcode.common.FileReader;
import org.ozzy.adventofcode.common.MapArray;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

public class Driver {

    public int countTrees(int startX, int startY, int xDelta, int yDelta, MapArray grid){
        MapArray.Extents e = grid.getExtents();
        int count=0;
        int x=0;
        for(int y=startY; y<=e.maxY; y+=yDelta){
            if(grid.get(x,y).equals("#")) count++;
            x=(x+xDelta)%(e.maxX+1);
        }
        return count;
    }

    public Driver() throws Exception {
        Path input = FileReader.getPathForClassPathResource("org/ozzy/adventofcode/day3/data.txt");
        List<String> lines = FileReader.getFileAsListOfString(input);

        //load the grid, it's a digital frontier
        AppendableMapArray grid = new AppendableMapArray();
        lines.forEach(a -> grid.appendRow(a.chars().mapToObj(c->""+(char)c).toArray(String[]::new)));

        //part1
        System.out.println("Part 1 "+countTrees(0,0,3,1,grid));

        //part2
        int [][]deltas = new int[][]{{1,1},{3,1},{5,1},{7,1},{1,2}};
        Long count = Arrays.stream(deltas).mapToLong(a->countTrees(0,0,a[0],a[1],grid)).boxed().reduce((long) 1, (a, b)-> a*b);
        System.out.println("Part 2 "+count);
    }

    public static void main(String []args) throws Exception {
        Driver d = new Driver();
    }
}
