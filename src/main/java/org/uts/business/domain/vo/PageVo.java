package org.uts.business.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description 分页VO 类
 * @Author codBoy
 * @Date 2024/7/14 20:26
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageVo {

    //页大小,默认为500
    private Integer pageSize = 500;

    //页码,默认为第0页
    private Integer pageNum = 0;

    //排序方式,默认为降序排序，即最新的数据在最前面
    private String sort = "DESC";
}
