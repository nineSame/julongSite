package com.chiyun.julong.controller;

import com.chiyun.julong.common.ApiPageResult;
import com.chiyun.julong.common.ApiResult;
import com.chiyun.julong.common.annotation.AccessRequired;
import com.chiyun.julong.entity.UserDisplay;
import com.chiyun.julong.entity.UserEntity;
import com.chiyun.julong.repository.UserRepository;
import com.chiyun.julong.repository.userDisplayRepository;
import com.chiyun.julong.utils.Md5Util;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Date;
import java.util.List;

//@RestController
@Controller
//@RequestMapping("/User")
public class UserController {
     private List<UserDisplay> listUser;
     //private String name;
    @Resource
    private userDisplayRepository userDisplayRepository;
    @Resource
    private UserRepository userRepository;


    @RequestMapping("/test")
    public String test() {
        return "test";
    }

    @RequestMapping("/index")
    public String index() {
        return "redirect:admin/html/login.html";
    }


    @ResponseBody
    @RequestMapping("/user/display")
    public ApiResult<Object> dislpay(HttpSession httpSession) throws Exception {

        //listUser = (List<UserEntity>) userRepository.findAllUser();

        listUser = userDisplayRepository.findAlldesc();
        long total = userDisplayRepository.count();
        System.out.print("total:" + total);
        System.out.print("-------------listUser:" + listUser);
        // listUser.add((total);
        if (listUser == null) {
            return ApiResult.FAILURE("数据库错误");
        }
        //httpSession.setAttribute("id", userEntity.getId());
        //ApiPageResult ApiPageResult = new
        return ApiPageResult.SUCCESS(listUser,total);

    }


    @ResponseBody
    @RequestMapping("/login")
    public ApiResult<Object> login(String zh, String mm, HttpSession httpSession) throws Exception {
        if (zh == null|| zh.isEmpty() || mm == null|| zh.isEmpty()) {
            return ApiResult.FAILURE("用户名或密码不能为空");
        }
        System.out.print("password:" + mm);
        String pwd = Md5Util.getMD5(mm);
        System.out.print("password:" + pwd);
        UserEntity userEntity = userRepository.findByZhAndMm(zh, pwd);
        if (userEntity == null) {
            return ApiResult.FAILURE("用户名或密码错误");
        }
        httpSession.setAttribute("id", userEntity.getId());
        return ApiResult.SUCCESS("登录成功");
    }

    @ResponseBody
    @RequestMapping("/user/create")
    @AccessRequired(menue = 0, action = 1)
    public ApiResult<Object> create(UserEntity userEntity, HttpServletRequest zp,  HttpSession httpSession) throws Exception {
//        String personid = (String) httpSession.getAttribute("id");
//        if (personid.isEmpty()) {
//            return ApiResult.UNKNOWN();
//        }
        //用户参数太多，所以在这里用帐号account作为唯一性的判断标志，新增方法中，帐号和密码属于必填字段
       String username=userEntity.getZh();
     if(username==null||username==""){
       return ApiResult.FAILURE("帐号为空");
     }
      if(userRepository.findByZh(username)!=null){
            return ApiResult.FAILURE("数据库已有该帐号的数据");
        }
      //处理传过来的图片路径
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) zp;
        MultipartFile file = multipartRequest.getFile("zpfile");
        String zpfile ="";
        if (file!=null){
             zpfile = file.getOriginalFilename();
        }
        userEntity.setZp(zpfile);
        //取出密码加密再存进去
        String password = userEntity.getMm();
       if(password==null||password==""){
           return ApiResult.FAILURE("密码不能为空");
       }
        userEntity.setMm(Md5Util.getMD5(password));
        //存更新时间
        userEntity.setGxsj(new Date());
        //存储操作
        UserEntity entity = userRepository.save(userEntity);
           if (entity == null) {
            return ApiResult.FAILURE("新建成员失败");
        }

