package csp_problems;

import java.io.*;
import java.util.*;

public class Sudoku implements CSPProblem<Square,Integer> {

    private final Map<Square, Variable<Square,Integer>> allVariables;

    //Map every square to the set of its neighboring squares.
    //"Neighbors" are those in the same row, column, or 3x3 box
    private final Map<Square,Set<Square>> neighbors = new HashMap<>();

    //Name of the file that contains the test case.
    private final String filename;

    public Sudoku(String filename) {
        this.filename = filename;
        allVariables = getAllVariables();
        //for each row, get all the squares in that row.
        for (int i=0; i<9; i++) {
            //build the row neighbor set (all square in the same row)
            Set<Square> rowNeighbors = new HashSet<>();
            for (int j=0; j<9; j++) {
                Square sq = new Square(i,j);
                rowNeighbors.add(sq);
            }
            //associate this neighbor set with each square in that row
            for(int j=0; j<9; j++){
                Square sq = new Square(i,j);
                neighbors.put(sq, new HashSet<>(rowNeighbors));
            }
        }
        //for each column, get all squares in that column
        for (int j=0; j<9; j++) {
            //build the column neighbor set (all squares in the same column)
            Set<Square> columnNeighbors = new HashSet<>();
            for (int i=0; i<9; i++) {
                Square sq = new Square(i,j);
                columnNeighbors.add(sq);
            }
            //associate this neighbor set with each square in that column
            for (int i=0; i<9; i++) {
                Square sq = new Square(i,j);
                neighbors.get(sq).addAll(columnNeighbors);
            }
        }
        for(int i=0; i<3; i++) {
            for (int j=0; j<3; j++) {
                //build the box neighbor set (all squares in the same 3x3 box)
                Set<Square> boxNeighbors = new HashSet<>();
                for (int x=0; x<3; x++) {
                    for (int y=0; y<3; y++) {
                        Square sq = new Square (i*3+x, j*3+y);
                        boxNeighbors.add(sq);
                    }
                }
                //associate this neighbor set with each square in that 3x3 box
                for (int x=0; x<3; x++) {
                    for (int y=0; y<3; y++) {
                        Square sq = new Square (i*3+x, j*3+y);
                        neighbors.get(sq).addAll(boxNeighbors);
                    }
                }
            }
        }
        //remove a square from its own neighbor set.
        for(Map.Entry<Square,Set<Square>> e : neighbors.entrySet()){
            e.getValue().remove(e.getKey());
        }
    }

    public Map<Square,Variable<Square,Integer>> getAllVariables() {
        Map<Square,Variable<Square,Integer>> allVariables = new HashMap<>();
        try {
            BufferedReader in = new BufferedReader(new FileReader(filename));
            String line;
            List<Integer> defaultDomain = List.of(1,2,3,4,5,6,7,8,9);
            //i: row number, j: column number
            for (int i=0; i<9; i++) {
                if ((line = in.readLine()) != null && !line.isEmpty()) {
                    String[] numbers = line.trim().split(" ");
                    for (int j=0; j<9; j++) {
                        Square vName = new Square(i,j);
                        int number = Integer.parseInt(numbers[j]);
                        Variable<Square, Integer> v;
                        if (number>0 && number<10) {
                            //this square is pre-assigned
                            v = new Variable<>(vName, new LinkedList<>(List.of(number)));
                        } else {
                            //this square is open
                            v = new Variable<>(vName, new LinkedList<>(defaultDomain));
                        }
                        allVariables.put(vName,v);
                    }
                } else {
                    //if a line is empty, or if the remaining lines are missing
                    //treat an empty or missing line as all squares in that line are open
                    for (int j=0; j<9; j++) {
                        Square vName = new Square(i, j);
                        Variable<Square,Integer> v =
                                new Variable<>(vName, new LinkedList<>(defaultDomain));
                        allVariables.put(vName,v);
                    }
                }
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return allVariables;
    }

    public void printPuzzle(Map<Square,Variable<Square,Integer>> allVariables) {
        for (int i=0; i<9; i++) {
            for (int j=0; j<9; j++) {
                if (allVariables.get(new Square(i,j)).domain().size() > 1) {
                    System.out.print("[ ]");
                } else {
                    System.out.print("["+allVariables.get(new Square(i,j)).domain().getFirst()+"]");
                }
            }
            System.out.print("\r\n");
        }
    }

    /**
     * Given a square, return the neighboring squares as a list.
     * "Neighbors" are those squares in the same row, column, or 3x3 box
     * @param sq the square
     * @return List of neighboring squares
     */
    public List<Square> getNeighborsOf(Square sq){
        //TODO:
    }

    /**
     * @return the list of pre-assigned squares
     */
    public List<Square> getPreAssignedVariables() {
        //TODO:
    }
}
