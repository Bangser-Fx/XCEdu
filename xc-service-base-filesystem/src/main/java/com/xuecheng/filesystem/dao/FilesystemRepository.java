package com.xuecheng.filesystem.dao;

import com.xuecheng.framework.domain.filesystem.FileSystem;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * @program: XCEdu->FilesystemRepository
 * @description: 文件信息MongoJPA
 * @author: Bangser
 * @create: 2019-08-13 15:14
 **/
public interface FilesystemRepository extends MongoRepository<FileSystem,String> {
}
