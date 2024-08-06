package org.uts.business.service;

import org.uts.business.domain.dto.ProductDto;
import org.uts.business.domain.vo.BatchAddProductVo;
import org.uts.business.domain.vo.SecKillProductPageVo;
import org.uts.business.domain.vo.ProductVo;

import java.util.List;

/**
 * @Description 秒杀商品 服务类
 * @Author codBoy
 * @Date 2024/7/14 18:58
 */
public interface SecKillProductService {

    /*
     * 查询秒杀商品列表信息 - 分页
     */
    List<ProductVo> secKillProductPage(SecKillProductPageVo productPageVo);

    /*
     * 查询秒杀商品信息 - 详情
     */
    public ProductVo selectById(Long id);

    /*
     * 秒杀接口
     */
    public Long secKill(ProductVo productVo);

    /*
     * 付款接口
     */
    public String pay(BatchAddProductVo batchAddProductVo);

    /*
     * 退款接口
     */
    public String refund(String id);



    /*
      查询当天的秒杀商品信息
     */
    public List<ProductDto> selectCurDayProduct();
}
