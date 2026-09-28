import java.util.*;

public class Sort{
    public static void main(String[] args){
        ArrayList<Integer> marks=new ArrayList<>();

        marks.add(75);
        marks.add(89);
        marks.add(62);
        marks.add(95);
        marks.add(80);

        Collections.sort(marks);
        System.out.println("Ascending order: "+marks);

        Collections.sort(marks,Collections.reverseOrder());
        System.out.println("Descending order: "+marks);
    }
}

