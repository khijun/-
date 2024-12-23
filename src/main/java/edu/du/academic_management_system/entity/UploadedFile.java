package edu.du.academic_management_system.entity;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@ToString
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "uploaded_file")
public class UploadedFile {
    @EmbeddedId
    private FileId fileId;  // 복합키로 FileId 클래스를 사용

    @Column(name = "file_uuid")
    private String fileUuid;

    @Column(name = "file_name")
    private String fileName;

    @Column(name = "file_type")
    private String fileType;

    @Column(name = "file_path")
    private String filePath;

    @Column(name = "file_size")
    private String fileSize;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}