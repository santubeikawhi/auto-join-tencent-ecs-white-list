package com.tencent.sample;

import com.aliyun.tea.utils.StringUtils;
import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.common.profile.ClientProfile;
import com.tencentcloudapi.common.profile.HttpProfile;
import com.tencentcloudapi.common.exception.TencentCloudSDKException;
import com.tencentcloudapi.lighthouse.v20200324.LighthouseClient;
import com.tencentcloudapi.lighthouse.v20200324.models.*;
import org.apache.http.HttpEntity;
import org.apache.http.ParseException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import java.io.IOException;


public class AutoJoinECSWhiteList {

    public static final String GET_IP_URL = "http://icanhazip.com,http://ident.me,http://ifconfig.me,http://ipecho.net/plain,http://whatismyip.akamai.com,http://myip.dnsomatic.com";
    
    public static LighthouseClient client(InParam inParam){
        // 实例化一个认证对象，入参需要传入腾讯云账户secretId，secretKey,此处还需注意密钥对的保密
        // 密钥可前往https://console.cloud.tencent.com/cam/capi网站进行获取
        Credential cred = new Credential(inParam.getSecretId(), inParam.getSecretKey());
        // 实例化一个http选项，可选的，没有特殊需求可以跳过
        HttpProfile httpProfile = new HttpProfile();
        httpProfile.setEndpoint(inParam.getEndpoint());
        // 实例化一个client选项，可选的，没有特殊需求可以跳过
        ClientProfile clientProfile = new ClientProfile();
        clientProfile.setHttpProfile(httpProfile);
        // 实例化要请求产品的client对象,clientProfile是可选的
        LighthouseClient client = new LighthouseClient(cred, inParam.getRegion(), clientProfile);
        return client;
    } 


    public static void main(String [] args) {
        String ip = getCurrentIp();
        if(StringUtils.isEmpty(ip)){
            System.out.println("未获取到IP");
            System.exit(1);
        }
        InParam inParam = new InParam(args);
        inParam.setCidrBlock(ip);
        LighthouseClient client = client(inParam);
        getWhiteList(client,inParam);
    }

    /**
     * 获取本机IP
     * @return
     */
    public static String getCurrentIp() {
        //循环遍历获取IP的url，查询本机IP
        String[] getIpUrls = GET_IP_URL.split(",");
        String ip = "";
        for(String getIpUrl : getIpUrls){
            ip = getIp(getIpUrl);
            if(!StringUtils.isEmpty(ip)){
                return ip;
            }
        }
        return null;
    }


