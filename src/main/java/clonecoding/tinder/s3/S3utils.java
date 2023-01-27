package clonecoding.tinder.s3;

import lombok.extern.slf4j.Slf4j;

import java.text.SimpleDateFormat;
import java.util.Date;

@Slf4j
public class S3utils {
    private static final String FILE_EXTENSION_SEPARATOR = ".";

    public static String buildFileName(String category, String originalFileName) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyMMdd");
        //.이 찍히는 인덱스번호 찾기
        int fileExtensionIndex = originalFileName.lastIndexOf(FILE_EXTENSION_SEPARATOR);
        //.이 찍히는 인덱스를 사용해서 . 앞에있는 글자들 지우기
        String fileExtension = originalFileName.substring(fileExtensionIndex);
        //. 앞에있는 글자들만 살리기
        String fileName = originalFileName.substring(0, fileExtensionIndex);
        //위 포맷으로 이미지 저장 시간 구하기
        String now = String.valueOf(simpleDateFormat.format(new Date()));

        log.info("fileExtension = " + fileExtension);

        //예시) profile-hyeunseungProfile-230127.jpg
        return category + "-" + fileName +"-"+ now + fileExtension;
    }
}
