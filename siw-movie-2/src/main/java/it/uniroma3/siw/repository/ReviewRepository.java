package it.uniroma3.siw.repository;

import java.util.List;


import org.springframework.data.repository.CrudRepository;

import it.uniroma3.siw.model.Review;

public interface ReviewRepository extends CrudRepository<Review, Long> {

	public boolean existsByTitle(String title);
	public List<Review> findByTitle(String title);
	
}
