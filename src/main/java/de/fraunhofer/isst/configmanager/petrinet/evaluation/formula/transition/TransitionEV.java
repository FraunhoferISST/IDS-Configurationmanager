package de.fraunhofer.isst.configmanager.petrinet.evaluation.formula.transition;

import de.fraunhofer.isst.configmanager.petrinet.model.Node;
import lombok.AllArgsConstructor;

import static de.fraunhofer.isst.configmanager.petrinet.evaluation.formula.TT.TT;
import static de.fraunhofer.isst.configmanager.petrinet.evaluation.formula.transition.TransitionFORALL_UNTIL.transitionFORALL_UNTIL;

@AllArgsConstructor
public class TransitionEV implements TransitionFormula {

    public static TransitionEV transitionEV(TransitionFormula parameter){
        return new TransitionEV(parameter);
    }

    private TransitionFormula parameter;

    @Override
    public boolean evaluate(Node node) {
        return transitionFORALL_UNTIL(TT(), parameter).evaluate(node);
    }

    @Override
    public String symbol() {
        return "EV";
    }

    @Override
    public String writeFormula() {
        return String.format("%s(%s)", symbol(), parameter.writeFormula());
    }
}
