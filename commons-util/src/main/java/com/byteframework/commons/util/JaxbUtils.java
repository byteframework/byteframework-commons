package com.byteframework.commons.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.StringReader;
import java.io.StringWriter;

/**
 * JAXB工具类
 *
 * @author sa
 * @date 2019-08-08
 */
public class JaxbUtils {
    private static Logger logger = LoggerFactory.getLogger(JaxbUtils.class);
    private static JAXBContext jaxbContext;

    //xml转java对象
    @SuppressWarnings("unchecked")
    public static <T> T xmlToBean(String xml, Class<T> c) {
        T t = null;
        try {
            jaxbContext = JAXBContext.newInstance(c);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            t = (T) unmarshaller.unmarshal(new StringReader(xml));
        } catch (JAXBException e) {
            logger.error(e.getMessage(), e);
            e.printStackTrace();
        }
        return t;
    }

    //java对象转xml
    public static String beanToXml(Object obj, Class clazz) {
        StringWriter writer = null;
        try {
            jaxbContext = JAXBContext.newInstance(obj.getClass(), clazz);
            Marshaller marshaller = jaxbContext.createMarshaller();
            //Marshaller.JAXB_FRAGMENT:是否省略xml头信息,true省略，false不省略
            marshaller.setProperty(Marshaller.JAXB_FRAGMENT, true);
            //Marshaller.JAXB_FORMATTED_OUTPUT:决定是否在转换成xml时同时进行格式化（即按标签自动换行，否则即是一行的xml）
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, false);
            //Marshaller.JAXB_ENCODING:xml的编码方式
            marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
            writer = new StringWriter();
            marshaller.marshal(obj, writer);
        } catch (JAXBException e) {
            e.printStackTrace();
            logger.error(e.getMessage(), e);
        }
//        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + writer.toString();
        return writer.toString();
    }
}
