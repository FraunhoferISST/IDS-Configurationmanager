/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.fraunhofer.isst.configmanager.extensions.routes.petrinet.evaluation.formula.transition;

import de.fraunhofer.isst.configmanager.extensions.routes.petrinet.evaluation.formula.state.StateFormula;
import de.fraunhofer.isst.configmanager.extensions.routes.petrinet.model.Arc;
import de.fraunhofer.isst.configmanager.extensions.routes.petrinet.model.Node;
import de.fraunhofer.isst.configmanager.extensions.routes.petrinet.model.Transition;
import lombok.AllArgsConstructor;

import java.util.List;

/**
 * Evaluates to true, if parameter evaluates to true for a place directly following the transition.
 */
@AllArgsConstructor
public class TransitionMODAL implements TransitionFormula {
    private StateFormula parameter;

    public static TransitionMODAL transitionMODAL(final StateFormula parameter) {
        return new TransitionMODAL(parameter);
    }

    // MODAL, is true if parameter evaluates to true for a state following the current transition
    @Override
    public boolean evaluate(final Node node, final List<List<Node>> paths) {
        return node instanceof Transition
                && node.getSourceArcs().stream()
                        .map(Arc::getTarget)
                        .map(place -> parameter.evaluate(place, paths))
                        .reduce(false, (a, b) -> a || b);
    }

    @Override
    public String symbol() {
        return "MODAL";
    }

    @Override
    public String writeFormula() {
        return String.format("%s(%s)", symbol(), parameter.writeFormula());
    }
}
