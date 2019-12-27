package com.kangxiaoguang.pgyer

import okhttp3.*
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.Project
import org.json.JSONObject

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
            errorHandling(apk, json)
            println "${apk.name} result: ${json.toString()}"
        }
    }

    private static void errorHandling(Apk apk, JSONObject json) {
        print(json)
    }

    private String getEndPoint(Project project) {
        String uKey = project.extensions.pgyer.uKey
        String _api_key = project.extensions.pgyer._api_key
        if (uKey == null || _api_key == null) {
            throw new GradleException("uKey or apiKey is missing")
        }
        String endPoint = API_END_POINT + "/app/upload"
        return endPoint
    }

    private HashMap<String, JSONObject> httpPost(String endPoint, List<Apk> apks) {
        HashMap<String, JSONObject> result = new HashMap<String, JSONObject>()
        OkHttpClient client = new OkHttpClient();
        def configBuilder = client.newBuilder()
        configBuilder.connectTimeout = 10 * 1000
        configBuilder.readTimeout = 60 * 1000

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
                println("add part key: " + key + " value: " + params.get(key))
                multipartBuilder.addFormDataPart(key, params.get(key))
            }

            Request request = new Request.Builder().url(getEndPoint(project)).
                    post(multipartBuilder.build()).
                    build()

            Response response = client.newCall(request).execute();

            if (response == null || response.body() == null) return null;
            InputStream is = response.body().byteStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(is))
            JSONObject json = new JSONObject(reader.readLine())
            result.put(apk.name, json)
            is.close()
        }
        return result
    }
}
