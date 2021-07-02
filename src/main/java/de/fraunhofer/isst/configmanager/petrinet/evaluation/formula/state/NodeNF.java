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
package de.fraunhofer.isst.configmanager.petrinet.evaluation.formula.state;

import de.fraunhofer.isst.configmanager.petrinet.evaluation.formula.transition.ArcExpression;
import de.fraunhofer.isst.configmanager.petrinet.model.Node;
import de.fraunhofer.isst.configmanager.petrinet.model.Place;
import lombok.AllArgsConstructor;

import java.util.List;

/**
 * evaluates to true, if given {@link NodeExpression} evaluates to true
 */
@AllArgsConstructor
public class NodeNF implements StateFormula {
    private NodeExpression parameter;

    public static NodeNF nodeNF(final NodeExpression parameter){
        return new NodeNF(parameter);
    }

    @Override
    public boolean evaluate(final Node node, final List<List<Node>> paths) {
        return node instanceof Place && parameter.getSubExpression().evaluate((Place) node);
    }

    @Override
    public String symbol() {
        return "NF";
    }

    @Override
    public String writeFormula() {
        return String.format("%s(%s)", symbol(), parameter.getMessage());
    }
}
