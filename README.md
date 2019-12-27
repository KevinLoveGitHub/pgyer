# pgyer
[![Download](https://api.bintray.com/packages/kevinlive/maven/pgyer/images/download.svg) ](https://bintray.com/kevinlive
/maven/pgyer/_latestVersion)

上传apk到蒲公英

## example
项目根目录 `build.gradle` 配置：

```groovy
dependencies {
    classpath 'com.kangxiaoguang.gradle.tools:pgyer:1.1'
}
```

app modules `app/build.gradle` 配置：

```
apply plugin: 'com.kangxiaoguang.pgyer'

pgyer {
    // 配置蒲公英的_api_key和userKey
    Properties properties = new Properties()
    properties.load(project.rootProject.file('local.properties').newDataInputStream())
    _api_key = properties.getProperty("ApiKey")
    uKey = properties.getProperty("UKey")

    // 配置需要上传的apk信息
    apks {
        release {
            // (选填)蒲公英中显示的名字
            buildName = "Gradle插件测试程序"
            // 需要上传的apk路径
            sourceFile = file("build/outputs/apk/debug/app-debug.apk")
            // (选填) 是否使用git log代替更新描述
            useGitLogInsteadDesc = true
            // (必填) 设置App安装密码
            buildPassword = "xiaoguang"
            // (必填)应用安装方式，值为(2,3)。2：密码安装，3：邀请安装
            buildInstallType = 2
        }
    }
}

```
