package com.amediamanager.dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.amediamanager.domain.Video;

@Repository
@Transactional
public class RdsVideoDaoImpl implements VideoDao {

	@Autowired
	ConnectionManager connectionManager;
	
	@Autowired
    private SessionFactory sessionFactory;
	
	public Session getCurrentSession() {
        return sessionFactory.getCurrentSession();
    }
	
	@Override
	public void save(Video video) {
		getCurrentSession().saveOrUpdate(video);
	}
}
