package com.sdl.einvoice.util;

import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoDestinationManager;
import com.sap.conn.jco.JCoException;
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

    @Autowired
    private static SapConfig sapConfig;

    static String ABAP_AS_POOLED = "ABAP_AS_WITH_POOL";

    public static Properties createProperties(String clientName){

        Properties connectProperties = new Properties();
        connectProperties.setProperty(DestinationDataProvider.JCO_ASHOST, sapConfig.getAshost());
        connectProperties.setProperty(DestinationDataProvider.JCO_SYSNR,  sapConfig.getSysnr());
        connectProperties.setProperty(DestinationDataProvider.JCO_CLIENT, sapConfig.getClient());
        connectProperties.setProperty(DestinationDataProvider.JCO_USER,   sapConfig.getUser());
        connectProperties.setProperty(DestinationDataProvider.JCO_PASSWD, sapConfig.getPasswd());
        connectProperties.setProperty(DestinationDataProvider.JCO_LANG,   sapConfig.getLang());
        createDataFile(clientName, "jcoDestination", connectProperties);
        return connectProperties;
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

    @Test
    public static void step1Connect() throws JCoException
    {
        Properties conPro = createProperties(ABAP_AS_POOLED);
        JCoDestination destination = JCoDestinationManager.getDestination(ABAP_AS_POOLED);
        destination.ping();
        System.out.println("Attributes:");
        System.out.println(destination.getAttributes());
        System.out.println();
    }
}
