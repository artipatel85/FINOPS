package com.finops.bean;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter @Setter
public class FileUploadRowBean extends AbstractBean{
    private String description = "Document";
    private MultipartFile document;
    private String fileName;
}
