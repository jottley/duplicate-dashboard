package net.ottleys.duplicate.model;

import java.nio.file.Path;
import java.nio.file.Paths;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
import net.ottleys.duplicate.dao.model.SearchPathEntity;

@Data
public class SearchPath {
    
    private @Setter(AccessLevel.NONE) int id; 
    private String path;
    private Boolean active;
    private Boolean exclude;


    public SearchPath(SearchPathEntity searchPathEntity) {
        this.id = searchPathEntity.getId();
        this.path = searchPathEntity.getPath();
        this.active = searchPathEntity.getActive();
        this.exclude = searchPathEntity.getExclude();
    }

    public Path generatePath() {
        return Paths.get(this.path);
    }
}
