package com.nowcoder.service;

import com.google.gson.Gson;
import com.nowcoder.utils.ToutiaoUtil;
import com.qiniu.common.QiniuException;
import com.qiniu.common.Zone;
import com.qiniu.http.Response;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.DefaultPutRet;
import com.qiniu.util.Auth;
import com.qiniu.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.UUID;

@Service
public class QiniuService {
    private static final Logger logger = LoggerFactory.getLogger(QiniuService.class);
    Configuration cfg = new Configuration(Zone.zone1());
    //...其他参数参考类注释
    UploadManager uploadManager = new UploadManager(cfg);
    //...生成上传凭证，然后准备上传
    String accessKey = "A_l1Fhx0cIKVhxNwetkpscFrFkcok0YYJIZlhlwf";
    String secretKey = "7TuAtGmcySVYbbqNfdS7RwIiHlDSrYNXH6fvWFV2";
    String bucket = "toutiao";
    //如果是Windows情况下，格式是 D:\\qiniu\\test.png
    String localFilePath = "/home/qiniu/test.png";
    //默认不指定key的情况下，以文件内容的hash值作为文件名
    String key = null;
    Auth auth = Auth.create(accessKey, secretKey);
    String upToken = auth.uploadToken(bucket);

    public String saveImage(MultipartFile file) throws IOException{
        try {
            int dosPos = file.getOriginalFilename().lastIndexOf(".");
            if(dosPos < 0)
                return null;
            String fileExt = file.getOriginalFilename().substring(dosPos + 1).toLowerCase();
            if(!ToutiaoUtil.isImage(fileExt))
                return null;
            String filename = UUID.randomUUID().toString().replaceAll("-", "") + "." + fileExt;
            Response response = uploadManager.put(file.getBytes(), filename, upToken);
            //解析上传成功的结果
            DefaultPutRet putRet = new Gson().fromJson(response.bodyString(), DefaultPutRet.class);
            if(response.isOK() && response.isJson())
                return ToutiaoUtil.QINIU_DOMIN_PREFIX + putRet.key;
            else {
                logger.error("七牛异常" + response.bodyString());
                return null;
            }
        } catch (QiniuException ex) {
            logger.error("上传图片到七牛云异常" + ex.getMessage());
            return null;
        }
    }

    public String downLoadImageURL(String fileName) {
        String domainOfBucket = ToutiaoUtil.QINIU_DOMIN_PREFIX;
        String encodedFileName = null;
        try {
            encodedFileName = URLEncoder.encode(fileName, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String finalUrl = String.format("%s/%s", domainOfBucket, encodedFileName);
        return finalUrl;
    }

}
