package edu.du.campusflow.entity;

import lombok.*;

import javax.persistence.*;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@Data
@Table(name = "dept")
public class Dept {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "dept_id")
    private Long deptId;

    @Column(name = "dept_name", length = 50)
    private String deptName;

    @Column(name = "max_students")
    private Integer maxStudents;

    @ManyToOne
    @JoinColumn(name = "dept_status", referencedColumnName = "code_id")
    private CommonCode deptStatus;

    @OneToMany(mappedBy = "dept", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @ToString.Exclude
    private List<Curriculum> curriculums;
}

