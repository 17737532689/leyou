package com.leyou.upload.service.Impl;

import com.github.tobato.fastdfs.domain.StorePath;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.upload.config.UploadProperties;
import com.leyou.upload.service.UploadService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

@Service
public class UploadServiceImpl implements UploadService {
    @Autowired
    private UploadProperties prop;

    @Autowired
    private FastFileStorageClient storageClient;
    @Override
    public String uploadImage(MultipartFile file) {

        //对文件格式进行校验
        String contentType = file.getContentType();

        if(!prop.getAllowTypes().contains(contentType)){
            throw new LyException(ExceptionEnum.INVALID_FILE_FORMAT);
        }

        try {
            BufferedImage image = ImageIO.read(file.getInputStream());
            if(image==null){
                throw new LyException(ExceptionEnum.INVALID_FILE_FORMAT);
            }
        } catch (IOException e) {e.printStackTrace();
            throw new LyException(ExceptionEnum.UPLOAD_IMAGE_EXCEPTION);
        }
        //保存图片
        try {
            String extensionName = StringUtils.substringAfterLast(file.getOriginalFilename(), ".");
            StorePath storePath = storageClient.uploadFile(file.getInputStream(), file.getSize(), extensionName, null);
            return prop.getBaseUrl()+storePath.getFullPath();
        } catch (IOException e) {
            throw new LyException(ExceptionEnum.UPLOAD_IMAGE_EXCEPTION);
        }
    }
}
