package com.xuecheng.manage_course.service;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.response.CmsCode;
import com.xuecheng.framework.domain.cms.response.CmsPageResult;
import com.xuecheng.framework.domain.cms.response.CmsPostPageResult;
import com.xuecheng.framework.domain.course.*;
import com.xuecheng.framework.domain.course.ext.CategoryNode;
import com.xuecheng.framework.domain.course.ext.CourseInfo;
import com.xuecheng.framework.domain.course.ext.CourseView;
import com.xuecheng.framework.domain.course.ext.TeachplanNode;
import com.xuecheng.framework.domain.course.request.CourseListRequest;
import com.xuecheng.framework.domain.course.response.CourseCode;
import com.xuecheng.framework.domain.course.response.CoursePublishResult;
import com.xuecheng.framework.exception.ExceptionCast;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.QueryResult;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.manage_course.client.CmsPageClient;
import com.xuecheng.manage_course.config.CmsPageConfig;
import com.xuecheng.manage_course.dao.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * @program: XCEdu->CourseService
 * @description: 课程管理service
 * @author: Bangser
 * @create: 2019-08-07 14:39
 **/
@Service
public class CourseService {

    @Autowired
    private CourseBaseRepository courseBaseRepository;

    @Autowired
    private CourseMapper courseMapper;

    @Autowired
    private TeachplanRepository teachplanRepository;

    @Autowired
    private CourseMarketRepository courseMarketRepository;

    @Autowired
    private CoursePicRepository coursePicRepository;

    @Autowired
    private CmsPageClient cmsPageClient;

    @Autowired
    private CmsPageConfig cmsPageConfig;

    @Autowired
    private CoursePubRepository coursePubRepository;

    @Autowired
    private TeachplanMediaRepository teachplanMediaRepository;

    @Autowired
    private TeachplanMediaPubRepository teachplanMediaPubRepository;

    //根据课程id获取教学计划
    public TeachplanNode getTeachplanNodeList(String courseId) {
        //判断是否存在该课程id
        getCourseById(courseId);
        return courseMapper.findTeachplanList(courseId);
    }

    //添加教学计划节点
    @Transactional
    public ResponseResult addTeachplan(Teachplan teachplan) {
        if (teachplan == null || StringUtils.isBlank(teachplan.getCourseid()) || StringUtils.isBlank(teachplan.getPname())) {
            ExceptionCast.throwCustomException(CommonCode.INVALIDPARAM);
        }
        //获取课程基本信息（检查课程id是否存在）
        CourseBase courseBase = getCourseById(teachplan.getCourseid());
        //未选择根节点则设置父节点为根节点
        if (StringUtils.isBlank(teachplan.getParentid())) {
            //查找该课程根节点
            List<Teachplan> teachplanList = teachplanRepository.findByCourseidAndParentid(teachplan.getCourseid(), "0");
            if (teachplanList == null && teachplanList.size() <= 0) {
                Teachplan teachplanRoot = new Teachplan();
                teachplanRoot.setCourseid(courseBase.getId());
                teachplanRoot.setPname(courseBase.getName());
                teachplanRoot.setParentid("0");
                teachplanRoot.setGrade("1");//1级
                teachplanRoot.setStatus("0");//未发布
                teachplanRepository.save(teachplanRoot);
                teachplan.setParentid(teachplanRoot.getId());
            } else {
                teachplan.setParentid(teachplanList.get(0).getId());
            }
        }
        //设置排序字段
        if (teachplan.getOrderby() == null) {
            teachplan.setOrderby(0);
        }
        //取出父节点信息
        Teachplan parentTeachplan = teachplanRepository.findById(teachplan.getParentid()).get();
        //根据父节点级别设置本身级别
        String grade = parentTeachplan.getGrade();
        if (grade.equals("1")) {
            teachplan.setGrade("2");
        } else if (grade.equals("2")) {
            teachplan.setGrade("3");
        }
        teachplanRepository.save(teachplan);
        return new ResponseResult(CommonCode.SUCCESS);
    }


