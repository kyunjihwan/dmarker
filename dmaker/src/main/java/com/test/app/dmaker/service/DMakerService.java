package com.test.app.dmaker.service;

import com.test.app.dmaker.dto.CreateDeveloper;
import com.test.app.dmaker.entity.Developer;
import com.test.app.dmaker.exception.DMakerException;
import com.test.app.dmaker.repository.DeveloperRepository;
import com.test.app.dmaker.type.DeveloperLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

import static com.test.app.dmaker.exception.DMakerErrorCode.DUPLICATED_MEMBER_ID;
import static com.test.app.dmaker.exception.DMakerErrorCode.LEVEL_EXPERIENCE_YEARS_NOT_MATCHED;

@Service
// 생성자 주입을 통해서 repository에 할당한다.
@RequiredArgsConstructor
public class DMakerService {
    private final DeveloperRepository developerRepository;
    @Transactional
    public CreateDeveloper.Response createDeveloper(CreateDeveloper.Request request){
        // 비지니스 로직 검증
        validateCreateDeveloperRequest(request);

        Developer developer = Developer.builder()
                .developerLevel(request.getDeveloperLevel())
                .developerSkillType(request.getDeveloperSkillType())
                .experienceYears(request.getExperienceYear())
                .memberId(request.getMemberId())
                .name(request.getName())
                .age(request.getAge())
                .build();

        developerRepository.save(developer);

        return CreateDeveloper.Response.fromEntity(developer);
    }

    // request 객체를 검증하기 위해 만든 메소드
    // 비지니스 로직(정책)에서 검증이 필요한 경우
    private void validateCreateDeveloperRequest(CreateDeveloper.Request request) {
        // 벨리데이션
        DeveloperLevel developerLevel = request.getDeveloperLevel();
        Integer experience = request.getExperienceYear();
        if(developerLevel == DeveloperLevel.SENIOR
                &&  experience < 10){
            throw new DMakerException(LEVEL_EXPERIENCE_YEARS_NOT_MATCHED);
        }
        
        if(developerLevel == DeveloperLevel.JUNGIOR
                && (experience < 4 || experience > 10)){
            throw new DMakerException(LEVEL_EXPERIENCE_YEARS_NOT_MATCHED);
        }
        
        if(developerLevel == DeveloperLevel.JUNIOR
                && experience > 4){
            throw new DMakerException(LEVEL_EXPERIENCE_YEARS_NOT_MATCHED);
        }

        // 중복된 id가 있을 시 에러 발생
        developerRepository.findByMemberId(request.getMemberId())
                .ifPresent((developer -> {
                    throw new DMakerException(DUPLICATED_MEMBER_ID);
                }));

    }
}
