package com.example.graduation_project.service;

import org.springframework.web.multipart.MultipartFile;

public interface FileService {

    String uploadImage(MultipartFile file);

}
