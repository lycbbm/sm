package com.example.sm.bean;

import lombok.Data;

/**
 * @description:
 * @author: bbm
 * @Date: 2020/9/14 2:22 下午
 */
@Data
public class CityCode {
    /**
     * 引导信息
     */
    private String idxInf;

    /**
     * 体系标识
     */
    private  String tagCode;

    /**
     * 分类标识
     */
    private String tagType;

    /**
     * 来源标识
     */
    private String tagSource;

    /**
     * 唯一编号
     */
    private String tagPuid;

    /**
     * 渠道标识
     */
    private  String tagAppid;

    /**
     * 处理标识
     */
    private  String tagBuss;

    /**
     * 域段长度 1
     */
    private  Integer tagLen;

    /**
     * 扩展字段
     */
    private  String extData;

    /**
     * 扩展字段长度
     */
    private byte extLen;

    /**
     * 生码时间
     */
    private Long generateCodeTime;

    /**
     * 码体的有效时间
     */
    private Long codeEffictiveTime;

    /**
     * 终端公钥证书
     */
    private String  terminalPubEncrypKey;

    /**
     * 证书失效时间
     */
    private  Long keyExpiredTime;

    /**
     * 公钥证书签名
     */
    private String keySign;

    /**
     * 码体数据签名
     */
    private String codeSing;

    /**
     * 码体版本标识
     */
    private Integer codeVersion;
}
