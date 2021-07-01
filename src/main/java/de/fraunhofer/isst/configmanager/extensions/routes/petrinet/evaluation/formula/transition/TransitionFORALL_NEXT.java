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

import de.fraunhofer.isst.configmanager.extensions.routes.petrinet.model.Node;
import lombok.AllArgsConstructor;

import java.util.List;

import static de.fraunhofer.isst.configmanager.extensions.routes.petrinet.evaluation.formula.transition.TransitionEXIST_NEXT.transitionEXIST_NEXT;
import static de.fraunhofer.isst.configmanager.extensions.routes.petrinet.evaluation.formula.transition.TransitionNOT.transitionNOT;

/**
 * Evaluates to true, if all following transitions satisfy the given formula.
 */
@AllArgsConstructor
public class TransitionFORALL_NEXT implements TransitionFormula {
    private TransitionFormula parameter;

    public static TransitionFORALL_NEXT transitionFORALL_NEXT(final TransitionFormula parameter) {
        return new TransitionFORALL_NEXT(parameter);
    }

    @Override
    public boolean evaluate(final Node node, final List<List<Node>> paths) {
        return transitionNOT(transitionEXIST_NEXT(transitionNOT(parameter))).evaluate(node, paths);
    }

    @Override
    public String symbol() {
        return "FORALL_NEXT";
    }

    @Override
    public String writeFormula() {
        return String.format("%s(%s)", symbol(), parameter.writeFormula());
    }
}
