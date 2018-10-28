package com.example.jpa.controller;

import com.example.jpa.exception.ResourceNotFoundException;
import com.example.jpa.model.Incident;
import com.example.jpa.model.User;
import com.example.jpa.repository.IncidentRepository;
import com.example.jpa.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
public class IncidentController {

    @Autowired
    private IncidentRepository incidentRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/users/{userId}/incidents")
    public List<Incident> getAllIncidentsByUserId(@PathVariable (value = "userId") Long userId,
                                                  Pageable pageable) {
        Optional<User> target = userRepository.findById(userId);
        return target.get().getIncidentList();
    }

    @PostMapping("/users/{userId}/incidents")
    public Incident createIncident(@PathVariable (value = "userId") Long userId,
                                   @Valid @RequestBody Incident incident) {
        return userRepository.findById(userId).map(user -> {
            incident.setUser(user);
            user.getIncidentList().add(incident);
            return incidentRepository.save(incident);
        }).orElseThrow(() -> new ResourceNotFoundException("UserId " + userId + " not found"));
    }

    @PutMapping("/users/{userId}/incidents/{incidentId}")
    public Incident updateIncident(@PathVariable (value = "userId") Long userId,
                                 @PathVariable (value = "incidentId") Long incidentId,
                                 @Valid @RequestBody Incident incidentRequest) {
        if(!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("UserId " + userId + " not found");
        }

        return incidentRepository.findById(incidentId).map(incident -> {
            incident.setDescription(incidentRequest.getDescription());
            return incidentRepository.save(incident);
        }).orElseThrow(() -> new ResourceNotFoundException("IncidentId " + incidentId + "not found"));
    }

    @DeleteMapping("/users/{userId}/incidents/{incidentId}")
    public ResponseEntity<?> deleteComment(@PathVariable (value = "userId") Long userId,
                              @PathVariable (value = "incidentId") Long incidentId) {
        if(!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("UserId " + userId + " not found");
        }

        return incidentRepository.findById(incidentId).map(incident -> {
             incidentRepository.delete(incident);
             return ResponseEntity.ok().build();
        }).orElseThrow(() -> new ResourceNotFoundException("IncidentId " + incidentId + " not found"));
    }
}
