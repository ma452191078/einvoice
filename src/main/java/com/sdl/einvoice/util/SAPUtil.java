package com.sdl.einvoice.util;

import com.sap.conn.jco.*;
import com.sap.conn.jco.ext.DestinationDataProvider;
import com.sdl.einvoice.config.SapConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.io.FileOutputStream;
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


    public static void executeWithTable(String funName, String importName, String exportName, String tableName) throws JCoException
    {
        JCoDestination destination = JCoDestinationManager.getDestination(ABAP_AS_POOLED);
        JCoFunction function = destination.getRepository().getFunction(funName);
        if(function == null)
            throw new RuntimeException(funName + " not found in SAP.");

        try
        {
            function.execute(destination);
        }
        catch(AbapException e)
        {
            System.out.println(e.toString());
            return;
        }

        JCoStructure returnStructure = function.getExportParameterList().getStructure(exportName);
        if (! (returnStructure.getString("TYPE").equals("")||returnStructure.getString("TYPE").equals("S"))  )
        {
            throw new RuntimeException(returnStructure.getString("MESSAGE"));
        }

        JCoTable codes = function.getTableParameterList().getTable("COMPANYCODE_LIST");
        for (int i = 0; i < codes.getNumRows(); i++)
        {
            codes.setRow(i);
            System.out.println(codes.getString("COMP_CODE") + '\t' + codes.getString("COMP_NAME"));
        }

        codes.firstRow();
        for (int i = 0; i < codes.getNumRows(); i++, codes.nextRow())
        {
            function = destination.getRepository().getFunction("BAPI_COMPANYCODE_GETDETAIL");
            if (function == null)
                throw new RuntimeException("BAPI_COMPANYCODE_GETDETAIL not found in SAP.");

            function.getImportParameterList().setValue("COMPANYCODEID", codes.getString("COMP_CODE"));
            function.getExportParameterList().setActive("COMPANYCODE_ADDRESS",false);

            try
            {
                function.execute(destination);
            }
            catch (AbapException e)
            {
                System.out.println(e.toString());
                return;
            }

            returnStructure = function.getExportParameterList().getStructure("RETURN");
            if (! (returnStructure.getString("TYPE").equals("") ||
                    returnStructure.getString("TYPE").equals("S") ||
                    returnStructure.getString("TYPE").equals("W")) )
            {
                throw new RuntimeException(returnStructure.getString("MESSAGE"));
            }

            JCoStructure detail = function.getExportParameterList().getStructure("COMPANYCODE_DETAIL");

            System.out.println(detail.getString("COMP_CODE") + '\t' +
                    detail.getString("COUNTRY") + '\t' +
                    detail.getString("CITY"));
        }//for
    }

    public static void main(String[] args) throws JCoException
    {
        SAPUtil sapUtil = new SAPUtil(null);
        createConnect();
    }
}
