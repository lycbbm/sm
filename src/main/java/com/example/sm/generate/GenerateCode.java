package com.example.sm.generate;

import com.example.sm.algorithm.password.SM2Utils;
import com.example.sm.algorithm.password.SM3Digest;
import com.example.sm.algorithm.password.Util;
import com.example.sm.utils.GenereateKey;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ConcurrentModificationException;


import com.google.common.annotations.VisibleForTesting;
import org.apache.logging.log4j.util.Base64Util;
import org.bouncycastle.asn1.icao.DataGroupHash;
import org.bouncycastle.util.encoders.Base64;
import org.bouncycastle.util.encoders.Hex;
import com.google.common.primitives.Ints;
import sun.misc.Version;

import javax.sound.midi.Soundbank;
import javax.swing.*;
import javax.swing.plaf.basic.BasicHTML;
import javax.xml.crypto.Data;

/**
 * @description:
 * @author: bbm
 * @Date: 2020/9/14 1:59 下午
 */
public class GenerateCode {
  /**
   * 引导信息
   */
//  private String idxInf = "https://www.baidu.com/search?q=maven&oq=&aqs=chrome.1.69i59l4.16990716j0j7&sourceid=chrome&ie=UTF-8#123456789009876543211234567";
  private String idxInf = "";
  /**
   * 体系标识
   */
  private  String tagCode = "ct";

  /**
   * 分类标识
   */
  private String tagType ="01";

  /**
   * 来源标识
   */
  private String tagSource = "bj01";

  /**
   * 唯一编号
   */
  private String tagPuid = "ymt811177f0e6c3a26";

  /**
   * 渠道标识
   */
  private  String tagAppid = "bjmj";

  /**
   * 处理标识
   */
  private  byte tagBuss =  1;

  /**
   * 域段长度 1
   */
  private  byte tagLen = (byte)32;

  /**
   * 扩展字段
   */
  private  String extData = "";
//    private  String extData = "313232323234364139333645364134333439343935383743444342303844344443434138364342433534383241443043313631364135303132363037313134463132323232343641393336453641343334393439353837434443423038443444434341383643424335343832414430433136313641353031323630373131344";

  /**
   * 扩展字段长度
   */
  private byte extLen = 127;

  /**
   * 生码时间
   */
  private Integer generateCodeTime = 1600247123;

  /**
   * 码体的有效时间
   */
  private short codeEffictiveTime = (short)60*60;

  /**
   * 证书失效时间
   */
  private Integer keyExpiredTime = 1600254362;

  /**
   * 码体版本标识
   */
  private String codeVersion = "1";

  /**
   * 获取加密ctl
   * @return
   */
  private String getCt1() throws IOException {
    // 来源标识 + 唯一编号 + 渠道标识 + 处理标识 + 域段长度 + 扩展数据 + 域段长度
    String data = tagSource + tagPuid + tagAppid;
    System.out.println(data.length());
    System.out.println(data);
    // 处理标识
    byte [] tagByte = new byte[]{tagBuss};
    byte [] mergeTagBuss = Util.byteMerger(data.getBytes(), tagByte);
    System.out.println("1:" + mergeTagBuss.length);

    // 域段长度
    byte [] tagLenByte = new byte[]{tagLen};
    byte [] mergeTagLen = Util.byteMerger(mergeTagBuss, tagLenByte);
    System.out.println("2:" + mergeTagLen.length);

    // 扩展字段
    byte [] extLenByte = new byte[]{extLen};
    byte [] mergeExtLen =  Util.byteMerger(extData.getBytes(),extLenByte);
    System.out.println("3:" + mergeExtLen.length);

    byte[] sourceByte = Util.byteMerger(mergeTagLen, mergeExtLen);
    System.out.println("source data ct1 length:" + sourceByte.length);
    System.out.println("4:" + sourceByte.length);

    //使用平台加密公钥19 加密码串4-10
    String cipherText = new String(Base64.encode(SM2Utils.encrypt(Base64.decode(new String(Base64.encode(Util.hexToByte(GenereateKey.PLATFORM_PUB_ENCRYP))).getBytes()), sourceByte)));
    System.out.println("encrypt data ct1 length:" + cipherText.length());
    System.out.println("encrypt data ct1:" + cipherText);
    return cipherText;

  }

