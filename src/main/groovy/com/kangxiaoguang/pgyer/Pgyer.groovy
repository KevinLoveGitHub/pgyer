package com.kangxiaoguang.pgyer

import org.gradle.api.Plugin
import org.gradle.api.Project

class Pgyer implements Plugin<Project> {
    void apply(Project project) {
        def apks = project.container(ApkTarget) {
            String apkName = it.toString()
            PgyerUserUploadTask userTask = project.task("uploadPgyer_${apkName}", type: PgyerUserUploadTask) as
                    PgyerUserUploadTask
            userTask.group = 'Pgyer'
            userTask.description = "Upload an APK file of ${apkName}"
            userTask.apkName = apkName

            project.extensions.create(it, ApkTarget, apkName)
        }

        def pgyer = new PgyerExtension(apks)
        project.extensions.pgyer = pgyer

        def apkUpload = project.task('uploadPgyer', type: PgyerAllUploadTask)
        apkUpload.group = 'Pgyer'
        apkUpload.description = 'Uploads the APK file. Also updates the distribution specified by distributionKey if configured'
    }
}
