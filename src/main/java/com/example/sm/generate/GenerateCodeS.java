package com.example.sm.generate;

import com.example.sm.algorithm.password.SM2Utils;
import com.example.sm.algorithm.password.SM3;
import com.example.sm.algorithm.password.SM3Digest;
import com.example.sm.algorithm.password.Util;
import com.example.sm.utils.GenereateKey;
import org.bouncycastle.util.encoders.Base64;
import org.bouncycastle.util.encoders.Hex;

import javax.sound.midi.Soundbank;
import java.io.IOException;

/**
 * @description:
 * @author: bbm
 * @Date: 2020/9/14 1:59 下午
 */
public class GenerateCodeS {
  /**
   * 引导信息
   */
  private String idxInf = "https://www.baidu.com/search?q=maven&oq=&aqs=chrome.1.69i59l4.16990716j0j7&sourceid=chrome&ie=UTF-8#123456789009876543211234567#";
//  private String idxInf = "";
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
  private  String  tagBuss =  "001";

  /**
   * 域段长度 1
   */
  private  String tagLen = "032";

  /**
   * 扩展字段
   */
//  private  String extData = "";
  private  String extData = "313232323234364139333645364134333439343935383743444342303844344443434138364342433534383241443043313631364135303132363037313134463132323232343641393336453641343334393439353837434443423038443444434341383643424335343832414430433136313641353031323630373131344";

  /**
   * 扩展字段长度
   */
  private String extLen = "127";

  /**
   * 生码时间
   */
  private String generateCodeTime = "1600247123";

  /**
   * 码体的有效时间
   */
  private String codeEffictiveTime = "120";

  /**
   * 证书失效时间
   */
  private String keyExpiredTime = "1600254362";

  /**
   * 码体版本标识
   */
  private String codeVersion = "202";

  public  static  final String userId = "rzx";

  /**
   * 获取加密ctl
   * @return
   */
  private String getCt1() throws IOException {
    // 来源标识 + 唯一编号 + 渠道标识 + 处理标识 + 域段长度 + 扩展数据 + 域段长度
    String data = tagSource + tagPuid + tagAppid + tagBuss + tagLen + extData + extLen;
//    String data = tagSource + tagPuid + tagAppid + tagBuss  + extData + ;
    //使用平台加密公钥19 加密码串4-10
    System.out.println("encrpt before length:" + data.length());
    String cipherText = new String(Base64.encode(SM2Utils.encrypt(Base64.decode(new String(Base64.encode(Util.hexToByte(GenereateKey.PLATFORM_PUB_ENCRYP))).getBytes()), data.getBytes())));
    System.out.println("encrypt data ct1 length:" + cipherText.length());
    System.out.println("encrypt data ct1:" + cipherText);
    return cipherText;

  }

  /**
   * 使用终端私钥证书18 对 1+2 +3 + ct1+ 11 + 12 + 13+14+15+17 = 码体签名数据16
   * @return
   */
  private String getCodeBodySign(String ct1) throws IOException {
     // 明文码体数据16 = 引导信息1 + 体系标识2 + 分类标识3 + ct1 + 码体生成时间11 + 码体有效标识12 + 码体版本标识17
     byte version = 1;
     // ct1 前的字节数组
//     String codeData = idxInf + tagCode + tagType + ct1 + generateCodeTime + codeEffictiveTime + codeVersion;
//    System.out.println("66666sign data:" + codeData);
     String design = summary(ct1);
     System.out.println("&&&&&&:" + design);
     String codeDataSign = sign(design, GenereateKey.PLATFORM_PRI_ENCRYP);
    System.out.println("code body encrypt sign:" + codeDataSign);
    System.out.println("code body encrypt sign length:" + codeDataSign.length());
//    String summary = summary(codeDataSign);
//    System.out.println("summary:" + summary.length());

    return codeDataSign;
  }

  private String getCodeContent2(String ct1) throws IOException {
    // 明文码体数据16 = 引导信息1 + 体系标识2 + 分类标识3 + ct1 + 码体版本标识17 + 码体签名

    String codeData = idxInf + tagCode + tagType + ct1 + generateCodeTime + codeEffictiveTime + codeVersion + getCodeBodySign(ct1);
    System.out.println("end codeData:" + codeData);
    System.out.println("end codeData length:" + codeData.length());
    return codeData;
  }


