package org.uts.business.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.util.CollectionUtils;
import org.uts.business.domain.dto.ProductDto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @Description 秒杀商品 VO类
 * @Author codBoy
 * @Date 2024/7/14 19:58
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductVo implements Serializable {

    //主键ID
    private Long id;

    //秒杀商品ID
    private Long seckillId;

    //关联商品ID
    private Long goodsId;

    //秒杀商品名称
    private String seckillName;

    //商品原价格
    private Float price;

    //商品秒杀价格
    private Float seckillPrice;

    //秒杀开始时间
    private Date startTime;

    //秒杀结束时间
    private Date endTime;

    //场次
    private Integer time;

    //商品库存
    private Integer stock;

    //秒杀商品状态
    private Integer status;

    //秒杀商品图片地址
    private String imageUrl;

    //秒杀商品简介
    private String desc;

    //创建时间
    private Date createTime;

    //更新时间
    private Date updateTime;

    //用户ID
    private Long userId;

    public ProductVo(Long userId, Long secKillId) {
        this.userId = userId;
        this.seckillId = secKillId;
    }

    /*
      将 商品VO 转换为 商品DTO
     */
    public ProductDto convertToDto() {
        ProductDto productDto = new ProductDto();
        BeanUtils.copyProperties(this, productDto);
        return productDto;
    }

    /*
      将 商品VO列表 转换为 商品DTO列表
     */
    public static List<ProductDto> convertToDto(List<ProductVo> productVoList) {
        List<ProductDto> res = new ArrayList<>();
        if(!CollectionUtils.isEmpty(productVoList)){
            for(ProductVo productVo : productVoList){
                res.add(productVo.convertToDto());
            }
        }
        return res;
    }
}
