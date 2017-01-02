package org.omni.view;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.ejb.SessionContext;
import javax.ejb.Stateful;
import javax.enterprise.context.Conversation;
import javax.enterprise.context.ConversationScoped;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.omni.model.Ordem;

/**
 * Backing bean for Ordem entities.
 * <p/>
 * This class provides CRUD functionality for all Ordem entities. It focuses
 * purely on Java EE 6 standards (e.g. <tt>&#64;ConversationScoped</tt> for
 * state management, <tt>PersistenceContext</tt> for persistence,
 * <tt>CriteriaBuilder</tt> for searches) rather than introducing a CRUD
 * framework or custom base class.
 */

@Named
@Stateful
@ConversationScoped
public class OrdemBean implements Serializable {

	private static final long serialVersionUID = 1L;

	/*
	 * Support creating and retrieving Ordem entities
	 */

	private Long id;

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	private Ordem ordem;

	public Ordem getOrdem() {
		return this.ordem;
	}

	public void setOrdem(Ordem ordem) {
		this.ordem = ordem;
	}

	@Inject
	private Conversation conversation;

	@PersistenceContext(unitName = "omni-persistence-unit", type = PersistenceContextType.EXTENDED)
	private EntityManager entityManager;

	public String create() {

		this.conversation.begin();
		this.conversation.setTimeout(1800000L);
		return "create?faces-redirect=true";
	}

	public void retrieve() {

		if (FacesContext.getCurrentInstance().isPostback()) {
			return;
		}

		if (this.conversation.isTransient()) {
			this.conversation.begin();
			this.conversation.setTimeout(1800000L);
		}

		if (this.id == null) {
			this.ordem = this.example;
		} else {
			this.ordem = findById(getId());
		}
	}

	public Ordem findById(Long id) {

		return this.entityManager.find(Ordem.class, id);
	}

	/*
	 * Support updating and deleting Ordem entities
	 */

	public String update() {
		this.conversation.end();

		try {
			if (this.id == null) {
				this.entityManager.persist(this.ordem);
				return "search?faces-redirect=true";
			} else {
				this.entityManager.merge(this.ordem);
				return "view?faces-redirect=true&id=" + this.ordem.getId();
			}
		} catch (Exception e) {
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(e.getMessage()));
			return null;
		}
	}

	public String delete() {
		this.conversation.end();

		try {
			Ordem deletableEntity = findById(getId());

			this.entityManager.remove(deletableEntity);
			this.entityManager.flush();
			return "search?faces-redirect=true";
		} catch (Exception e) {
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(e.getMessage()));
			return null;
		}
	}

	/*
	 * Support searching Ordem entities with pagination
	 */

	private int page;
	private long count;
	private List<Ordem> pageItems;

	private Ordem example = new Ordem();

	public int getPage() {
		return this.page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public int getPageSize() {
		return 10;
	}

	public Ordem getExample() {
		return this.example;
	}

	public void setExample(Ordem example) {
		this.example = example;
	}

	public String search() {
		this.page = 0;
		return null;
	}

	public void paginate() {

		CriteriaBuilder builder = this.entityManager.getCriteriaBuilder();

		// Populate this.count

		CriteriaQuery<Long> countCriteria = builder.createQuery(Long.class);
		Root<Ordem> root = countCriteria.from(Ordem.class);
		countCriteria = countCriteria.select(builder.count(root)).where(
				getSearchPredicates(root));
		this.count = this.entityManager.createQuery(countCriteria)
				.getSingleResult();

		// Populate this.pageItems

		CriteriaQuery<Ordem> criteria = builder.createQuery(Ordem.class);
		root = criteria.from(Ordem.class);
		TypedQuery<Ordem> query = this.entityManager.createQuery(criteria
				.select(root).where(getSearchPredicates(root)));
		query.setFirstResult(this.page * getPageSize()).setMaxResults(
				getPageSize());
		this.pageItems = query.getResultList();
	}

	private Predicate[] getSearchPredicates(Root<Ordem> root) {

		CriteriaBuilder builder = this.entityManager.getCriteriaBuilder();
		List<Predicate> predicatesList = new ArrayList<Predicate>();

		String ordem = this.example.getOrdem();
		if (ordem != null && !"".equals(ordem)) {
			predicatesList.add(builder.like(
					builder.lower(root.<String> get("ordem")),
					'%' + ordem.toLowerCase() + '%'));
		}
		String nodo = this.example.getNodo();
		if (nodo != null && !"".equals(nodo)) {
			predicatesList.add(builder.like(
					builder.lower(root.<String> get("nodo")),
					'%' + nodo.toLowerCase() + '%'));
		}

		return predicatesList.toArray(new Predicate[predicatesList.size()]);
	}

	public List<Ordem> getPageItems() {
		return this.pageItems;
	}

	public long getCount() {
		return this.count;
	}

	/*
	 * Support listing and POSTing back Ordem entities (e.g. from inside an
	 * HtmlSelectOneMenu)
	 */

	public List<Ordem> getAll() {

		CriteriaQuery<Ordem> criteria = this.entityManager.getCriteriaBuilder()
				.createQuery(Ordem.class);
		return this.entityManager.createQuery(
				criteria.select(criteria.from(Ordem.class))).getResultList();
	}

	@Resource
	private SessionContext sessionContext;

	public Converter getConverter() {

		final OrdemBean ejbProxy = this.sessionContext
				.getBusinessObject(OrdemBean.class);

		return new Converter() {

			@Override
			public Object getAsObject(FacesContext context,
					UIComponent component, String value) {

				return ejbProxy.findById(Long.valueOf(value));
			}

			@Override
			public String getAsString(FacesContext context,
					UIComponent component, Object value) {

				if (value == null) {
					return "";
				}

				return String.valueOf(((Ordem) value).getId());
			}
		};
	}

	/*
	 * Support adding children to bidirectional, one-to-many tables
	 */

	private Ordem add = new Ordem();

	public Ordem getAdd() {
		return this.add;
	}

	public Ordem getAdded() {
		Ordem added = this.add;
		this.add = new Ordem();
		return added;
	}
}
