package de.fraunhofer.isst.configmanager.petrinet.simulator;

import de.fraunhofer.isst.configmanager.petrinet.builder.GraphVizGenerator;
import de.fraunhofer.isst.configmanager.petrinet.model.*;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Class Providing static methods to simulate a PetriNet based on a given initial state,
 * or creating the graph of all possible steps the PetriNet can take in an execution.
 *
 * (both methods could be running indefinitely, if the given initial PetriNet contains a
 * marker generating circle, so the PetriNet has an infinite amount of reachable states)
 */
@Slf4j
public class PetriNetSimulator {
    /**
     * Make a step in the current petriNet, finding all transitions that can be used
     * and taking all of them
     * (normally a petri net only uses one random transition at a time
     *  TODO: only take one random transition at a time)
     * @param petriNet the current step in the petrinet
     * @return true if something in the petrinet changed after taking the transitions
     */
    private static boolean makeStep(PetriNet petriNet){
        var changed = false;
        var nodesLosingMarkers = new ArrayList<Node>();
        var nodesGainingMarkers = new ArrayList<Node>();
        for(var node : petriNet.getNodes()){
            if(node instanceof TransitionImpl){
                var allPreviousHaveMarker = isPossible(node);
                if(allPreviousHaveMarker){
                    nodesLosingMarkers.addAll(node.getTargetArcs().stream()
                            .map(Arc::getSource).collect(Collectors.toList()));
                    nodesGainingMarkers.addAll(node.getSourceArcs().stream()
                            .map(Arc::getTarget).collect(Collectors.toList()));
                    changed = true;
                }
            }
        }
        nodesGainingMarkers.stream().distinct().forEach(node -> {
            ((PlaceImpl) node).setMarkers(((PlaceImpl) node).getMarkers() + 1);
        });
        nodesLosingMarkers.stream().distinct().forEach(node -> {
            ((PlaceImpl) node).setMarkers(((PlaceImpl) node).getMarkers() - 1);
        });
        return changed;
    }
    
    /**
     * For a given initial PetriNet: execute a step as long as something changes
     * @param petriNet the initial PetriNet
     */
    public static void simulateNet(PetriNet petriNet){
        int i = 0;
        log.info("Starting Simulation!");
        log.info(GraphVizGenerator.generateGraphViz(petriNet));
        while(makeStep(petriNet)){
            log.info("Something changed!");
            i++;
            log.info(GraphVizGenerator.generateGraphViz(petriNet));
        }
        log.info("Nothing changed! Finished simulation of PetriNet!");
    }
    
    /**
     * For a given petriNet, find all transitions that can be taken
     * @param petriNet a given PetriNet
     * @return List of Transition nodes, for which all previous nodes have markers
     */
    private static List<Node> getPossibleTransitions(PetriNet petriNet){
        var possible = new ArrayList<Node>();
        for(var node : petriNet.getNodes()){
            if(isPossible(node)) possible.add(node);
        }
        return possible;
    }
    
    /**
     * Given a petri net and a (transition) node: do the transition
     * @param petriNet a given PetriNet
     * @param node a given Node of the PetriNet
     */
    private static void doTransition(PetriNet petriNet, Node node){
        if(!isPossible(node)){
            return;
        }
        for(var arc : node.getTargetArcs()){
            var place = (PlaceImpl) arc.getSource();
            place.setMarkers(place.getMarkers()-1);
        }
        for(var arc : node.getSourceArcs()){
            var place = (PlaceImpl) arc.getTarget();
            place.setMarkers(place.getMarkers()+1);
        }
    }
    
    /**
     * Build a StepGraph with the given PetriNet as starting Point for executions.
     *
     * @param petriNet the initial PetriNet
     * @return the StepGraph with all reachable states of the given PetriNet
     */
    public static StepGraph buildStepGraph(PetriNet petriNet){
        var stepGraph = new StepGraph(petriNet);
        stepGraph.getSteps().add(petriNet);
        for(var node : getPossibleTransitions(petriNet)){
            addStepToStepGraph(petriNet, petriNet.deepCopy(), node, stepGraph);
        }
        return stepGraph;
    }
    
