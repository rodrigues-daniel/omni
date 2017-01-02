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

import org.omni.model.Endereco;

/**
 * Backing bean for Endereco entities.
 * <p/>
 * This class provides CRUD functionality for all Endereco entities. It focuses
 * purely on Java EE 6 standards (e.g. <tt>&#64;ConversationScoped</tt> for
 * state management, <tt>PersistenceContext</tt> for persistence,
 * <tt>CriteriaBuilder</tt> for searches) rather than introducing a CRUD
 * framework or custom base class.
 */

@Named
@Stateful
@ConversationScoped
public class EnderecoBean implements Serializable {

	private static final long serialVersionUID = 1L;

	/*
	 * Support creating and retrieving Endereco entities
	 */

	private Long id;

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	private Endereco endereco;

	public Endereco getEndereco() {
		return this.endereco;
	}

	public void setEndereco(Endereco endereco) {
		this.endereco = endereco;
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
			this.endereco = this.example;
		} else {
			this.endereco = findById(getId());
		}
	}

	public Endereco findById(Long id) {

		return this.entityManager.find(Endereco.class, id);
	}

	/*
	 * Support updating and deleting Endereco entities
	 */

	public String update() {
		this.conversation.end();

		try {
			if (this.id == null) {
				this.entityManager.persist(this.endereco);
				return "search?faces-redirect=true";
			} else {
				this.entityManager.merge(this.endereco);
				return "view?faces-redirect=true&id=" + this.endereco.getId();
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
			Endereco deletableEntity = findById(getId());

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
	 * Support searching Endereco entities with pagination
	 */

	private int page;
	private long count;
	private List<Endereco> pageItems;

	private Endereco example = new Endereco();

	public int getPage() {
		return this.page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public int getPageSize() {
		return 10;
	}

	public Endereco getExample() {
		return this.example;
	}

	public void setExample(Endereco example) {
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
		Root<Endereco> root = countCriteria.from(Endereco.class);
		countCriteria = countCriteria.select(builder.count(root)).where(
				getSearchPredicates(root));
		this.count = this.entityManager.createQuery(countCriteria)
				.getSingleResult();

		// Populate this.pageItems

		CriteriaQuery<Endereco> criteria = builder.createQuery(Endereco.class);
		root = criteria.from(Endereco.class);
		TypedQuery<Endereco> query = this.entityManager.createQuery(criteria
				.select(root).where(getSearchPredicates(root)));
		query.setFirstResult(this.page * getPageSize()).setMaxResults(
				getPageSize());
		this.pageItems = query.getResultList();
	}

	private Predicate[] getSearchPredicates(Root<Endereco> root) {

		CriteriaBuilder builder = this.entityManager.getCriteriaBuilder();
		List<Predicate> predicatesList = new ArrayList<Predicate>();

		String endereco = this.example.getEndereco();
		if (endereco != null && !"".equals(endereco)) {
			predicatesList.add(builder.like(
					builder.lower(root.<String> get("endereco")),
					'%' + endereco.toLowerCase() + '%'));
		}

		return predicatesList.toArray(new Predicate[predicatesList.size()]);
	}

	public List<Endereco> getPageItems() {
		return this.pageItems;
	}

	public long getCount() {
		return this.count;
	}

	/*
	 * Support listing and POSTing back Endereco entities (e.g. from inside an
	 * HtmlSelectOneMenu)
	 */

	public List<Endereco> getAll() {

		CriteriaQuery<Endereco> criteria = this.entityManager
				.getCriteriaBuilder().createQuery(Endereco.class);
		return this.entityManager.createQuery(
				criteria.select(criteria.from(Endereco.class))).getResultList();
	}

	@Resource
	private SessionContext sessionContext;

	public Converter getConverter() {

		final EnderecoBean ejbProxy = this.sessionContext
				.getBusinessObject(EnderecoBean.class);

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

				return String.valueOf(((Endereco) value).getId());
			}
		};
	}

	/*
	 * Support adding children to bidirectional, one-to-many tables
	 */

	private Endereco add = new Endereco();

	public Endereco getAdd() {
		return this.add;
	}

	public Endereco getAdded() {
		Endereco added = this.add;
		this.add = new Endereco();
		return added;
	}
}
