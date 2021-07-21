package net.ottleys.duplicate.services;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.Getter;
import lombok.Setter;
import net.ottleys.duplicate.dao.SearchPathRepository;
import net.ottleys.duplicate.dao.model.ContentEntity;
import net.ottleys.duplicate.dao.model.SearchPathEntity;
import net.ottleys.duplicate.model.SearchPath;

@Service
public class SearchPathServices {

    Log log = LogFactory.getLog(SearchPathServices.class);
    
    @Autowired
    private @Getter @Setter List<Path> searchPaths;

    @Autowired
    private @Getter @Setter List<Path> excludedPaths;

    @Autowired
    private @Getter @Setter List<Path> indexedPaths;

    @Autowired
    ContentServices contentServices;

    @Autowired
    SearchPathRepository searchPathRepository;

    @PostConstruct
    public void init() {
        searchPaths = retrieveSearchPaths();
        excludedPaths = retrieveExcludedPaths();
    }

    /**
     * Add a search path to the collection of search paths. The search path is added if:
     * <ul> The path is valid ie it exists
     * <ul> The path is not a symlink
     * <ul> The path is a directory
     * <ul> The path is not already in the collection
     * @param searchPath
     * @return 
     */
    public void addSearchPath(String searchPath) {
        if (StringUtils.isNotEmpty(searchPath)) {
            Path path = Paths.get(searchPath);
            if (Files.exists(path, LinkOption.NOFOLLOW_LINKS) && Files.isDirectory(path, LinkOption.NOFOLLOW_LINKS)) {
                if (!searchPaths.contains(path)) {
                    searchPaths.add(path);
                } else {
                    log.debug("The provieed search path is already in the collection: " + searchPath);
                }
            } else {
                log.debug("The provided search path does not exist: " + searchPath);
            }
        } else {
            log.debug("The provided search path is invalid: " + searchPath);
        }
    }


    public List<Path> indexSearchPaths() {
        List<Path> pathIndex = Collections.synchronizedList(new LinkedList<>()); 

        if (!searchPaths.isEmpty()) {
            Iterator<Path> iterator = searchPaths.iterator();
            while (iterator.hasNext()) {
                try {
                    pathIndex.addAll(indexPath(iterator.next()));
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        } else {
            log.debug("There are no search paths to index");
        }

        return pathIndex;
    }

    public List<Path> indexPath(Path path) throws IOException {
        if (isPathAccessible(path) && !excludedPaths.contains(path)) {
            try(DirectoryStream<Path> directoryStream = Files.newDirectoryStream(path))
            {
                for (Path child : directoryStream) {
                    if (Files.isRegularFile(child, LinkOption.NOFOLLOW_LINKS)) {
                        log.debug("File: " + child.getFileName());

                        indexedPaths.add(child);
                        ContentEntity content = new ContentEntity(child.getFileName().toString(),child.normalize().toString());

                        log.debug("Content: " + content.getName() + " " + content.getPath());
                    }
                    else if (Files.isDirectory(child, LinkOption.NOFOLLOW_LINKS)) {
                        log.debug("Directory: " + child.getFileName());
                        indexPath(child);
                    }
                }
            }
        } else {
            log.debug(path + " is either not accessible or is excluded from indexing.");
        }

        return indexedPaths;
        
    }


    private boolean isPathAccessible(Path path) throws IOException {
        return (path != null && !Files.isHidden(path) && Files.isReadable(path) && !StringUtils.startsWith(path.getFileName().toString(), "."));
    }


    private List<Path> retrieveExcludedPaths() {
        List<SearchPathEntity> excludedPathsResults = searchPathRepository.findAllExcluded();

        for (SearchPathEntity searchPathEntity : excludedPathsResults) {
            SearchPath searchPath = new SearchPath(searchPathEntity);
            excludedPaths.add(searchPath.generatePath());
        }
        
        return excludedPaths;
    }

    private List<Path> retrieveSearchPaths() {
        List<SearchPathEntity> searchPathsResults = searchPathRepository.findAllActive();

        for (SearchPathEntity searchPathEntity : searchPathsResults) {
            SearchPath searchPath = new SearchPath(searchPathEntity);
            searchPaths.add(searchPath.generatePath());
        }
        
        return searchPaths;
    }

    public List<Path> resetIndex() {
        contentServices.resetIndex();
        indexedPaths = Collections.synchronizedList(new LinkedList<>());
        return indexedPaths;
    }
}
