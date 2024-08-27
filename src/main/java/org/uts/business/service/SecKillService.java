package org.uts.business.service;

import org.uts.business.domain.dto.ProductDto;
import org.uts.business.domain.vo.BatchAddProductVo;
import org.uts.business.domain.vo.SecKillProductPageVo;
import org.uts.business.domain.vo.ProductVo;
import org.uts.exception.BusinessException;

import java.util.List;

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
    public String pay(BatchAddProductVo batchAddProductVo);

    /*
     * 退款接口
     */
    public String refund(String id);





    /*
     * 订单创建异常，回滚操作
     */
    public void failedRollback(ProductVo productVo, boolean isUpdateStock);

    /*
     * 订单创建异常，回滚操作
     */
    public void removeSoldOver(Long userId);
}
