package cn.tedu.img.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.jt.common.vo.PicUploadResult;

import cn.tedu.img.service.ImgService;

@RestController
public class ImgController {
    @Autowired
    private ImgService imgService;

    //图片上传
    @RequestMapping("pic/upload")
    public PicUploadResult picUpload(
            //{"url":"http://image.jt.com/upload/**"
            //"error":0/1}
            MultipartFile pic) {
        return imgService.picUpload(pic);
    }
}
