package org.uts.business.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * @Description 批量添加商品 VO类
 * @Author codBoy
 * @Date 2024/7/15 21:04
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BatchAddProductVo {

    //商品VO列表
    @NotEmpty
    private List<ProductVo> productVoList;

}
