package it.uniroma3.siw.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import it.uniroma3.siw.controller.validation.MovieValidator;
import it.uniroma3.siw.model.Movie;
import it.uniroma3.siw.service.ArtistService;
import it.uniroma3.siw.service.FileUploadUtil;
import it.uniroma3.siw.service.MovieService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;

@Controller
public class MovieController {
	@Autowired MovieService movieService;
	@Autowired ArtistService artistService;
	@Autowired MovieValidator movieValidator;
	
	@GetMapping("/admin/formNewMovie")
	public String formNewMovie(Model model) {
		model.addAttribute("movie", new Movie());
		return "/admin/formNewMovie.html";
	}
	
	@PostMapping("/admin/manageMovies")
	public String newMovie(@Valid @ModelAttribute("movie")Movie movie, BindingResult bindingResult, @RequestParam("image") MultipartFile multipartFile, Model model) throws IOException {
		this.movieValidator.validate(movie, bindingResult);
		if(!bindingResult.hasErrors()) {
			String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
			movie.setPhotos(fileName);
			model.addAttribute("movie", this.movieService.saveMovie(movie));
			String uploadDir = "movie-photos/" + movie.getId();
			FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
			return "movie.html";
		} else {
			return "/admin/formNewMovie.html";
		}
	}
	
	@GetMapping("/movies/{id}")
	public String getMovie(@PathVariable("id") Long id, Model model) {
		model.addAttribute("movie", this.movieService.getMovie(id));
		return "movie.html";
	}
	
	@GetMapping("/admin/manageMovies")
	public String showMovies(Model model) {
		model.addAttribute("movies", this.movieService.getAllMovies());
		return "/admin/manageMovies.html";
	}
	
	@GetMapping("/admin/deleteMovie/{id}")
	public String deleteMovie(@PathVariable("id") Long id, Model model) {
		model.addAttribute("movies", this.movieService.deleteMovie(id));
		return "/admin/manageMovies.html";
	}
	
	@GetMapping("/moviesList")
	public String listMovies(Model model) {
		model.addAttribute("movies", this.movieService.getAllMovies());
		return "movies.html";
	}
	
	@GetMapping("/moviesView")
	public String viewMovies(Model model) {
		model.addAttribute("movies", this.movieService.getAllMovies());
		return "viewMovies.html";
	}
	
	@GetMapping("/admin/formUpdateMovie/{id}")
	public String updateMovie(@PathVariable("id") Long id, Model model) {
		model.addAttribute("movie", this.movieService.getMovie(id));
		model.addAttribute("actors", this.movieService.getMovieActors(id));
		return "/admin/formUpdateMovie.html";
	}
	
	@GetMapping("/admin/modifyMovie/{id}")
		public String modifyMovie(@PathVariable("id") Long id, Model model) {
			model.addAttribute("movie", this.movieService.getMovie(id));
			return "/admin/formModifyMovie.html";
		}
	
	@PostMapping("/admin/formModifiedMovie/{id}")
	public String modify(@PathVariable("id") Long id, Model model, @RequestParam("newYear") String newYear, @RequestParam("newTitle") String newTitle) {
		Movie m = this.movieService.modifyTitleAndYear(id, newTitle, newYear);
		model.addAttribute("movie", m);
		model.addAttribute("actors", this.movieService.getMovieActors(id));
		return "/admin/formUpdateMovie.html";
	}
	
	@GetMapping("/admin/addDirector/{id}")
	public String addDirector(@PathVariable("id") Long id, Model model) {
		model.addAttribute("movie", this.movieService.getMovie(id));
		model.addAttribute("artists", this.artistService.getAllArtists());
		return "/admin/addDirector.html";
	}
	
	@GetMapping("/admin/deleteDirector/{id}")
	public String deleteDirector(@PathVariable("id") Long id, Model model) {
		this.movieService.deleteDirector(id, model);
		return "/admin/formUpdateMovie.html";
	}
	
	@GetMapping("/admin/addActor/{id}")
	public String addActor(@PathVariable("id") Long id, Model model) {
		this.movieService.addActor(id, model);
		return "/admin/addActors.html";
	}
	
	@Transactional
	@GetMapping("/admin/setActorsToMovie/{movieId}/{artId}")
	public String setActors(@PathVariable("movieId") Long movieId, @PathVariable("artId") Long artId, Model model) {
		this.movieService.setActors(movieId, artId, model);
		return "/admin/addActors.html";
	}
	
	@GetMapping("/admin/deleteActorsFromMovie/{movieId}/{artId}")
	public String deleteActors(@PathVariable("movieId") Long movieId, @PathVariable("artId") Long artId, Model model) {
		this.movieService.deleteActors(movieId, artId, model);
		return "/admin/addActors.html";
	}
	
	@GetMapping("/admin/setDirectorToMovie/{movieId}/{artId}")
	public String setDirector(@PathVariable("movieId") Long movieId, @PathVariable("artId") Long artId, Model model) {
		model.addAttribute("movie", this.movieService.getMovie(movieId));
		model.addAttribute("artist", this.artistService.getArtist(artId));
		this.movieService.getMovie(movieId).setDirector(this.artistService.getArtist(artId));
		this.movieService.saveMovie(this.movieService.getMovie(movieId));
		return "/admin/formUpdateMovie";
	}
	
	@GetMapping("/formSearchMovies")
	public String formSearchMovies() {
		return "formSearchMovies.html";
	}
	
	@PostMapping("/searchMovies")
	public String searchMovies(Model model, @RequestParam Integer year) {
		model.addAttribute("movies", this.movieService.getMoviesByYear(year));
		return "foundMovies.html";
	}
}
