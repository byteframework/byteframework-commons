package com.byteframework.commons.web;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.byteframework.commons.constant.ErrorCodeEnum;
import com.byteframework.commons.domain.ResponseEntity;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.context.support.ApplicationObjectSupport;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.annotation.InitBinder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BaseAction extends ApplicationObjectSupport {
    protected Log log = LogFactory.getLog(this.getClass());

    @InitBinder
    public void initBinder(ServletRequestDataBinder binder) {
        //自动转换日期类型的字段格式
        binder.registerCustomEditor(Date.class, new CustomDateEditor(new SimpleDateFormat("yyyy-MM-dd"), true));
        //防止XSS攻击
//		binder.registerCustomEditor(String.class, new StringEscapeEditor(true, false));
    }

    /**
     * 分页查询列表成功-返回
     *
     * @param totalCount
     * @param objs
     * @return
     */
    protected void responseSuccess(Long totalCount, List<? extends Object> objs, HttpServletRequest request, HttpServletResponse response) {
        ResponseEntity resp = new ResponseEntity(ErrorCodeEnum.SUCCESS_00000.getCode(), objs);
        Map<String, Object> map = new HashMap<>();
        map.put(ResponseEntity.LIST_KEY, objs);
        map.put(ResponseEntity.TOTAL_COUNT_KEY, totalCount);
        resp.setData(map);
        this.writecallback(resp, request, response);
    }


    /**
     * 查询单个对象成功-返回
     *
     * @param obj
     * @return
     */
    protected void responseSuccess(Object obj, HttpServletRequest request, HttpServletResponse response) {
        ResponseEntity resp = new ResponseEntity(ErrorCodeEnum.SUCCESS_00000.getCode(), obj);
        this.writecallback(resp, request, response);
    }

    protected void responseSuccess(HttpServletRequest request, HttpServletResponse response) {
        ResponseEntity resp = new ResponseEntity(ErrorCodeEnum.SUCCESS_00000.getCode());
        this.writecallback(resp, request, response);
    }

    protected void responseSuccess(HttpServletRequest request, HttpServletResponse response, String msg) {
        ResponseEntity resp = new ResponseEntity(ErrorCodeEnum.SUCCESS_00000.getCode());
        resp.setMessage(msg);
        this.writecallback(resp, request, response);
    }

    protected void responseFailure(HttpServletRequest request, HttpServletResponse response, String msg) {
        this.responseFailure(ErrorCodeEnum.ERROR_B0001.getCode(), msg, request, response);
    }

    protected void responseFailure(String code, String msg, HttpServletRequest request, HttpServletResponse response) {
        ResponseEntity resp = new ResponseEntity(code);
        resp.setMessage(msg);
        this.writecallback(resp, request, response);
    }

    /**
     * 向跨域访问的静态页面写回调数据
     *
     * @param obj
     * @param response
     * @param request
     */
    protected void writecallback(Object obj, HttpServletRequest request, HttpServletResponse response) {
//        response.setHeader("Access-Control-Allow-Origin", "*");

        if (request.getMethod().equals("OPTIONS")) {
            response.setStatus(HttpServletResponse.SC_OK);
            return;
        }

        String json = JSONObject.toJSONString(obj, SerializerFeature.DisableCircularReferenceDetect,
                SerializerFeature.WriteMapNullValue, SerializerFeature.SkipTransientField);
        try {
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(json); //返回json数据
            response.getWriter().flush();
            response.getWriter().close();
            log.info(String.format("响应信息: <%s>  %s", request.getRequestURI(), json));
        } catch (IOException e) {
            log.error("writecallback excption -> ", e);
            log.error(String.format("响应信息: <%s>  %s", request.getRequestURI(), json));
        }
    }

}
