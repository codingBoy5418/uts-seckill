package org.uts.business.service.product;

import org.uts.business.domain.dto.ProductDto;
import org.uts.business.domain.vo.ProductVo;
import org.uts.business.domain.vo.SecKillProductPageVo;
import java.util.List;

/**
 * @Description 秒杀商品 服务类
 * @Author codBoy
 * @Date 2024/7/14 18:58
 */
public interface ProductService {

    /*
     * 查询秒杀商品列表信息 - 分页
     */
    List<ProductVo> secKillProductPage(SecKillProductPageVo productPageVo);

    /*
     * 查询秒杀商品信息 - 详情
     */
    public ProductVo selectById(Long id);

    /*
     * 扣库存接口
     */
    public int updateStock(long seckillId, int count);

    /*
     * 查询当天的秒杀商品信息
     */
    public List<ProductDto> selectCurDayProduct();
}