  /**
   * 使用终端私钥证书18 对 1+2 +3 + ct1+ 11 + 12 + 13+14+15+17 = 码体签名数据16
   * @return
   */
  private String getCodeBodySign() throws IOException {
     // 终端公钥证书 13 + 证书失效时间 14 = 数字签名公钥证书 15
     String pubKeySign = getPubKyeSign();

     // 明文码体数据16 = 引导信息1 + 体系标识2 + 分类标识3 + ct1 + 码体生成时间11 + 码体有效标识12 + 终端公钥证书13 + 证书的失效时间14 + 公钥证书签名15 +
     //  码体版本标识17
     byte version = 1;
     // ct1 前的字节数组
     String codeData = idxInf + tagCode + tagType + getCt1();
    System.out.println("code body 1:" + codeData.length());
     byte [] mergeCodeTime = Util.byteMerger(codeData.getBytes(), Util.intToBytes(generateCodeTime));
    System.out.println("code body 2:" + mergeCodeTime.length);
     byte [] mergeEfficTime = Util.byteMerger(mergeCodeTime, Util.shortToByte(codeEffictiveTime));
    System.out.println("code body 3:" + mergeEfficTime.length);
     byte [] mergePubkey = Util.byteMerger(mergeEfficTime, Util.hexStringToBytes(GenereateKey.TERMINAL_PUB_ENCRYP));
    System.out.println("code body 4:" + mergePubkey.length);
     byte [] mergeExpiredTime  = Util.byteMerger(mergePubkey, Util.intToBytes(keyExpiredTime));
    System.out.println("code body 5:" + mergeExpiredTime.length);
     byte [] mergeKeySign  = Util.byteMerger(mergeExpiredTime, Util.hexStringToBytes(pubKeySign));
    System.out.println("code body 6:" + mergeKeySign.length);
     byte [] bodySign  = Util.byteMerger(mergeKeySign, codeVersion.getBytes());
     System.out.println("code body source length:" + bodySign.length);
     String codeDataSign = getTerminalSignByte(bodySign, GenereateKey.PLATFORM_PRI_ENCRYP);
    System.out.println("code body encrypt sign:" + codeDataSign);
    System.out.println("code body encrypt sign length:" + codeDataSign.length());
    return codeDataSign;
  }

  /**
   * 使用终端私钥证书18 对 1+2 +3 + ct1+ 11 + 12 + 13+14+15+17 = 码体签名数据16
   * @return
   */
  private String getCodeBodySign2() throws IOException {
    // 终端公钥证书 13 + 证书失效时间 14 = 数字签名公钥证书 15
    String pubKeySign = getPubKyeSign();

    // 明文码体数据16 = 引导信息1 + 体系标识2 + 分类标识3 + ct1 + 码体生成时间11 + 码体有效标识12 + 终端公钥证书13 + 证书的失效时间14 + 公钥证书签名15 +
    //  码体版本标识17
    byte version = 1;
    // ct1 前的字节数组
    String codeData = idxInf + tagCode + tagType + getCt1();
    System.out.println("code body 1:" + codeData.length());
    byte [] mergeCodeTime = Util.byteMerger(codeData.getBytes(), Util.intToBytes(generateCodeTime));
    System.out.println("code body 2:" + mergeCodeTime.length);
    byte [] mergeEfficTime = Util.byteMerger(mergeCodeTime, Util.shortToByte(codeEffictiveTime));
//    System.out.println("code body 3:" + mergeEfficTime.length);
//    byte [] mergePubkey = Util.byteMerger(mergeEfficTime, Util.hexStringToBytes(GenereateKey.TERMINAL_PUB_ENCRYP));
//    System.out.println("code body 4:" + mergePubkey.length);
//    byte [] mergeExpiredTime  = Util.byteMerger(mergePubkey, Util.intToBytes(keyExpiredTime));
//    System.out.println("code body 5:" + mergeExpiredTime.length);
//    byte [] mergeKeySign  = Util.byteMerger(mergeExpiredTime, Util.hexStringToBytes(pubKeySign));
//    System.out.println("code body 6:" + mergeKeySign.length);
    byte [] bodySign  = Util.byteMerger(mergeEfficTime, codeVersion.getBytes());
//    byte [] bodySign = Util.byteMerger(versionSign, Util.hexStringToBytes(getCodeBodySign2()));
    System.out.println("code body source length:" + bodySign.length);
    String codeDataSign = getTerminalSignByte(bodySign, GenereateKey.PLATFORM_PRI_ENCRYP);
    System.out.println("code body encrypt sign:" + codeDataSign);
    System.out.println("code body encrypt sign length:" + codeDataSign.length());
    return codeDataSign;
  }

