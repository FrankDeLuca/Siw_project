package it.uniroma3.siw.model;

import java.util.List;

import java.util.Objects;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
public class Movie {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	@NotBlank
	private String title;
	@NotNull
	@Min(1900)
	@Max(2023)
	private Integer year;
	
	@Column(nullable=true, length=64)
	private String photos;
	
	
	public String getPhotos() {
		return photos;
	}

	public void setPhotos(String photos) {
		this.photos = photos;
	}
	
	@Transient
	public String getPhotosImagePath() {
		if(photos == null || id == null) return null;
		return "/movie-photos/" + id + "/" + photos;
	}

	@ManyToOne
	private Artist director;
	
	
	@ManyToMany
	private List<Artist> actors;
	
	@OneToMany(cascade = CascadeType.REMOVE)
	@JoinColumn(name = "movies_id")
	private List<Review> reviews;
	
	
	public Movie() {
		
	}
	
	public Long getId() {
		return this.id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	
	public String getTitle() {
		return this.title;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	public Integer getYear() {
		return this.year;
	}
	
	public void setYear(Integer year) {
		this.year = year;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(title, year);
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Movie other = (Movie) obj;
		return Objects.equals(this.title, other.title) && Objects.equals(this.year, other.year);
	}

	
	public Artist getDirector() {
		return this.director;
	}

	public void setDirector(Artist director) {
		this.director = director;
	}

	public List<Artist> getActors() {
		return this.actors;
	}

	public void setActors(List<Artist> actors) {
		this.actors = actors;
	}

	public List<Review> getReviews() {
		return this.reviews;
	}

	public void setReviews(List<Review> reviews) {
		this.reviews = reviews;
	}

}
