package hr.fer.oprpp2.jmbag0036534519.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.*;

@NamedQueries({
	@NamedQuery(
			name = "BlogEntry.upit1",
			query = "select b from BlogComment as b where b.blogEntry=:be and b.postedOn>:when")
})
@Entity
@Table(name="blog_entries")
@Cacheable(true)
public class BlogEntry {

	@Id @GeneratedValue
	private Long id;
	@OneToMany(mappedBy="blogEntry",fetch=FetchType.LAZY, cascade=CascadeType.PERSIST, orphanRemoval=true)
	@OrderBy("postedOn")
	private List<BlogComment> comments = new ArrayList<>();
	@Temporal(TemporalType.TIMESTAMP)
	@Column(nullable=false)
	private Date createdAt;
	@Temporal(TemporalType.TIMESTAMP)
	@Column(nullable=true)
	private Date lastModifiedAt;
	@Column(length=200,nullable=false)
	private String title;
	@Column(length=4096,nullable=false)
	private String text;
	@ManyToOne
	@JoinColumn(nullable = false)
	private BlogUser creator;

	public Long getId() {
		return id;
	}

	public List<BlogComment> getComments() {
		return comments;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public Date getLastModifiedAt() {
		return lastModifiedAt;
	}

	public String getTitle() {
		return title;
	}

	public String getText() {
		return text;
	}

	public BlogUser getCreator() {
		return creator;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setComments(List<BlogComment> comments) {
		this.comments = comments;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

	public void setLastModifiedAt(Date lastModifiedAt) {
		this.lastModifiedAt = lastModifiedAt;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setText(String text) {
		this.text = text;
	}

	public void setCreator(BlogUser creator) {
		this.creator = creator;
	}
}