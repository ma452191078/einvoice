package com.sdl.einvoice.util;

import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoDestinationManager;
import com.sap.conn.jco.JCoException;
import com.sdl.einvoice.config.SapConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * SAP连接工具类
 * @Author majingyuan
 * @Date Create in 2017/9/29 10:06
 */
public class SAPUtil {

    @Autowired
    private SapConfig sapConfig;

    public JCoDestination getDestination() throws JCoException
    {
        /**
         * Get instance of JCoDestination from file: ECC.jcodestination
         * which should be located in the installation folder of project
         */

        JCoDestination dest = JCoDestinationManager.getDestination("ECC");
        return dest;
    }


    @Test
    public void pingDestination() throws JCoException {
        JCoDestination dest = this.getDestination();
        dest.ping();
    }
}
