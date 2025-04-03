package core_algorithms;

import csp_problems.CSPProblem;
import csp_problems.CSPProblem.Variable;

import java.util.*;

/**
 * A generic solver for CSPs of the Alldiff type of constraints.
 * This solver implements the following techniques:
 *   maintaining arc consistency (MAC) +
 *   minimum-remaining-values (MRV)
 * Note: MAC = backtracking search + dynamic arc consistency (AC-3)
 *
 * @param <X> the data type of the "names" of variables
 *  *  *     (e.g., for Sudoku, we could use Strings such as "03", "75", etc.
 *           to name the squares of the 9x9 board, where the first digit specifies
 *           the row # and the second the column #.)
 * @param <V> the data type of values.
 *     (e.g., in Sudoku, values should be integers between 1 and 9.)
 */
public abstract class BacktrackingSearch <X, V> {

    /**
     * The data type that represents an arc in the AC-3 algorithm.
     * We defind the equals method for this data type.
     * @param head
     * @param tail
     * @param <X>
     */
    public  record Arc<X>(X head, X tail){
        @Override
        public boolean equals(Object o){
            if (this == o){
                return true;
            }
            if (o == null){
                return false;
            }
            if(getClass() != o.getClass()){
                return false;
            }
            Arc<?> arc = (Arc<?>) o;
            return Objects.equals(head, arc.head) &&
                    Objects.equals(tail, arc.tail);
        }
    }

    //a map from variable names to variable objects
    private Map<X,Variable<X,V>> allVariables;

    //keeps track of the variables that have been assigned so far
    private final Set<X> assigned;

    private final CSPProblem<X,V> problem;

    public BacktrackingSearch(CSPProblem<X,V> problem){
        this.problem = problem;
        this.allVariables = problem.getAllVariables();
        //populate the assigned set with the names of any pre-assigned variables
        this.assigned = new HashSet<>(problem.getPreAssignedVariables());
    }

    /**
     * An implementation of the AC-3 algorithm; see textbook, Figure 6.3 on page 186
     * Note that the revise() is a separate method that you will need to
     * implement in BacktrackingSearch_Sudoku.java
     * @param arcs the list of arcs for which consistency will be maintained
     * @return false if consistency could not be maintained, true otherwise
     */
    public boolean AC3(Queue<Arc<X>> arcs) {
        Set<Arc<X>> unique = new HashSet<>(arcs);
        while (!arcs.isEmpty()) {
            Arc<X> arc = arcs.poll();
            unique.remove(arc);

            if (revise(arc.head(), arc.tail())) {
                Variable<X, V> var = allVariables.get(arc.head());
                if (var.domain().isEmpty()) {
                    return false;
                }


                for (X neighbor : problem.getNeighborsOf(arc.head())) {
                    if (!neighbor.equals(arc.tail())) {
                        Arc<X> newArc = new Arc<>(neighbor, arc.head());
                        if (!unique.contains(newArc)) {
                            arcs.add(newArc);
                            unique.add(newArc);
                        }
                    }
                }
            }
        }
        return true;
    }

    /**
     * Performs the AC-3 algorithm as preprocessing before search begins
     * @return true or false
     */
    public boolean initAC3(){
        Queue<Arc<X>> arcs = new LinkedList<>();
        for(X v : allVariables.keySet()){
            for(X n : problem.getNeighborsOf(v)){
                arcs.add(new Arc<>(v,n));
            }
        }
        return AC3(arcs);
    }

    /**
     * An implementation of the backtracking search with maintaining arc consistency (MAC)
     * @return
     */
    public boolean search() {
        X u = selectUnassigned();
        if (u == null) {
            return true;
        }


        Map<X, Variable<X, V>> savedState = deepClone();
        assigned.add(u);


        for (V value : new LinkedList<>(allVariables.get(u).domain())) {

            allVariables.get(u).domain().clear();
            allVariables.get(u).domain().add(value);


            Queue<Arc<X>> arcs = new LinkedList<>();
            for (X neighbor : problem.getNeighborsOf(u)) {
                if (!assigned(neighbor)) {
                    arcs.add(new Arc<>(neighbor, u));
                }
            }

            if (AC3(arcs)) {
                if (search()) {
                    return true;
                }
            }


            revert(savedState);
            assigned.add(u);
        }


        assigned.remove(u);
        revert(savedState);
        return false;
    }

    /**
     * Create a deep clone of the allVariables map in case we will need to back track in future
     * (Deep clone means to clone every element of the list.)
     * This method should be used inside search().
     */
    public Map<X,Variable<X,V>> deepClone(){
        Map<X,Variable<X,V>> allVariablesClone = new HashMap<>();
        for(Variable<X,V> var : allVariables.values()){
            //deep clone the variable domain
            Variable<X,V> varClone =
                    new Variable<>(var.name(), new LinkedList<>(var.domain()));
            allVariablesClone.put(var.name(),varClone);
        }
        return allVariablesClone;
    }

    /**
     * Revert the allVariables map to the deep clone copy.
     * This method should be used inside search().
     */
    public void revert(Map<X,Variable<X,V>> allVariablesClone){
        allVariables = allVariablesClone;
    }

    /**
     *
     * @return allVariables map
     */
    public Map<X,Variable<X, V>> getAllVariables() {
        return allVariables;
    }


    /**
     * Check if the variable of the given name has been assigned a value already.
     * @param name name of the variable whose assignment will be checked
     * @return true if assigned, false otherwise
     */
    public boolean assigned(X name){
        return assigned.contains(name);
    }

    //the two abstract methods below should be implemented in BacktrackingSearch_Sudoku.java

    /**
     * revise an arc to maintain arc consistency
     *
     * @param tail
     * @param head
     * @return false if no value is deleted, true otherwise
     */
    public abstract boolean revise(X tail, X head);

    /**
     *
     * @return an unassigned variable according to the MRV heuristic;
     *         null if all variables have been assigned
     */
    public abstract X selectUnassigned();

}
