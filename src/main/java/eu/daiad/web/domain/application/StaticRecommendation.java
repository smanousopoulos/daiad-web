package eu.daiad.web.domain.application;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.annotations.Type;

@Entity(name = "static_recommendation")
@Table(schema = "public", name = "static_recommendation")
public class StaticRecommendation {

	@Id()
	@Column(name = "id")
	@SequenceGenerator(sequenceName = "static_recommendation_id_seq", name = "static_recommendation_id_seq", allocationSize = 1, initialValue = 1)
	@GeneratedValue(generator = "static_recommendation_id_seq", strategy = GenerationType.SEQUENCE)
	private int id;

	@Basic()
	private int index;

	@ManyToOne(cascade = { CascadeType.ALL })
	@JoinColumn(name = "category_id", nullable = false)
	private StaticRecommendationCategory category;

	@Column(name = "locale", columnDefinition = "bpchar", length = 2)
	private String locale;

	@Basic()
	private String title;

	@Basic()
	private String description;

	@Basic(fetch = FetchType.LAZY)
	@Column(name = "image_binary")
	@Type(type = "org.hibernate.type.BinaryType")
	private byte image[];

	@Column(name = "image_link")
	private String imageLink;

	@Basic()
	private String prompt;

	@Column(name = "externa_link")
	private String externaLink;

	@Basic()
	private String source;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public StaticRecommendationCategory getCategory() {
		return category;
	}

	public void setCategory(StaticRecommendationCategory category) {
		this.category = category;
	}

	public String getLocale() {
		return locale;
	}

	public void setLocale(String locale) {
		this.locale = locale;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public byte[] getImage() {
		return image;
	}

	public void setImage(byte[] image) {
		this.image = image;
	}

	public String getImageLink() {
		return imageLink;
	}

	public void setImageLink(String imageLink) {
		this.imageLink = imageLink;
	}

	public String getPrompt() {
		return prompt;
	}

	public void setPrompt(String prompt) {
		this.prompt = prompt;
	}

	public String getExternaLink() {
		return externaLink;
	}

	public void setExternaLink(String externaLink) {
		this.externaLink = externaLink;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

}