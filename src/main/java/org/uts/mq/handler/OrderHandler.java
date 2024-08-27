package org.uts.mq.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.uts.business.domain.vo.ProductVo;
import org.uts.business.service.SecKillService;
import org.uts.global.constant.BusinessConstant;
import org.uts.mq.message.AddOrderResultMessage;
import org.uts.mq.message.RemoveSoldOutMessage;
import org.uts.utils.SnowflakeUtils;

/**
 * @Description 订单MQ消息处理类
 * @Author codBoy
 * @Date 2024/8/24 21:09
 */
@Component
@Slf4j
public class OrderHandler {

    private final SnowflakeUtils snowflakeUtils = new SnowflakeUtils(1, 1);

    @Autowired
    private SecKillService secKillService;

    /*
     处理新增订单消息
     WS适用于服务端和客户端之间的场景。因此发送创建订单结果消息到MQ,由事件中心服务接收MQ消息，然后发送WS消息,由前端接收
     */
    public void dealWithAddOrderResult(AddOrderResultMessage addOrderResultMsg) {
        String success = addOrderResultMsg.getSuccess();
        //订单创建成功，不做处理
        if(BusinessConstant.SUCCESS.equals(success)) {
            return;
        }
        //订单创建失败，进行回滚操作
        ProductVo productVo = new ProductVo(addOrderResultMsg.getUserId(), addOrderResultMsg.getProductId());
        secKillService.failedRollback(productVo, true);
    }

    /*
     处理删除订单售完标记消息
     */
    public void dealWithAddOrderResult(RemoveSoldOutMessage removeSoldOutMessage) {
        log.info("Delete Sold Out Flag From JVM Cache, SecKillId: {}", removeSoldOutMessage.getSeckillId());
        secKillService.removeSoldOver(removeSoldOutMessage.getSeckillId());
    }
}
