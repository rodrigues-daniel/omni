package org.omni.dao;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import org.omni.model.Tecnico;

/**
 * DAO for Tecnico
 */
@Stateless
public class TecnicoDao {
	@PersistenceContext(unitName = "omni-persistence-unit")
	private EntityManager em;

	public void create(Tecnico entity) {
		em.persist(entity);
	}

	public void deleteById(Long id) {
		Tecnico entity = em.find(Tecnico.class, id);
		if (entity != null) {
			em.remove(entity);
		}
	}

	public Tecnico findById(Long id) {
		return em.find(Tecnico.class, id);
	}

	public Tecnico update(Tecnico entity) {
		return em.merge(entity);
	}

	public List<Tecnico> listAll(Integer startPosition, Integer maxResult) {
		TypedQuery<Tecnico> findAllQuery = em
				.createQuery("SELECT DISTINCT t FROM Tecnico t ORDER BY t.id",
						Tecnico.class);
		if (startPosition != null) {
			findAllQuery.setFirstResult(startPosition);
		}
		if (maxResult != null) {
			findAllQuery.setMaxResults(maxResult);
		}
		return findAllQuery.getResultList();
	}
}
