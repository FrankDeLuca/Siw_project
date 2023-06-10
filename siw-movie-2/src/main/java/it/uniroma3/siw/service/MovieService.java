package it.uniroma3.siw.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;

import it.uniroma3.siw.model.Artist;
import it.uniroma3.siw.model.Movie;
import it.uniroma3.siw.model.Review;
import it.uniroma3.siw.repository.MovieRepository;

@Service
public class MovieService {

	@Autowired
	private MovieRepository movieRepository;
	
	@Autowired 
	private ArtistService artistService;
	
	@Transactional
	public boolean existByTitle(String title) {
		return this.movieRepository.existsByTitle(title);
	}
	
	@Transactional
	public boolean existByTitleAndYear(String title, Integer year) {
		return this.movieRepository.existsByTitleAndYear(title, year);
	}
	
	@Transactional
	public Movie saveMovie(Movie movie) {
		return this.movieRepository.save(movie);
	}
	
	@Transactional
	public Movie getMovieByTitle(String title) {
		return this.movieRepository.findByTitle(title);
	}
	
	@Transactional
	public Movie getMovie(Long id) {
		Movie movie = this.movieRepository.findById(id).get();
		return movie;
	}
	
	@Transactional
	public List<Artist> getMovieActors(Long id){
		List<Artist> result = new ArrayList<>();
		Iterable<Artist> iterable = this.getMovie(id).getActors();
		for(Artist a : iterable) {
			result.add(a);
		}
		return result;
	}
	
	@Transactional
	public List<Movie> getAllMovies(){
		List<Movie> result = new ArrayList<>();
		Iterable<Movie> iterable = this.movieRepository.findAll();
		for (Movie movie : iterable) {
			result.add(movie);
		}
		return result;
	}
	
	@Transactional
	public List<Movie> deleteMovie(Long id){
		Movie movie = this.movieRepository.findById(id).get();
		List<Review> reviews = movie.getReviews();
		for(Review review : reviews) {
			review.getAuthor().setRecensione(null);
		}
		this.movieRepository.deleteById(id);
		List<Movie> res = this.getAllMovies();
		return res;
	}
	
	@Transactional
	public void deleteDirector(Long id, Model model) {
		Movie m1 = this.getMovie(id);
		model.addAttribute("movie", m1);
		m1.setDirector(null);
		this.movieRepository.save(m1);
	}
	
	@Transactional(propagation = Propagation.REQUIRED)
	public void addActor(Long id, Model model) {
		List<Artist> actorsOut = (List<Artist>) this.artistService.getAllArtists();
		List<Artist> actorsIn = (List<Artist>) this.getMovieActors(id);
		actorsOut.removeAll(actorsIn);
		model.addAttribute("artistsIn", actorsIn);
		model.addAttribute("artistsOut", actorsOut);
		model.addAttribute("movie", this.getMovie(id));
	}
	
	@Transactional(propagation = Propagation.REQUIRED)
	public void setActors(Long movieId, Long artId, Model model) {
		Movie m = this.getMovie(movieId);
		Artist actor = this.artistService.getArtist(artId);
		m.getActors().add(actor);
		actor.getActedMovies().add(m);
		this.saveMovie(m);
		this.artistService.saveArtist(actor);
		List<Artist> actorsOut = this.artistService.getActedMoviesNotContaining(m);
		List<Artist> actorsIn = m.getActors();
		model.addAttribute("artistsIn", actorsIn);
		model.addAttribute("artistsOut", actorsOut);
		model.addAttribute("movie", m);
	}
	
	@Transactional(propagation = Propagation.REQUIRED)
	public void deleteActors(Long movieId, Long artId, Model model) {
		Movie m = this.getMovie(movieId);
		Artist actor = this.artistService.getArtist(artId);
		m.getActors().remove(actor);
		actor.getActedMovies().remove(m);
		this.saveMovie(m);
		List<Artist> actorsOut = this.artistService.getActedMoviesNotContaining(m);
		List<Artist> actorsIn = m.getActors();
		model.addAttribute("artistsIn", actorsIn);
		model.addAttribute("artistsOut", actorsOut);
		model.addAttribute("movie", m);
	}
	
	@Transactional
	public List<Movie> getMoviesByYear(Integer year) {
		return this.movieRepository.findByYear(year);
	}
	
	
}
