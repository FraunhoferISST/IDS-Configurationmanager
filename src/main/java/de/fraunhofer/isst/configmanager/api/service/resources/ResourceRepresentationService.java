package de.fraunhofer.isst.configmanager.api.service.resources;

import de.fraunhofer.iais.eis.AppRouteImpl;
import de.fraunhofer.iais.eis.DigitalContent;
import de.fraunhofer.iais.eis.RepresentationImpl;
import de.fraunhofer.iais.eis.Resource;
import de.fraunhofer.iais.eis.RouteStep;
import de.fraunhofer.iais.eis.RouteStepImpl;
import de.fraunhofer.iais.eis.util.Util;
import de.fraunhofer.isst.configmanager.api.service.ConfigModelService;
import de.fraunhofer.isst.configmanager.api.service.EndpointService;
import de.fraunhofer.isst.configmanager.connector.clients.DefaultConnectorClient;
import de.fraunhofer.isst.configmanager.model.configlists.EndpointInformationRepository;
import de.fraunhofer.isst.configmanager.model.endpointinfo.EndpointInformation;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


/**
 * Service class for managing resource representations.
 */
@Slf4j
@Service
@Transactional
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ResourceRepresentationService extends AbstractResourceService {

    transient EndpointService endpointService;
    transient EndpointInformationRepository endpointInformationRepository;

    @Autowired
    public ResourceRepresentationService(final ConfigModelService configModelService,
                                         final DefaultConnectorClient connectorClient,
                                         final EndpointService endpointService,
                                         final EndpointInformationRepository endpointInformationRepository) {
        super(configModelService, connectorClient);
        this.endpointService = endpointService;
        this.endpointInformationRepository = endpointInformationRepository;
    }

    /**
     * @param representationId id of the representation
     * @return representation implementation
     */
    public RepresentationImpl getResourceRepresentationInCatalog(final URI representationId) {
        return (RepresentationImpl) getResources()
                .stream()
                .map(DigitalContent::getRepresentation)
                .flatMap(Collection::stream)
                .filter(representation -> representation.getId().equals(representationId))
                .findAny()
                .orElse(null);
    }

    /**
     * @param resourceId       id of the resource
     * @param representationId id of the representation to delete
     */
    public void deleteResourceRepresentationFromAppRoute(final URI resourceId,
                                                         final URI representationId) {
        if (configModelService.getConfigModel().getAppRoute() == null) {
            log.info("---- [ResourceRepresentationService deleteResourceRepresentationFromAppRoute] Could not find"
                    + " any app route to delete the resource");
        } else {
            final ArrayList<RouteStep> emptyList = new ArrayList<>();
            for (final var route : configModelService.getConfigModel().getAppRoute()) {
                if (route == null) {
                    continue;
                }
                if (route.getAppRouteOutput() != null) {
                    for (final var resource : route.getAppRouteOutput()) {
                        if (resource.getRepresentation() != null) {
                            resource.getRepresentation().removeIf(representation ->
                                    representation.getId().equals(representationId)
                            );
                        }
                    }
                }
                if (route.getHasSubRoute() == null) {
                    continue;
                }
                for (final var subRoute : route.getHasSubRoute()) {
                    deleteRepresentationFromSubRoutes(subRoute, emptyList, resourceId,
                            representationId);
                }
            }
        }
        configModelService.saveState();
    }

    /**
     * Delete occurrence of a resource representation with resourceID and representationID from.
     * all SubRoutes
     *
     * @param current          current Node in AppRoute
     * @param visited          already visited AppRoutes
     * @param resourceId       ID of the Resource for which the representation should be deleted
     * @param representationId ID of the Representation to delete
     */
    private void deleteRepresentationFromSubRoutes(final RouteStep current,
                                                   final List<RouteStep> visited,
                                                   final URI resourceId,
                                                   final URI representationId) {
        if (current == null) {
            return;
        }
        if (current.getAppRouteOutput() != null) {
            for (final var resource : current.getAppRouteOutput()) {
                if (resource.getRepresentation() != null) {
                    resource.getRepresentation().removeIf(representation ->
                            representation.getId().equals(representationId)
                    );
                }
            }
        }
        if (current.getHasSubRoute() == null) {
            return;
        }
        for (final var subRoute : current.getHasSubRoute()) {
            if (!visited.contains(subRoute)) {
                visited.add(current);
                deleteFromSubRoutes(subRoute, visited, resourceId);
            }
        }
    }

    /**
     * This method updates a backend connection.
     *
     * @param resourceId id of the resource
     * @param endpointId id of the endpoint
     */
    public void updateBackendConnection(final URI resourceId, final URI endpointId) {
        if (configModelService.getConfigModel().getAppRoute() != null) {
            RouteStepImpl foundRouteStep = null;
            AppRouteImpl appRouteImpl = null;
            for (final var appRoute : configModelService.getConfigModel().getAppRoute()) {
                for (final var routeStep : appRoute.getHasSubRoute()) {
                    for (final var resource : routeStep.getAppRouteOutput()) {
                        if (resourceId.equals(resource.getId())) {
                            appRouteImpl = (AppRouteImpl) appRoute;
                            foundRouteStep = (RouteStepImpl) routeStep;
                            break;
                        }
                    }
                }
            }

            // Set app route start and subroute start to the updated endpoint
            if (appRouteImpl != null) {
                final var endpoint = endpointService.getGenericEndpoint(endpointId);
                if (endpoint != null) {
                    appRouteImpl.setAppRouteStart(Util.asList(endpoint));
                    foundRouteStep.setAppRouteStart(Util.asList(endpoint));
                }
            }
        }

        // Set first entry of endpoint informations to the new endpoint
        if (endpointInformationRepository.findAll().size() > 0) {
            final var endpointInfo = endpointInformationRepository.findAll().get(0);
            endpointInfo.setEndpointId(endpointId.toString());
            endpointInformationRepository.saveAndFlush(endpointInfo);
        } else {
            final var endpointInformation = new EndpointInformation();
            endpointInformation.setEndpointId(endpointId.toString());
            endpointInformationRepository.saveAndFlush(endpointInformation);
        }
    }

    /**
     * This method returns the resource if it is exists in an app route.
     *
     * @param resourceId id of the resource
     * @return resource
     */
    public Resource getResourceInAppRoute(final URI resourceId) {

        Resource foundResource = null;
        final ArrayList<RouteStep> emptyList = new ArrayList<>();
        for (final var appRoute : configModelService.getConfigModel().getAppRoute()) {

            if (appRoute == null) {
                continue;
            }
            if (appRoute.getAppRouteOutput() != null) {
                for (final var resource : appRoute.getAppRouteOutput()) {
                    if (resourceId.equals(resource.getId())) {
                        foundResource = resource;
                        log.info("---- [ResourceRepresentationService getResourceInAppRoute] Resource is found in the app route");
                        break;
                    }
                }
            }
            if (appRoute.getHasSubRoute() == null) {
                continue;
            }
            for (final var subRoute : appRoute.getHasSubRoute()) {
                foundResource = getResourceInSubroutes(subRoute, emptyList, resourceId);
            }
        }
        if (foundResource == null) {
            log.info("---- [ResourceRepresentationService getResourceInAppRoute] Could not find any resource"
                    + " in app routes and subroutes");
        }
        return foundResource;
    }

    /**
     * @param routeStep  routestep
     * @param visited    list of route steps already visited
     * @param resourceId id of the resource
     * @return resource
     */
    private Resource getResourceInSubroutes(final RouteStep routeStep,
                                            final List<RouteStep> visited,
                                            final URI resourceId) {
        Resource foundResource = null;
        if (routeStep == null) {
            return null;
        }
        if (routeStep.getAppRouteOutput() != null) {
            for (final var resource : routeStep.getAppRouteOutput()) {
                if (resourceId.equals(resource.getId())) {
                    foundResource = resource;
                    log.info("---- [ResourceRepresentationService getResourceInSubroutes] Resource is found in subroute");
                    break;
                }
            }
        }
        if (routeStep.getHasSubRoute() != null && !routeStep.getHasSubRoute().isEmpty()) {
            for (final var subRoute : routeStep.getHasSubRoute()) {
                if (!visited.contains(subRoute)) {
                    visited.add(routeStep);
                    foundResource = getResourceInSubroutes(subRoute, visited, resourceId);
                }
            }
        }
        return foundResource;
    }
}