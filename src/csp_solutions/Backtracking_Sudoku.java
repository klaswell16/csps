package csp_solutions;
import core_algorithms.BacktrackingSearch;
import csp_problems.*;
import csp_problems.CSPProblem.Variable;
import java.util.*;

public class Backtracking_Sudoku extends BacktrackingSearch<Square,Integer>{
    public Backtracking_Sudoku(Sudoku problem){
        super(problem);
    }

    /**
     * @param tail tail of the arc
     * @param head head of the arc
     * @return true if the tail has been revised (lost some values), false otherwise
     */
    public boolean revise(Square tail, Square head) {
        boolean revised = false;
        Variable<Square, Integer> tailVar = getAllVariables().get(tail);
        Variable<Square, Integer> headVar = getAllVariables().get(head);

        Iterator<Integer> iterator = tailVar.domain().iterator();
        while (iterator.hasNext()) {
            int value = iterator.next();
            if (headVar.domain().size() == 1 && headVar.domain().contains(value)) {
                iterator.remove();
                revised = true;
            }
        }
        return revised;
    }

    /**
     * Implementing the Minimum Remaining Values (MRV) ordering heuristic.
     * @return the variable with the smallest domain among all the unassigned variables;
     * null if all variables have been assigned
     */
    public Square selectUnassigned() {
        Square minVar = null;
        int minSize = Integer.MAX_VALUE;

        for (Map.Entry<Square, Variable<Square, Integer>> entry : getAllVariables().entrySet()) {
            Square var = entry.getKey();
            Variable<Square, Integer> variable = entry.getValue();
            if (!assigned(var) && variable.domain().size() < minSize) {
                minSize = variable.domain().size();
                minVar = var;
            }
        }
        return minVar;
    }

    /**
     * @param args (no command-line argument is needed to run this program)
     */
    public static void main(String[] args) {
        String filename = "./SudokuTestCases/TestCase5.txt";
        Sudoku problem = new Sudoku(filename);
        Backtracking_Sudoku agent = new Backtracking_Sudoku(problem);

        System.out.println("Loading puzzle from " + filename + "...");
        problem.printPuzzle(problem.getAllVariables());

        if(agent.initAC3() && agent.search()){
            System.out.println("Solution found:");
            problem.printPuzzle(agent.getAllVariables());
        } else {
            System.out.println("Unable to find a solution.");
        }
    }
}
