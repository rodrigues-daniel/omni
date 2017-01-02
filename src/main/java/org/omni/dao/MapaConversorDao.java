package org.omni.dao;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import org.omni.model.MapaConversor;

/**
 * DAO for MapaConversor
 */
@Stateless
public class MapaConversorDao {
	@PersistenceContext(unitName = "omni-persistence-unit")
	private EntityManager em;

	public void create(MapaConversor entity) {
		em.persist(entity);
	}

	public void deleteById(Long id) {
		MapaConversor entity = em.find(MapaConversor.class, id);
		if (entity != null) {
			em.remove(entity);
		}
	}

	public MapaConversor findById(Long id) {
		return em.find(MapaConversor.class, id);
	}

	public MapaConversor update(MapaConversor entity) {
		return em.merge(entity);
	}

	public List<MapaConversor> listAll(Integer startPosition, Integer maxResult) {
		TypedQuery<MapaConversor> findAllQuery = em.createQuery(
				"SELECT DISTINCT m FROM MapaConversor m ORDER BY m.id",
				MapaConversor.class);
		if (startPosition != null) {
			findAllQuery.setFirstResult(startPosition);
		}
		if (maxResult != null) {
			findAllQuery.setMaxResults(maxResult);
		}
		return findAllQuery.getResultList();
	}
}
