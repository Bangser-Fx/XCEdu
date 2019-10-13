package com.xuecheng.manage_course.dao;

import com.xuecheng.framework.domain.system.SysDictionary;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * @program: XCEdu->SysDictionaryRepository
 * @description: 数据字典MongodbJpa
 * @author: Bangser
 * @create: 2019-08-09 16:21
 **/
public interface SysDictionaryRepository extends MongoRepository<SysDictionary,String> {
    SysDictionary findByDType(String dType);
}
