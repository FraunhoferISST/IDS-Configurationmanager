package de.fraunhofer.isst.configmanager.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

public interface UtilApi {
    @GetMapping(value = "/api/ui/enum/{enumName}")
    @Operation(summary = "Get the specific enum")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Successfully get the enums")})
    ResponseEntity<String> getSpecificEnum(@PathVariable String enumName);

    @PostMapping(value = "/policy-pattern")
    @Operation(summary = "Get pattern of policy")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Successfully get the pattern of policy")})
    ResponseEntity<String> getPolicyPattern(@RequestBody String policy);
}