    /**
     * 获取本地IP
     * @param url
     * @return
     */
    public static String getIp(String url) {
        String ip = null;
        // 获得Http客户端(可以理解为:你得先有一个浏览器;注意:实际上HttpClient与浏览器是不一样的)
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        // 创建Get请求
        HttpGet httpGet = new HttpGet(url);
        // 响应模型
        CloseableHttpResponse response = null;
        try {
            // 由客户端执行(发送)Get请求
            response = httpClient.execute(httpGet);
            // 从响应模型中获取响应实体
            HttpEntity responseEntity = response.getEntity();
            System.out.println("getIp响应状态为:" + response.getStatusLine());
            if (responseEntity != null) {
                System.out.println("getIp响应内容长度为:" + responseEntity.getContentLength());
                ip = EntityUtils.toString(responseEntity).replace("\n","");
                System.out.println("getIp响应内容为:" + ip);
            }
        } catch (ParseException | IOException e) {
            e.printStackTrace();
            System.exit(1);
        } finally {
            try {
                // 释放资源
                if (httpClient != null) {
                    httpClient.close();
                }
                if (response != null) {
                    response.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
                System.exit(1);
            }
        }
        return ip;
    }

    public static void getWhiteList(LighthouseClient client,InParam inParam){
        try{
            // 实例化一个请求对象,每个接口都会对应一个request对象
            DescribeFirewallRulesRequest req = new DescribeFirewallRulesRequest();
            req.setInstanceId(inParam.getInstanceId());
            // 返回的resp是一个DescribeFirewallRulesResponse的实例，与请求对象对应
            DescribeFirewallRulesResponse resp = client.DescribeFirewallRules(req);
            System.out.println("----------getWhiteList------------");
            // 输出json格式的字符串回包
            System.out.println(DescribeFirewallRulesResponse.toJsonString(resp));
            System.out.println("----------end getWhiteList------------");
            
            FirewallRuleInfo[] firewallRuleSet = resp.getFirewallRuleSet();
            for (FirewallRuleInfo firewallRule : firewallRuleSet) {
                if(inParam.getFirewallRuleDescription().equals(firewallRule.getFirewallRuleDescription())){
                    //找到相同规则
                    if(inParam.getCidrBlock().equals(firewallRule.getCidrBlock())){ 
                        System.out.println("无需添加");
                        System.exit(0);
                    }
                    //删除过期规则
                    deleteWhiteList(client, inParam,firewallRule);
                    break;
                }
            }
            //添加规则
            joinWhiteList(client, inParam);
        } catch (TencentCloudSDKException e) {
            System.out.println(e.toString());
            System.exit(1);
        }
    }
    
    public static void deleteWhiteList(LighthouseClient client,InParam inParam,FirewallRuleInfo firewallRule){
        try{
            DeleteFirewallRulesRequest req = new DeleteFirewallRulesRequest();
            req.setInstanceId(inParam.getInstanceId());

            FirewallRule[] firewallRules1 = new FirewallRule[1];
            FirewallRule firewallRule1 = new FirewallRule();
            firewallRule1.setProtocol(firewallRule.getProtocol());
            firewallRule1.setPort(firewallRule.getPort());
            firewallRule1.setCidrBlock(firewallRule.getCidrBlock());
            firewallRule1.setAction(firewallRule.getAction());
            firewallRule1.setFirewallRuleDescription(firewallRule.getFirewallRuleDescription());
            firewallRules1[0] = firewallRule1;

            req.setFirewallRules(firewallRules1);

            // 返回的resp是一个DeleteFirewallRulesResponse的实例，与请求对象对应
            DeleteFirewallRulesResponse resp = client.DeleteFirewallRules(req);

            System.out.println("----------deleteWhiteList------------");
            // 输出json格式的字符串回包
            System.out.println(DeleteFirewallRulesResponse.toJsonString(resp));
        } catch (TencentCloudSDKException e) {
            System.out.println(e.toString());
            System.exit(1);
        } finally {

            System.out.println("----------deleteWhiteList------------");
        }
    }
    
    public static void joinWhiteList(LighthouseClient client,InParam inParam){

        System.out.println("----------joinWhiteList------------");
        try{
            // 实例化一个请求对象,每个接口都会对应一个request对象
            CreateFirewallRulesRequest req = new CreateFirewallRulesRequest();
            req.setInstanceId(inParam.getInstanceId());

            FirewallRule[] firewallRules1 = new FirewallRule[1];
            FirewallRule firewallRule1 = new FirewallRule();
            firewallRule1.setProtocol(inParam.getProtocol());
            firewallRule1.setPort(inParam.getPort());
            firewallRule1.setCidrBlock(inParam.getCidrBlock());
            firewallRule1.setAction(inParam.getAction());
            firewallRule1.setFirewallRuleDescription(inParam.getFirewallRuleDescription());
            firewallRules1[0] = firewallRule1;

            req.setFirewallRules(firewallRules1);

            // 返回的resp是一个CreateFirewallRulesResponse的实例，与请求对象对应
            CreateFirewallRulesResponse resp = client.CreateFirewallRules(req);
            // 输出json格式的字符串回包
            System.out.println(CreateFirewallRulesResponse.toJsonString(resp));
            System.exit(0);
        } catch (TencentCloudSDKException e) {
            System.out.println(e.toString());
            System.exit(1);
        }finally {

            System.out.println("----------end joinWhiteList------------");
        }
    }
}
