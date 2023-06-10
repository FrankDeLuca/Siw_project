package it.uniroma3.siw.model;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
public class Artist {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	@NotBlank
	private String name;
	@NotBlank
	private String surname;
	@NotNull
	private LocalDate birth;
	private LocalDate death;
	
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
		return "/artist-photos/" + id + "/" + photos;
	}
	
	@OneToMany(mappedBy = "director")
	private List<Movie> directedMovies;
	
	@ManyToMany(mappedBy = "actors")
	private List<Movie> actedMovies;
	
	public Long getId() {
		return this.id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getSurname() {
		return surname;
	}
	public void setSurname(String surname) {
		this.surname = surname;
	}
	public LocalDate getBirth() {
		return this.birth;
	}
	public void setBirth(LocalDate birth) {
		this.birth = birth;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(birth, name, surname);
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Artist other = (Artist) obj;
		return Objects.equals(birth, other.birth) && Objects.equals(name, other.name)
				&& Objects.equals(surname, other.surname);
	}
	
	public LocalDate getDeath() {
		return death;
	}
	public void setDeath(LocalDate death) {
		this.death = death;
	}
	
	public List<Movie> getDirectedMovies() {
		return directedMovies;
	}
	public void setDirectedMovies(List<Movie> directedMovies) {
		this.directedMovies = directedMovies;
	}
	public List<Movie> getActedMovies() {
		return actedMovies;
	}
	public void setActedMovies(List<Movie> actedMovies) {
		this.actedMovies = actedMovies;
	}
	
}
