package net.gidosa.full.webadmin.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Log4j2
@Service
@RequiredArgsConstructor
public class FileStorageService {

    // @Value("${file.upload-dir:./uploads}")
    // private String uploadDir;
    @Value("${file.upload.path}")
    private String uploadDir;

    /**
     * 파일을 저장하고 접근 URL을 반환합니다.
     *
     * @param file 업로드할 파일
     * @param subDirectory 저장할 하위 디렉토리 (예: "risk-factors")
     * @return 저장된 파일의 URL
     * @throws IOException 파일 저장 중 오류 발생 시
     */
    public String storeFile(MultipartFile file, String subDirectory) throws IOException {
        // 파일명 정리
        String originalFileName = StringUtils.cleanPath(file.getOriginalFilename());
        
        // 파일명 충돌 방지를 위한 UUID 추가
        String fileExtension = getFileExtension(originalFileName);
        String newFileName = UUID.randomUUID() + fileExtension;
        
        // 저장 경로 생성
        //Path uploadPath = Paths.get(uploadDir, subDirectory);
        Path uploadPath = Paths.get(uploadDir);
        
        // 디렉토리가 없으면 생성
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
        
        // 파일 저장
        Path targetLocation = uploadPath.resolve(newFileName);
        Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
        
        // 접근 URL 반환 (상대 경로)
        //return "/uploads/" + subDirectory + "/" + newFileName;
        //return uploadDir + "\\" + subDirectory + "\\" + newFileName;
        //return subDirectory + "/" + newFileName;
        return newFileName;
    }
    
    /**
     * 파일 확장자를 추출합니다.
     *
     * @param fileName 파일명
     * @return 파일 확장자 (예: ".jpg")
     */
    private String getFileExtension(String fileName) {
        if (fileName.lastIndexOf(".") != -1 && fileName.lastIndexOf(".") != 0) {
            return fileName.substring(fileName.lastIndexOf("."));
        } else {
            return "";
        }
    }
} 