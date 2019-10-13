package com.xuecheng;

import org.csource.common.MyException;
import org.csource.fastdfs.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TestFastDFSApplicationTests {


    @Test
    public void testUploadImg() {
        try {
            ClientGlobal.initByProperties("config/fastdfs-client.properties");
            TrackerClient trackerClient = new TrackerClient();
            TrackerServer connection = trackerClient.getConnection();
            StorageServer storage = trackerClient.getStoreStorage(connection);
            StorageClient storageClient = new StorageClient(connection, storage);
            String[] jpgs = storageClient.upload_file("d:/wz2.JPG", "jpg", null);
            String s = jpgs[0] + "/" + jpgs[1];
            System.out.println(s);
            //虚拟机（192.168.14.129:22122）group1/M00/00/00/wKgOgV1QJHyAezHeAAEgI1I6UGw415.jpg
            //阿里云（39.108.184.244:22122）group1/M00/00/00/rBECR11Q9TqAO4uKAAEgI5kXI7c643.jpg
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testDownloadImg() {
        try {
            ClientGlobal.initByProperties("config/fastdfs-client.properties");
            TrackerClient trackerClient = new TrackerClient();
            TrackerServer connection = trackerClient.getConnection();
            StorageServer storage = trackerClient.getStoreStorage(connection);
            StorageClient storageClient = new StorageClient(connection, storage);
            int download_file = storageClient.download_file("group1", "M00/00/00/wKgOgV1QJHyAezHeAAEgI1I6UGw415.jpg", "d:/fastdfs.jpg");
            System.out.println(download_file);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (MyException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testSubString() {
        String fileName = "group1/M00/00/00/wKgOgV1QJHyAezHeAAEgI1I6UGw415.jpg";
        int i = fileName.lastIndexOf(".");
        String extName = fileName.substring(i + 1);
        System.out.println(extName);
    }

}