  /**
   * 获取加密ct2
   * @return 加密后的ct2
   * @throws IOException
   */
  private String getCt2() throws IOException {
    //  ct1 + 码体生成时间11 + 码体有效标识12 + 终端公钥证书13 + 证书的失效时间14 + 公钥签名15 + 码体数据签名16
    byte [] mergeCodeTime = Util.byteMerger(getCt1().getBytes(), Util.intToBytes(generateCodeTime));
    System.out.println("get ct2 1:" + mergeCodeTime.length);
    byte [] mergeEfficTime = Util.byteMerger(mergeCodeTime, Util.shortToByte(codeEffictiveTime));
    System.out.println("get ct2 2:" + mergeEfficTime.length);
    byte [] mergePubkey = Util.byteMerger(mergeEfficTime, Util.hexStringToBytes(GenereateKey.TERMINAL_PUB_ENCRYP));
    System.out.println("get ct2 3:" + mergePubkey.length);
    byte [] mergeExpiredTime  = Util.byteMerger(mergePubkey, Util.intToBytes(keyExpiredTime));
    System.out.println("get ct2 4:" + mergeExpiredTime.length);
    byte [] mergeKeySign  = Util.byteMerger(mergeExpiredTime, Util.hexStringToBytes(getPubKyeSign()));
    System.out.println("get ct2 5:" + mergeKeySign.length);
    byte [] mergeCodeBodySign  = Util.byteMerger(mergeKeySign, Util.hexStringToBytes(getCodeBodySign()));
    System.out.println("get ct2 6:" + mergeCodeBodySign.length);

    System.out.println("ct2 source length:" + mergeCodeBodySign.length);
    String cipherText = new String(Base64.encode(SM2Utils.encrypt(Base64.decode(new String(Base64.encode(Util.hexToByte(GenereateKey.PLATFORM_PUB_ENCRYP))).getBytes()), mergeCodeBodySign)));
    System.out.println("ct2 encrypt length:" + cipherText.length());
    System.out.println("ct2 encrypt data:" + cipherText);
    return cipherText;
  }

  private String getCodeContent() throws IOException {
    // 明文码体数据16 = 引导信息1 + 体系标识2 + 分类标识3 + ct2 + 码体版本标识17
    String ct2 = getCt2();
    System.out.println("***getCodeContent ct2 1:" + ct2);
    System.out.println("****getCodeContent ct2 2:" + ct2.length());
    String codeData = idxInf + tagCode + tagType + getCt2() + codeVersion;
    System.out.println("end codeData:" + codeData);
    System.out.println("end codeData length:" + codeData.length());

    return codeData;
  }

  private String getCodeContent2() throws IOException {
    // 明文码体数据16 = 引导信息1 + 体系标识2 + 分类标识3 + ct1 + 码体版本标识17 + 码体签名
    String ct1 = getCt1();
    System.out.println("***getCodeContent ct2 1:" + ct1);
    System.out.println("****getCodeContent ct2 2:" + ct1.length());
    String codeData = idxInf + tagCode + tagType + getCt1() ;
//    System.out.println("88888888" + codeData);
//    System.out.println("getCodeContent 1:" + codeData.length());
////            codeData = codeData + codeVersion ;
//    byte [] mergeCodeTime = Util.byteMerger(codeData.getBytes(), Util.intToBytes(generateCodeTime));
//    System.out.println("get getCodeContent2 1:" + mergeCodeTime.length);
//    byte [] mergeEfficTime = Util.byteMerger(mergeCodeTime, Util.shortToByte(codeEffictiveTime));
//    System.out.println("get getCodeContent2 2:" + mergeEfficTime.length);
//
//    byte [] version = Util.byteMerger(mergeEfficTime, new byte[] {codeVersion});
//    byte [] code = Util.byteMerger(version, Util.hexToByte(getCodeBodySign2()));
//    System.out.println("*******mergeEfficTime length 3:" + code.length);
////    String endData = Util.byteToString(code);
//    String endData = new String(Base64.encode(code));
//    String co = Util.byteToString("abcd".getBytes());
//    String endData = new String((code));
    String endData = codeData + generateCodeTime + codeEffictiveTime + codeVersion + getCodeBodySign2();
    System.out.println("end codeData:" + endData);
    System.out.println("end codeData length:" + endData.length());
    String summary = summary(endData);

    return summary;
  }