    /**
     * Execute a possible transition of the current PetriNet and add the result
     * to the StepGraph.
     *
     * @param parent the current PetriNet
     * @param copy a copy of the current PetriNet which will be modified
     * @param transition the transition the PetriNet should execute
     * @param stepGraph the stepgraph the resulting PetriNet will be added to
     *                  (if it doesn't already contain an equal PetriNet)
     */
    private static void addStepToStepGraph(PetriNet parent, PetriNet copy, Node transition, StepGraph stepGraph){
        log.info("Adding Step!");
        Node transitionCopy = null;
        for(var node : copy.getNodes()){
            if(node.getID().equals(transition.getID())){
                transitionCopy = node;
            }
        }
        doTransition(copy, transitionCopy);
        for(var net : stepGraph.getSteps()){
            if (net.equals(copy)){
                stepGraph.getArcs().add(new NetArc(parent, net, transition.getID()));
                return;
            }
        }
        stepGraph.getArcs().add(new NetArc(parent, copy, transition.getID()));
        stepGraph.getSteps().add(copy);
        for(var node : getPossibleTransitions(copy)){
            addStepToStepGraph(copy, copy.deepCopy(), node, stepGraph);
        }
    }
    
    /**
     * For a given node: if it is a transition, check if all previous nodes have markers
     * @param node a given Node
     * @return true if it is a transition ready to be used
     */
    private static boolean isPossible(Node node){
        if(node instanceof TransitionImpl) {
            return node.getTargetArcs().stream()
                    .map(Arc::getSource)
                    .map(place -> (PlaceImpl) place)
                    .map(PlaceImpl::getMarkers)
                    .allMatch(markers -> markers > 0);
        }
        return false;
    }

    /**
     * @param stepGraph PetriNet StepGraph
     * @return all paths possible in given petriNet
     */
    public static List<List<Node>> getAllPaths(StepGraph stepGraph){
        List<List<Node>> len1 =getPathsOfLength1(stepGraph);
        List<List<Node>> lenN = new ArrayList<>(len1);
        List<List<Node>> allPaths = new ArrayList<>(len1);
        int i = 1;
        while(!lenN.isEmpty()){
            log.info("Calculating paths of length " + ++i);
            lenN = getPathsOfLengthNplus1(len1, lenN);
            if(!lenN.isEmpty()){
                allPaths.addAll(lenN);
            }
        }
        allPaths.sort(Comparator.comparingInt(List::size));
        return allPaths;
    }

    /**
     * @param stepGraph PetriNet StepGraph
     * @return all possible paths of length 1 (either Place -> Transition or Transition -> Place)
     */
    private static List<List<Node>> getPathsOfLength1(StepGraph stepGraph){
        List<List<Node>> paths = new ArrayList<>();
        for(var node : stepGraph.getInitial().getNodes()){
            if(node instanceof Place){
                var followingTransitions = node.getSourceArcs().stream().map(Arc::getTarget)
                        .filter(trans -> stepGraph.getArcs().stream()
                                .map(NetArc::getUsedTransition)
                                .anyMatch(used -> used.equals(trans.getID())))
                        .collect(Collectors.toList());
                for(var succ : followingTransitions){
                    paths.add(List.of(node, succ));
                }
            }
            if(node instanceof Transition){
                for(var succ : node.getSourceArcs().stream().map(Arc::getTarget).collect(Collectors.toSet())){
                    paths.add(List.of(node, succ));
                }
            }
        }
        return paths;
    }

    /**
     * @param pathsLen1 set of possible paths of length 1
     * @param pathsLenN all possible paths of length n (stop considering as soon as path gets circular (first = last))
     * @return all possible paths of length n+1
     */
    private static List<List<Node>> getPathsOfLengthNplus1(List<List<Node>> pathsLen1, List<List<Node>> pathsLenN){
        List<List<Node>> pathsLenNplus1 = new ArrayList<>();
        for(var pathN : pathsLenN){
            for(var path1 : pathsLen1){
                if(pathN.get(pathN.size()-1).equals(path1.get(0)) && circleFree(pathN)){
                    var pathNplus1 = new ArrayList<>(pathN);
                    pathNplus1.add(path1.get(path1.size()-1));
                    pathsLenNplus1.add(pathNplus1);
                }
            }
        }
        return pathsLenNplus1;
    }

    private static boolean circleFree(List list){
        return list.stream().distinct().count() == list.size();
    }
}
