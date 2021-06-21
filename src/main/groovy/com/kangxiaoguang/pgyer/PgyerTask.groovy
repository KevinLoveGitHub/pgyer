package com.kangxiaoguang.pgyer

import okhttp3.*
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.Project
import org.json.JSONObject

import java.security.InvalidParameterException
import java.util.concurrent.TimeUnit

class PgyerTask extends DefaultTask {
    private final String API_END_POINT = "http://www.pgyer.com/apiv2"

    void upload(Project project, List<Apk> apks) {
        if (apks == null || apks.size() == 0) {
            println("No apk to upload")
            return
        }
        String endPoint = getEndPoint(project)

        HashMap<String, JSONObject> result = httpPost(endPoint, apks)
        for (Apk apk in apks) {
            JSONObject json = result.get(apk.name)
            println "${apk.name} result: ${json.toString()}"
        }
    }

    private String getEndPoint(Project project) {
        String _api_key = project.extensions.pgyer._api_key
        if (_api_key == null) {
            throw new GradleException("apiKey is missing")
        }
        String endPoint = API_END_POINT + "/app/upload"
        return endPoint
    }

    private HashMap<String, JSONObject> httpPost(String endPoint, List<Apk> apks) {
        HashMap<String, JSONObject> result = new HashMap<String, JSONObject>()
        OkHttpClient client = new OkHttpClient()
                .newBuilder()
                .connectTimeout(10 * 60, TimeUnit.SECONDS)
                .readTimeout(10 * 60, TimeUnit.SECONDS)
                .writeTimeout(10 * 60, TimeUnit.SECONDS)
                .build()


        for (Apk apk in apks) {
            MultipartBody.Builder multipartBuilder = new MultipartBody.Builder()

            multipartBuilder.setType(MultipartBody.FORM)
            multipartBuilder.addFormDataPart("_api_key", new String(project.extensions.pgyer._api_key))

            multipartBuilder.addFormDataPart("file", apk.file.name,
                    RequestBody.create(
                            apk.file,
                            MediaType.parse("application/vnd.android.package-archive")
                    )
            )

            HashMap<String, String> params = apk.getParams()
            for (String key : params.keySet()) {
                def value = params.get(key)
                if (value != null) {
                    println("add part key: " + key + " value: " + value)
                    multipartBuilder.addFormDataPart(key, value)
                }
            }

            Request request = new Request.Builder().url(getEndPoint(project)).
                    header("content-type", "application/x-www-form-urlencoded").
                    header("enctype", "multipart/form-data").
                    post(multipartBuilder.build()).
                    build()

            Response response = client.newCall(request).execute();

            if (response == null || response.body() == null) return null;
            InputStream is = response.body().byteStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(is))
            JSONObject json = new JSONObject(reader.readLine())
            result.put(apk.name, json)
            is.close()

            errorHandling(apk, json)
        }
        return result
    }

    private void errorHandling(Apk apk, JSONObject json) {
        def code = json.getInt("code")
        if (code > 0) {
            throw new InvalidParameterException(apk.name + ": " + json.toString())
        }
    }
}
