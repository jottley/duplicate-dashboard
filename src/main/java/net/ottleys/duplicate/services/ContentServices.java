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

        try (InputStream inputStream = Files.newInputStream(Paths.get(content.getPath()))) {
             DigestInputStream digestInputStream = new DigestInputStream(inputStream, messageDigest);

            while (digestInputStream.read() != -1) ; //empty loop to clear the data
            messageDigest = digestInputStream.getMessageDigest();

            byte[] bytes = messageDigest.digest();

            StringBuilder stringBuilder = new StringBuilder();
            for (byte b : bytes) {
                stringBuilder.append(String.format("%02x", b));
            }

            content.setChecksum(stringBuilder.toString());
            log.debug(content.getName() + " Checksum: " + content.getChecksum());

        } catch (IOException e) {
            throw new IllegalArgumentException(e);
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
            //log.debug("Content to be saved: " + contentEntity);
            if (contentEntity.getName().length() > 255) {
                log.debug("Name length: " + contentEntity.getName().length());
            }

            if (contentEntity.getPath().length() > 255) {
                log.debug("Path length: " + contentEntity.getPath().length());
            }
            //log.debug("Name length: " + contentEntity.getName().length() + " ***** Path length: " + contentEntity.getPath().length());
            contentList.add(contentEntity);
        }

        contentRepository.saveAll(contentList);
    }

    public void saveContentEntities(List<ContentEntity> contentEntityList) {
        contentRepository.saveAll(contentEntityList);
    }


    private ContentEntity buildContentEntity(Path path) {
        ContentEntity contentEntity = new ContentEntity(path.getFileName().toString(), path.normalize().toString());

        contentEntity.setChecksum("checksum");

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

}
