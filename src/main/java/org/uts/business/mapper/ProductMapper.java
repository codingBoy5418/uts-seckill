package org.uts.business.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.uts.business.domain.dto.ProductDto;
import org.uts.business.domain.vo.SecKillProductPageVo;

import java.util.List;

/**
 * @Description 秒杀商品 Mapper类
 * @Author codBoy
 * @Date 2024/7/14 20:03
 */
@Mapper
public interface ProductMapper {

    /*
     * 查询秒杀商品列表信息 - 分页
     */
    public List<ProductDto> selectProduct(SecKillProductPageVo productPageVo);

    /*
     * 查询秒杀商品信息 - 详情
     */
    public ProductDto selectById(Long seckillId);

    /*
      查询当天的秒杀商品信息
     */
    public List<ProductDto> selectCurDayProduct();

    /*
      更新商品库存
     */
    public Integer updateStock(Long secKillId, Integer count);
}
