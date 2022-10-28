package edu.ucsb.cs156.example.controllers;

import edu.ucsb.cs156.example.entities.HelpRequest;
import edu.ucsb.cs156.example.errors.EntityNotFoundException;
import edu.ucsb.cs156.example.repositories.HelpRequestRepository;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;

import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.nimbusds.oauth2.sdk.GeneralException;

import java.time.LocalDateTime;

import javax.naming.NameNotFoundException;
import javax.validation.Valid;


@Api(description = "HelpRequest")
@RequestMapping("/api/HelpRequest")
@RestController
@Slf4j
public class HelpRequestController extends ApiController {

    @Autowired
    HelpRequestRepository helpRequestRepository;

    @ApiOperation(value = "List all help requests")
    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("/all")
    public Iterable<HelpRequest> allRequests() {
        Iterable<HelpRequest> request = helpRequestRepository.findAll();
        return request;
    }

    @ApiOperation(value = "Create a new help request")
    @PreAuthorize("hasRole('ROLE_USER')")
    @PostMapping("/post")
    public HelpRequest postRequest(
        @ApiParam("requesterEmail") @RequestParam String requesterEmail,
        @ApiParam("teamId") @RequestParam String teamId,
        @ApiParam("tableOrBreakoutRoom") @RequestParam String tableOrBreakoutRoom,
        @ApiParam("requestTime") @RequestParam LocalDateTime requestTime,
        @ApiParam("explanation") @RequestParam String explanation,
        @ApiParam("solved") @RequestParam boolean solved
        )
        {

        HelpRequest request = new HelpRequest();
        request.setRequesterEmail(requesterEmail);
        request.setTeamId(teamId);
        request.setTableOrBreakoutRoom(tableOrBreakoutRoom);
        request.setRequestTime(requestTime);
        request.setExplanation(explanation);
        request.setSolved(solved);

        HelpRequest savedRequest = helpRequestRepository.save(request);

        return savedRequest;
    }

    @ApiOperation(value = "Get a single help request")
    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("")
    public HelpRequest getById(
            @ApiParam("id") @RequestParam Long id) {
        HelpRequest request = helpRequestRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(HelpRequest.class, id));

        return request;
    }

    @ApiOperation(value = "Update a single help request")
    @PreAuthorize("hasRole('ROLE_USER')")
    @PutMapping("")
    public HelpRequest updateHelpRequest(
            @ApiParam("id") @RequestParam Long id,
            @RequestBody @Valid HelpRequest incoming) {

        HelpRequest request = helpRequestRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(HelpRequest.class, id));

        request.setRequesterEmail(incoming.getRequesterEmail());
        request.setTeamId(incoming.getTeamId());
        request.setTableOrBreakoutRoom(incoming.getTableOrBreakoutRoom());
        request.setRequestTime(incoming.getRequestTime());
        request.setExplanation(incoming.getExplanation());
        request.setSolved(incoming.getSolved());

        helpRequestRepository.save(request);

        return request;
    }

    @ApiOperation(value = "Delete a help request")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("")
    public Object deleteUCSBDate(
            @ApiParam("id") @RequestParam Long id) {
        HelpRequest request = helpRequestRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(HelpRequest.class, id));

        helpRequestRepository.delete(request);
        return genericMessage("HelpRequest with id %s deleted".formatted(id));
    }
}
