package org.uts.valid;

import javax.validation.groups.Default;

/**
 * 新增的分组校验接口
 * */

/**
 * @Validated(value = ValidGroup.Crud.Update.class)指定了具体的分组，上面提到的是否继承Default的区别在于：
 * 如果继承了Default，@Validated标注的注解也会校验未指定分组或者Default分组的参数，比如email
 * 如果不继承Default则不会校验未指定分组的参数，需要加上@Validated(value = {ValidGroup.Crud.Update.class, Default.class}才会校验
 * 参考：https://zhuanlan.zhihu.com/p/528230341
 * */
public interface Add extends Default {
}
