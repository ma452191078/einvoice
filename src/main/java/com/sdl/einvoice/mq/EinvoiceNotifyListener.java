package com.sdl.einvoice.mq;

import com.sap.conn.jco.JCoException;
import com.sdl.einvoice.config.SapConfig;
import com.sdl.einvoice.util.SAPUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.List;

/**
 * 消息监听器
 * @author majingyuan
 * @date Create in 2017/12/1 15:37
 */
@Slf4j
public class EinvoiceNotifyListener implements MessageListenerConcurrently {

    @Autowired
    private SapConfig sapConfig;
    /**
     * 消息到达时对消息进行处理
     * @param list  消息内容
     * @param consumeConcurrentlyContext 成功消息
     * @return
     */
    @Override
    public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> list, ConsumeConcurrentlyContext consumeConcurrentlyContext) {

        ConsumeConcurrentlyStatus consumeMsg = ConsumeConcurrentlyStatus.RECONSUME_LATER;
        for (MessageExt message : list) {

            String msgBody = new String(message.getBody());
            System.out.println("msg data from rocketMQ:" + msgBody);

            SAPUtil sapUtil = new SAPUtil(sapConfig);
            //function名称
            String functionName = "Z_SDL_RH_NOTIFY";
            HashMap<String, String> importParam = new HashMap<>();
            HashMap<String, Object> exportParam;
            HashMap<String,Object> returnParam = new HashMap<>();

            importParam.put("IJSON", msgBody);
            returnParam.put("OFLAG","");
            returnParam.put("OMSG","");

            // 调用RFC
            try {
                exportParam = sapUtil.executeSapFun(functionName,importParam,null,returnParam);
                if ("0".equals(exportParam.get("OFLAG"))){
                    log.info("sap执行成功，数据已接收");
                    consumeMsg = ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
                }else {
                    log.info("sap执行失败，{}",exportParam.get("OMSG"));
                }
            } catch (JCoException e) {
                log.info("sap执行失败，{}",e.getMessage());
                e.printStackTrace();
            }
        }

        return consumeMsg;
    }
}
