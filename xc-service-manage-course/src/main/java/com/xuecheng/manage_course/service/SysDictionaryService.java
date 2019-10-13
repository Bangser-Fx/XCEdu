package com.xuecheng.manage_course.service;

import com.xuecheng.framework.domain.system.SysDictionary;
import com.xuecheng.manage_course.dao.SysDictionaryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @program: XCEdu->SysDictionaryService
 * @description: 数据字典Service
 * @author: Bangser
 * @create: 2019-08-09 16:23
 **/
@Service
public class SysDictionaryService {

    @Autowired
    private SysDictionaryRepository sysDictionaryRepository;

    public SysDictionary getByType(String type) {
        return sysDictionaryRepository.findByDType(type);
    }

}
