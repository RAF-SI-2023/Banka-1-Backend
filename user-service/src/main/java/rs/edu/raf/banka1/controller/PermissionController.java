package rs.edu.raf.banka1.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import rs.edu.raf.banka1.dtos.PermissionDto;
import rs.edu.raf.banka1.services.PermissionService;

import java.util.List;

@RestController
@RequestMapping("/permission")
@Tag(name = "Permission", description = "Permission API")
//@SecurityRequirement() TODO
@SecurityRequirement(name = "basicScheme")
@CrossOrigin
@SecurityRequirement(name = "Authorization")
public class PermissionController {
    private PermissionService permissionService;

    @Autowired
    public PermissionController(PermissionService permissionService) {
        this.permissionService = permissionService;
    }

    @GetMapping(value = "/getAll", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get all permissions", description = "Returns all permissions",
            parameters = {
                    @Parameter(name = "Authorization", description = "JWT token", required = true, in = ParameterIn.HEADER)
            })
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = List.class,
                                    subTypes = {PermissionDto.class}))}),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<PermissionDto>> getAllPermissions() {
        return ResponseEntity.ok(permissionService.getAllPermissions());
    }



}
