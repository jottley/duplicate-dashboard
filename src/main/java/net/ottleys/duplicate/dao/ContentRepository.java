package net.ottleys.duplicate.dao;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import net.ottleys.duplicate.dao.model.ContentEntity;

public interface ContentRepository extends CrudRepository<ContentEntity, Integer> {

    @Query(value = "SELECT c.* FROM content c JOIN (SELECT name, checksum, COUNT(*) FROM content GROUP BY name, checksum HAVING count(*) > 1 ) d ON c.name = d.name AND c.checksum = d.checksum ORDER BY c.name", nativeQuery = true)
    List<ContentEntity> findAllDuplicates();
    
}
