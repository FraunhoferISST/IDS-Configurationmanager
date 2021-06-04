package de.fraunhofer.isst.configmanager.petrinet.policy;

import antlr.Utils;
import de.fraunhofer.iais.eis.Contract;
import de.fraunhofer.iais.eis.Rule;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * PolicyUtils Class from DataspaceConnector
 */
public class PolicyUtils {

    /**
     * Iterate over all rules of a contract and add the ones with the element as their target to a
     * rule list.
     *
     * @param contract The contract.
     * @param element  The requested element.
     * @return List of ids rules.
     * @throws IllegalArgumentException If the message is null.
     */
    public static List<? extends Rule> getRulesForTargetId(final Contract contract,
                                                           final URI element) {
        final var rules = new ArrayList<Rule>();

        for (final var permission : contract.getPermission()) {
            final var target = permission.getTarget();
            if (target != null && target.equals(element)) {
                rules.add(permission);
            }
        }

        for (final var prohibition : contract.getProhibition()) {
            final var target = prohibition.getTarget();
            if (target != null && target.equals(element)) {
                rules.add(prohibition);
            }
        }

        for (final var obligation : contract.getObligation()) {
            final var target = obligation.getTarget();
            if (target != null && target.equals(element)) {
                rules.add(obligation);
            }
        }

        return rules;
    }

}
