package com.salesmanager.shop.tags;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.SimpleTagSupport;
import java.io.IOException;

public class CustomResponseHeadersTag extends SimpleTagSupport {
    private static final Logger LOGGER = LoggerFactory.getLogger(CustomResponseHeadersTag.class);

    private String attribute1 = "attribute1";
    private String attribute2 = "attribute2";
    private String attribute3 = "attribute3";

    @Override
    public void doTag() throws JspException, IOException {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        Object attr1Obj = request.getAttribute(attribute1);
        Object attr2Obj = request.getAttribute(attribute2);
        Object attr3Obj = request.getAttribute(attribute3);
        LOGGER.info("attribute1:{},attribute2:{},attribute3:{}", attr1Obj, attr2Obj, attr3Obj);
        HttpServletResponse response = attributes.getResponse();
        response.setHeader(attribute1, attr1Obj == null ? "" : attr1Obj.toString());
        response.setHeader(attribute2, attr1Obj == null ? "" : attr2Obj.toString());
        response.setHeader(attribute3, attr1Obj == null ? "" : attr3Obj.toString());
    }

    public void setAttribute1(String attribute1) {
        this.attribute1 = attribute1;
    }

    public void setAttribute2(String attribute2) {
        this.attribute2 = attribute2;
    }

    public void setAttribute3(String attribute3) {
        this.attribute3 = attribute3;
    }
}
