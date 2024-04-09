package com.practice.scheduler.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class JobLog {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private long id;

    @Column
    private String classNm;
    @Column
    private String jobId;
    @Column
    private String executeId;
    @Column
    private String parameter;
    @Column
    private String status;
    @Column
    private Long runtime;
    @Column(columnDefinition = "TEXT")
    private String resultSummary;
    @Column
    private String cause;
    @Column(columnDefinition = "TEXT")
    private String causeDetail;
    @Column
    private String serverHost;
    @Column
    private LocalDateTime executeDateTime;
    @Column
    private LocalDateTime endDateTime;

}
