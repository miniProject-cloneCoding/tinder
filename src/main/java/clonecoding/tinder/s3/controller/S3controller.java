package clonecoding.tinder.s3.controller;

import clonecoding.tinder.s3.service.S3service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RequiredArgsConstructor
@RestController
@Slf4j
public class S3controller {
    private final S3service s3Service;

    @PostMapping("/upload")
    public String uploadFile(
            @RequestParam("category") String category,
            //multiPartFile을 사용하기 위해 @RequestPart를 사용하였습니다
            //@RequestBody가 필요하지만 Binary Stream(바이너리 파일을 입출력 하는 것!
            // 바이너리 파일은 텍스트가 아닌 실행 파일이나 그림 파일)이 포함되는 경우(MultipartFile과 같은)에 사용할 수 있다고 합니다.
            @RequestPart(value = "file") MultipartFile multipartFile) throws IOException {
        return s3Service.upload(category, multipartFile);
    }
}
