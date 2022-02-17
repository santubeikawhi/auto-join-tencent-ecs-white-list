# auto-join-tencent-ecs-white-list

# 功能描述
- 1.获取本机在局域网的IP
- 2.查询现有的安全组的入方向规则
- 3.遍历查询匹配是否和入参的FirewallRuleDescription和port相同的入方向规则
- 4.如果存在则删除该规则（旧规则已经过时）
- 5.根据当前pc在局域网的IP生成新的入方向规则，添加到安全组中

# 在官方文档上做了一些小修改
- 获取本地连接的IP
- bat脚本（暂时没做shell）
--java -jar tencentEcsAutoJoinWhitlist.jar secretId secretKey endpoint region instanceId protocol port action firewallRuleDescription

#加入tencent的maven 仓库
- 	<profile>
	  <id>public-tencent</id> 
	  <repositories>
		<repository>
		  <id>public-tencent</id> 
		  <url>https://mirrors.tencent.com/nexus/repository/maven-public/</url> 
		  <releases>
			<enabled>true</enabled>
		  </releases> 
		  <snapshots>
			<enabled>true</enabled> 
			<updatePolicy>always</updatePolicy>
		  </snapshots>
		</repository>
	  </repositories>
	</profile>
  
# 添加maven依赖
-         <dependency>
            <groupId>com.tencentcloudapi</groupId>
            <artifactId>tencentcloud-sdk-java</artifactId>
            <!-- go to https://search.maven.org/search?q=tencentcloud-sdk-java and get the latest version. -->
            <!-- 请到https://search.maven.org/search?q=tencentcloud-sdk-java查询所有版本，最新版本如下 -->
            <version>3.1.449</version>
        </dependency>
