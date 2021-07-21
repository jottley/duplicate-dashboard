package net.ottleys.duplicate.dao;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import net.ottleys.duplicate.dao.model.SearchPathEntity;

public interface SearchPathRepository extends CrudRepository<SearchPathEntity, Integer> {

    @Query(value = "SELECT * FROM searchpath WHERE active = 'true' AND exclude = 'true'", nativeQuery = true)
    List<SearchPathEntity> findAllExcluded();

    @Query(value = "SELECT * FROM searchpath WHERE active = 'true' AND exclude = 'false'", nativeQuery = true)
    List<SearchPathEntity> findAllActive();
    
}