    //分页查询课程列表（细粒度授权）
    public QueryResponseResult findCourseList(int page, int size, CourseListRequest courseListRequest) {
        //查看查询条件中是否包含公司id
        if(courseListRequest==null || StringUtils.isBlank(courseListRequest.getCompanyId())){
            ExceptionCast.throwCustomException(CommonCode.UNAUTHENTICATED);
        }
        //分页
        if (page <= 0) {
            page = 1;
        }
        if (size <= 0) {
            size = 5;
        }
        PageHelper.startPage(page, size);
        Page<CourseInfo> infoPageList = courseMapper.findCourseInfoPageList(courseListRequest);
        QueryResult<CourseInfo> courseInfoQueryResult = new QueryResult<>();
        courseInfoQueryResult.setTotal(infoPageList.getTotal());
        courseInfoQueryResult.setList(infoPageList.getResult());
        QueryResponseResult result = new QueryResponseResult(CommonCode.SUCCESS, courseInfoQueryResult);
        return result;
    }

    //查找课程分类
    public CategoryNode findCategoryList() {
        return courseMapper.findCategoryList();
    }

    //修改课程基本信息
    @Transactional
    public ResponseResult updateCourseBase(String id, CourseBase courseBase) {
        if (courseBase == null || StringUtils.isBlank(courseBase.getName()) ||
                StringUtils.isBlank(courseBase.getGrade()) || StringUtils.isBlank(courseBase.getStudymodel())) {
            ExceptionCast.throwCustomException(CommonCode.INVALIDPARAM);
        }
        Optional<CourseBase> optional = courseBaseRepository.findById(id);
        if (!optional.isPresent()) {
            ExceptionCast.throwCustomException(CourseCode.COURSE_PUBLISH_COURSEIDISNULL);
        }
        courseBaseRepository.save(courseBase);
        return ResponseResult.SUCCESS();
    }

    //查找课程基本信息
    public CourseBase getCourseById(String courseId) {
        Optional<CourseBase> optional = courseBaseRepository.findById(courseId);
        if (!optional.isPresent()) {
            ExceptionCast.throwCustomException(CourseCode.COURSE_PUBLISH_COURSEIDISNULL);
        }
        return optional.get();
    }

    //查找课程营销信息
    public CourseMarket getCourseMarketById(String courseId) {
        Optional<CourseMarket> optional = courseMarketRepository.findById(courseId);
        if (!optional.isPresent()) {
            ExceptionCast.throwCustomException(CourseCode.COURSE_PUBLISH_COURSEIDISNULL);
        }
        return optional.get();
    }

    //修改课程营销信息
    @Transactional
    public ResponseResult updateCourseMarket(String id, CourseMarket courseMarket) {
        if (courseMarket == null || StringUtils.isBlank(courseMarket.getCharge())) {
            ExceptionCast.throwCustomException(CommonCode.INVALIDPARAM);
        }
        getCourseMarketById(id);
        courseMarketRepository.save(courseMarket);
        return ResponseResult.SUCCESS();
    }

    //添加课程图片
    @Transactional
    public ResponseResult addCoursePic(String courseId, String pic) {
        CoursePic coursePic;
        Optional<CoursePic> optional = coursePicRepository.findById(courseId);
        if (optional.isPresent()) {
            coursePic = optional.get();
        } else {
            coursePic = new CoursePic();
        }
        coursePic.setCourseid(courseId);
        coursePic.setPic(pic);
        coursePicRepository.save(coursePic);
        return new ResponseResult(CommonCode.SUCCESS);
    }

    //查找课程图片
    public CoursePic findCoursePic(String courseId) {
        Optional<CoursePic> optional = coursePicRepository.findById(courseId);
        if (optional.isPresent()) {
            CoursePic coursePic = optional.get();
            return coursePic;
        }
        return null;
    }

    //删除课程图片
    @Transactional
    public ResponseResult deleteCoursePic(String courseId) {
        long l = coursePicRepository.deleteByCourseid(courseId);
        if (l > 0) {
            return new ResponseResult(CommonCode.SUCCESS);
        } else {
            return new ResponseResult(CommonCode.FAIL);
        }
    }

    //查找课程视图，供静态化使用
    public CourseView findCourseViewById(String id) {
        CourseView courseView = new CourseView();
        Optional<CourseBase> baseOptional = courseBaseRepository.findById(id);
        if (!baseOptional.isPresent()) {
            ExceptionCast.throwCustomException(CourseCode.COURSE_PUBLISH_COURSEIDISNULL);
        }
        courseView.setCourseBase(baseOptional.get());
        CoursePic coursePic = this.findCoursePic(id);
        courseView.setCoursePic(coursePic);
        Optional<CourseMarket> marketOptional = courseMarketRepository.findById(id);
        if (marketOptional.isPresent()) {
            courseView.setCourseMarket(marketOptional.get());
        }
        TeachplanNode teachplanNodeList = courseMapper.findTeachplanList(id);
        courseView.setTeachplanNode(teachplanNodeList);
        return courseView;
    }

