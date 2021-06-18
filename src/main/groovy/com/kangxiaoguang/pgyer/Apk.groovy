package com.kangxiaoguang.pgyer

import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.process.ExecSpec

class Apk {
    String name
    File file

    /**
     * (选填) 版本更新描述，请传空字符串，或不传
     */
    String buildUpdateDescription

    /**
     * (选填) 是否使用git log代替更新描述
     */
    boolean useGitLogInsteadDesc

    /**
     * (选填) 设置App安装密码
     */
    String buildPassword

    /**
     * (选填)应用安装方式，值为(2,3)。2：密码安装，3：邀请安装
     */
    String buildInstallType

    /**
     * (选填)所需更新的指定渠道的下载短链接，只可指定一个渠道，字符串型，如：abcd
     */
    String buildChannelShortcut

    public HashMap<String, String> getParams() {
//        if (buildPassword == null) {
//            throw new GradleException("buildPassword is missing")
//        }
//
//        if (buildInstallType == 0) {
//            throw new GradleException("buildInstallType is missing")
//        }

        HashMap<String, String> params = new HashMap<String, String>()
        params.put("buildUpdateDescription", buildUpdateDescription)
        params.put("buildPassword", buildPassword)
        params.put("buildInstallType", buildInstallType)
        params.put("buildChannelShortcut", buildChannelShortcut)
        return params
    }

    public static List<Apk> getApks(Project project, String searchApkName = "") {
        List<Apk> apks = []
        for (_apk in project.extensions.pgyer.deploygateApks) {
            String name = _apk.name
            if (searchApkName != "" && searchApkName != name) continue
            Apk apk = new Apk()
            apk.name = name
            apk.file = _apk.sourceFile
            if (_apk.useGitLogInsteadDesc as boolean) {
                def result = getExecResult(project, "git", "log", "--pretty=format:%s", "HEAD", "-1")
                if (result != null) {
                    apk.buildUpdateDescription = result
                }
            } else if (_apk.buildUpdateDescription == null) {
                apk.buildUpdateDescription = ""
            } else {
                apk.buildUpdateDescription = _apk.buildUpdateDescription
            }
            apk.buildPassword = _apk.buildPassword
            apk.buildInstallType = _apk.buildInstallType
            apk.buildChannelShortcut = _apk.buildChannelShortcut
            apks.add(apk)
        }
        return apks
    }

    public static String getExecResult(Project project, String... args) {
        def count = new ByteArrayOutputStream()
        def error = new ByteArrayOutputStream()
        def action = new Action<ExecSpec>() {
            @Override
            void execute(ExecSpec execSpec) {
                execSpec.workingDir("./")
                execSpec.commandLine(args)
                execSpec.setStandardOutput(count)
                execSpec.setErrorOutput(error)
            }
        }
        try {
            def exec = project.exec(action)
            if (exec.exitValue != 0) {
                return null
            }
            return count.toString('UTF-8').trim()
        } catch (Exception e) {
            project.logger.error(e.getMessage())
        }
        return null
    }
}
