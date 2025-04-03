package csp_solutions;

import core_algorithms.BacktrackingSearch;
import csp_problems.*;


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
        //TODO:
        //for each value in tail's domain, there must be a value in head's domain that's different
        //if not, delete the value from the tail's domain

    }

    /**
     * Implementing the Minimum Remaining Values(MRV) ordering heuristic.
     * @return the variable with the smallest domain among all the unassigned variables;
     *         null if all variables have been assigned
     */
    public Square selectUnassigned(){
       //TODO:
    }

    /**
     * @param args (no command-line argument is needed to run this program)
     */
    public static void main(String[] args) {
        String filename = "./SudokuTestCases/TestCase1   .txt";
        Sudoku problem = new Sudoku(filename);
        Backtracking_Sudoku agent = new Backtracking_Sudoku(problem);
        System.out.println("loading puzzle from " + filename + "...");
        problem.printPuzzle(problem.getAllVariables());
        if(agent.initAC3() && agent.search()){
            System.out.println("Solution found:");
            problem.printPuzzle(agent.getAllVariables());
        }else{
            System.out.println("Unable to find a solution.");
        }
    }
}
