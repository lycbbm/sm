package com.example.sm.generate;

import com.example.sm.algorithm.password.SM2Utils;
import com.example.sm.algorithm.password.Util;

import java.io.IOException;

/**
 * @description:
 * @author: bbm
 * @Date: 2020/9/24 7:23 下午
 */
public class Test {
    /**
     * 验签
     * @return
     */
    public static boolean verify(String summary,String sign, String pubk) {
        boolean vs = false;
        try {
            vs = SM2Utils.verifySign("bbm test".getBytes(), Util.hexToByte(pubk),Util.hexToByte(summary), Util.hexToByte(sign));
            System.out.println("bbm st");
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return vs;
    }
}
