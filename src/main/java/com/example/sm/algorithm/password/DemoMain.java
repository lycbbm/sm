package com.example.sm.algorithm.password;

import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.util.encoders.Base64;
import org.bouncycastle.util.encoders.Hex;

import java.io.IOException;
import java.math.BigInteger;

/**
 * 
 * @ClassName: DemoMain 
 * @Description: TODO(国密SM2签名验签/SM3报文摘要) 
 * @date 2019年5月10日
 */
public class DemoMain {
	// 国密规范测试用户ID
	private static final String userId = "rzx";
	// 国密规范测试私钥
	private static final String prik = "211E7686A0BCAFF554F0FD366F428E40A2C4FC1AB6E7E7F74E8696494B723AA2";
	//国密规范测试公钥
	private static final String pubk = "0402B1B28FB8C84696417DD172FBA233DEB751D871772BDE2A48E7964CDFE468DC02197EB57F20FDF341860B9A2006E76377AC2507D454BC217D3F22D611284D15";
	
	public static void main(String[] arg) {
//		createKey();
//		String msg = "123456789";//原始数据
		String msg = "https://www.baidu.com/search?q=maven&oq=&aqs=chrome.1.69i59l4.16990716j0j7&sourceid=chrome&ie=UTF-8#123456789009876543211234567#ct01MIIBjAIgAi7gCwCDlliJrlFISpr624lH56fCsSZOvYT90Ie+sgcCICJ7ZbUlhilhQbm6UBhmYIPC/kjQD3++qIZDpJxtKehSBCD1rY1TR43q87j8/pJwdx12J4lFDsoUeaeCTKg8hELu6QSCASLjd93raIxdK0qYTb1WKRJ8V5Og+OmUZqQXJpxMcv4rr7+sg6eu5HJZAIlGiKRhFZM811aL0QNKtH4jzI5rt208FcgquqiWjcC2x+WTPHVYqXptjb75TMb0dfQlfqII3lhYkxzNzKPjxMtT24OcfwDdTAbiQcmCLhR8H2uqm86L4ylWnamq8vEZqrHMAklLs1F+ApjJNLNjPFgAClOeSvnJaSO0GcH0DGcOzdibBY1EHMJPwC7sI3FDipH2+1YVCGCmtxrS0I9wFF0SDJbO3Esfw03oATodgYl9RaFLOGW98+mYEmw2XpUCQjGL7Ti/M5gU+/h0M4mEmiJ65BLIRdfIGbozpZhh4njQX/500PEcp3dW7o96dUGQkF09WdtEyrHlag==1600247123120202";
		System.out.println("原始数据：" + msg);
		String summaryString = summary(msg);
		System.out.println("摘要：" + summaryString);
		String signString = sign(summaryString, prik);
//		System.out.println("摘要签名："+signString);
		boolean status = verify(summaryString,signString);
		System.out.println("验签结果：" + status);

//		System.out.println("加密: ");
//		byte[] cipherText = null;
//		try {
//			cipherText = SM2Utils.encrypt(Base64.decode(new String(Base64.encode(Util.hexToByte(pubk))).getBytes()), msg.getBytes());
//		} catch (IllegalArgumentException e1) {
//			// TODO 自动生成的 catch 块
//			e1.printStackTrace();
//		} catch (IOException e1) {
//			// TODO 自动生成的 catch 块
//			e1.printStackTrace();
//		}
//		System.out.println(new String(Base64.encode(cipherText)));
//		System.out.println("");
//
//		System.out.println("解密: ");
//		String res = null;
//		try {
//			res = new String(SM2Utils.decrypt(Base64.decode(new String(Base64.encode(Util.hexToByte(prik))).getBytes()), cipherText));
//		} catch (IllegalArgumentException e) {
//			// TODO 自动生成的 catch 块
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO 自动生成的 catch 块
//			e.printStackTrace();
//		}
//		System.out.println(res);
		
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
	  * 签名
	 * @return
	 */
	public static String sign(String summaryString, String privateKey) {
		String prikS = new String(Base64.encode(Util.hexToByte(privateKey)));
		System.out.println("prikS: " + prikS);
		System.out.println("");
		
		System.out.println("ID: " + Util.getHexString(userId.getBytes()));
		System.out.println("");
		System.out.println("签名: ");
		byte[] sign = null; //摘要签名
		try {
			sign = SM2Utils.sign(userId.getBytes(), Base64.decode(prikS.getBytes()), Util.hexToByte(summaryString));
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
	public static boolean verify(String summary,String sign) {
		String pubkS = new String(Base64.encode(Util.hexToByte(pubk)));
		System.out.println("pubkS: " + pubkS);
		System.out.println("");
		
		System.out.println("验签 ");
		boolean vs = false; //验签结果
		try {
			vs = SM2Utils.verifySign(userId.getBytes(), Base64.decode(pubkS.getBytes()), Util.hexToByte(summary), Util.hexToByte(sign));
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return vs;
	}
	
	/**
	 * 生成随机密钥对
	 */
	public static void createKey() {
		SM2 sm2 = SM2.Instance();
        AsymmetricCipherKeyPair key = sm2.ecc_key_pair_generator.generateKeyPair();
        ECPrivateKeyParameters ecpriv = (ECPrivateKeyParameters) key.getPrivate();
        ECPublicKeyParameters ecpub = (ECPublicKeyParameters) key.getPublic();
        BigInteger privateKey = ecpriv.getD();
        ECPoint publicKey = ecpub.getQ();
//
        System.out.println("公钥: " + new String(publicKey.getEncoded()));
        System.out.println("私钥: " + Util.byteToHex(privateKey.toByteArray()));
//		System.out.println("-----");
//		System.out.println("公钥: " + Util.byteToHex(publicKey.getEncoded()));
//		System.out.println("私钥: " + Util.byteToHex(privateKey.toByteArray()));
//		System.out.println("------");
//		System.out.println("公钥: " + Util.byteToHex(publicKey.getEncoded()));
//		System.out.println("私钥: " + Util.byteToHex(privateKey.toByteArray()));
		System.out.println(pubk.length());
	}
	
}
