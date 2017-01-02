package org.omni.dao;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import org.omni.model.Ordem;

/**
 * DAO for Ordem
 */
@Stateless
public class OrdemDao {
	@PersistenceContext(unitName = "omni-persistence-unit")
	private EntityManager em;

	public void create(Ordem entity) {
		em.persist(entity);
	}

	public void deleteById(Long id) {
		Ordem entity = em.find(Ordem.class, id);
		if (entity != null) {
			em.remove(entity);
		}
	}

	public Ordem findById(Long id) {
		return em.find(Ordem.class, id);
	}

	public Ordem update(Ordem entity) {
		return em.merge(entity);
	}

	public List<Ordem> listAll(Integer startPosition, Integer maxResult) {
		TypedQuery<Ordem> findAllQuery = em.createQuery(
				"SELECT DISTINCT o FROM Ordem o ORDER BY o.id", Ordem.class);
		if (startPosition != null) {
			findAllQuery.setFirstResult(startPosition);
		}
		if (maxResult != null) {
			findAllQuery.setMaxResults(maxResult);
		}
		return findAllQuery.getResultList();
	}
}
