package org.uts.global.errorCode;

/**
 * 错误码枚举类
 * 错误码命名规范：应用标识+功能域+错误类型+错误编码
 * 	 错误码位数：8位
 * 	 错误码示例：I102P001
 * 	 使用规范：只增不改，避免混乱
 * 	     应用标识(2位字母和数字)
 * 	 		AXXX平台：A1
 * 	 		AXXX平台：A2
 * 	  		VXXX平台：V1
 * 	  		ZXXX平台：Z1
 * 	  	功能域(2位数字)
 * 	 		未分类：00
 * 	  		X1相关：01
 * 	  		X2相关：02
 * 	  		X3相关：03
 * 	  		……
 * 	  	错误类型(1位字母)
 * 	  		参数错误：P
 * 	  		业务错误：B
 * 	  		系统错误：S
 * 	  		网络错误：N
 * 	  		数据库错误：D
 * 	  		缓存错误：C
 * 	 		RPC错误：R
 * 	  		文件IO错误：F
 * 	  		其他错误：O
 * 	  	   错误编码(3位数字)
 * 	  		自增即可
 * */

public enum ErrorCode {
    /* ********************************** 系统异常 ************************************/
    SYSTEM_ERROR("500", "系统异常"),
    COMMON_UNKNOWN_ERROR("999999", "未知异常"),
    COMMON_ILLEGAL_ARGUMENT("000001", "参数不合法"),
    COMMON_DB_OPT_ERROR("000002", "数据库操作异常"),
    COMMON_NULL_POINT("000003", "空指针异常"),
    COMMON_IO_ERROR("000004", "读写异常"),
    COMMON_ARITHMETIC_ERROR("000005", "数学运算异常"),
    COMMON_OUT_OF_BOUNDS("000006", "数组越界异常"),
    COMMON_CLASS_CAST_ERROR("000007", "类型转换错误"),
    COMMON_ILLEGAL_SECURITY("000008", "违背安全原则异常"),
    COMMON_SQL_ERROR("000009", "SQL相关错误"),
    COMMON_UNKNOWN_RUNTIME_ERROR("000010", "未知的运行时错误"),
    COMMON_ERROR_ARGUMENT_VALID("000011", "参数校验不通过"),
    COMMON_TYPE_MISS_MATCH("000012", "参数类型不匹配"),
    COMMON_TYPE_MISS_PARAMETER("000013", "参数缺失"),

    /* ********************************** 业务异常 ************************************/
    //商品信息
    BUSINESS_GOODS_NOT_EXITS("010001", "商品不存在"),

    BUSINESS_GOODS_SALE_OVER("010002", "商品已卖完"),

    ;

    //错误码
    private String code;

    //错误消息
    private String errorMsg;

    ErrorCode(String code, String errorMsg) {
        this.code = code;
        this.errorMsg = errorMsg;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }
}
