package com.example.sm.generate;

import java.io.IOException;

/**
 * @description:
 * @author: bbm
 * @Date: 2020/9/23 7:25 下午
 */
public class Test {
    private String getCodeContent() throws IOException {
        // 明文码体数据16 = 引导信息1 + 体系标识2 + 分类标识3 + ct2 + 码体版本标识17
        String ct2 = "getCt2()";
        System.out.println("***getCodeContent ct2 1:" + ct2);
        System.out.println("****getCodeContent ct2 2:" + ct2.length());
        String codeData = "idxInf + tagCode + tagType + getCt2() + codeVersion";

        System.out.println("end codeData:" + codeData);
        System.out.println("end codeData length:" + codeData.length());

        return codeData;
    }

    private String getCodeContent2() throws IOException {
        // 明文码体数据16 = 引导信息1 + 体系标识2 + 分类标识3 + ct1 + 码体版本标识17 + 码体签名
        String ct1 = "getCt1()";
        System.out.println("***getCodeContent ct2 1:" + ct1);
        System.out.println("****getCodeContent ct2 2:" + ct1.length());
        String codeData = "idxInf + tagCode + tagType + getCt1()";
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
        String endData = codeData + "generateCodeTime + codeEffictiveTime + codeVersion + getCodeBodySign2()";
        System.out.println("end codeData:" + endData);
        System.out.println("end codeData length:" + endData.length());
        return "summary";
    }
}
