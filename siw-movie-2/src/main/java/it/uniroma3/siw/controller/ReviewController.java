package it.uniroma3.siw.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import it.uniroma3.siw.controller.validation.ReviewValidator;
import it.uniroma3.siw.model.Credentials;
import it.uniroma3.siw.model.Movie;
import it.uniroma3.siw.model.Review;
import it.uniroma3.siw.model.User;
import it.uniroma3.siw.service.CredentialsService;
import it.uniroma3.siw.service.MovieService;
import it.uniroma3.siw.service.ReviewService;
import it.uniroma3.siw.service.UserService;
import jakarta.validation.Valid;

@Controller
public class ReviewController {
	
@Autowired ReviewService reviewService;
@Autowired ReviewValidator reviewValidator;
@Autowired MovieService movieService;
@Autowired CredentialsService credentialsService;
@Autowired UserService userService;
	
	
	@GetMapping("/formNewReview")
	public String formNewReview(Model model) {
		model.addAttribute("review", new Review());
		return "formNewReview.html";
	}
	
	@PostMapping("/reviews")
	public String newReview(@Valid @ModelAttribute("review")Review review, BindingResult bindingResult, Model model, @RequestParam String film) {
			if(!movieService.existByTitle(film)) {
				model.addAttribute("messaggioErrore", "Questo film non esiste!");
				return "formNewReview.html";
			}
			UserDetails userDetails = (UserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
	    	Credentials credentials = credentialsService.getCredentials(userDetails.getUsername());
	    	User user = credentials.getUser();
	    	if(!(user.getRecensione()==null)) {
	    		model.addAttribute("messaggioErrore", "Hai già inserito una recensione!");
	    		return "formNewReview.html";
	    	}
	    	this.reviewValidator.validate(review, bindingResult);
			if(!bindingResult.hasErrors()) {
				review.setAuthor(user);
				this.reviewService.saveReview(review);
				model.addAttribute("review", review);
				Movie movie = this.movieService.getMovieByTitle(film);
				movie.getReviews().add(review);
				user.setRecensione(review);
				this.userService.saveUser(user);
				this.movieService.saveMovie(movie);
				return "review.html";
			} else {
				return "formNewReview.html";
			}
	}
	
	@GetMapping("/reviews/{id}")
	public String getReview(@PathVariable("id") Long id, Model model) {
		model.addAttribute("review", this.reviewService.getReview(id));
		return "review.html";
	}
	
	@GetMapping("/reviews")
	public String showReviews(Model model) {
		model.addAttribute("reviews", this.reviewService.getAllReviews());
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication instanceof AnonymousAuthenticationToken) {
	        return "reviews.html";
		}
		UserDetails userDetails = (UserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		Credentials credentials = credentialsService.getCredentials(userDetails.getUsername());
		if (credentials.getRole().equals(Credentials.ADMIN_ROLE)) {
			model.addAttribute("user", (UserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal());
            return "admin/manageReviews.html";
		} 
		return "reviews.html";
	}
	
	@GetMapping("/admin/manageReviews/{id}")
	public String manageReviews(@PathVariable("id") Long id, Model model) {
		this.reviewService.manageReviews(id, model);
		return "admin/manageReviews.html";
	}
	
	@GetMapping("/formSearchReview")
	public String formSearchReview() {
		return "formSearchReview.html";
	}
	
	@PostMapping("/searchReview")
	public String searchReview(Model model, @RequestParam String title) {
		model.addAttribute("reviews", this.reviewService.getByTitle(title));
		return "foundReviews.html";
	}
}
