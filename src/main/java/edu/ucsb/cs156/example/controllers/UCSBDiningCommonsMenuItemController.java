package edu.ucsb.cs156.example.controllers;

import edu.ucsb.cs156.example.entities.UCSBDiningCommonsMenuItem;
import edu.ucsb.cs156.example.errors.EntityNotFoundException;
import edu.ucsb.cs156.example.repositories.UCSBDiningCommonsMenuItemRepository;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;

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


@Api(description = "UCSBDiningCommonsMenuItem")
@RequestMapping("/api/UCSBDiningCommonsMenuItem")
@RestController
@Slf4j
public class UCSBDiningCommonsMenuItemController extends ApiController {

    @Autowired
    UCSBDiningCommonsMenuItemRepository ucsbDiningCommonsMenuItemRepository;

    @ApiOperation(value = "List all ucsb dining commons")
    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("/all")
    public Iterable<UCSBDiningCommonsMenuItem> allCommons() {
        Iterable<UCSBDiningCommonsMenuItem> commons = ucsbDiningCommonsMenuItemRepository.findAll();
        return commons;
    }

    
    @ApiOperation(value = "Get a single menu item")
    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("")
    public UCSBDiningCommonsMenuItem getById(
            @ApiParam("id") @RequestParam Long id) {
        UCSBDiningCommonsMenuItem commons = ucsbDiningCommonsMenuItemRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(UCSBDiningCommonsMenuItem.class, id));

        return commons;
    }
    

    @ApiOperation(value = "Create a new menu item")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/post")
    public UCSBDiningCommonsMenuItem postCommons(
        @ApiParam("id") @RequestParam Long id,
        @ApiParam("diningCommonsCode") @RequestParam String diningCommonsCode,
        @ApiParam("name") @RequestParam String name,
        @ApiParam("station") @RequestParam String station
        )
        {

        UCSBDiningCommonsMenuItem commons = new UCSBDiningCommonsMenuItem();
        commons.setId(id);
        commons.setDiningCommonsCode(diningCommonsCode);
        commons.setName(name);
        commons.setStation(station);

        UCSBDiningCommonsMenuItem savedCommons = ucsbDiningCommonsMenuItemRepository.save(commons);

        return savedCommons;
    }

    /*
    @ApiOperation(value = "Delete a UCSBDiningCommonsMenuItem")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("")
    public Object deleteCommons(
            @ApiParam("id") @RequestParam Long id) {
        UCSBDiningCommonsMenuItem commons = ucsbDiningCommonsMenuItemRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(UCSBDiningCommonsMenuItem.class, id));

        ucsbDiningCommonsMenuItemRepository.delete(commons);
        return genericMessage("UCSBDiningCommonsMenuItem with id %s deleted".formatted(id));
    }
    */

    /*
    @ApiOperation(value = "Update a single commons")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("")
    public UCSBDiningCommonsMenuItem updateCommons(
            @ApiParam("id") @RequestParam Long id,
            @RequestBody @Valid UCSBDiningCommonsMenuItem incoming) {

        UCSBDiningCommonsMenuItem commons = ucsbDiningCommonsMenuItemRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(UCSBDiningCommonsMenuItem.class, id));


        commons.setName(incoming.getName());  
        commons.setStation(incoming.getStation());


        ucsbDiningCommonsMenuItemRepository.save(commons);

        return commons;
    }
    */
}