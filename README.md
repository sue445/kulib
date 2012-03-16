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
* 0.0.3
 * add RegexUtil, UrlGenerateUtil
 * create Javadoc site
* 0.0.2
 * add MemcacheUtil
* 0.0.1
 * first release

[![endorse](http://api.coderwall.com/sue445/endorsecount.png)](http://coderwall.com/sue445)
