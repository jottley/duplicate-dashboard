package net.ottleys.duplicate.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import net.ottleys.duplicate.dao.SearchPathRepository;
import net.ottleys.duplicate.dao.model.ContentEntity;
import net.ottleys.duplicate.dao.model.SearchPathEntity;
import net.ottleys.duplicate.services.ContentServices;
import net.ottleys.duplicate.services.SearchPathServices;

@RestController
public class SearchPathController {

    @Autowired
    private SearchPathRepository searchPathRepository;

    @Autowired
    private SearchPathServices searchPathServices;

    @Autowired
    private ContentServices contentServices;

    @Autowired
    private List<Path> indexedPaths;

    @GetMapping("/test")
    public List<SearchPathEntity> getSearchPaths() {
        return (List<SearchPathEntity>) searchPathRepository.findAll();

    }

    @GetMapping("/index")
    public List<Path> index() {
        return searchPathServices.indexSearchPaths();
    }

    @GetMapping("/filetest")
    public List<ContentEntity> filetest () throws IOException {

        List<ContentEntity> contentList = new ArrayList<>();
        Path file1 = Paths.get("/Users/jottley/test.txt");
        Path file2 = Paths.get("/Users/jottley/test/new_name_test.txt");

        ContentEntity content1 = new ContentEntity(file1.getFileName().toString(), file1.normalize().toString());
        contentServices.generateChecksum(content1);
        BasicFileAttributes basicFileAttributes = Files.readAttributes(file1, BasicFileAttributes.class);
        content1.setCreatedDateTime(LocalDateTime.ofInstant(basicFileAttributes.creationTime().toInstant(), ZoneId.systemDefault()));
        content1.setLastModifiedDateTime(LocalDateTime.ofInstant(basicFileAttributes.lastModifiedTime().toInstant(), ZoneId.systemDefault()));
        contentList.add(content1);

        ContentEntity content2 = new ContentEntity(file2.getFileName().toString(), file2.normalize().toString());
        contentServices.generateChecksum(content2);
        basicFileAttributes = Files.readAttributes(file2, BasicFileAttributes.class);
        content2.setCreatedDateTime(LocalDateTime.ofInstant(basicFileAttributes.creationTime().toInstant(), ZoneId.systemDefault()));
        content2.setLastModifiedDateTime(LocalDateTime.ofInstant(basicFileAttributes.lastModifiedTime().toInstant(), ZoneId.systemDefault()));
        contentList.add(content2);

        return contentList;
    }

    @GetMapping("/excludedpaths")
    public List<Path> getExcludedPaths() {
        return searchPathServices.getExcludedPaths();
    }

    @GetMapping("/cached-index")
    public List<Path> getCachedIndex() {
        return searchPathServices.getIndexedPaths();
    }

    @GetMapping("/indexed")
    public List<ContentEntity> getIndexed() {
        return contentServices.getIndex();
    }

    @GetMapping("/indexed-bean")
    public List<Path> getIndexedBean() {
        return indexedPaths;
    }

    @GetMapping("/reset-index")
    public List<Path> resetIndex() {
        return searchPathServices.resetIndex();
    }

    @GetMapping("/save-index")
    public String saveIndex() {
        return contentServices.saveContent();
    }

    @GetMapping("/index-size")
    public int indexSize() {
        return indexedPaths.size();
    }

    @GetMapping("/generatechecksums")
    public String generateChecksums() {
        contentServices.calculateChecksum();
        return "success";
    }

    @GetMapping("/findduplicates")
    public List<ContentEntity> findDuplicates() {
        return contentServices.findDuplicates();
    }


    
}
