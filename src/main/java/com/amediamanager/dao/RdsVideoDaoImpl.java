package com.amediamanager.dao;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.internal.SessionImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.amediamanager.domain.Video;

@Repository
@Transactional
public class RdsVideoDaoImpl implements VideoDao {
	
	@Autowired
    private SessionFactory sessionFactory;
	
	public Session getCurrentSession() {
        return sessionFactory.getCurrentSession();
    }
	
	@Override
	public void save(Video video) {
		getCurrentSession().saveOrUpdate(video);
	}
	
	@Override
	public void update(Video video) {
		getCurrentSession().saveOrUpdate(video);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Video> findByUserId(String userId) {
		List<Video> videos = getCurrentSession().createQuery(
			    "from Video as video where video.owner = :owner")
			    .setParameter("owner", userId)
			    .list();
		
		return videos;
		
	}
}
