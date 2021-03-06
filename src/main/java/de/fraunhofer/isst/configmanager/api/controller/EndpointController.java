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
package de.fraunhofer.isst.configmanager.api.controller;


import de.fraunhofer.iais.eis.BaseConnectorImpl;
import de.fraunhofer.iais.eis.ConfigurationModelImpl;
import de.fraunhofer.iais.eis.ConnectorEndpoint;
import de.fraunhofer.iais.eis.ConnectorEndpointBuilder;
import de.fraunhofer.iais.eis.ids.jsonld.Serializer;
import de.fraunhofer.isst.configmanager.api.EndpointApi;
import de.fraunhofer.isst.configmanager.api.service.ConfigModelService;
import de.fraunhofer.isst.configmanager.api.service.EndpointService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;

/**
 * The api class implements the EndpointApi and offers the possibilities to manage
 * the endpoints in the configuration manager.
 */
@Slf4j
@RestController
@RequestMapping("/api/ui")
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Tag(name = "Endpoints Management", description = "Different endpoint types can be managed here")
public class EndpointController implements EndpointApi {

    transient Serializer serializer;
    transient ConfigModelService configModelService;
    transient EndpointService endpointService;

    @Autowired
    public EndpointController(final Serializer serializer,
                              final ConfigModelService configModelService,
                              final EndpointService endpointService) {
        this.serializer = serializer;
        this.configModelService = configModelService;
        this.endpointService = endpointService;
    }

    /**
     * This method creates a generic endpoint with the given parameters.
     *
     * @param accessURL access url of the parameter
     * @param sourceType source type of the endpoint
     * @param username  username for the authentication
     * @param password  password for the authentication
     * @return a suitable http response depending on success
     */
    @Override
    public ResponseEntity<String> createGenericEndpoint(final URI accessURL,
                                                        final String sourceType,
                                                        final String username,
                                                        final String password) {
        if (log.isInfoEnabled()) {
            log.info(">> POST /generic/endpoint accessURL: " + accessURL + " username: " + username);
        }
        ResponseEntity<String> response;

        final var genericEndpoint = endpointService.createGenericEndpoint(accessURL, sourceType, username, password);
        if (genericEndpoint != null) {
            final var jsonObject = new JSONObject();
            jsonObject.put("id", genericEndpoint.getId().toString());
            jsonObject.put("message", "Created a new generic endpoint");

            response = ResponseEntity.ok(jsonObject.toJSONString());
        } else {
            response = ResponseEntity.badRequest().body("Could not create a generic endpoint");
        }

        return response;
    }

    /**
     * This method returns a list of generic endpoints.
     *
     * @return a suitable http response depending on success
     */
    @Override
    public ResponseEntity<String> getGenericEndpoints() {
        if (log.isInfoEnabled()) {
            log.info(">> GET /generic/endpoints");
        }
        ResponseEntity<String> response;

        final var endpoints = endpointService.getGenericEndpoints();

        try {
            response = ResponseEntity.ok(serializer.serialize(endpoints));
        } catch (IOException e) {
            if (log.isErrorEnabled()) {
                log.error(e.getMessage(), e);
            }
            response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

        return response;
    }

    /**
     * This method deletes a generic endpoint.
     *
     * @param endpointId id of the generic endpoint
     * @return a suitable http response depending on success
     */
    @Override
    public ResponseEntity<String> deleteGenericEndpoint(final URI endpointId) {
        if (log.isInfoEnabled()) {
            log.info(">> DELETE /generic/endpoint endpointId: " + endpointId);
        }
        ResponseEntity<String> response;

        final var deleted = endpointService.deleteGenericEndpoint(endpointId);

        if (deleted) {
            response = ResponseEntity.ok("Deleted the generic endpoint with id: " + endpointId);
        } else {
            response = ResponseEntity.badRequest().body("Could not delete the generic endpoint with id: " + endpointId);
        }

        return response;
    }

    /**
     * This method updates a generic endpoint with the given parameters.
     *
     * @param endpointId id of the generic endpoint
     * @param accessURL  access url of the endpoint
     * @param sourceType source type of the endpoint
     * @param username   username for authentication
     * @param password   password for authentication
     * @return a suitable http response depending on success
     */
    @Override
    public ResponseEntity<String> updateGenericEndpoint(final URI endpointId,
                                                        final URI accessURL,
                                                        final String sourceType,
                                                        final String username,
                                                        final String password) {
        if (log.isInfoEnabled()) {
            log.info(">> PUT /generic/endpoint endpointId: " + endpointId + " accessURL: " + accessURL);
        }
        ResponseEntity<String> response;

        final var updated = endpointService.updateGenericEndpoint(endpointId, accessURL, sourceType, username, password);

        if (updated) {
            response = ResponseEntity.ok("Updated the generic endpoint with id: " + endpointId);
        } else {
            response = ResponseEntity.badRequest().body("Could not update the generic endpoint with id: " + endpointId);
        }

        return response;
    }

    /**
     * This method creates a connector endpoint with given parameters.
     *
     * @param accessUrl access url of the endpoint
     * @param sourceType source type of the endpoint
     * @return a suitable http response depending on success
     */
    public ResponseEntity<String> createConnectorEndpoint(final URI accessUrl, final String sourceType) {
        if (log.isInfoEnabled()) {
            log.info(">> POST /connector/endpoint accessUrl: " + accessUrl + " sourceType: " + sourceType);
        }

        final var configModelImpl = (ConfigurationModelImpl) configModelService.getConfigModel();
        final var baseConnector = (BaseConnectorImpl) configModelImpl.getConnectorDescription();

        if (baseConnector.getHasEndpoint() == null) {
            baseConnector.setHasEndpoint(new ArrayList<>());
        }

        final var connectorEndpoints = (ArrayList<ConnectorEndpoint>) baseConnector.getHasEndpoint();
        final var connectorEndpoint = new ConnectorEndpointBuilder()._accessURL_(accessUrl).build();

        connectorEndpoint.setProperty("ids:sourceType", sourceType);

        connectorEndpoints.add(connectorEndpoint);
        configModelService.saveState();

        final var jsonObject = new JSONObject();

        jsonObject.put("connectorEndpointId", connectorEndpoint.getId().toString());
        jsonObject.put("message", "Created a new connector endpoint for the connector");

        return ResponseEntity.ok(jsonObject.toJSONString());
    }
}
