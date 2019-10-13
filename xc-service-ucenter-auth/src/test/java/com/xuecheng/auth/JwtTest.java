package com.xuecheng.auth;

import com.alibaba.fastjson.JSON;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.jwt.Jwt;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.security.jwt.crypto.sign.RsaSigner;
import org.springframework.security.jwt.crypto.sign.RsaVerifier;
import org.springframework.security.oauth2.provider.token.store.KeyStoreKeyFactory;
import org.springframework.test.context.junit4.SpringRunner;

import java.security.KeyPair;
import java.security.interfaces.RSAPrivateKey;
import java.util.HashMap;
import java.util.Map;

/**
 * @program: XCEdu->JwtTest
 * @description:
 * @author: Bangser
 * @create: 2019-08-31 19:52
 **/
@SpringBootTest
@RunWith(SpringRunner.class)
public class JwtTest {

    //生成jwt令牌
    @Test
    public void testCreatJwt() {
        //证书文件
        String key_location = "xc.keystore";
        //密钥库密码
        String keystore_password = "xuechengkeystore";
        //密钥别名
        String alias = "xckey";
        //密钥的密码，此密码和别名要匹配
        String keypassword = "xuecheng";
        //证书文件路径
        ClassPathResource resource = new ClassPathResource(key_location);
        //秘钥工厂
        KeyStoreKeyFactory factory = new KeyStoreKeyFactory(resource,keystore_password.toCharArray());
        //获取密钥对（包含公钥和私钥）
        KeyPair keyPair = factory.getKeyPair(alias, keypassword.toCharArray());
        //获取私钥
        RSAPrivateKey rsaPrivateKey = (RSAPrivateKey) keyPair.getPrivate();
        //以RSA算法对私钥签名
        RsaSigner signer = new RsaSigner(rsaPrivateKey);
        //自定义body信息
        Map<String,String> body = new HashMap<>();
        body.put("test","Jwt令牌");
        String bodyString = JSON.toJSONString(body);
        //生成jwt令牌
        Jwt jwt = JwtHelper.encode(bodyString, signer);
        //获取令牌码
        String token = jwt.getEncoded();
        System.out.println(token);//eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJ0ZXN0IjoiSnd05Luk54mMIn0.f2DbVCdEJtFhpu_2ekPguVJCA-poRGbiQ_MWf6JsMXF1hwuziXqBaTzNR2zDrs4zgpGay1D191TWuGFWn30bw9Q38f9jExXL1JIqp1Ap8ihDYbaAjv9AXQ4vbPsxF4ZcpyEBpU88z0h6EA4UsSg3NdnbhftRMmxti7byr7xqnvpGSkhSQ-fMfo6KVypgiMDGpUdPOQ0gJCq6Z9eoXGWaEI84AtaNRlbeP8Em9dMBbOCKWylsyTGAqeWwsYE3AqVTPNF1gKHByo3CP6QbFiKmRN1YoCaDULJlPcDqtOnF5zXTGpLf7YqjdqycwM01jLTD21avOzZ7R8aj1XutjeXEfQ
    }

    //验证令牌
    @Test
    public void testVerifyJwt() {
        //公钥
        String publicKey = "-----BEGIN PUBLIC KEY-----MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAnASXh9oSvLRLxk901HANYM6KcYMzX8vFPnH/To2R+SrUVw1O9rEX6m1+rIaMzrEKPm12qPjVq3HMXDbRdUaJEXsB7NgGrAhepYAdJnYMizdltLdGsbfyjITUCOvzZ/QgM1M4INPMD+Ce859xse06jnOkCUzinZmasxrmgNV3Db1GtpyHIiGVUY0lSO1Frr9m5dpemylaT0BV3UwTQWVW9ljm6yR3dBncOdDENumT5tGbaDVyClV0FEB1XdSKd7VjiDCDbUAUbDTG1fm3K9sx7kO1uMGElbXLgMfboJ963HEJcU01km7BmFntqI5liyKheX+HBUCD4zbYNPw236U+7QIDAQAB-----END PUBLIC KEY-----";
        //令牌
        String token = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJjb21wYW55SWQiOiIxIiwidXNlcnBpYyI6bnVsbCwidXNlcl9uYW1lIjoiaXRjYXN0Iiwic2NvcGUiOlsiYXBwIl0sIm5hbWUiOiJ0ZXN0MDIiLCJ1dHlwZSI6bnVsbCwiaWQiOiI0OSIsImV4cCI6MTU2NzYxNzI3NywiYXV0aG9yaXRpZXMiOlsieGNfdGVhY2htYW5hZ2VyX2NvdXJzZV9iYXNlIiwieGNfdGVhY2htYW5hZ2VyX2NvdXJzZV9kZWwiLCJ4Y190ZWFjaG1hbmFnZXJfY291cnNlX2xpc3QiLCJ4Y190ZWFjaG1hbmFnZXJfY291cnNlX3BsYW4iLCJ4Y190ZWFjaG1hbmFnZXJfY291cnNlIiwiY291cnNlX2ZpbmRfbGlzdCIsInhjX3RlYWNobWFuYWdlciIsInhjX3RlYWNobWFuYWdlcl9jb3Vyc2VfbWFya2V0IiwieGNfdGVhY2htYW5hZ2VyX2NvdXJzZV9wdWJsaXNoIiwieGNfdGVhY2htYW5hZ2VyX2NvdXJzZV9hZGQiXSwianRpIjoiOGY3MTUwM2EtYTEyYy00NTBmLWI2ODAtNDNkNzFlZDA0NDU4IiwiY2xpZW50X2lkIjoiWGNXZWJBcHAifQ.Ce7HjqGAOMIB9vCQtuCFO0H9Fraxb4C5OOxJZR4Fq8nzhUWm6POqdpPgA8lr-dMNQiuNS5A3kYz58iqf9kPZYxDzH_RQgUChyUXvIXqT5C_10mIzltIQfJVHSGIHiG0qw0jf0GQDU1Zu54y5km8cW3NNc9KBkmjR50WyL--qVWQZCBNLE2N6kI_bn6s6gBjccatPTxQLKuPtGGpb8N6IC1SXVr0Z42eeSztMQa_qEnZ5AwSIsytJmuxgP2dEF2y7o1njdkchSIyid6mjrkCWX6jldTe_jy4F_-NZNymWm46DLgFXFKpc7nKaCpzXpDD234VrpFpl3uFB4ESg70t_SA";
        //验证令牌
        Jwt jwt = JwtHelper.decodeAndVerify(token, new RsaVerifier(publicKey));
        //获取自定义body信息
        String claims = jwt.getClaims();
        System.out.println(claims);
    }
}
