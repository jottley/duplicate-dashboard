package net.ottleys.duplicate.services;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.xml.bind.DatatypeConverter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import net.ottleys.duplicate.dao.ContentRepository;
import net.ottleys.duplicate.dao.model.ContentEntity;

@Service
public class ContentServices {

    Log log = LogFactory.getLog(ContentServices.class);

    @Autowired
    private ContentRepository contentRepository;

    @Autowired
    private List<Path> indexedPaths;

    @Autowired
    private MessageDigest messageDigest;


    public void generateChecksum(ContentEntity content) {

        log.info("File: " + content.getName());
        try (InputStream inputStream = Files.newInputStream(Paths.get(content.getPath()))) {

            DigestInputStream digestInputStream = new DigestInputStream(inputStream, messageDigest);
            byte[] buffer = new byte[1024 * 8];
            while (digestInputStream.read(buffer) != -1);
            digestInputStream.close();

            content.setChecksum(DatatypeConverter.printHexBinary(messageDigest.digest()).toLowerCase());
            messageDigest.reset();

  
            log.debug(content.getName() + " Checksum: " + content.getChecksum());
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public String saveContent() {
       if (indexedPaths.isEmpty()) {
           return "No paths to index";
       } else {
           saveContentIndex(indexedPaths);
           return "Success";
       }
    }


    public void saveContentIndex(List<Path> indexedPaths) {
        List<ContentEntity> contentList = Collections.synchronizedList(new LinkedList<>());

        for (Path path : indexedPaths) {
            ContentEntity contentEntity = buildContentEntity(path);
            log.debug("Content to be saved: " + contentEntity);
            contentList.add(contentEntity);
        }

        contentRepository.saveAll(contentList);

        indexedPaths = Collections.synchronizedList(new LinkedList<>());
    }

    public void saveContentEntities(List<ContentEntity> contentEntityList) {
        contentRepository.saveAll(contentEntityList);
    }


    private ContentEntity buildContentEntity(Path path) {
        ContentEntity contentEntity = new ContentEntity(path.getFileName().toString(), path.normalize().toString());

        Map<String, Object> fileAttributes = getAttributes(path);

        contentEntity.setCreatedDateTime((LocalDateTime) fileAttributes.get("creationDateTime"));
        contentEntity.setLastModifiedDateTime((LocalDateTime) fileAttributes.get("lastModifiedDateTime"));

        return contentEntity;
    }

    private Map<String, Object> getAttributes(Path path) {
        Map<String, Object> attributeMap = new HashMap<>();

        try {
            BasicFileAttributes basicFileAttributes = Files.readAttributes(path, BasicFileAttributes.class);
            attributeMap.put("creationDateTime" , LocalDateTime.ofInstant(basicFileAttributes.creationTime().toInstant(), ZoneId.systemDefault()));
            attributeMap.put("lastModifiedDateTime" , LocalDateTime.ofInstant(basicFileAttributes.lastModifiedTime().toInstant(), ZoneId.systemDefault()));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
       
        return attributeMap;
    }

    public void calculateChecksum() {
        Iterable<ContentEntity> iterable = contentRepository.findAll();
        List<ContentEntity> updatedList = Collections.synchronizedList(new LinkedList<>());

        for (ContentEntity contentEntity : iterable) {
            generateChecksum(contentEntity);
            updatedList.add(contentEntity);
        }

        saveContentEntities(updatedList);
    }

    public List<ContentEntity> findDuplicates() {
        return contentRepository.findAllDuplicates();
    }

    public void resetIndex() {
        contentRepository.deleteAll();
    }

    public List<ContentEntity> getIndex() {
        List<ContentEntity> contentList = Collections.synchronizedList(new LinkedList<>());
        
        Iterable<ContentEntity> iterable = contentRepository.findAll();
        iterable.forEach(contentList::add);

        return contentList;
    }

    public void save(List<ContentEntity> contentEntities) {
        contentRepository.saveAll(contentEntities);
    }

    public void save(ContentEntity contentEntity) {
        contentRepository.save(contentEntity);
    }


}
