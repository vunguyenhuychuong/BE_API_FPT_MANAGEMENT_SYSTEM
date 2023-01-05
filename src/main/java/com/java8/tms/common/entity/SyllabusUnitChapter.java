package com.java8.tms.common.entity;

import java.util.List;
import java.util.UUID;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

@Entity
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SyllabusUnitChapter {
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
	private double duration;
	private boolean isOnline;

	@OneToMany(mappedBy = "unitChapter", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Material> materials;
	// unit_id
//	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinColumn(name = "unit_id")
	private SyllabusUnit syllabusUnit;

	// output_standard_id
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "output_standard_id")
	private OutputStandard outputStandard;

	// delivery_type_id
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "delivery_type_id")
	private DeliveryType deliveryType;

}

