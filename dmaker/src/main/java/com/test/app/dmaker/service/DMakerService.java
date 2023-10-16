package com.test.app.dmaker.service;

import com.test.app.dmaker.dto.CreateDeveloper;
import com.test.app.dmaker.dto.DeveloperDetailDto;
import com.test.app.dmaker.dto.DeveloperDto;
import com.test.app.dmaker.dto.EditDeveloper;
import com.test.app.dmaker.entity.Developer;
import com.test.app.dmaker.entity.RetiredDeveloper;
import com.test.app.dmaker.exception.DMakerException;
import com.test.app.dmaker.repository.DeveloperRepository;
import com.test.app.dmaker.repository.RetiredDeveloperRepository;
import com.test.app.dmaker.type.DeveloperLevel;
import com.test.app.dmaker.type.StatusCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static com.test.app.dmaker.exception.DMakerErrorCode.*;

@Service
// 생성자 주입을 통해서 repository에 할당한다.
@RequiredArgsConstructor
public class DMakerService {
    private final DeveloperRepository developerRepository;
    private final RetiredDeveloperRepository retiredDeveloperRepository;
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
                .statusCode(StatusCode.EMPLOYED)
                .age(request.getAge())
                .build();

        developerRepository.save(developer);

        return CreateDeveloper.Response.fromEntity(developer);
    }

    // request 객체를 검증하기 위해 만든 메소드
    // 비지니스 로직(정책)에서 검증이 필요한 경우
    private void validateCreateDeveloperRequest(CreateDeveloper.Request request) {
        // 벨리데이션 (비지니스 로직 확인)
        validateDeveloperLevel(request.getDeveloperLevel(), request.getExperienceYear());

        // 중복된 id가 있을 시 에러 발생
        developerRepository.findByMemberId(request.getMemberId())
                .ifPresent((developer -> {
                    throw new DMakerException(DUPLICATED_MEMBER_ID);
                }));

    }

    public List<DeveloperDto> getAllEmployedDevelopers() {
        return developerRepository.findDevelopersByStatusCodeEquals(StatusCode.EMPLOYED)
                .stream().map(DeveloperDto::fromEntity)
                .collect(Collectors.toList());
    }

    public DeveloperDetailDto getDeveloperDetail(String memberId) {
        return developerRepository.findByMemberId(memberId)
                .map(DeveloperDetailDto::fromEntity)
                .orElseThrow(() -> new DMakerException(NO_DEVELOPER));
    }

    @Transactional
    public DeveloperDetailDto editDeveloper(String memberId, EditDeveloper.Request request) {
        // 밸류데이션 비지니스 로직 실행
        validateEditDeveloperRequest(request, memberId);

        // developer가 있는지 확인하고 없으면 에러 발생
        Developer developer = developerRepository.findByMemberId(memberId).orElseThrow(() -> new DMakerException(NO_DEVELOPER));

        // developer update 값 할당해주기
        developer.setDeveloperLevel(request.getDeveloperLevel());
        developer.setDeveloperSkillType(request.getDeveloperSkillType());
        developer.setExperienceYears(request.getExperienceYear());

        return DeveloperDetailDto.fromEntity(developer);
    }

    private void validateEditDeveloperRequest(EditDeveloper.Request request, String memberID) {
        // 벨리데이션 비지니스 로직 확인
        validateDeveloperLevel(request.getDeveloperLevel(), request.getExperienceYear());

    }

    private static void validateDeveloperLevel(DeveloperLevel developerLevel, Integer experience) {
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
    }

    @Transactional
    public DeveloperDetailDto deleteDeveloper(String memberId) {
        // 1. EMPLOYED -> RETIRED
        Developer developer = developerRepository.findByMemberId(memberId).orElseThrow(() -> new DMakerException(NO_DEVELOPER));
        developer.setStatusCode(StatusCode.RETIRED);

        // 2. save into RetiredDeveloper
        RetiredDeveloper retiredDeveloper = RetiredDeveloper.builder()
                .memberId(memberId)
                .name(developer.getName())
                .build();

        retiredDeveloperRepository.save(retiredDeveloper);
        return DeveloperDetailDto.fromEntity(developer);
    }
}
