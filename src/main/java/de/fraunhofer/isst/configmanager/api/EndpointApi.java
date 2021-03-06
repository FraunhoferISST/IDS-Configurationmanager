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
package de.fraunhofer.isst.configmanager.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

public interface EndpointApi {
    @PostMapping(value = "/generic/endpoint", produces = "application/ld+json")
    @Operation(summary = "Creates a generic endpoint")
    @ApiResponse(responseCode = "200", description = "Created a generic endpoint")
    @ApiResponse(responseCode = "400", description = "Can not create the generic endpoint")
    ResponseEntity<String> createGenericEndpoint(@RequestParam(value = "accessURL") URI accessURL,
                                                 @RequestParam(value = "sourceType") String sourceType,
                                                 @RequestParam(value = "username", required = false) String username,
                                                 @RequestParam(value = "password", required = false) String password);

    @GetMapping(value = "/generic/endpoints", produces = "application/ld+json")
    @Operation(summary = "Returns a list of generic endpoints")
    @ApiResponse(responseCode = "200", description = "Returned a list of generic endpoints")
    @ApiResponse(responseCode = "500", description = "Internal server error")
    ResponseEntity<String> getGenericEndpoints();

    @DeleteMapping(value = "/generic/endpoint", produces = "application/ld+json")
    @Operation(summary = "Deletes a generic endpoint")
    @ApiResponse(responseCode = "200", description = "Deleted a generic endpoint")
    @ApiResponse(responseCode = "400", description = "Can not delete the generic endpoint")
    ResponseEntity<String> deleteGenericEndpoint(@RequestParam(value = "endpointId") URI endpointId);

    @PutMapping(value = "/generic/endpoint", produces = "application/ld+json")
    @Operation(summary = "Updates a generic endpoint")
    @ApiResponse(responseCode = "200", description = "Updated a generic endpoint")
    @ApiResponse(responseCode = "400", description = "Can not update the generic endpoint")
    ResponseEntity<String> updateGenericEndpoint(@RequestParam(value = "id") URI id,
                                                 @RequestParam(value = "accessURL", required = false) URI accessURL,
                                                 @RequestParam(value = "sourceType", required = false) String sourceType,
                                                 @RequestParam(value = "username", required = false) String username,
                                                 @RequestParam(value = "password", required = false) String password);

    @PostMapping(value = "/connector/endpoint", produces = "application/ld+json")
    @Operation(summary = "Creates a new connector endpoint for the connector")
    @ApiResponse(responseCode = "200", description = "Successfully created the connector endpoint for the connector")
    ResponseEntity<String> createConnectorEndpoint(@RequestParam("accessUrl") URI accessUrl,
                                                   @RequestParam(value = "sourceType", required = false) String sourceType);
}
