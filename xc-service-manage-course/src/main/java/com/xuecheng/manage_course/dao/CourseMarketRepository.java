package com.xuecheng.manage_course.dao;

import com.xuecheng.framework.domain.course.CourseMarket;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @program: XCEdu->CourseMarketRepository
 * @description: 课程营销信息Jpa
 * @author: Bangser
 * @create: 2019-08-09 17:22
 **/
public interface CourseMarketRepository extends JpaRepository<CourseMarket,String> {
}
