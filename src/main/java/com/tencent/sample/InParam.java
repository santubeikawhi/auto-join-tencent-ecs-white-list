package com.tencent.sample;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class InParam {

    /**
     * SecretId
     */
    private String secretId;

    /**
     * SecretKey
     */
    private String secretKey;

    /**
     * EndPoint
     */
    private String endpoint;

    /**
     * 地域参数，建议您阅读文档了解地域以及计费情况
     */
    private String region;

    /**
     * 实例 ID
     */
    private String instanceId;


    /**
     * 协议，取值：TCP，UDP，ICMP，ALL。
     */
    @SerializedName("Protocol")
    @Expose
    private String protocol;

    /**
     * 网段或 IP (互斥)。默认为 0.0.0.0/0，表示所有来源。 不从main args获取
     */
    private String cidrBlock;
    
    /**
     * 端口，取值：ALL，单独的端口，逗号分隔的离散端口，减号分隔的端口范围。
     */
    @SerializedName("Port")
    @Expose
    private String port;

    /**
     * 取值：ACCEPT，DROP。默认为 ACCEPT。
     */
    @SerializedName("Action")
    @Expose
    private String action;

    /**
     * 防火墙规则描述。
     */
    @SerializedName("FirewallRuleDescription")
    @Expose
    private String firewallRuleDescription;

    public InParam() {
    }

    public InParam(String[] args) {
        this.secretId = args[0];
        this.secretKey = args[1];
        this.endpoint = args[2];
        this.region = args[3];
        this.instanceId = args[4];
        this.protocol = args[5];
        this.port =args[6];
        this.action = args[7];
        this.firewallRuleDescription = args[8];
    }

    public String getSecretId() {
        return secretId;
    }

    public void setSecretId(String secretId) {
        this.secretId = secretId;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getFirewallRuleDescription() {
        return firewallRuleDescription;
    }

    public void setFirewallRuleDescription(String firewallRuleDescription) {
        this.firewallRuleDescription = firewallRuleDescription;
    }

    public String getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(String instanceId) {
        this.instanceId = instanceId;
    }

    public String getCidrBlock() {
        return cidrBlock;
    }

    public void setCidrBlock(String cidrBlock) {
        this.cidrBlock = cidrBlock;
    }
}