        return ApiResult.SUCCESS("新建成员成功");
    }

    @ResponseBody
    @RequestMapping("/user/update")
    @AccessRequired(menue = 0, action = 1)
    public ApiResult<Object> update(UserEntity userEntity,  HttpServletRequest zp, HttpSession httpSession) throws Exception {
      /*  String personid = (String) httpSession.getAttribute("id");
        if (personid.isEmpty()) {
            return ApiResult.UNKNOWN();
        }*/
        /*if (userEntity == null) {
            return ApiResult.FAILURE("参数错误");
        }*/
        //用户参数太多，所以在这里用帐号account作为唯一性的判断标志
        String username=userEntity.getZh();
        if(username==null||username==""){
            return ApiResult.FAILURE("帐号为空");
        }
        UserEntity userEntity1 = userRepository.findById(userEntity.getId());
        if(userEntity1==null){
            return ApiResult.FAILURE("未找到该用户");
        }
       /* if (userEntity1.getName()==null||userEntity1.getName().isEmpty()){

        }else if (!userEntity1.getName().equals(userEntity.getName())) {
            userEntity1.setName(userEntity.getName());
        }*/
        //处理传过来的图片路径
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) zp;
        MultipartFile file = multipartRequest.getFile("zpfile");
        String zpfile ="";
        if (file!=null){
            zpfile = file.getOriginalFilename();
        }else
        userEntity.setZp(zpfile);
       //更新的时候把密码加密
        String password=userEntity.getMm();
        if(password!=null && password!=""){
            userEntity.setMm(Md5Util.getMD5(password));
        }
        userEntity.setGxsj(new Date());
        UserEntity entity = userRepository.save(userEntity);
        if (entity == null) {
            return ApiResult.FAILURE("修改失败");
        }
        return ApiResult.SUCCESS("修改成功");
    }


    @ResponseBody
    @RequestMapping("/user/del")
    public ApiResult<Object> login(String id, HttpSession httpSession) throws Exception {
       /*UserEntity userEntity = userRepository.delete(id);
        if (userEntity == null) {
            return ApiResult.FAILURE("用户名或密码错误");
        }
        httpSession.setAttribute("id", userEntity.getId());*/
        //判断是否为管理员

        //通过id实行删除操作
        //System.out.print("id:"+ id);
        if (id == "" || id==null) {
            return ApiResult.FAILURE("参数错误");
        }
        int isdel = userRepository.deleteOrderById(id);
       // System.out.print("-------------isdel:"+ isdel);
        if (isdel == 0) {
            return ApiResult.FAILURE("删除失败");
        }
        return ApiResult.SUCCESS("删除成功");
    }


    @RequestMapping("/valid")
    @AccessRequired(menue = 0, action = 1)
    public ApiResult<Object> valid(String id, int sfyx, HttpSession httpSession) {
        String personid = (String) httpSession.getAttribute("id");
        if (personid.isEmpty()) {
            return ApiResult.UNKNOWN();
        }
        UserEntity userEntity = userRepository.findById(id);
        if (userEntity == null) {
            return ApiResult.FAILURE("不存在的用户");
        }
        userEntity.setSfyx(sfyx);
        userRepository.save(userEntity);
        return ApiResult.SUCCESS();
    }

    @RequestMapping("/setrole")
    @AccessRequired(menue = 0, action = 1)
    public ApiResult<Object> setrole(String id, int js, HttpSession httpSession) {
        String personid = (String) httpSession.getAttribute("id");
        if (personid.isEmpty()) {
            return ApiResult.UNKNOWN();
        }
        //if (id == null || role == null) {
            if (id == null) {
            return ApiResult.FAILURE("参数错误");
        }
        UserEntity userEntity = userRepository.findById(id);
        if (userEntity == null) {
            return ApiResult.FAILURE("不存在的用户");
        }
        userEntity.setJs(js);
        userRepository.save(userEntity);
        return ApiResult.SUCCESS();
    }

    @RequestMapping("/resetPassword")
    public ApiResult<Object> changepassword(String oldpwd, String newpwd, HttpSession httpSession) throws Exception {
        String personid = (String) httpSession.getAttribute("id");
        if (personid.isEmpty()) {
            return ApiResult.UNKNOWN();
        }
        if (oldpwd == null || newpwd == null) {
            return ApiResult.FAILURE("参数错误");
        }
        UserEntity userEntity = userRepository.findById(personid);
        if (userEntity == null) {
            return ApiResult.FAILURE("不存在的用户");
        }
        if (!Md5Util.getMD5(oldpwd).equals(userEntity.getMm())) {
            return ApiResult.FAILURE("原密码错误");
        }
        userEntity.setMm(Md5Util.getMD5(newpwd));
        userRepository.save(userEntity);
        return ApiResult.SUCCESS("密码修改成功");
    }



    @ResponseBody
    @RequestMapping("/user/seaarchById")
    public ApiResult<Object> searchById(String id,HttpSession httpSession) throws Exception {

        UserDisplay oneUser = userDisplayRepository.findById(id);
        if (oneUser == null) {
            return ApiResult.FAILURE("未找到该用户");
        }
        //httpSession.setAttribute("id", userEntity.getId());
        //ApiPageResult ApiPageResult = new
        return ApiResult.SUCCESS(oneUser);

    }


    @ResponseBody
    @RequestMapping("/user/page")
    public ApiResult<Object> page(String zh,int page,int size, HttpSession httpSession){
//zh为关键字查询，目前只有帐号一个需求
if(zh==null){
    zh="%%";
}
        Page<UserDisplay> list = userDisplayRepository.findAllByZhLike(zh,PageRequest.of(page-1,size, Sort.unsorted()));

        if (list == null) {
            return ApiResult.FAILURE("没有数据");
        }
        //httpSession.setAttribute("id", userEntity.getId());
        //ApiPageResult ApiPageResult = new
        return ApiResult.SUCCESS(list);

    }
}
