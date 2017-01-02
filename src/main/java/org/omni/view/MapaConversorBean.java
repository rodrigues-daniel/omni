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

import org.omni.model.MapaConversor;

/**
 * Backing bean for MapaConversor entities.
 * <p/>
 * This class provides CRUD functionality for all MapaConversor entities. It
 * focuses purely on Java EE 6 standards (e.g. <tt>&#64;ConversationScoped</tt>
 * for state management, <tt>PersistenceContext</tt> for persistence,
 * <tt>CriteriaBuilder</tt> for searches) rather than introducing a CRUD
 * framework or custom base class.
 */

@Named
@Stateful
@ConversationScoped
public class MapaConversorBean implements Serializable {

	private static final long serialVersionUID = 1L;

	/*
	 * Support creating and retrieving MapaConversor entities
	 */

	private Long id;

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	private MapaConversor mapaConversor;

	public MapaConversor getMapaConversor() {
		return this.mapaConversor;
	}

	public void setMapaConversor(MapaConversor mapaConversor) {
		this.mapaConversor = mapaConversor;
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
			this.mapaConversor = this.example;
		} else {
			this.mapaConversor = findById(getId());
		}
	}

	public MapaConversor findById(Long id) {

		return this.entityManager.find(MapaConversor.class, id);
	}

	/*
	 * Support updating and deleting MapaConversor entities
	 */

	public String update() {
		this.conversation.end();

		try {
			if (this.id == null) {
				this.entityManager.persist(this.mapaConversor);
				return "search?faces-redirect=true";
			} else {
				this.entityManager.merge(this.mapaConversor);
				return "view?faces-redirect=true&id="
						+ this.mapaConversor.getId();
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
			MapaConversor deletableEntity = findById(getId());

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
	 * Support searching MapaConversor entities with pagination
	 */

	private int page;
	private long count;
	private List<MapaConversor> pageItems;

	private MapaConversor example = new MapaConversor();

	public int getPage() {
		return this.page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public int getPageSize() {
		return 10;
	}

	public MapaConversor getExample() {
		return this.example;
	}

	public void setExample(MapaConversor example) {
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
		Root<MapaConversor> root = countCriteria.from(MapaConversor.class);
		countCriteria = countCriteria.select(builder.count(root)).where(
				getSearchPredicates(root));
		this.count = this.entityManager.createQuery(countCriteria)
				.getSingleResult();

		// Populate this.pageItems

		CriteriaQuery<MapaConversor> criteria = builder
				.createQuery(MapaConversor.class);
		root = criteria.from(MapaConversor.class);
		TypedQuery<MapaConversor> query = this.entityManager
				.createQuery(criteria.select(root).where(
						getSearchPredicates(root)));
		query.setFirstResult(this.page * getPageSize()).setMaxResults(
				getPageSize());
		this.pageItems = query.getResultList();
	}

	private Predicate[] getSearchPredicates(Root<MapaConversor> root) {

		CriteriaBuilder builder = this.entityManager.getCriteriaBuilder();
		List<Predicate> predicatesList = new ArrayList<Predicate>();

		String logradouro = this.example.getLogradouro();
		if (logradouro != null && !"".equals(logradouro)) {
			predicatesList.add(builder.like(
					builder.lower(root.<String> get("logradouro")),
					'%' + logradouro.toLowerCase() + '%'));
		}

		return predicatesList.toArray(new Predicate[predicatesList.size()]);
	}

	public List<MapaConversor> getPageItems() {
		return this.pageItems;
	}

	public long getCount() {
		return this.count;
	}

	/*
	 * Support listing and POSTing back MapaConversor entities (e.g. from inside
	 * an HtmlSelectOneMenu)
	 */

	public List<MapaConversor> getAll() {

		CriteriaQuery<MapaConversor> criteria = this.entityManager
				.getCriteriaBuilder().createQuery(MapaConversor.class);
		return this.entityManager.createQuery(
				criteria.select(criteria.from(MapaConversor.class)))
				.getResultList();
	}

	@Resource
	private SessionContext sessionContext;

	public Converter getConverter() {

		final MapaConversorBean ejbProxy = this.sessionContext
				.getBusinessObject(MapaConversorBean.class);

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

				return String.valueOf(((MapaConversor) value).getId());
			}
		};
	}

	/*
	 * Support adding children to bidirectional, one-to-many tables
	 */

	private MapaConversor add = new MapaConversor();

	public MapaConversor getAdd() {
		return this.add;
	}

	public MapaConversor getAdded() {
		MapaConversor added = this.add;
		this.add = new MapaConversor();
		return added;
	}
}
