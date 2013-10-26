package com.amediamanager.dao;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.amediamanager.domain.Tag;
import com.amediamanager.domain.Video;

@Repository
@Transactional
public class RDSTagDaoImpl implements TagDao {

	@Autowired
    private SessionFactory sessionFactory;
	
	public Session getCurrentSession() {
        return sessionFactory.getCurrentSession();
    }

	@Override
	public void save(Tag t) {
		sessionFactory.getCurrentSession().saveOrUpdate(t);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<TagCount> getTagsForUser(String u) {
		Query query = sessionFactory.getCurrentSession().createQuery("select new com.amediamanager.dao.TagDao$TagCount(tag.tagId, tag.name, count(video.id)) from Tag tag join tag.videos video where video.owner = :owner group by tag.tagId");
		query.setParameter("owner", u);
		return query.list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Video> getVideosForUserByTag(String user, String tagId) {
		Query query = sessionFactory.getCurrentSession().createQuery("select video from Video video join video.tags tag where video.owner = :owner and tag.tagId = :tag");
		query.setParameter("owner", user);
		query.setParameter("tag", tagId);
		return query.list();
	}
}
