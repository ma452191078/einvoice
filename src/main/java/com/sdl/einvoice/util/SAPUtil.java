package com.sdl.einvoice.util;

import com.sap.conn.jco.*;
import com.sap.conn.jco.ext.DestinationDataProvider;
import com.sdl.einvoice.config.SapConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

/**
 * SAP连接工具类
 * @Author majingyuan
 * @Date Create in 2017/9/29 10:06
 */
public class SAPUtil {

    static String ABAP_AS_POOLED = "ABAP_AS_WITH_POOL";

    public SAPUtil(SapConfig sapConfig){

        Properties connectProperties = new Properties();
//        connectProperties.setProperty(DestinationDataProvider.JCO_ASHOST, sapConfig.getAshost());
//        connectProperties.setProperty(DestinationDataProvider.JCO_SYSNR,  sapConfig.getSysnr());
//        connectProperties.setProperty(DestinationDataProvider.JCO_CLIENT, sapConfig.getClient());
//        connectProperties.setProperty(DestinationDataProvider.JCO_USER,   sapConfig.getUser());
//        connectProperties.setProperty(DestinationDataProvider.JCO_PASSWD, sapConfig.getPasswd());
//        connectProperties.setProperty(DestinationDataProvider.JCO_LANG,   sapConfig.getLang());

        connectProperties.setProperty(DestinationDataProvider.JCO_ASHOST, "192.168.7.11");
        connectProperties.setProperty(DestinationDataProvider.JCO_SYSNR,  "00");
        connectProperties.setProperty(DestinationDataProvider.JCO_CLIENT, "202");
        connectProperties.setProperty(DestinationDataProvider.JCO_USER,   "majy");
        connectProperties.setProperty(DestinationDataProvider.JCO_PASSWD, "20140254");
        connectProperties.setProperty(DestinationDataProvider.JCO_LANG,   "zh");
        connectProperties.setProperty(DestinationDataProvider.JCO_POOL_CAPACITY, "3");  //最大空连接数
        connectProperties.setProperty(DestinationDataProvider.JCO_PEAK_LIMIT,    "10"); //最大活动连接数
        createDataFile("ABAP_AS_POOLED", "jcoDestination", connectProperties);

    }


    private static void createDataFile(String name, String suffix, Properties properties)
    {
        File cfg = new File(name+"."+suffix);
        if(!cfg.exists())
        {
            try
            {
                FileOutputStream fos = new FileOutputStream(cfg, false);
                properties.store(fos, "for tests only !");
                fos.close();

            }
            catch (Exception e)
            {
                throw new RuntimeException("Unable to create the destination file " + cfg.getName(), e);

            }
        }
    }


    /**
     * 创建sap连接
     * @throws JCoException
     */
    static void createConnect() throws JCoException
    {
//        Properties conPro = createProperties(ABAP_AS_POOLED);

        JCoDestination destination = JCoDestinationManager.getDestination(ABAP_AS_POOLED);
        destination.ping();
        System.out.println("Attributes:");
        System.out.println(destination.getAttributes());
        System.out.println();
    }


    /**
     * 返回Structure
     * @param funName   Function名称
     * @param importList    import参数
     * @param importTableList 传入Table
     * @param returnPara    返回structure结构
     * @return  返回值
     * @throws JCoException
     */
    public HashMap<String, Object> executeSapFun(String funName, HashMap<String, String> importList, HashMap<String, List<HashMap<String, Object>>> importTableList, HashMap<String, Object> returnPara) throws JCoException
    {
        JCoDestination destination = JCoDestinationManager.getDestination(ABAP_AS_POOLED);
        JCoFunction function = destination.getRepository().getFunction(funName);
//        检查function是否存在
        if(function == null)
            throw new RuntimeException(funName + " not found in SAP.");
        JCoParameterList inputParams = null;
        JCoParameterList exportParams = null;

        HashMap<String, Object> result = new HashMap<>();
        try
        {
            //遍历import参数，写入传入参数
            if (importList != null){
                for (String str : importList.keySet()){
                    inputParams.setValue(importList.get(str), str);
                }
            }

            //遍历Table参数
            JCoTable importTable;
            if (importTableList != null){
                for (String str : importTableList.keySet()){
                    importTable = function.getTableParameterList().getTable(str);
                    List<HashMap<String, Object>> li = importTableList.get(str);
                    SetJcoTable(li, importTable); // 设置内表参数
                }
            }

            //执行function
            function.execute(destination);

            //取返回数据
            if (returnPara != null){
                //检查export参数
                for (String str : returnPara.keySet()){
                    exportParams = function.getExportParameterList();
                    result.put(str, exportParams.getString(str));
                }
                //检查Table参数
//                for (String str : returnPara.keySet()){
//                    JCoTable table = function.getTableParameterList().getTable(str);
//                    result.put(str, table);
//                }
            }
        }
        catch(AbapException e)
        {
            System.out.println(e.toString());
        }
        return result;
    }



    // 设置SAP表格参数
    private static void SetJcoTable(List<HashMap<String, Object>> li,
                                    JCoTable importTable) {
        int i = 0;
        for (HashMap<String, Object> u : li) {
            importTable.appendRow();
            importTable.setRow(i);
            for (String t : u.keySet()) {
                importTable.setValue(String.valueOf(u.get(t)), t);
            }
        }
    }


    public static void main(String[] args) throws JCoException
    {
        SAPUtil sapUtil = new SAPUtil(null);
        String functionName = "Z_SDL_RH_NOTIFY";
        HashMap<String, String> importParam = new HashMap<>();
        importParam.put("IJSON", "");

        HashMap<String,Object> returnParam = new HashMap<>();
        returnParam.put("OFLAG","");
        returnParam.put("OMSG","");


        HashMap<String, Object> result = sapUtil.executeSapFun(functionName,importParam,null,returnParam);
        System.out.println(result);
    }
}
