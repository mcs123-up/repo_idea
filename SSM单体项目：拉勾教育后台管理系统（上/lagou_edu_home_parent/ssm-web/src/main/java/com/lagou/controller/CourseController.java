package com.lagou.controller;

import com.lagou.domain.Course;
import com.lagou.domain.CourseVO;
import com.lagou.domain.ResponseResult;
import com.lagou.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController //组合注解
@RequestMapping("/course")
public class CourseController {
    @Autowired
    private CourseService courseService;

    @RequestMapping("/findCourseByCondition")
    public ResponseResult findCourseByCondition(@RequestBody CourseVO courseVO){
        //调用service
        List<Course> list = courseService.findCourseByConditioin(courseVO);

        ResponseResult result = new ResponseResult(true, 200, "响应成功", list);

        return result;


    }

    /*
        课程图片上传
     */
    @RequestMapping("/courseUpload")
    public ResponseResult fileUpload(@RequestParam("file") MultipartFile file, HttpServletRequest request) throws IOException {

        //1.判断接收到的上传文件是否为空
        if(file.isEmpty()){
            throw  new RuntimeException();
        }
        //2.获取项目部署路径
        String realPath = request.getServletContext().getRealPath("/");

        String substring = realPath.substring(0, realPath.indexOf("ssm-web"));

        //3.获取原文件名
        String originalFilename = file.getOriginalFilename();

        //4.生成新文件名
       String newFileName =  System.currentTimeMillis()+originalFilename.substring(originalFilename.lastIndexOf("."));

       //5.文件上传
        String uploadPath = substring+"upload\\";
        File filePath = new File(uploadPath, newFileName);

        //如果目录不存在就创建目录
        if(!filePath.getParentFile().exists()){
            filePath.getParentFile().mkdirs();
            System.out.println("创建目录"+filePath);
        }

        //图片进行了真正的上传
        file.transferTo(filePath);

        //6.将文件吗和文件路径返回，进行响应
        Map<String,String> map = new HashMap<>();
        map.put("fileName",newFileName);
        map.put("filePath","http://localhost:8081/upload/"+newFileName);


        ResponseResult result = new ResponseResult(true, 200, "图片上传成功", map);
        return result;


    }


    /*
        新增课程信息及讲师信息
        新增课程信息和修改信息在一个方法中国
     */
    @RequestMapping("/saveOrUpdateCourse")
    public ResponseResult  saveOrUpdateCourse(@RequestBody CourseVO courseVO) {

        if (courseVO.getId() == null){
            //调用service
            courseService.saveCourseOrTeacher(courseVO);

            ResponseResult result = new ResponseResult(true, 200, "新增成功", null);

            return result;
        }else {

            courseService.updateCourseOrTeacher(courseVO);
            ResponseResult result = new ResponseResult(true,200,"修改成功",null);
            return result;
        }


    }



    /*
        根据id查找具体的课程信息及关联的讲师信息
     */
    @RequestMapping("/findCourseById")
    public ResponseResult findCourseById(Integer id){
        CourseVO courseVO = courseService.findCourseById(id);

        ResponseResult result = new ResponseResult(true, 200, "根据id查询课程信息成功", courseVO);


        return  result;


    }

    /*
        修改课程状态
     */
    @RequestMapping("/updateCourseStatus")
    public ResponseResult updateCourseStatus(Integer id,Integer status){

        courseService.updateCourseStatus(id,status);
        //响应数据
        Map<String,Object> objectMapHashMap = new HashMap<>();
        objectMapHashMap.put("status",status);
        ResponseResult result = new ResponseResult(true,200,"课程状态变更成功",objectMapHashMap);

        return result;


    }

}
