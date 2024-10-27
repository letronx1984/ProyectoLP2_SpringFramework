package com.proyecto.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.proyecto.model.LibroEntity;

@Repository
public interface LibroRepository extends JpaRepository<LibroEntity, String> {
	
	Optional<LibroEntity> findByISBN(String isbn);
	
}
