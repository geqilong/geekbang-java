package com.salesmanager.core.business.management;

import com.salesmanager.core.business.modules.email.EmailConfig;

import javax.management.*;

public class DynamicEmailConfigMBean extends EmailConfig implements DynamicMBean {

    @Override
    public Object getAttribute(String attribute) throws AttributeNotFoundException, MBeanException, ReflectionException {
        String attributeValue = null;
        switch (attribute) {
            case "username":
                attributeValue = getUsername();
                break;
            default:
                throw new AttributeNotFoundException(attribute);
        }
        return attributeValue;
    }

    @Override
    public void setAttribute(Attribute attribute) throws AttributeNotFoundException, InvalidAttributeValueException, MBeanException, ReflectionException {
        switch (attribute.getName()) {
            case "username":
                setUsername((String) attribute.getValue());
                break;
            default:
                throw new AttributeNotFoundException(attribute.getName());
        }
    }

    @Override
    public AttributeList getAttributes(String[] attributes) {
        return null;
    }

    @Override
    public AttributeList setAttributes(AttributeList attributes) {
        return null;
    }

    @Override
    public Object invoke(String actionName, Object[] params, String[] signature) throws MBeanException, ReflectionException {
        Object returnValue = null;
        switch (actionName) {
            case "toJSONString":
                returnValue = toJSONString();
                break;
            default:
                throw new RuntimeOperationsException(new IllegalArgumentException(), actionName);
        }
        return returnValue;
    }

    @Override
    public MBeanInfo getMBeanInfo() {
        return new MBeanInfo(getClass().getName(), "No Desc",
                of(mBeanAttributeInfo("username", String.class, true, true)),
                of(),
                of(mBeanOperationInfo("toJSONString", String.class)),
                of());
    }

    public static <T> T[] of(T... values) {
        return values;
    }

    private MBeanOperationInfo mBeanOperationInfo(String methodName, Class returnType) {
        return new MBeanOperationInfo(methodName, methodName, of(), returnType.getName(), MBeanOperationInfo.ACTION);
    }

    private MBeanAttributeInfo mBeanAttributeInfo(String name, Class type, boolean isReadable, boolean isWritable) {
        return new MBeanAttributeInfo(name, type.getName(), name, isReadable, isWritable, !(isReadable || isWritable));
    }
}
