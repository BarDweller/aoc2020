package org.ozzy.adventofcode.day5;

import org.ozzy.adventofcode.common.FileReader;

import java.nio.file.Path;
import java.util.*;

public class Driver {

    public Driver() throws Exception {
        Path input = FileReader.getPathForClassPathResource("org/ozzy/adventofcode/day5/data.txt");
        List<String> lines = FileReader.getFileAsListOfString(input);

        LinkedList<Integer> seen=new LinkedList<>();
        int id = 0;
        for (String line : lines) {
            //debug file has : separator for correct values =)
            line = line.split(":")[0];

            //rewrite it as binary
            line = line.replace('B','1').replace('F','0').replace('R','1').replace('L','0');
            //read the value as binary
            int i = Integer.parseInt(line,2);

            //not needed! ;)
            //int row = (i & 1016)>>3;
            //int col = (i & 7);

            //track highest id for part 1
            if(i>id)id=i;
            //remember all id's for part 2
            seen.add(i);
        }
        System.out.println("Part 1:"+id);

        //assume we'll be the gap in the list
        seen.sort(Integer::compare);
        Integer current;
        while(!seen.isEmpty() && (current = seen.remove()) !=null){
            if(seen.peek()!=current+1){
                System.out.println("Part 2:"+(current+1));
                break;
            }
        }
    }

    public static void main(String []args) throws Exception {
        Driver d = new Driver();
    }
}
