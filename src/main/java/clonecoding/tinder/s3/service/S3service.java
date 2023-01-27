package clonecoding.tinder.s3.service;

import clonecoding.tinder.s3.S3utils;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

@Slf4j
@RequiredArgsConstructor
@Service
public class S3service {

    private final AmazonS3Client amazonS3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;


    //param으로 가져온 카테고리 -> 파일 네이밍에 붙이기 위함입니다
    public String upload(String category, MultipartFile multipartFile) throws IOException {

        //파일이 제대로 되어있는지 확인
        //가독성이 더 좋을까 해서 추가하였습니다
        validateFileExists(multipartFile);

        //파일 이름을 설정합니다.
        //buildFileName을 보시면 상세 내용 확인이 가능합니다.
        String fileName = S3utils.buildFileName(category, multipartFile.getOriginalFilename());


        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentType(multipartFile.getContentType());

        try (InputStream inputStream = multipartFile.getInputStream()) {
            amazonS3Client.putObject(new PutObjectRequest(bucket, fileName, inputStream, objectMetadata)
                    .withCannedAcl(CannedAccessControlList.PublicRead));
        } catch (IOException e) {
            throw new IllegalArgumentException("파일 전송 실패");
        }

        //파일 주소 리턴
        return amazonS3Client.getUrl(bucket, fileName).toString();
    }


    private void validateFileExists(MultipartFile multipartFile) {
        if (multipartFile.isEmpty()) {
            throw new IllegalArgumentException("파일이 없습니다.");
        }
    }


}
