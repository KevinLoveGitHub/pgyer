package com.kangxiaoguang.pgyer

import org.gradle.api.Named
import org.gradle.api.internal.project.ProjectInternal

class ApkTarget implements Named {
    String name
    ProjectInternal target

    File sourceFile

    /**
     * (选填) 版本更新描述，请传空字符串，或不传
     */
    String buildUpdateDescription

    /**
     * (选填) 是否使用git log代替更新描述
     */
    boolean useGitLogInsteadDesc

    /**
     * (必填) 设置App安装密码
     */
    String buildPassword

    /**
     * (必填)应用安装方式，值为(2,3)。2：密码安装，3：邀请安装
     */
    int buildInstallType

    public ApkTarget(String name) {
        super()
        this.name = name
        this.target = target
    }
}
