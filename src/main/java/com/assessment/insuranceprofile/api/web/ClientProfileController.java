package com.assessment.insuranceprofile.api.web;

import com.assessment.insuranceprofile.api.ClientNotFoundException;
import com.assessment.insuranceprofile.api.ClientService;
import com.assessment.insuranceprofile.domain.Client;
import com.assessment.insuranceprofile.domain.RiskProfile;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Value;
import org.springdoc.core.converters.PageableAsQueryParam;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@AllArgsConstructor
class ClientProfileController {

    private final ClientService clientService;

    @PageableAsQueryParam
    @Operation(description = "Returns a list of existing clients")
    @ApiResponse(responseCode = "200", description = "if operation succeeded")
    @GetMapping("/clients/")
    public Collection<ClientResource> getExistingClients(
            @Parameter(hidden = true)
            @PageableDefault(size = Integer.MAX_VALUE, page = 0) Pageable pageable
    ) {
        return clientService.clients(pageable).stream()
                .map(ClientResource::new)
                .collect(Collectors.toList());
    }

    @Operation(description = "Returns a specific client provided a client id had been passed")
    @GetMapping(value = "/clients/{client-id}")
    public ClientResource getExistingClient(
            @Parameter(description = "Identifier of an existing client", required = true)
            @PathVariable("client-id") Long id
    ) {
        return clientService.findClient(id)
                .map(ClientResource::new)
                .orElseThrow(() -> new ClientNotFoundException(id));
    }

    @Operation(description = "Creates a new client")
    @PostMapping(value = "/clients/", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ClientResource createNewClient(
            @Parameter(description = "Client to create", required = true)
            @Validated @RequestBody NewClientRequest request
    ) {
        return new ClientResource(
                clientService.createNewClient(request.toClient())
        );
    }

    @Operation(description = "Updates an existing client")
    @PutMapping(value = "/clients/{client-id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ClientResource updateClient(
            @Parameter(description = "Identifier of an existing client", required = true)
            @PathVariable("client-id")
                    Long id,
            @Parameter(description = "A set of attributes used for update operation", required = true)
            @Validated
            @RequestBody
                    UpdateClientRequest request
    ) {
        return new ClientResource(clientService.updateExistingClient(request.toClient(id)));
    }

    @Operation(description = "Deletes an existing client")
    @DeleteMapping(value = "/clients/{client-id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteClient(
            @Parameter(description = "Identifier of an existing client", required = true)
            @PathVariable("client-id") Long id
    ) {
        clientService.deleteExistingClient(id);
    }

    @Operation(description = "Merges existing clients and returns a new client with the highest risk profile")
    @PostMapping(value = "/clients/merging", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ClientResource mergeClients(
            @Parameter(description = "A set of client profile identifies to be merged", required = true)
            @Validated
            @RequestBody
                    MergeClientsRequest request
    ) {
        return new ClientResource(clientService.mergeClients(request.ids()));
    }

}

@Schema(description = "A request to create a new customer")
@Value
@Getter(onMethod_ = @JsonProperty)
class NewClientRequest {
    @Schema(
            description = "Risk profile of the client",
            example = "LOW",
            implementation = RiskProfile.class
    )
    @NotNull
    private final String riskProfile;

    public Client toClient() {
        return Client.newClient(RiskProfile.valueOf(riskProfile));
    }
}

@Schema(description = "A request to update an existing customer")
@Value
@Getter(onMethod_ = @JsonProperty)
class UpdateClientRequest {
    @Schema(
            description = "Risk profile of the client",
            example = "LOW",
            implementation = RiskProfile.class
    )
    @NotNull
    @JsonProperty
    private final String riskProfile;

    public Client toClient(Long id) {
        return new Client(id, RiskProfile.valueOf(riskProfile));
    }
}

@Schema(description = "A request to merge existing clients")
@Value
@Getter(onMethod_ = @JsonProperty)
class MergeClientsRequest {
    @Schema(
            description = "Set of client identifies to be merged",
            example = "[1, 2, 3]"
    )
    @NotNull
    @Size(min = 2)
    private final Set<Long> ids;
}

@Schema(description = "Client profile")
@Value
@Getter(onMethod_ = @JsonProperty)
class ClientResource {

    @Schema(
            description = "A unique identifier of the client",
            example = "1"
    )
    @JsonSerialize(using = ToStringSerializer.class)
    private final Long id;

    @Schema(
            description = "Risk profile of the client",
            example = "LOW",
            implementation = RiskProfile.class
    )
    private final String riskProfile;

    public ClientResource(Client client) {
        this.id = client.id();
        this.riskProfile = client.riskProfile().name();
    }
}