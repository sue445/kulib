# First
This is my private liblary for [Google App Engine](http://code.google.com/intl/en/appengine/) and [Slim3](https://sites.google.com/site/slim3appengine/).

This is used by [jubeat++](http://jubeatplusplus.appspot.com/) and [AZusaar!](http://azusaar.appspot.com/)

# How to use
## pom.xml
    <project>
      <repositories>
        <repository>
          <id>sue445-repo</id>
          <url>http://sue445.github.com/maven/</url>
          <releases>
            <enabled>true</enabled>
            <updatePolicy>always</updatePolicy>
          </releases>
          <snapshots>
            <enabled>true</enabled>
            <updatePolicy>always</updatePolicy>
          </snapshots>
        </repository>
      </repositories>
      <pluginRepositories>
        <pluginRepository>
          <id>sue445-repo</id>
          <url>http://sue445.github.com/maven/</url>
          <releases>
            <enabled>true</enabled>
            <updatePolicy>always</updatePolicy>
          </releases>
          <snapshots>
            <enabled>true</enabled>
            <updatePolicy>always</updatePolicy>
          </snapshots>
        </pluginRepository>
      </pluginRepositories>
      <dependencies>
        <dependency>
          <groupId>net.sue445</groupId>
          <artifactId>kulib</artifactId>
          <version>[0.0,)</version>
        </dependency>
      </dependencies>
    </project>

# Javadoc
http://sue445.github.com/kulib/site/apidocs/index.html

# History
* 0.0.5
 * Issue #1
 * impl AlertMailService#sendMailToAdmins
 * CompressMemcacheUtil: package priavte methods -> public
* [0.0.4](https://github.com/sue445/kulib/commit/5192c5636d08d67e5b193e0f0af113dd2f45edc7)
 * add JsonController
 * fix JavaDoc
* [0.0.3](https://github.com/sue445/kulib/commit/0a956220db35fef8124af3246d2e533198ee6dcd)
 * add RegexUtil, UrlGenerateUtil
 * create Javadoc site
* [0.0.2](https://github.com/sue445/kulib/commit/442d2612a7a42f7a7e416b0bf9ff8a4bbbe7e4d9)
 * add MemcacheUtil
* 0.0.1
 * first release

[![endorse](http://api.coderwall.com/sue445/endorsecount.png)](http://coderwall.com/sue445)