    //课程预览（返回值中包含课程预览的Url）
    public CoursePublishResult preview(String id) {
        //创建CmsPage
        CmsPage cmsPage = this.creatCmsPage(id);
        //远程访问添加页面
        CmsPageResult result = cmsPageClient.save(cmsPage);
        if (!result.isSuccess()) {
            ExceptionCast.throwCustomException(CourseCode.COURSE_PUBLISH_CDETAILERROR);
        }
        String pageId = result.getCmsPage().getPageId();
        String url = cmsPageConfig.getPreviewUrl() + pageId;
        return new CoursePublishResult(CommonCode.SUCCESS, url);
    }

    //一键发布课程
    @Transactional
    public CoursePublishResult publish(String id) {
        //创建CmsPage
        CmsPage cmsPage = creatCmsPage(id);
        //调用远程接口（发布静态化页面）
        CmsPostPageResult result = cmsPageClient.postPageQuick(cmsPage);
        if (!result.isSuccess()) {
            ExceptionCast.throwCustomException(CmsCode.CMS_POST_FAIL);
        }
        //修改课程发布状态为已发布
        this.updateCourseStatus(id);
        String pageUrl = result.getPageUrl();
        //创建CoursePub对象，并存储到数据库
        CoursePub coursePub = this.creatCoursePub(id);
        CoursePub save = coursePubRepository.save(coursePub);
        if (save == null) {
            ExceptionCast.throwCustomException(CourseCode.COURSE_PUBLISH_PUBFAIL);
        }
        //将该课程教学计划与媒资关系表中的信息插入到teachplan_media_pub表中
        saveTeachplanMediaPub(id);
        return new CoursePublishResult(CommonCode.SUCCESS, pageUrl);
    }

    private void saveTeachplanMediaPub(String courseId){
        List<TeachplanMedia> teachplanMediaList = teachplanMediaRepository.findByCourseId(courseId);
        if(teachplanMediaList!=null && teachplanMediaList.size()>0){
            //删除旧的发布信息
            teachplanMediaPubRepository.deleteByCourseId(courseId);
            List<TeachplanMediaPub> pubList = new ArrayList<>();
            for (TeachplanMedia teachplanMedia : teachplanMediaList) {
                TeachplanMediaPub pub = new TeachplanMediaPub();
                BeanUtils.copyProperties(teachplanMedia,pub);
                pub.setTimestamp(new Date());
                pubList.add(pub);
            }
            teachplanMediaPubRepository.saveAll(pubList);
        }
    }

    //创建CoursePub
    private CoursePub creatCoursePub(String courseId) {
        CoursePub coursePub;
        //先从从数据库查询
        Optional<CoursePub> pubOptional = coursePubRepository.findById(courseId);
        if (pubOptional.isPresent()) {
            coursePub = pubOptional.get();
        } else {
            coursePub = new CoursePub();
        }
        //获取课程基本信息，并将相关属性复制到CoursePub
        Optional<CourseBase> baseOptional = courseBaseRepository.findById(courseId);
        if (!baseOptional.isPresent()) {
            ExceptionCast.throwCustomException(CourseCode.COURSE_PUBLISH_COURSEIDISNULL);
        }
        CourseBase courseBase = baseOptional.get();
        BeanUtils.copyProperties(courseBase, coursePub);
        //查询课程图片信息，并将相关属性复制到CoursePub
        CoursePic coursePic = this.findCoursePic(courseId);
        if (coursePic != null) {
            coursePub.setPic(coursePic.getPic());
        }
        //查询课程营销信息并将相关属性复制到CoursePub
        Optional<CourseMarket> marketOptional = courseMarketRepository.findById(courseId);
        if (marketOptional.isPresent()) {
            BeanUtils.copyProperties(marketOptional.get(), coursePub);
        } else {
            ExceptionCast.throwCustomException(CourseCode.COURSE_TEACHPLAN_ADDFAIL);
        }
        //查询课程计划，将结果转为json串，设置到CoursePub中
        TeachplanNode teachplanNode = this.getTeachplanNodeList(courseId);
        if (teachplanNode != null) {
            String json = JSON.toJSONString(teachplanNode);
            coursePub.setTeachplan(json);
        }
        //设置时间戳（用于搜索服务同步索引）
        coursePub.setTimestamp(new Date());
        //设置发布时间
        SimpleDateFormat format = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");
        String time = format.format(new Date());
        coursePub.setPubTime(time);
        return coursePub;
    }

