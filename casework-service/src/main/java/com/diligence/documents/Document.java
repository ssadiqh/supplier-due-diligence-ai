package com.diligence.documents;

import jakarta.persistence.*;
import lombok.*;
import com.diligence.casework.DueDiligenceCase;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "documents")
@Data
@NoArgsConstructor
@EqualsAndHashCode(exclude = {"createdAt"})
@ToString(exclude = {"createdAt"})
public class Document {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "case_id", nullable = false)
    @JsonIgnore
    private DueDiligenceCase caseEntity;

    @Column(nullable = false)
    private String fileName;

    @Column(nullable = false)
    private String fileType;

    @Column(nullable = false)
    private Long fileSize;

    @Column(nullable = false)
    private String filePath;

    @Column(nullable = false, updatable = false)
    private LocalDateTime uploadedAt = LocalDateTime.now();

    @Column
    private String uploadedBy;

    public Document(DueDiligenceCase caseEntity, String fileName, String fileType, Long fileSize, String filePath, String uploadedBy) {
        this.caseEntity = caseEntity;
        this.fileName = fileName;
        this.fileType = fileType;
        this.fileSize = fileSize;
        this.filePath = filePath;
        this.uploadedBy = uploadedBy;
        this.uploadedAt = LocalDateTime.now();
    }
}
