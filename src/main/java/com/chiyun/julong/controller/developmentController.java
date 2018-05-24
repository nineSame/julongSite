package com.chiyun.julong.controller;

import com.chiyun.julong.common.ApiResult;
import com.chiyun.julong.common.annotation.AccessRequired;
import com.chiyun.julong.entity.developmentEntity;
import com.chiyun.julong.repository.developmentRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.Date;

@Controller
public class developmentController {
    @Resource
    private developmentRepository developmentRepository;

    @ResponseBody
    @RequestMapping("/development/page")
    public ApiResult<Object> page(Date fzlcsj, int page, int size, HttpSession httpSession){

        if(fzlcsj==null){
            return ApiResult.FAILURE("发展历程时间为空");
        }
        Page<developmentEntity> list = developmentRepository.findAllByFzlcsj(fzlcsj,PageRequest.of(page-1,size, Sort.unsorted()));

        //httpSession.setAttribute("id", userEntity.getId());
        //ApiPageResult ApiPageResult = new
        return ApiResult.SUCCESS(list);

    }
    @ResponseBody
    @RequestMapping("/development/create")
    @AccessRequired(menue = 0, action = 1)
    public ApiResult<Object> create(developmentEntity developmentEntity, HttpSession httpSession) throws Exception {

        if(developmentEntity.getFzlcbt()==null||developmentEntity.getFzlcbt()==""||developmentEntity.getFzlcnr()==null||developmentEntity.getFzlcnr()==""||developmentEntity.getFzlcsj()==null){
            return ApiResult.FAILURE("数据为空");
        }
        //存储操作
        developmentEntity entity = developmentRepository.save(developmentEntity);
        if (entity == null) {
            return ApiResult.FAILURE("新建发展历程失败");
        }

        return ApiResult.SUCCESS("新建发展历程成功");
    }

    @ResponseBody
    @RequestMapping("/development/update")
    @AccessRequired(menue = 0, action = 1)
    public ApiResult<Object> update(developmentEntity developmentEntity, HttpSession httpSession) throws Exception {
      /*  String personid = (String) httpSession.getAttribute("id");
        if (personid.isEmpty()) {
            return ApiResult.UNKNOWN();
        }*/
        /*if (developmentEntity == null) {
            return ApiResult.FAILURE("参数错误");
        }*/
       ;
        if(developmentEntity.getFzlcbt()==null||developmentEntity.getFzlcbt()==""||developmentEntity.getFzlcnr()==null||developmentEntity.getFzlcnr()==""||developmentEntity.getFzlcsj()==null){
            return ApiResult.FAILURE("数据为空");
        }
        developmentEntity developmentEntity1 = developmentRepository.findById(developmentEntity.getId());
        if(developmentEntity1==null){
            return ApiResult.FAILURE("未找到该发展历程");
        }
        developmentEntity entity = developmentRepository.save(developmentEntity);
        if (entity == null) {
            return ApiResult.FAILURE("修改失败");
        }
        return ApiResult.SUCCESS("修改成功");
    }

    @ResponseBody
    @RequestMapping("/development/del")
    public ApiResult<Object> del(String id, HttpSession httpSession) throws Exception {

        if (id == "" || id==null) {
            return ApiResult.FAILURE("参数错误");
        }
        int isdel = developmentRepository.deleteOrderById(id);
        // System.out.print("-------------isdel:"+ isdel);
        if (isdel == 0) {
            return ApiResult.FAILURE("删除失败");
        }
        return ApiResult.SUCCESS("删除成功");
    }

}