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
package de.fraunhofer.isst.configmanager.extensions.routes.petrinet.evaluation.formula.state;

import de.fraunhofer.isst.configmanager.extensions.routes.petrinet.model.Node;
import de.fraunhofer.isst.configmanager.extensions.routes.petrinet.model.Place;
import de.fraunhofer.isst.configmanager.extensions.routes.petrinet.simulator.PetriNetSimulator;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * Evaluates to true, if on any possible path every place fulfills parameter1, until a place fulfills parameter2.
 */
@Slf4j
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NodeFORALL_UNTIL implements StateFormula {

    StateFormula parameter1;
    StateFormula parameter2;

    public static NodeFORALL_UNTIL nodeFORALL_UNTIL(final StateFormula parameter1,
                                                    final StateFormula parameter2) {
        return new NodeFORALL_UNTIL(parameter1, parameter2);
    }

    //like EXIST_UNTIL for all paths
    //TODO fix evaluation: use filtered paths
    @Override
    public boolean evaluate(final Node node, final List<List<Node>> paths) {
        if (!(node instanceof Place)) {
            return false;
        }

        check: for (final var path: paths) {
            if (!path.get(0).equals(node)) {
                continue;
            }

            int offset;

            if (PetriNetSimulator.circleFree(path)) {
                if (path.size() % 2 == 1) {
                    offset = 1;
                } else {
                    offset = 2;
                }

                for (var i = 2; i < path.size() - offset; i += 2) {
                    final var res1 = parameter1.evaluate(path.get(i), paths);
                    final var res2 = parameter2.evaluate(path.get(i), paths);
                    if (res2) {
                        continue check;
                    }
                    if (!res1) {
                        return false;
                    }
                }

                if (!parameter2.evaluate(path.get(path.size() - offset), paths)) {
                    return false;
                }
            } else {
                //TODO path contains circle
                //if something on the circle fulfills param2 accept, if something does not fulfill param1 reject
                for (var i = 2; i < path.size() - 1; i += 2) {
                    final var res1 = parameter1.evaluate(path.get(i), paths);
                    final var res2 = parameter2.evaluate(path.get(i), paths);
                    if (res2) {
                        continue check;
                    }
                    if (!res1) {
                        return false;
                    }
                }

                //if everything on circle fulfills param1 but not param2: complicated case
                final var lastPlace = path.get(path.size() - 1) instanceof Place ? path.get(path.size() - 1) : path.get(path.size() - 2);
                final var newPaths = new ArrayList<>(paths);

                newPaths.remove(path);

                if (newPaths.stream().noneMatch(x -> x.get(0).equals(node))) {
                    return false;
                }
                if (!this.evaluate(lastPlace, newPaths)) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public String symbol() {
        return "FORALL_UNTIL";
    }

    @Override
    public String writeFormula() {
        return String.format("%s(%s, %s)", symbol(), parameter1.writeFormula(), parameter2.writeFormula());
    }
}
