package org.ozzy.adventofcode.day2;

import org.ozzy.adventofcode.common.FileReader;

import java.nio.file.Path;
import java.util.List;

public class Driver {

    private static class PasswordLine {
        int minCount;
        int maxCount;
        char target;
        String password;

        public PasswordLine(String inputLine){
            String []parts = inputLine.split(" ");
            String []counts = parts[0].split("-");
            minCount = Integer.parseInt(counts[0]);
            maxCount = Integer.parseInt(counts[1]);
            target = parts[1].charAt(0);
            password = parts[2];
        }

        public boolean isValidPart1() {
            int idx=0;
            int next;
            int count=0;
            while(idx<password.length() && (next = password.indexOf(target,idx)) != -1){
                count++;
                if(count>maxCount){
                    return false;
                }
                idx=next+1;
            }
            return count >= minCount;
        }

        public boolean isValidPart1Alt() {
            long count = password.chars().filter(a -> a==target).count();
            return count<=maxCount && count>=minCount;
        }

        public boolean isValidPart2() {
            return (password.charAt(minCount-1)==target && password.charAt(maxCount-1)!=target) ||
                    (password.charAt(minCount-1)!=target && password.charAt(maxCount-1)==target);
        }
    }

    Driver() throws Exception{
        Path input = FileReader.getPathForClassPathResource("org/ozzy/adventofcode/day2/data.txt");
        List<String> lines = FileReader.getFileAsListOfString(input);

        long validPart1 = lines.stream()
                .map(PasswordLine::new)
                .filter(PasswordLine::isValidPart1)
                .count();

        System.out.println("Valid (Part1): "+validPart1);

        long validPart1Alt = lines.stream()
                .map(PasswordLine::new)
                .filter(PasswordLine::isValidPart1Alt)
                .count();

        System.out.println("Valid (Part1/Alt): "+validPart1Alt);

        long validPart2 = lines.stream()
                .map(PasswordLine::new)
                .filter(PasswordLine::isValidPart2)
                .count();

        System.out.println("Valid (Part2): "+validPart2);
    }


    public static void main(String []args) throws Exception {
        Driver d = new Driver();
    }
}
