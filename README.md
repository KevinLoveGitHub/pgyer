# pgyer

上传apk到蒲公英

## example
项目根目录 `build.gradle` 配置：

```groovy
dependencies {
    classpath 'com.kangxiaoguang.gradle.tools:pgyer:0.0.1'
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

    // 配置需要上传的apk信息
    apks {
        release {
            // 需要上传的apk路径
            sourceFile = file("build/outputs/apk/debug/app-debug.apk")
            // (选填) 是否使用git log代替更新描述
            useGitLogInsteadDesc = true
            // (选填) 设置App安装密码
            buildPassword = "xiaoguang"
            // (选填)应用安装方式，值为(2,3)。2：密码安装，3：邀请安装
            buildInstallType = 2
            // (选填)所需更新的指定渠道的下载短链接，只可指定一个渠道，字符串型，如：abcd
            buildChannelShortcut = "shine"
        }
    }
}

```
