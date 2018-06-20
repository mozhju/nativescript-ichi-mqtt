package com.aliyun.alink.linksdk.channel.core.base;

/**请求返回错误类
 */

public class AError {
    public static final String ERR_DOMAIN_NAME_ALINK = "alinkErrorDomain";
    
    /**
     * 业务成功
     */
    public static final int AKErrorSuccess = 0; ///< 业务成功
    /**
     *  客户端内部错误
     */
    //public static final int AKErrorIllegalArgument = 4200; ///< 客户端内部错误
    public static final int AKErrorUnknownError = 4201;

    /**网络错误
     *
     */
    public static final int AKErrorInvokeNetError = 4101;
    /**
     * 业务网关错误
     */
    public static final int AKErrorInvokeServerError = 4102;
    /**
     * 业务错误
     */
    public static final int AKErrorServerBusinessError = 4103;

    /**登录 token失效  , session过期
     *
     */
    public static final int AKErrorLoginTokenIllegalError = 4001;

    private String domain;
    private int code;
    private String msg;
    private String subDomain;
    private int subCode;
    private String subMsg;
    private Object originResponseObj;

    public AError() {
        domain = ERR_DOMAIN_NAME_ALINK;
    }

    /** 设置 Domain
     * @param domain 错误域,默认为 alinkErrorDomain
     */
    public void setDomain(String domain) {
        this.domain = domain;
    }

    /** 设置 Code
     * @param code 错误码
     */
    public void setCode(int code) {
        this.code = code;
    }

    /** 设置 Message
     * @param msg
     */
    public void setMsg(String msg) {
        this.msg = msg;
    }

    /** 设置 SubDomain
     * @param subDomain
     */
    public void setSubDomain(String subDomain) {
        this.subDomain = subDomain;
    }

    /** 设置 SubCode
     * @param subCode
     */
    public void setSubCode(int subCode) {
        this.subCode = subCode;
    }

    /** 设置Sub Message
     * @param subMsg
     */
    public void setSubMsg(String subMsg) {
        this.subMsg = subMsg;
    }

    /** 获取 Domain
     * @return 错误域,默认为 alinkErrorDomain
     */
    public String getDomain() {
        return domain;
    }

    /**返回错误码
     * @return 错误码
     */
    public int getCode() {
        return code;
    }

    /**返回错误描述
     * @return 错误描述
     */
    public String getMsg() {
        return msg;
    }

    /** 获取 Sub Domain
     * @return
     */
    public String getSubDomain() {
        return subDomain;
    }

    /**返回子错误码
     * @return 子错误码
     */
    public int getSubCode() {
        return subCode;
    }

    /**返回子错误描述
     * @return 子错误描述
     */
    public String getSubMsg() {
        return subMsg;
    }

    /** 获取原始响应对象
     * @return
     */
    public Object getOriginResponseObject() {
        return originResponseObj;
    }

    /** 设置原始响应对象
     * @param originResponseObj
     */
    public void setOriginResponseObject(Object originResponseObj) {
        this.originResponseObj = originResponseObj;
    }
}
