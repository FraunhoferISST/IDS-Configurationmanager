package de.fraunhofer.isst.configmanager.petrinet.evaluation.formula.transition;

import de.fraunhofer.isst.configmanager.petrinet.model.Node;
import de.fraunhofer.isst.configmanager.petrinet.model.Transition;
import lombok.AllArgsConstructor;

import java.util.List;

@AllArgsConstructor
public class TransitionEXIST_UNTIL implements TransitionFormula {
    private TransitionFormula parameter1;
    private TransitionFormula parameter2;

    public static TransitionEXIST_UNTIL transitionEXIST_UNTIL(final TransitionFormula parameter1,
                                                              final TransitionFormula parameter2) {
        return new TransitionEXIST_UNTIL(parameter1, parameter2);
    }

    // True if a path exists, where parameter1 is true on each transition of the path,
    // and parameter2 is true on the final transition of the path
    //TODO fix evaluation: use filtered paths
    @Override
    public boolean evaluate(final Node node, final List<List<Node>> paths) {
        if (!(node instanceof Transition)) {
            return false;
        }

        check: for (final var path: paths){
            if (path.get(0).equals(node) && paths.size() % 2 == 1) {
                for (var i = 0; i < path.size() - 1; i += 2){
                    var res1 = parameter1.evaluate(path.get(i), paths);
                    var res2 = parameter2.evaluate(path.get(i), paths);
                    if(res2) return true;
                    if(!res1) continue check;
                }

                if(parameter2.evaluate(path.get(path.size()-1), paths)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public String symbol() {
        return "EXIST_UNTIL";
    }

    @Override
    public String writeFormula() {
        return String.format("%s(%s, %s)", symbol(), parameter1.writeFormula(), parameter2.writeFormula());
    }
}
