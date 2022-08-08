package com.aoya.learn.spring;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Auther: lingfeng.wei
 * @Date: 2022/8/8 09:49
 * @Description:
 */
public class MyApplicationContext {

    private Class configClazz;

    private Map<String, BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<>();

    private Map<String, Object> singletonObjects = new ConcurrentHashMap<>();

    public MyApplicationContext(Class configClazz) {
        this.configClazz = configClazz;
        String path = configClazz.getPackage().getName();
        path = path.replace(".", "/");
        URL resource = configClazz.getClassLoader().getResource(path);

        File file = new File(resource.getFile());
        for (File f : file.listFiles()) {
            if(f.getName().endsWith(".class")){
                String classPath = f.getAbsolutePath().substring(f.getAbsolutePath().indexOf("com"), f.getAbsolutePath().indexOf(".class"));
                classPath = classPath.replace("\\", ".");
                try {
                    Class<?> loadClass = this.configClazz.getClassLoader().loadClass(classPath);
                    if(loadClass.isAnnotationPresent(Component.class)){
                        Component componentAnnoation = loadClass.getAnnotation(Component.class);
                        Scope scope = loadClass.getAnnotation(Scope.class);
                        BeanDefinition beandefinition = new BeanDefinition();
                        beandefinition.setType(loadClass);
                        if(scope.value().equals("prototype")){
                            beandefinition.setScope("prototype");
                        }else{
                            beandefinition.setScope("singleton");
                        }
                        String beanName = componentAnnoation.value();
                        if(beanName.equals("")){
                            beanName = lowerFirstChar(loadClass.getSimpleName());
                        }
                        beanDefinitionMap.put(beanName, beandefinition);
                        singletonObjects.put(beanName, createBean(beanName, beandefinition));
                    }
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    public Object createBean(String beanName, BeanDefinition beanDefinition)  {
        if(singletonObjects.containsKey(beanName)){
            return singletonObjects.get(beanName);
        }
        try {
            Object bean = beanDefinition.getType().getConstructor().newInstance();
            Field[] fields = beanDefinition.getType().getFields();
            for (Field field : fields) {
                if (field.isAnnotationPresent(Autowired.class)) {
                    field.setAccessible(true);
                    String fileName = field.getName();
                    Object o = createBean(fileName, beanDefinitionMap.get(beanName));
                    field.set(bean, o);
                }
            }
            return bean;
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Object getBean(String beanName){
        if(singletonObjects.containsKey(beanName)){
            return singletonObjects.get(beanName);
        }
        if (beanDefinitionMap.containsKey(beanName)) {
            BeanDefinition beanDefinition = beanDefinitionMap.get(beanName);
            if(beanDefinition.getScope().equals("prototype")){
                return createBean(beanName, beanDefinition);
            }else{
                Object o = createBean(beanName, beanDefinition);
                singletonObjects.put(beanName, o);
                return o;
            }
        }
        return null;
    }

    /**
     * 将首字母小写
     *
     * @param str
     * @return
     */
    private static String lowerFirstChar(String str) {
        char[] chars = str.toCharArray();
        chars[0] += 32;
        return String.valueOf(chars);
    }
}
