package com.datax.test.two;
import com.alibaba.datax.common.exception.DataXException;
import com.alibaba.datax.common.exception.ExceptionTracker;
import com.alibaba.datax.core.Engine;
import groovy.util.logging.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//测试失败
public class dataxTest {
    public static void main( String[] args ){
        /**
        * classPath 为刚才打包的目录 bin的上一级目录 我的dataX源码目录为E:\study\code\DataX
        * jobFilePath:要放在job目录中和job.json同级
        *  System.setProperty("datax.home", classPath); 必须要设置datax.home这个属性不然会找不到执行的入口
        */
        String classPath ="D:\\DataX\\datax";

        String jobFilePath="D:\\DataX\\datax\\job\\job.json";

        System.setProperty("datax.home", classPath);
        String[] dataXArgs = {"-job", jobFilePath, "-mode", "standalone", "-jobid", "-1"};
        try {
            Engine.entry(dataXArgs);

        }catch (DataXException e){
            e.printStackTrace();
        }
        catch (Throwable throwable) {
            /**
             * 拿到异常信息入库.
             */
            System.out.println("\n\n经DataX智能分析,该任务最可能的错误原因::\n" + ExceptionTracker.trace(throwable));

        }
    }
}
