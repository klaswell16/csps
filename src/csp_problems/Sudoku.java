package csp_problems;

import java.io.*;
import java.util.*;

public class Sudoku implements CSPProblem<Square, Integer> {
    private final Map<Square, Variable<Square, Integer>> allVariables;
    private final Map<Square, Set<Square>> neighbors = new HashMap<>();
    private final String filename;

    public Sudoku(String filename) {
        this.filename = filename;
        allVariables = getAllVariables();

        for (int i = 0; i < 9; i++) {
            Set<Square> rowNeighbors = new HashSet<>();
            for (int j = 0; j < 9; j++) {
                rowNeighbors.add(new Square(i, j));
            }
            for (int j = 0; j < 9; j++) {
                neighbors.put(new Square(i, j), new HashSet<>(rowNeighbors));
            }
        }

        for (int j = 0; j < 9; j++) {
            Set<Square> columnNeighbors = new HashSet<>();
            for (int i = 0; i < 9; i++) {
                columnNeighbors.add(new Square(i, j));
            }
            for (int i = 0; i < 9; i++) {
                neighbors.get(new Square(i, j)).addAll(columnNeighbors);
            }
        }

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                Set<Square> boxNeighbors = new HashSet<>();
                for (int x = 0; x < 3; x++) {
                    for (int y = 0; y < 3; y++) {
                        boxNeighbors.add(new Square(i * 3 + x, j * 3 + y));
                    }
                }
                for (int x = 0; x < 3; x++) {
                    for (int y = 0; y < 3; y++) {
                        neighbors.get(new Square(i * 3 + x, j * 3 + y)).addAll(boxNeighbors);
                    }
                }
            }
        }

        for (Map.Entry<Square, Set<Square>> e : neighbors.entrySet()) {
            e.getValue().remove(e.getKey());
        }
    }

    public Map<Square, Variable<Square, Integer>> getAllVariables() {
        Map<Square, Variable<Square, Integer>> allVariables = new HashMap<>();
        try (BufferedReader in = new BufferedReader(new FileReader(filename))) {
            String line;
            List<Integer> defaultDomain = List.of(1, 2, 3, 4, 5, 6, 7, 8, 9);
            for (int i = 0; i < 9; i++) {
                if ((line = in.readLine()) != null && !line.isEmpty()) {
                    String[] numbers = line.trim().split(" ");
                    for (int j = 0; j < 9; j++) {
                        Square vName = new Square(i, j);
                        int number = Integer.parseInt(numbers[j]);
                        Variable<Square, Integer> v = (number > 0 && number < 10)
                                ? new Variable<>(vName, new LinkedList<>(List.of(number)))
                                : new Variable<>(vName, new LinkedList<>(defaultDomain));
                        allVariables.put(vName, v);
                    }
                } else {
                    for (int j = 0; j < 9; j++) {
                        Square vName = new Square(i, j);
                        allVariables.put(vName, new Variable<>(vName, new LinkedList<>(defaultDomain)));
                    }
                }
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return allVariables;
    }

    public void printPuzzle(Map<Square, Variable<Square, Integer>> allVariables) {
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (allVariables.get(new Square(i, j)).domain().size() > 1) {
                    System.out.print("[ ]");
                } else {
                    System.out.print("[" + allVariables.get(new Square(i, j)).domain().getFirst() + "]");
                }
            }
            System.out.print("\r\n");
        }
    }

    public List<Square> getNeighborsOf(Square sq) {
        return new ArrayList<>(neighbors.get(sq));
    }

    public List<Square> getPreAssignedVariables() {
        List<Square> preAssigned = new ArrayList<>();
        for (Map.Entry<Square, Variable<Square, Integer>> entry : allVariables.entrySet()) {
            if (entry.getValue().domain().size() == 1) {
                preAssigned.add(entry.getKey());
            }
        }
        return preAssigned;
    }
}
