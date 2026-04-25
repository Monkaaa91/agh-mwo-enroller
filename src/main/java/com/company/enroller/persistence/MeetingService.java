package com.company.enroller.persistence;

import com.company.enroller.model.Meeting;
import com.company.enroller.model.Participant;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@Component("meetingService")
@RestController
@RequestMapping("/participants")
public class MeetingService {
    @Autowired
    ParticipantService participantService;


    DatabaseConnector connector;

	public MeetingService() {
		connector = DatabaseConnector.getInstance();
	}

	public Collection<Meeting> getAll() {
		String hql = "FROM Meeting";
		Query query = connector.getSession().createQuery(hql);
		return query.list();
	}


    public Meeting findById(Long id) {
        String hql = "FROM Meeting WHERE id = :id";
        Query query = connector.getSession().createQuery(hql);
        query.setParameter("id", id);
        return (Meeting) query.uniqueResult();

    }

    public void add(Meeting meeting) {
        connector.getSession().save(meeting);
    }
    public void delete(Meeting meeting) {
        connector.getSession().delete(meeting);
    }
    public void  update(Meeting meeting) {
        Transaction transaction = connector.getSession().beginTransaction();
        connector.getSession().merge(meeting);
        transaction.commit();
    }
    public boolean removeParticipant(Meeting meeting, Participant participant) {
        if (!meeting.getParticipants().contains(participant)) {
            return false;
        }
        Transaction tx = connector.getSession().beginTransaction();
        meeting.removeParticipant(participant);
        connector.getSession().merge(meeting);
        tx.commit();
        return true;
    }
    public boolean addParticipant(Meeting meeting, Participant participant) {
        if (!meeting.getParticipants().contains(participant)) {
            return false;
        }
        Transaction tx = connector.getSession().beginTransaction();
        meeting.addParticipant(participant);
        connector.getSession().merge(meeting);
        tx.commit();
        return true;
    }

}
