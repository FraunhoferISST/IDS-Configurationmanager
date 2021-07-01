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
import de.fraunhofer.isst.configmanager.extensions.routes.petrinet.model.Transition;
import lombok.AllArgsConstructor;

import java.util.List;

/**
 * Wvaluates to true, if given {@link ArcExpression} evaluates to true.
 */
@AllArgsConstructor
public class TransitionAF implements TransitionFormula {
    private ArcExpression parameter;

    public static TransitionAF transitionAF(final ArcExpression parameter) {
        return new TransitionAF(parameter);
    }

    @Override
    public boolean evaluate(final Node node, final List<List<Node>> paths) {
        return node instanceof Transition && parameter.getSubExpression().evaluate((Transition) node);
    }

    @Override
    public String symbol() {
        return "AF";
    }

    @Override
    public String writeFormula() {
        return String.format("%s(%s)", symbol(), "expression");
    }
}
