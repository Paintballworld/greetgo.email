package kz.greetgo.gbatis.modelreader;

import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import kz.greetgo.gbatis.model.FutureCall;
import kz.greetgo.gbatis.model.Param;
import kz.greetgo.gbatis.model.Request;
import kz.greetgo.gbatis.model.RequestType;
import kz.greetgo.gbatis.model.ResultType;
import kz.greetgo.gbatis.model.WithView;
import kz.greetgo.gbatis.t.Call;
import kz.greetgo.gbatis.t.DefaultXml;
import kz.greetgo.gbatis.t.FromXml;
import kz.greetgo.gbatis.t.MapKey;
import kz.greetgo.gbatis.t.Modi;
import kz.greetgo.gbatis.t.Prm;
import kz.greetgo.gbatis.t.Sele;
import kz.greetgo.gbatis.t.T1;
import kz.greetgo.gbatis.t.T10;
import kz.greetgo.gbatis.t.T11;
import kz.greetgo.gbatis.t.T12;
import kz.greetgo.gbatis.t.T13;
import kz.greetgo.gbatis.t.T14;
import kz.greetgo.gbatis.t.T15;
import kz.greetgo.gbatis.t.T16;
import kz.greetgo.gbatis.t.T17;
import kz.greetgo.gbatis.t.T18;
import kz.greetgo.gbatis.t.T19;
import kz.greetgo.gbatis.t.T2;
import kz.greetgo.gbatis.t.T3;
import kz.greetgo.gbatis.t.T4;
import kz.greetgo.gbatis.t.T5;
import kz.greetgo.gbatis.t.T6;
import kz.greetgo.gbatis.t.T7;
import kz.greetgo.gbatis.t.T8;
import kz.greetgo.gbatis.t.T9;
import kz.greetgo.sqlmanager.gen.Conf;
import kz.greetgo.sqlmanager.model.Field;
import kz.greetgo.sqlmanager.model.Stru;
import kz.greetgo.sqlmanager.model.Table;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * Генератор класса {@link Request}
 * 
 * <p>
 * Предназначет для реализации метода <nobr>{@link #methodToRequest(Method, Stru, Conf)}</nobr>
 * </p>
 * 
 * <p>
 * Содержит основную логику GBatis - именно сюда нужно будет вносить изменения, если будут
 * появляться новые фичи
 * </p>
 * 
 * @author pompei
 */
public class RequestGenerator {
  
  private static class T_dot implements Comparable<T_dot> {
    final int index;
    final WithView withView;
    
    public T_dot(int index, WithView withView) {
      this.index = index;
      this.withView = withView;
    }
    
    @Override
    public int compareTo(T_dot o) {
      return index - o.index;
    }
  }
  
  /**
   * Генерация объекта класса {@link Request} из метода Dao-интерфейса в соответствии с
   * предоставленной структурой DOM и конфигурацией
   * 
   * @param method
   *          метод запроса
   * @param stru
   *          структура DOM
   * @param conf
   *          конфигурация
   * @return сгенерированный запрос
   * @throws Exception
   *           пробрасывается во избежание try/catch-ей
   */
  public static Request methodToRequest(Method method, Stru stru, Conf conf) throws Exception {
    Request ret = new Request();
    
    readAnnotations(ret, method, stru, conf);
    
    readXmls(ret, method, stru, conf);
    
    fillParams(ret, method);
    
    fillResult(ret, method);
    
    if (ret.sql == null) {
      throw new RequestGeneratorException("No sql for " + method);
    }
    if (ret.type == null) {
      throw new RequestGeneratorException("No type for " + method);
    }
    
    return ret;
  }
  
  private static void readAnnotations(Request ret, Method method, Stru stru, Conf conf)
      throws Exception {
    final List<T_dot> tdotList = new ArrayList<>();
    
    ret.type = null;
    
    FOR: for (Annotation ann : method.getAnnotations()) {
      {
        T_dot tdot = readTDot(ann, stru, conf);
        if (tdot != null) {
          tdotList.add(tdot);
          continue FOR;
        }
      }
      
      if (ann instanceof Modi) {
        if (ret.type != null) {
          throw new RequestGeneratorException("request.type = " + ret.type + " but found " + ann);
        }
        ret.type = RequestType.Modi;
        ret.sql = ((Modi)ann).value();
        continue FOR;
      }
      
      if (ann instanceof Sele) {
        if (ret.type != null) {
          throw new RequestGeneratorException("request.type = " + ret.type + " but found " + ann);
        }
        ret.type = RequestType.Sele;
        ret.sql = ((Sele)ann).value();
        continue FOR;
      }
      
      if (ann instanceof Call) {
        if (ret.type != null) {
          throw new RequestGeneratorException("request.type = " + ret.type + " but found " + ann);
        }
        ret.type = RequestType.Call;
        ret.sql = ((Call)ann).value();
        continue FOR;
      }
      
      if (ann instanceof MapKey) {
        ret.mapKeyField = ((MapKey)ann).value();
        continue FOR;
      }
    }
    
    Collections.sort(tdotList);
    for (T_dot x : tdotList) {
      ret.withList.add(x.withView);
    }
    
  }
  
  private static void fillParams(Request ret, Method method) {
    {
      Annotation[][] parAnns = method.getParameterAnnotations();
      Class<?>[] pclasses = method.getParameterTypes();
      if (parAnns.length != pclasses.length) {
        throw new RuntimeException("BAD dsadfsdfdsfd");
      }
      for (int i = 0, C = pclasses.length; i < C; i++) {
        Annotation[] annotations = parAnns[i];
        Class<?> pclass = pclasses[i];
        ret.paramList.add(convertParam(pclass, annotations));
      }
    }
  }
  
  private static Param convertParam(Class<?> pclass, Annotation[] annotations) {
    Param ret = new Param();
    ret.type = pclass;
    
    for (Annotation ann : annotations) {
      if (ann instanceof Prm) {
        ret.name = ((Prm)ann).value();
        break;
      }
    }
    
    return ret;
  }
  
  private static T_dot readTDot(Annotation ann, Stru stru, Conf conf) throws Exception {
    Integer index = getTIndex(ann);
    if (index == null) return null;
    
    String tableName = callMethod(ann, "value");
    String[] fields = callMethod(ann, "fields");
    String name = callMethod(ann, "name");
    
    WithView withView = assembleWithView(tableName, fields, name, stru, conf);
    
    return new T_dot(index, withView);
  }
  
  private static WithView assembleWithView(String tableName, String[] fields, String name,
      Stru stru, Conf conf) {
    List<String> fieldList = new ArrayList<>();
    for (String field : fields) {
      String[] split = field.split(",");
      for (String fld : split) {
        fieldList.add(fld.trim());
      }
    }
    
    if (!tableName.startsWith(conf.tabPrefix)) {
      throw new RequestGeneratorException("Table " + tableName + " is not started with "
          + conf.tabPrefix);
    }
    
    Table table = stru.tables.get(tableName.substring(conf.tabPrefix.length()));
    
    if (table == null) {
      throw new RequestGeneratorException("No table " + tableName);
    }
    
    if (name == null || name.trim().length() == 0) {
      name = tableName;
      name = name.substring(conf.tabPrefix.length());
      name = conf.withPrefix + name;
    }
    
    if (fieldList.size() == 0) {
      for (Field field : table.fields) {
        fieldList.add(field.name);
      }
    } else {
      Set<String> fieldNames = new HashSet<>();
      for (Field field : table.fields) {
        fieldNames.add(field.name);
      }
    }
    
    WithView withView = new WithView();
    withView.fields.addAll(fieldList);
    withView.table = tableName;
    withView.view = name;
    return withView;
  }
  
  private static Integer getTIndex(Annotation ann) {
    if (ann instanceof T1) return 1;
    if (ann instanceof T2) return 2;
    if (ann instanceof T3) return 3;
    if (ann instanceof T4) return 4;
    if (ann instanceof T5) return 5;
    if (ann instanceof T6) return 6;
    if (ann instanceof T7) return 7;
    if (ann instanceof T8) return 8;
    if (ann instanceof T9) return 9;
    if (ann instanceof T10) return 10;
    if (ann instanceof T11) return 11;
    if (ann instanceof T12) return 12;
    if (ann instanceof T13) return 13;
    if (ann instanceof T14) return 14;
    if (ann instanceof T15) return 15;
    if (ann instanceof T16) return 16;
    if (ann instanceof T17) return 17;
    if (ann instanceof T18) return 18;
    if (ann instanceof T19) return 19;
    return null;
  }
  
  @SuppressWarnings("unchecked")
  private static <T> T callMethod(Object object, String methodName) throws Exception {
    return (T)object.getClass().getMethod(methodName).invoke(object);
  }
  
  private static void fillResult(Request ret, Method method) {
    if (ret.type == RequestType.Modi) {
      ret.resultDataClass = method.getReturnType();
      ret.resultType = ResultType.SIMPLE;
      ret.callNow = true;
      return;
    }
    
    if (method.getReturnType().isAssignableFrom(List.class)) {
      if (method.getGenericReturnType() instanceof ParameterizedType) {
        ParameterizedType type = (ParameterizedType)method.getGenericReturnType();
        if (type.getActualTypeArguments().length != 1) {
          throw new RequestGeneratorException(List.class + " has more or less then one type argument");
        }
        ret.resultDataClass = (Class<?>)type.getActualTypeArguments()[0];
        ret.resultType = ResultType.LIST;
        ret.callNow = true;
        return;
      }
      throw new RequestGeneratorException("Result List without type arguments in " + method);
    }
    
    if (method.getReturnType().isAssignableFrom(Set.class)) {
      if (method.getGenericReturnType() instanceof ParameterizedType) {
        ParameterizedType type = (ParameterizedType)method.getGenericReturnType();
        if (type.getActualTypeArguments().length != 1) {
          throw new RequestGeneratorException(List.class + " has more or less then one type argument");
        }
        ret.resultDataClass = (Class<?>)type.getActualTypeArguments()[0];
        ret.resultType = ResultType.SET;
        ret.callNow = true;
        return;
      }
      throw new RequestGeneratorException("Result List without type arguments in " + method);
    }
    
    if (method.getReturnType().isAssignableFrom(Map.class)) {
      if (method.getGenericReturnType() instanceof ParameterizedType) {
        if (ret.mapKeyField == null) {
          throw new RequestGeneratorException("No MapKey in " + method);
        }
        
        ParameterizedType type = (ParameterizedType)method.getGenericReturnType();
        if (type.getActualTypeArguments().length != 2) {
          throw new RequestGeneratorException(List.class + " has more or less then two type argument");
        }
        ret.mapKeyClass = (Class<?>)type.getActualTypeArguments()[0];
        ret.resultDataClass = (Class<?>)type.getActualTypeArguments()[1];
        ret.resultType = ResultType.MAP;
        ret.callNow = true;
        return;
      }
      throw new RequestGeneratorException("Result Map without type arguments in " + method);
    }
    
    if (method.getReturnType().isAssignableFrom(FutureCall.class)) {
      if (method.getGenericReturnType() instanceof ParameterizedType) {
        ParameterizedType type = (ParameterizedType)method.getGenericReturnType();
        if (type.getActualTypeArguments().length != 1) {
          throw new RequestGeneratorException(List.class + " has more or less then one type argument");
        }
        
        Type futureCallArgType = type.getActualTypeArguments()[0];
        
        fillFutureCallResult(ret, futureCallArgType, method);
        return;
      }
      throw new RequestGeneratorException("Result FutureCall without type argument in " + method);
    }
    
    ret.resultDataClass = method.getReturnType();
    ret.resultType = ResultType.SIMPLE;
    ret.callNow = true;
  }
  
  private static void fillFutureCallResult(Request ret, Type futureCallArgType, Method method) {
    
    ret.callNow = false;
    
    if (futureCallArgType instanceof Class) {
      if (((Class<?>)futureCallArgType).isAssignableFrom(Map.class)) {
        throw new RequestGeneratorException("Result FutureCall Map without type arguments in " + method);
      }
      if (((Class<?>)futureCallArgType).isAssignableFrom(List.class)) {
        throw new RequestGeneratorException("Result List in FutureCall without type arguments in "
            + method);
      }
    }
    
    Class<?> futureCallArgClass = null;
    
    if (futureCallArgType instanceof Class) {
      futureCallArgClass = (Class<?>)futureCallArgType;
    } else if (futureCallArgType instanceof ParameterizedType) {
      ParameterizedType ptype = (ParameterizedType)futureCallArgType;
      
      if (ptype.getRawType() instanceof Class) {
        
        futureCallArgClass = (Class<?>)ptype.getRawType();
        
        if (((Class<?>)ptype.getRawType()).isAssignableFrom(Map.class)) {
          if (ret.mapKeyField == null) {
            throw new RequestGeneratorException("No MapKey in " + method);
          }
          if (ptype.getActualTypeArguments().length != 2) {
            throw new RequestGeneratorException(Map.class
                + " has more or less then two type argument in result of " + method);
          }
          
          ret.resultType = ResultType.MAP;
          ret.mapKeyClass = (Class<?>)ptype.getActualTypeArguments()[0];
          ret.resultDataClass = (Class<?>)ptype.getActualTypeArguments()[1];
          return;
        }
        
        if (((Class<?>)ptype.getRawType()).isAssignableFrom(List.class)) {
          if (ptype.getActualTypeArguments().length != 1) {
            throw new RequestGeneratorException(List.class
                + " has more or less then one type argument in result of " + method);
          }
          
          ret.resultType = ResultType.LIST;
          ret.resultDataClass = (Class<?>)ptype.getActualTypeArguments()[0];
          return;
        }
        
        if (((Class<?>)ptype.getRawType()).isAssignableFrom(Set.class)) {
          if (ptype.getActualTypeArguments().length != 1) {
            throw new RequestGeneratorException(List.class
                + " has more or less then one type argument in result of " + method);
          }
          
          ret.resultType = ResultType.SET;
          ret.resultDataClass = (Class<?>)ptype.getActualTypeArguments()[0];
          return;
        }
        
      }
      
    }
    
    if (futureCallArgClass == null) {
      throw new RequestGeneratorException("Cannot prepare result for " + method);
    }
    
    ret.resultType = ResultType.SIMPLE;
    ret.resultDataClass = futureCallArgClass;
  }
  
  private static void readXmls(Request ret, Method method, Stru stru, Conf conf) throws Exception {
    FromXml fromXml = method.getAnnotation(FromXml.class);
    if (fromXml == null) return;
    if (ret.sql != null) throw new ExcessXmlException(
        "Избыточный SQL при определении из xml: SQL = " + ret.sql);
    
    URL xmlURL = getXmlUrl(method);
    Map<String, XmlRequest> xmlRequestMap = urlToXmlRequestMap(xmlURL);
    
    XmlRequest xmlRequest = xmlRequestMap.get(fromXml.value());
    if (xmlRequest == null) throw new NoXmlRequestIdException("No xml request id = "
        + fromXml.value() + " for method " + method);
    
    applyXmlRequest(ret, xmlRequest, stru, conf);
  }
  
  private static URL getXmlUrl(Method method) {
    FromXml fromXml = method.getAnnotation(FromXml.class);
    {
      String relPath = fromXml.xml().trim();
      if (relPath.length() > 0) {
        return method.getDeclaringClass().getResource(relPath);
      }
    }
    
    {
      DefaultXml defaultXml = method.getDeclaringClass().getAnnotation(DefaultXml.class);
      return method.getDeclaringClass().getResource(defaultXml.value());
    }
  }
  
  private static final Map<URL, Map<String, XmlRequest>> xmlRequestMapCache = new HashMap<>();
  
  private static Map<String, XmlRequest> urlToXmlRequestMap(URL xmlURL) throws Exception {
    {
      Map<String, XmlRequest> ret = xmlRequestMapCache.get(xmlURL);
      if (ret != null) return ret;
    }
    
    synchronized (xmlRequestMapCache) {
      {
        Map<String, XmlRequest> ret = xmlRequestMapCache.get(xmlURL);
        if (ret != null) return ret;
      }
      
      {
        Map<String, XmlRequest> ret = createXmlRequestMap(xmlURL);
        xmlRequestMapCache.put(xmlURL, ret);
        return ret;
      }
    }
  }
  
  private static Map<String, XmlRequest> createXmlRequestMap(URL xmlURL) throws Exception {
    final Map<String, XmlRequest> ret = new HashMap<>();
    
    XmlRequestAcceptor acceptor = new XmlRequestAcceptor() {
      @Override
      public void accept(XmlRequest xmlRequest) {
        ret.put(xmlRequest.id, xmlRequest);
      }
    };
    
    runParser(xmlURL, acceptor);
    
    return ret;
  }
  
  private static void runParser(URL xmlURL, XmlRequestAcceptor acceptor) throws SAXException,
      IOException {
    XMLReader reader = XMLReaderFactory.createXMLReader();
    reader.setContentHandler(new XmlRequestSaxHandler(acceptor));
    InputStream inputStream = xmlURL.openStream();
    try {
      reader.parse(new InputSource(inputStream));
    } finally {
      inputStream.close();
    }
  }
  
  private static void applyXmlRequest(Request ret, XmlRequest xmlRequest, Stru stru, Conf conf) {
    ret.sql = xmlRequest.sql;
    ret.type = xmlRequest.type;
    
    for (WithView x : xmlRequest.withViewList) {
      WithView correctedWithView = assembleWithView(x.table, x.fieldsAsArray(), x.view, stru, conf);
      ret.withList.add(correctedWithView);
    }
  }
}