  /**
   * 摘要
   * @return
   */
  public static String summary(String msg) {
    //1.摘要
    byte[] md = new byte[32];
    SM3Digest sm = new SM3Digest();
    sm.update(msg.getBytes(), 0, msg.getBytes().length);
    sm.doFinal(md, 0);
    String s = new String(Hex.encode(md));
    return s.toUpperCase();
  }

  /**
   * 使用平台签名私钥证书22  终端公钥证书 13 + 证书失效时间 14 = 数字签名公钥证书 15
   * @return
   */
  private String getPubKyeSign() throws IOException {
    byte [] pubKeyByte = Util.hexStringToBytes(GenereateKey.TERMINAL_PUB_ENCRYP);
    byte [] expiredByte = Util.intToBytes(keyExpiredTime);
    System.out.println("pubKeyByte length:" + pubKeyByte.length);
    System.out.println("expiredByte length:" + expiredByte.length);

    // 终端公钥证书 13 + 证书失效时间 14 字节数组
    byte [] signByte = Util.byteMerger(pubKeyByte, expiredByte);
    String s = new String(signByte);
    //byte[] pubKey = Util.byteMerger(GenereateKey.TERMINAL_PUB_ENCRYP.getBytes(), signByte);
    System.out.println("terminal sign sourcee data length:" + signByte.length);
    String pubKeySign = getTerminalSignByte(signByte, GenereateKey.TERMINAL_PRI_ENCRYP);
    System.out.println("terminal sign data:" + pubKeySign);
    System.out.println("terminal sign data length:" + pubKeySign.length());
    return pubKeySign;
  }


  /**
   * 获取签名15
   * @param data 签名数据
   * @param key 私钥
   * @return 签名串
   * @throws IOException 异常
   */
  private String getTerminalSign(String data,  String key) throws IOException {
    //1.摘要
    byte[] md = new byte[32];
    SM3Digest sm = new SM3Digest();
    sm.update(data.getBytes(), 0, data.getBytes().length);
    sm.doFinal(md, 0);
    String design = new String(Hex.encode(md)).toUpperCase();
    System.out.println("design length:" + design.length());
    byte[] sign = SM2Utils.sign("rst".getBytes(), Util.hexToByte(key), design.getBytes());

    return Util.getHexString(sign);
  }

  private String getTerminalSignByte(byte[] data,  String key) throws IOException {
    //1.摘要
    byte[] md = new byte[32];
    SM3Digest sm = new SM3Digest();
    sm.update(data, 0, data.length);
    sm.doFinal(md, 0);
    String design = new String(Hex.encode(md)).toUpperCase();
//    System.out.println("design length:" + design.length());
    byte[] sign = SM2Utils.sign("rst".getBytes(), Util.hexToByte(key), design.getBytes());
    return Util.getHexString(sign);
  }


  public static void main(String[] args) throws IOException {
    GenerateCode generateCode = new GenerateCode();
    // 验证ct1
//    generateCode.getCt1();

    // 验证公钥15
//    generateCode.getPubKyeSign();

    // 验证16
//    generateCode.getCodeBodySign();

    // 验证ct2
//    generateCode.getCt2();

    // 验证 码体内容
    generateCode.getCodeContent();
//

     generateCode.getCodeContent2();


  }


  public static void test() {
    System.out.println("bbm test");
    int i = 10;
  }

}
