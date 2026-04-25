package com.company.enroller.persistence;

import java.util.Collection;
import java.util.List;

import com.company.enroller.model.Meeting;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.springframework.stereotype.Component;

import com.company.enroller.model.Participant;

@Component("participantService")
public class ParticipantService {

	DatabaseConnector connector;

	public ParticipantService() {
		connector = DatabaseConnector.getInstance();
	}

	public Collection<Participant> getAll() {
		String hql = "FROM Participant";
		Query query = connector.getSession().createQuery(hql);
		return query.list();
	}

    public Participant findByLogin(String login) {
        String hql = "FROM Participant WHERE login = :login";
        Query query = connector.getSession().createQuery(hql);
        query.setParameter("login", login);
        return (Participant) query.uniqueResult();

    }

    public void add(Participant participant) {

        connector.getSession().save(participant);
    }
    public void delete(Participant participant) {
        connector.getSession().delete(participant);

    }
    public void update(Participant participant) {
        Transaction transaction = connector.getSession().beginTransaction();
        connector.getSession().merge(participant);
        transaction.commit();
    }
    public void add(Meeting meeting) {
        connector.getSession().save(meeting);
    }

    public List<Participant> findAll() {
        String hql = "FROM Participant";
        Query query = connector.getSession().createQuery(hql);
        return query.list();
    }

}
