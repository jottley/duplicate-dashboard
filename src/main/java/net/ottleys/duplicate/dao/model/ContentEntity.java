package net.ottleys.duplicate.dao.model;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Entity
@Table(name="content")
@Data
@RequiredArgsConstructor
@NoArgsConstructor
public class ContentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @Column(columnDefinition = "varchar(256)")
    @NonNull private String name;
    @Column(columnDefinition = "varchar(1024)")
    @NonNull private String path;
    @Column(columnDefinition = "varchar(512)")
    private String checksum;
    private LocalDateTime createdDateTime;
    private LocalDateTime lastModifiedDateTime;
}
