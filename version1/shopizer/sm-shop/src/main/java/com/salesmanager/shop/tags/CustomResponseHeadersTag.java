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

    private final String ATTRIBUTE1 = "attribute1";
    private final String ATTRIBUTE2 = "attribute2";
    private final String ATTRIBUTE3 = "attribute3";

    @Override
    public void doTag() throws JspException, IOException {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        Object attr1Obj = request.getAttribute(ATTRIBUTE1);
        Object attr2Obj = request.getAttribute(ATTRIBUTE1);
        Object attr3Obj = request.getAttribute(ATTRIBUTE1);
        LOGGER.info("attribute1:{},attribute2:{},attribute3:{}", attr1Obj, attr2Obj, attr3Obj);
        HttpServletResponse response = attributes.getResponse();
        response.setHeader(ATTRIBUTE1, attr1Obj == null ? "" : attr1Obj.toString());
        response.setHeader(ATTRIBUTE2, attr1Obj == null ? "" : attr2Obj.toString());
        response.setHeader(ATTRIBUTE3, attr1Obj == null ? "" : attr3Obj.toString());
    }
}
