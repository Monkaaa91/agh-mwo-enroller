package com.company.enroller.controllers;

import java.util.Collection;
import java.util.Map;

import com.company.enroller.model.Meeting;
import com.company.enroller.persistence.MeetingService;
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
    @RequestMapping(value = "/{id}/participants", method =  RequestMethod.GET)
    public ResponseEntity<?> getMeetingParticipants(@PathVariable("id") Long id) {
        Meeting meeting = meetingService.findById(id);
        if (meeting == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(meeting.getParticipants(), HttpStatus.OK);
    }
    @RequestMapping(value = "/{id}/participants", method = RequestMethod.POST)
    public ResponseEntity<?> addParticipantToMeeting(@PathVariable long id, @RequestBody Map<String, String> json) {
        String login = json.get("login");

        Meeting meeting = meetingService.findById(id);
        Participant participant = participantService.findByLogin(login);

        if (meeting == null || participant == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        meeting.addParticipant(participant);
        meetingService.update(meeting);

        return new ResponseEntity<>(HttpStatus.OK);
    }
    @RequestMapping(value = "/{id}/participants/{login}", method = RequestMethod.DELETE)
    public ResponseEntity<?> removeParticipantFromMeeting(@PathVariable long id, @PathVariable String login) {
        Meeting meeting = meetingService.findById(id);
        Participant participant = participantService.findByLogin(login);

        if (meeting == null || participant == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        meeting.removeParticipant(participant);
        meetingService.update(meeting);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    @RequestMapping(value = "", method = RequestMethod.GET)
    public ResponseEntity<?> getMeetings(@RequestParam(value = "filter", defaultValue = "") String filter) {
        Collection<Meeting> meetings;
        if (filter.isEmpty()) {
            meetings = meetingService.getAll();
        } else {
            meetings = meetingService.filterByTitle(filter);
        }
        return new ResponseEntity<Collection<Meeting>>(meetings, HttpStatus.OK);
    }
}











