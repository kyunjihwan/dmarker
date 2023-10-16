package com.test.app.dmaker.controller;

import com.test.app.dmaker.dto.CreateDeveloper;
import com.test.app.dmaker.dto.DeveloperDetailDto;
import com.test.app.dmaker.dto.DeveloperDto;
import com.test.app.dmaker.dto.EditDeveloper;
import com.test.app.dmaker.service.DMakerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController // @Controller + @ResponseBody
@RequiredArgsConstructor
public class DMakerController {

    private final DMakerService dMakerService;

    @GetMapping("/developers")
    public List<DeveloperDto> getAllDevelopers() {
        log.info("GET /developers HTTP/1.1");

        return dMakerService.getAllEmployedDevelopers();
    }

    @GetMapping("/developers/{memberId}")
    public DeveloperDetailDto getDeveloperDetail(
            @PathVariable String memberId
    ){

        log.info("GET /developers/{memberId} HTTP/1.1");

        return dMakerService.getDeveloperDetail(memberId);
    }

    // 개발자 insert하는 메소드
    @PostMapping("/create-developer")
    public CreateDeveloper.Response createDevelopers(
            @Valid @RequestBody CreateDeveloper.Request request
    ) {
        log.info("request : {}", request);

        return dMakerService.createDeveloper(request);
    }

    @PutMapping("developer/{memberId}")
    public DeveloperDetailDto editDeveloper(@PathVariable String memberId, @Valid @RequestBody EditDeveloper.Request request){

        return  dMakerService.editDeveloper(memberId, request);
    }

    @DeleteMapping("/developer/{memberId}")
    public DeveloperDetailDto deleteDeveloper(
            @PathVariable String memberId
    ){
        return dMakerService.deleteDeveloper(memberId);
    }

}
