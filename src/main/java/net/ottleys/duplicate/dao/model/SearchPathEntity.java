package net.ottleys.duplicate.dao.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name="searchpath")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SearchPathEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    @Column(columnDefinition = "varchar(1024)")
    private String path;
    private Boolean active;
    private Boolean exclude;
    
}

