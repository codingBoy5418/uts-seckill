package org.uts.business.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @Description 秒杀商品分页 VO类
 * @Author codBoy
 * @Date 2024/7/14 19:58
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SecKillProductPageVo extends PageVo {

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

    //场次
    private Integer time;

    //秒杀开始时间
    private Date startTime;

    //秒杀结束时间
    private Date endTime;

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
}
