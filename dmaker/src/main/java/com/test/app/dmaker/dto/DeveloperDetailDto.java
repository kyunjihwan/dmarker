package com.test.app.dmaker.dto;

import com.test.app.dmaker.entity.Developer;
import com.test.app.dmaker.type.DeveloperLevel;
import com.test.app.dmaker.type.DeveloperSkillType;
import com.test.app.dmaker.type.StatusCode;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DeveloperDetailDto {

    private DeveloperLevel developerLevel;

    private DeveloperSkillType developerSkillType;

    private Integer experienceYears;
    private StatusCode statusCode;
    private String memberId;
    private String name;
    private Integer age;

    public static DeveloperDetailDto fromEntity(Developer developer){
        return DeveloperDetailDto.builder()
                .developerLevel(developer.getDeveloperLevel())
                .developerSkillType(developer.getDeveloperSkillType())
                .experienceYears(developer.getExperienceYears())
                .name(developer.getName())
                .statusCode(developer.getStatusCode())
                .age(developer.getAge())
                .memberId(developer.getMemberId())
                .build();

    }

}
