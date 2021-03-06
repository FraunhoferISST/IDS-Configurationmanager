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
package de.fraunhofer.isst.configmanager.petrinet.evaluation.formula;

import de.fraunhofer.isst.configmanager.petrinet.evaluation.formula.state.StateFormula;
import de.fraunhofer.isst.configmanager.petrinet.evaluation.formula.transition.TransitionFormula;
import de.fraunhofer.isst.configmanager.petrinet.model.Node;

import java.util.List;

/**
 * FF operator evaluates to False everytime
 */
public class FF implements StateFormula, TransitionFormula {

    public static FF FF() {
        return new FF();
    }

    @Override
    public boolean evaluate(final Node node, final List<List<Node>> paths) {
        return false;
    }

    @Override
    public String symbol() {
        return "FF";
    }

    @Override
    public String writeFormula() {
        return symbol();
    }
}
