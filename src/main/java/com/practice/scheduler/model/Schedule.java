package com.practice.scheduler.model;

import com.practice.scheduler.util.MapUtil;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * DB로 부터 조회해오는 스케줄 정보
 * 필수정보 : 클래스명, JobId, Cron, Parameter
 */
@ToString
@Getter
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Schedule {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private long id;

    /** 기본 값 **/
    @Column
    private String classNm;
    @Column(unique = true)
    private String jobId;
    @Column
    private String cron;
    @Column
    private String paramString;
    @Column
    private String dupYn;

    @Transient
    private Object parameter;

    /** API job 처리 **/
    @Column
    private String apiUrl;
    @Column
    private String method;
    @Column
    private String apiYn;
    @Column
    private String callback;

    @CreatedDate
    private LocalDateTime createdDate;
    @LastModifiedDate
    private LocalDateTime updateDate;

    public void updateSchedule(RequestDto dto) {
        if (dto.getClassNm() != null) this.classNm = dto.getClassNm();
        if (dto.getJobId() != null) this.jobId = dto.getJobId();
        if (dto.getCron() != null) this.cron = dto.getCron();
        if (dto.getParamString() != null) this.paramString = dto.getParamString();
        if (dto.getDupYn() != null) this.dupYn = dto.getDupYn();
        if (dto.getApiYn() != null) {
            if(dto.getApiYn().equalsIgnoreCase("N")){
                this.apiUrl = "";
                this.method = "";
            } else {
                if (dto.getApiUrl() != null) this.apiUrl = dto.getApiUrl();
                if (dto.getMethod() != null) this.method = dto.getMethod();
                if (dto.getCallback() != null) this.callback = dto.getCallback();
            }
        }
    }

    public Object getParameter() {
        String str = paramString;
        Map<String, Object> map = new HashMap<>();
        try {
            map = new MapUtil().jsonToMap(str);
        } catch (Exception e) {
            map.put("param", str);
        }
        return map;
    }
}
