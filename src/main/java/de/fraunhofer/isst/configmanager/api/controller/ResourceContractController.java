package de.fraunhofer.isst.configmanager.api.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import de.fraunhofer.iais.eis.ContractOffer;
import de.fraunhofer.iais.eis.ids.jsonld.Serializer;
import de.fraunhofer.isst.configmanager.api.ResourceContractApi;
import de.fraunhofer.isst.configmanager.api.service.ResourceService;
import de.fraunhofer.isst.configmanager.connector.clients.DefaultResourceClient;
import de.fraunhofer.isst.configmanager.model.usagecontrol.Pattern;
import de.fraunhofer.isst.configmanager.util.ValidateApiInput;
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

/**
 * The api class implements the ResourceContractApi and offers the possibilities to manage
 * the contracts in a resource.
 */
@Slf4j
@RestController
@RequestMapping("/api/ui")
@Tag(name = "Resource contracts Management", description = "Endpoints for managing the contracts of a resource")
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ResourceContractController implements ResourceContractApi {

    transient ResourceService resourceService;
    transient Serializer serializer;
    transient DefaultResourceClient client;

    @Autowired
    public ResourceContractController(final ResourceService resourceService,
                                      final Serializer serializer,
                                      final DefaultResourceClient client) {
        this.resourceService = resourceService;
        this.serializer = serializer;
        this.client = client;
    }

    /**
     * This method returns the contract from a specific resource.
     *
     * @param resourceId id of the resource
     * @return a suitable http response depending on success
     */
    @Override
    public ResponseEntity<String> getResourceContract(final URI resourceId) {
        log.info(">> GET /resource/contract resourceId: " + resourceId);
        ResponseEntity<String> response;

        final var contractOffer = resourceService.getResourceContract(resourceId);
        if (contractOffer != null) {
            try {
                response = ResponseEntity.ok(serializer.serialize(contractOffer));
            } catch (IOException e) {
                log.error(e.getMessage(), e);
                response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Problems while parsing serializing the contract offer");
            }
        } else {
            response = ResponseEntity.badRequest().body("Could not get the resource contract");
        }

        return response;
    }

    /**
     * This method updates the contract of a resource.
     *
     * @param resourceId   id of the resource
     * @param contractJson id of the contract
     * @return a suitable http response depending on success
     */
    @Override
    public ResponseEntity<String> updateResourceContract(final URI resourceId, final String contractJson) {
        log.info(">> PUT /resource/contract resourceId: " + resourceId + " contractJson: " + contractJson);
        ResponseEntity<String> response;

        if ("{}".equals(contractJson) && ValidateApiInput.notValid(resourceId.toString())) {
            response = ResponseEntity.badRequest().body("All validated parameter have undefined as value!");
        } else {
            ContractOffer contractOffer;

            try {
                contractOffer = serializer.deserialize(contractJson, ContractOffer.class);

                if (contractOffer != null) {
                    final var jsonObject = new JSONObject();

                    try {
                        jsonObject.put("resourceID", resourceId.toString());
                        jsonObject.put("contractID", contractOffer.getId().toString());

                        final var clientResponse = client.updateResourceContract(resourceId.toString(), contractJson);

                        resourceService.updateResourceContractInAppRoute(resourceId, contractOffer);
                        jsonObject.put("connectorResponse", clientResponse);
                        response = ResponseEntity.ok(jsonObject.toJSONString());
                    } catch (IOException e) {
                        log.error(e.getMessage(), e);
                        jsonObject.put("message", "Problems while updating the contract at the connector");
                        response = ResponseEntity.badRequest().body(jsonObject.toJSONString());
                    }
                } else {
                    response = ResponseEntity.badRequest().body("Could not update the resource representation");
                }
            } catch (IOException e) {
                log.error(e.getMessage(), e);
                response = ResponseEntity.badRequest().body("Problems while deserializing the contract");
            }
        }

        return response;
    }

    /**
     * @param resourceId   id of the resource
     * @param pattern      the pattern of the contract
     * @param contractJson the created contract for the resource
     * @return a suitable http response depending on success
     */
    @Override
    public ResponseEntity<String> updateContractForResource(URI resourceId, Pattern pattern, String contractJson) {
        log.info(">> PUT /resource/contract/update resourceId: " + resourceId + "pattern" + pattern.toString() +
                " contractJson: " + contractJson);

        if (ValidateApiInput.notValid(resourceId.toString())) {
            return ResponseEntity.badRequest().body("All validated parameter have undefined as " +
                    "value!");
        }

        ContractOffer contractOffer = null;
        try {
            contractOffer = resourceService.getContractOffer(pattern, contractJson);
        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
        }

        // Update the resource contract
        if (contractOffer != null) {
            final var jsonObject = new JSONObject();
            try {
                String contract = serializer.serialize(contractOffer);
                jsonObject.put("resourceID", resourceId.toString());
                jsonObject.put("contractID", contractOffer.getId().toString());
                final var response = client.updateResourceContract(resourceId.toString(), contract);
                resourceService.updateResourceContractInAppRoute(resourceId, contractOffer);
                jsonObject.put("connectorResponse", response);
                return ResponseEntity.ok(jsonObject.toJSONString());
            } catch (IOException e) {
                log.error(e.getMessage(), e);
                jsonObject.put("message", "Problems while updating the contract at the connector");
                return ResponseEntity.badRequest().body(jsonObject.toJSONString());
            }
        } else {
            return ResponseEntity.badRequest().body("Could not update the resource representation");
        }
    }
}