  private String getTerminalSignByte(byte[] data,  String key) throws IOException {
    byte[] sign = SM2Utils.sign(userId.getBytes(), Util.hexToByte(key), data);
    return Util.getHexString(sign);
  }

  /**
   * 摘要
   * @return
   */
  public  String summary(String msg) {
    //1.摘要
    byte[] md = new byte[32];
    SM3Digest sm = new SM3Digest();
    sm.update(msg.getBytes(), 0, msg.getBytes().length);
    sm.doFinal(md, 0);
    String s = new String(Hex.encode(md));
    return s.toUpperCase();
  }

  public void descrpy() throws IOException {
    String ct1 = getCt1();
    String codeContents = getCodeContent2(ct1);
    String codeContent = codeContents;
    String sign = codeContent.substring(codeContent.length() - 140, codeContent.length());
    codeContent = codeContent.substring(0, codeContent.length() - 140);
    System.out.println("****sign:" + sign);


    String version = codeContent.substring(codeContent.length()-3, codeContent.length());
    codeContent = codeContent.substring(0, codeContent.length() - 3);
    System.out.println("*****version" + version);

//
    String effctiveTime = codeContent.substring(codeContent.length()-3,codeContent.length() );
    System.out.println("*****effctiveTime" + effctiveTime);
    codeContent = codeContent.substring(0, codeContent.length() - 3);


    String generateTime = codeContent.substring(codeContent.length() - 10,codeContent.length());
    System.out.println("*****generateTime" + generateTime);
    codeContent = codeContent.substring(0, codeContent.length() - 10);


    String idx = codeContent.substring(0,codeContent.lastIndexOf("#") + 1);
    System.out.println("idx:" + idx);
    codeContent = codeContent.substring(idx.length(), codeContent.length());

//
    String tagCode = codeContent.substring(0, 2);
    System.out.println("*****tagCode" + tagCode);
    codeContent = codeContent.substring(2,codeContent.length());

    String tagType = codeContent.substring(0, 2);
    System.out.println("*****tagType" + tagType);
    codeContent = codeContent.substring(2,codeContent.length());
    System.out.println("****" +codeContent);

    String decrpt = new String(SM2Utils.decrypt(Base64.decode(new String(Base64.encode(Util.hexToByte(GenereateKey.PLATFORM_PRI_ENCRYP))).getBytes()), Base64.decode(getCt1())));
    System.out.println(decrpt);

    String designData = idxInf + tagCode + tagType + ct1 + generateCodeTime + codeEffictiveTime + codeVersion;
    System.out.println("*******verify sum:" + designData);
    String sum = summary(designData);
    System.out.println("*******sum:" + sum);
    boolean verify = verify(sum, sign, GenereateKey.PLATFORM_PUB_ENCRYP);
    System.out.println(verify);

  }

  /**
   * 签名
   * @return
   */
  public static String sign(String summaryString, String privateKey) {
    byte[] sign = null;
    try {
      sign = SM2Utils.sign(userId.getBytes(), Util.hexToByte(privateKey), Util.hexToByte(summaryString));
    } catch (IllegalArgumentException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return Util.getHexString(sign);
  }

  /**
   * 验签
   * @return
   */
  public static boolean verify(String summary,String sign, String pubk) {
    boolean vs = false;
    try {
      vs = SM2Utils.verifySign(userId.getBytes(), Util.hexToByte(pubk),Util.hexToByte(summary), Util.hexToByte(sign));
    } catch (IllegalArgumentException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return vs;
  }




  public static void main(String[] args) throws IOException {
    GenerateCodeS generateCode = new GenerateCodeS();

    // 验证ct1
//    generateCode.getCt1();
    generateCode.getCodeBodySign("abc22334343");


    // 验证 码体内容
//    generateCode.getCodeContent2(generateCode.getCt1());
//    generateCode.getCodeBodySign();

//     generateCode.descrpy();


  }




}
