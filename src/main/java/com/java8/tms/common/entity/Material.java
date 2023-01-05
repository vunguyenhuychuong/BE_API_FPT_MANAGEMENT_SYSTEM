package com.java8.tms.common.entity;

import com.java8.tms.common.meta.MaterialStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.Date;
import java.util.UUID;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Material {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    @Column(name = "id", updatable = false, nullable = false)
    @Type(type = "org.hibernate.type.UUIDCharType")
    private UUID id;
    private String name;
    @Column(length = 65555)
    private String url;
    @Type(type = "org.hibernate.type.UUIDCharType")
    private UUID createdBy;
    private Date createdDate;
    @Type(type = "org.hibernate.type.UUIDCharType")
    private UUID updatedBy;
    private Date updatedDate;

    @Lob
    private byte[] data;

    @Enumerated(EnumType.ORDINAL)
    private MaterialStatus materialStatus;

    // unit_chapter_id
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "unit_chapter_id")
    private SyllabusUnitChapter unitChapter;
}

