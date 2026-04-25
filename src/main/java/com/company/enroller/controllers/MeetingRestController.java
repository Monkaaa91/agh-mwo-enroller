package com.company.enroller.controllers;

import java.util.Collection;
import java.util.List;

import com.company.enroller.model.Meeting;
import com.company.enroller.persistence.MeetingService;
import org.hibernate.sql.Delete;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.company.enroller.model.Participant;
import com.company.enroller.persistence.ParticipantService;

@RestController
@RequestMapping("/meetings")

public class MeetingRestController {
    @Qualifier("meetingService")
    @Autowired
    MeetingService meetingService;
    @Autowired
    ParticipantService participantService;


    @RequestMapping(value = "", method = RequestMethod.GET)
    public ResponseEntity<?> getMeeting() {
        Collection<Meeting> meetings = meetingService.getAll();
        return new ResponseEntity<Collection<Meeting>>(meetings, HttpStatus.OK);
    }
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<?> getMeeting(@PathVariable("id") Long id) {
        Meeting meeting = meetingService.findById(id);
        if (meeting == null) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<Meeting>(meeting, HttpStatus.OK);
    }

    @RequestMapping(value = "", method = RequestMethod.POST)
    public ResponseEntity<?> registerMeeting(@RequestBody Meeting meeting) {
        Meeting existing = meetingService.findById(meeting.getId());

        if (existing != null) {
            return new ResponseEntity("Unable to create. A participant with login " + meeting.getId() + " already exist.", HttpStatus.CONFLICT);
        }
        meetingService.add(meeting);
        return new ResponseEntity(HttpStatus.CREATED);
    }
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteParticipant(@PathVariable("id") Long id) {
        Meeting meeting = meetingService.findById(id);
        if (meeting != null) {
            meetingService.delete(meeting);
            return new ResponseEntity(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity(HttpStatus.NOT_FOUND);

    }
    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public ResponseEntity<?> updateMeeting(@PathVariable("id") Long id , @RequestBody Meeting meeting) {
        Meeting existing = meetingService.findById(id);
        if (existing != null) {
            return new ResponseEntity(HttpStatus.CONFLICT);

        }
        meetingService.update(meeting);
        return new ResponseEntity(HttpStatus.CREATED);
    }
    @GetMapping("/{id}/participants")
    public ResponseEntity<?> getMeetingParticipants(@PathVariable("id") Long meetingId) {
        Meeting meeting = meetingService.findById(meetingId);
        if (meeting == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(meeting.getParticipants(), HttpStatus.OK);
    }
    @PostMapping("/{id}/participants")
    public ResponseEntity<?> addParticipantToMeeting(
            @PathVariable("id") Long meetingId,
            @RequestBody Participant participant) {

        Meeting meeting = meetingService.findById(meetingId);
        if (meeting == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        Participant existingParticipant = participantService.findByLogin(participant.getLogin());
        if (existingParticipant == null) {
            return new ResponseEntity<>("Participant not registered", HttpStatus.BAD_REQUEST);
        }

        boolean added = meetingService.addParticipant(meeting, existingParticipant);
        if (!added) {
            return new ResponseEntity<>("Participant already in meeting", HttpStatus.CONFLICT);
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }





}