    //修改发布状态为已发布“202002”
    private CourseBase updateCourseStatus(String id) {
        Optional<CourseBase> optional = courseBaseRepository.findById(id);
        if (!optional.isPresent()) {
            ExceptionCast.throwCustomException(CourseCode.COURSE_PUBLISH_COURSEIDISNULL);
        }
        CourseBase courseBase = optional.get();
        courseBase.setStatus("202002");
        CourseBase save = courseBaseRepository.save(courseBase);
        if (save == null) {
            ExceptionCast.throwCustomException(CourseCode.COURSE_STATUS_UPDATEFAIL);
        }
        return save;
    }

    //创建CmsPage提供给Cms服务创建页面
    private CmsPage creatCmsPage(String id) {
        //获取页面基本信息
        Optional<CourseBase> optional = courseBaseRepository.findById(id);
        if (!optional.isPresent()) {
            ExceptionCast.throwCustomException(CourseCode.COURSE_PUBLISH_COURSEIDISNULL);
        }
        CourseBase courseBase = optional.get();
        //创建CmsPage对象
        CmsPage cmsPage = new CmsPage();
        cmsPage.setSiteId(cmsPageConfig.getSiteId());//站点id
        cmsPage.setPageName(id + ".html");//页面名称
        cmsPage.setPageAliase(courseBase.getName());//页面别名
        cmsPage.setTemplateId(cmsPageConfig.getTemplateId());//模板id
        cmsPage.setPageWebPath(cmsPageConfig.getPageWebPath());//页面访问路径
        cmsPage.setPagePhysicalPath(cmsPageConfig.getPagePhysicalPath());//页面存储路径
        cmsPage.setDataUrl(cmsPageConfig.getDataUrlPre() + id);//数据url
        return cmsPage;
    }

    //保存媒资文件与教学计划的关系
    @Transactional
    public ResponseResult savemedia(TeachplanMedia teachplanMedia) {
        //检查必要参数
        if (teachplanMedia == null || StringUtils.isBlank(teachplanMedia.getCourseId())
                || StringUtils.isBlank(teachplanMedia.getMediaId()) || StringUtils.isBlank(teachplanMedia.getTeachplanId())) {
            ExceptionCast.throwCustomException(CommonCode.INVALIDPARAM);
        }
        //查询课程计划
        Optional<Teachplan> teachplanOptional = teachplanRepository.findById(teachplanMedia.getTeachplanId());
        if (!teachplanOptional.isPresent()) {
            ExceptionCast.throwCustomException(CourseCode.MEDIA_TEACHPLAN_ISNULL);
        }
        Teachplan teachplan = teachplanOptional.get();
        //检查课程计划是否符合妹子选择标准
        String grade = teachplan.getGrade();
        if (!grade.equals("3")) {
            ExceptionCast.throwCustomException(CourseCode.MEDIA_TEACHPLAN_ISBANK);
        }
        //查询计划媒资关系表
        Optional<TeachplanMedia> teachplanMediaOptional = teachplanMediaRepository.findById(teachplanMedia.getTeachplanId());
        TeachplanMedia teachplanMediaNew;
        if(!teachplanMediaOptional.isPresent()){
            teachplanMediaNew = new TeachplanMedia();
        }else {
            teachplanMediaNew = teachplanMediaOptional.get();
        }
        teachplanMediaNew.setCourseId(teachplanMedia.getCourseId());
        teachplanMediaNew.setMediaFileOriginalName(teachplanMedia.getMediaFileOriginalName());
        teachplanMediaNew.setMediaId(teachplanMedia.getMediaId());
        teachplanMediaNew.setMediaUrl(teachplanMedia.getMediaUrl());
        teachplanMediaNew.setTeachplanId(teachplanMedia.getTeachplanId());
        teachplanMediaRepository.save(teachplanMediaNew);
        return new ResponseResult(CommonCode.SUCCESS);
    }
}
