package org.omni.dao;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import org.omni.model.Endereco;

/**
 * DAO for Endereco
 */
@Stateless
public class EnderecoDao {
	@PersistenceContext(unitName = "omni-persistence-unit")
	private EntityManager em;

	public void create(Endereco entity) {
		em.persist(entity);
	}

	public void deleteById(Long id) {
		Endereco entity = em.find(Endereco.class, id);
		if (entity != null) {
			em.remove(entity);
		}
	}

	public Endereco findById(Long id) {
		return em.find(Endereco.class, id);
	}

	public Endereco update(Endereco entity) {
		return em.merge(entity);
	}

	public List<Endereco> listAll(Integer startPosition, Integer maxResult) {
		TypedQuery<Endereco> findAllQuery = em.createQuery(
				"SELECT DISTINCT e FROM Endereco e ORDER BY e.id",
				Endereco.class);
		if (startPosition != null) {
			findAllQuery.setFirstResult(startPosition);
		}
		if (maxResult != null) {
			findAllQuery.setMaxResults(maxResult);
		}
		return findAllQuery.getResultList();
	}
}
