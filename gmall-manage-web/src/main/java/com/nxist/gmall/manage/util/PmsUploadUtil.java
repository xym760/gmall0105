package com.nxist.gmall.manage.util;

import org.csource.fastdfs.ClientGlobal;
import org.csource.fastdfs.StorageClient;
import org.csource.fastdfs.TrackerClient;
import org.csource.fastdfs.TrackerServer;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * @author YuanmaoXu
 * @date 2020/3/10 22:58
 */
public class PmsUploadUtil {

    public static String uploadImage(MultipartFile multipartFile) {
        String imgUrl = "http://192.168.159.128";
        //上传图片到服务器
        //配置fdfs的全局连接地址
        String tracker = PmsUploadUtil.class.getResource("/tracker.conf").getPath();//获得配置文件的路径
        try {
            ClientGlobal.init(tracker);
        } catch (Exception e) {
            e.printStackTrace();
        }
        TrackerClient trackerClient = new TrackerClient();
        //获得一个trackerServer的实例
        TrackerServer trackerServer = null;
        try {
            trackerServer = trackerClient.getTrackerServer();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //通过TrackerServer获得一个Storage连接客户端
        StorageClient storageClient = new StorageClient(trackerServer, null);
        try {
            byte[] bytes = multipartFile.getBytes();//获得上传的二进制对象
            //获得文件扩展名
            String originalFilename = multipartFile.getOriginalFilename();//得到文件名
            System.out.println(originalFilename);
            int i = originalFilename.lastIndexOf(".");
            String extName = originalFilename.substring(i + 1);
            String[] uploadInfos = storageClient.upload_file(bytes, extName, null);
            for (String uploadInfo : uploadInfos) {
                imgUrl += "/" + uploadInfo;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return imgUrl;
    }
}
