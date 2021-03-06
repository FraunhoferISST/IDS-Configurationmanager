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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.net.URI;
import java.util.ArrayList;

public interface ResourceApi {
    @GetMapping(value = "/resource", produces = "application/ld+json")
    @Operation(summary = "Returns the specific resource from the connector")
    @ApiResponse(responseCode = "200", description = "Successfully returned the specifc resource from the connector")
    @ApiResponse(responseCode = "400", description = "Can not find the resource")
    @ApiResponse(responseCode = "500", description = "Internal server error")
    ResponseEntity<String> getResource(@RequestParam(value = "resourceId") URI resourceId);

    @GetMapping(value = "/resources", produces = "application/ld+json")
    @Operation(summary = "Returns all resources from the connector")
    @ApiResponse(responseCode = "200", description = "Successfully returned all resources from the connector")
    @ApiResponse(responseCode = "500", description = "Internal server error")
    ResponseEntity<String> getResources();

    @GetMapping(value = "/resources/requested", produces = "application/ld+json")
    @Operation(summary = "Returns all requested resources from the connector")
    @ApiResponse(responseCode = "200", description = "Successfully returned all requested resources from the connector")
    ResponseEntity<String> getRequestedResources();

    @PostMapping(value = "/resource", produces = "application/ld+json")
    @Operation(summary = "Creates a resource for the connector")
    @ApiResponse(responseCode = "200", description = "Successfully created a resource for the connector")
    @ApiResponse(responseCode = "400", description = "Can not create the resource")
    ResponseEntity<String> createResource(@RequestParam("title") String title,
                                          @RequestParam("description") String description,
                                          @RequestParam("language") String language,
                                          @RequestParam("keyword") ArrayList<String> keywords,
                                          @RequestParam("version") String version,
                                          @RequestParam("standardlicense") URI standardlicense,
                                          @RequestParam("publisher") URI publisher);

    @PutMapping(value = "/resource", produces = "application/ld+json")
    @Operation(summary = "Updates the specific resource at the connector")
    @ApiResponse(responseCode = "200", description = "Successfully updated the specific resource at the connector")
    @ApiResponse(responseCode = "400", description = "Validation failed. Can not update the resource")
    @ApiResponse(responseCode = "404", description = "Can not find the resource")
    @ApiResponse(responseCode = "500", description = "Internal server error")
    ResponseEntity<String> updateResource(@RequestParam("resourceId") URI resourceId,
                                          @RequestParam(value = "title", required = false) String title,
                                          @RequestParam(value = "description", required = false) String description,
                                          @RequestParam(value = "language", required = false) String language,
                                          @RequestParam(value = "keyword", required = false) ArrayList<String> keywords,
                                          @RequestParam(value = "version", required = false) String version,
                                          @RequestParam(value = "standardlicense", required = false) URI standardlicense,
                                          @RequestParam(value = "publisher", required = false) URI publisher);

    @DeleteMapping(value = "/resource")
    @Operation(summary = "Deletes the specific resource from the connector")
    @ApiResponse(responseCode = "200", description = "Successfully deleted the specific resource from the connector")
    @ApiResponse(responseCode = "400", description = "Can not delete the resource")
    ResponseEntity<String> deleteResource(@RequestParam(value = "resourceId") URI resourceId);
}
