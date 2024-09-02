package org.uts.business.service.product;

import org.uts.business.domain.vo.ProductVo;
import org.uts.exception.BusinessException;
import org.uts.vo.order.OrderVo;
import org.uts.vo.order.RefundVo;

/**
 * @Description 秒杀商品 服务类
 * @Author codBoy
 * @Date 2024/7/14 18:58
 */
public interface SecKillService {
    /*
     * 秒杀接口
     */
    public void secKill(ProductVo productVo) throws BusinessException;

    /*
     * 付款接口
     */
    public String pay(OrderVo orderVo) throws BusinessException;

    /*
     * 退款接口
     */
    public boolean refund(RefundVo refundVo) throws BusinessException;





    /*
     * 订单创建异常，回滚操作
     */
    public void failedRollback(ProductVo productVo, boolean isUpdateStock);

    /*
     * 订单创建异常，回滚操作
     */
    public void removeSoldOver(Long userId);
}
