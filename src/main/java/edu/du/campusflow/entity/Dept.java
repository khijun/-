package edu.du.campusflow.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@Table(name = "department")
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

}

