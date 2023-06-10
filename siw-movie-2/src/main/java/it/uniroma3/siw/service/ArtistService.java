package it.uniroma3.siw.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;

import it.uniroma3.siw.model.Artist;
import it.uniroma3.siw.model.Movie;
import it.uniroma3.siw.repository.ArtistRepository;

@Service
public class ArtistService {

	@Autowired
	private ArtistRepository artistRepository;
	
	@Transactional
	public List<Artist> getAllArtists(){
		List<Artist> result = new ArrayList<>();
		Iterable<Artist> iterable = this.artistRepository.findAll();
		for (Artist a : iterable) {
			result.add(a);
		}
		return result;
	}
	
	@Transactional
	public boolean existByNameAndSurname(String name, String surname) {
		return this.artistRepository.existsByNameAndSurname(name, surname);
	}
	
	@Transactional
	public Artist getArtist(Long id) {
		return this.artistRepository.findById(id).get();
	}
	
	@Transactional
	public void saveArtist(Artist artist) {
		this.artistRepository.save(artist);
	}
	
	@Transactional
	public List<Artist> getActedMoviesNotContaining(Movie movie){
		return this.artistRepository.findAllByActedMoviesNotContaining(movie);
	}
	
	@Transactional
	public void manageArtists(Long id, Model model) {
		Artist artist = this.getArtist(id);
		List<Movie> actedMovies = artist.getActedMovies();
		for (Movie movie : actedMovies) {
			movie.getActors().remove(artist);
		}
		List<Movie> directedMovies = artist.getDirectedMovies();
		for (Movie movie : directedMovies) {
			movie.setDirector(null);
		}
		this.artistRepository.deleteById(id);
		model.addAttribute("artists", this.getAllArtists());
	}
}
