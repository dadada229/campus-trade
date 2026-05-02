package com.example.graduation_project.service.impl;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import com.example.graduation_project.service.FileService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

@Service
public class FileServiceImpl implements FileService {

    @Value("${file.upload-path:F:/ideaProject/upload/}")
    private String uploadPath;

    @Override
    public String uploadImage(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        String suffix = originalFilename != null ? originalFilename.substring(originalFilename.lastIndexOf(".")) : ".jpg";
        String filename = IdUtil.simpleUUID() + suffix;
        String filePath = uploadPath + filename;

        try {
            FileUtil.writeBytes(file.getBytes(), filePath);
        } catch (Exception e) {
            throw new RuntimeException("文件上传失败", e);
        }

        return "/uploads/" + filename;
    }

}
