package it.uniroma3.siw.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;

import it.uniroma3.siw.model.Review;
import it.uniroma3.siw.repository.ReviewRepository;

@Service
public class ReviewService {

	@Autowired
	private ReviewRepository reviewRepository;
	
	@Transactional
	public boolean existByTitle(String title) {
		return this.reviewRepository.existsByTitle(title);
	}
	
	@Transactional
	public void saveReview(Review review) {
		this.reviewRepository.save(review);
	}
	
	@Transactional
	public Review getReview(Long id) {
		return this.reviewRepository.findById(id).get();
	}
	
	@Transactional
	public List<Review> getAllReviews(){
		List<Review> result = new ArrayList<>();
		Iterable<Review> iterable = this.reviewRepository.findAll();
		for (Review r : iterable) {
			result.add(r);
		}
		return result;
	}
	
	@Transactional
	public void manageReviews(Long id, Model model) {
		Review review = this.getReview(id);
		review.getAuthor().setRecensione(null);
		this.reviewRepository.deleteById(id);
		model.addAttribute("reviews", this.getAllReviews());
	}
	
	@Transactional
	public List<Review> getByTitle(String title) {
		return this.reviewRepository.findByTitle(title);
	}
}
