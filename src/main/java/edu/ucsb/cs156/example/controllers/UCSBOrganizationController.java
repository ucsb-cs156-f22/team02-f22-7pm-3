package edu.ucsb.cs156.example.controllers;

import edu.ucsb.cs156.example.entities.UCSBOrganization;
import edu.ucsb.cs156.example.errors.EntityNotFoundException;
import edu.ucsb.cs156.example.repositories.UCSBOrganizationRepository;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;

import com.fasterxml.jackson.core.JsonProcessingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@Api(description = "UCSBOrganizations")
@RequestMapping("/api/UCSBOrganization")
@RestController
@Slf4j
public class UCSBOrganizationController extends ApiController {

    @Autowired
    UCSBOrganizationRepository ucsbOrganizationRepository;

    @ApiOperation(value = "List all ucsb organizations")
    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("/all")
    public Iterable<UCSBOrganization> allUCSBOrganizations() {
        Iterable<UCSBOrganization> orgs = ucsbOrganizationRepository.findAll();
        return orgs;
    }

    @ApiOperation(value = "Get a single organization")
    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("")
    public UCSBOrganization getById(
            @ApiParam("id") @RequestParam String id) {
        UCSBOrganization org = ucsbOrganizationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(UCSBOrganization.class, id));

        return org;
    }

    @ApiOperation(value = "Create a new organization")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/post")
    public UCSBOrganization postUCSBOrganization(
            @ApiParam("orgCode") @RequestParam String orgCode,
            @ApiParam("orgTranslationShort") @RequestParam String orgTranslationShort,
            @ApiParam("orgTranslation") @RequestParam String orgTranslation,
            @ApiParam("inactive") @RequestParam boolean inactive)
            {

            UCSBOrganization org = new UCSBOrganization();
            org.setOrgCode(orgCode);
            org.setOrgTranslationShort(orgTranslationShort);
            org.setOrgTranslation(orgTranslation);
            org.setInactive(inactive);
    
            UCSBOrganization savedOrg = ucsbOrganizationRepository.save(org);
    
            return savedOrg;
    }

    @ApiOperation(value = "Update a single organization")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("")
    public UCSBOrganization updateOrganization(
            @ApiParam("id") @RequestParam String id,
            @RequestBody @Valid UCSBOrganization incoming) {

            UCSBOrganization org = ucsbOrganizationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(UCSBOrganization.class, id));


        org.setInactive(incoming.getInactive());  
        org.setOrgTranslation(incoming.getOrgTranslation());
        org.setOrgTranslationShort(incoming.getOrgTranslationShort());

        ucsbOrganizationRepository.save(org);

        return org;
    }
}
