package com.hpu.xinqing.controller;
import com.hpu.xinqing.service.ModulesService;
import com.hpu.xinqingcommon.constant.MessageConstant;
import com.hpu.xinqingcommon.utils.AliOssUtil;
import com.hpu.xinqingpojo.entity.Modules;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.hpu.xinqingcommon.result.Result;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@CrossOrigin
@RequestMapping("/common")
public class CommonController {

    @Autowired
    private AliOssUtil aliOssUtil;
    @PostMapping("/upload")
    public Result<String> upload(MultipartFile file)
    {
        log.info("文件上传{}",file);
        try{
            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String objectName = UUID.randomUUID()+extension;
            String filePath = aliOssUtil.upload(file.getBytes(), objectName);
            return Result.success(filePath);
        }catch (Exception e){
            log.info("文件上传失败{}",e);
        }
        return Result.error(MessageConstant.UPLOAD_FAILED);
    }

}
