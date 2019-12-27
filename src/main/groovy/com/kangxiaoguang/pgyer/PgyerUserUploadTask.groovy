package com.kangxiaoguang.pgyer

import org.gradle.api.tasks.TaskAction

class PgyerUserUploadTask extends PgyerTask {
    String apkName
    @TaskAction
    def userUploadTasks() {
        List<Apk> apks = Apk.getApks(project, apkName)
        super.upload(project, apks)
    }
}
