import com.google.gson.GsonBuilder
import okhttp3.*
import java.io.IOException
import java.time.LocalDate
import android.util.Base64
import android.util.Log
import com.brickcommander.whoseturnisit.BuildConfig
import com.brickcommander.whoseturnisit.data.SharedData
import com.brickcommander.whoseturnisit.logic.Calculate
import com.brickcommander.whoseturnisit.model.Person
import com.brickcommander.whoseturnisit.model.Work
import com.google.gson.reflect.TypeToken
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody

class GitHubJsonHandler() {
    private val githubToken = BuildConfig.GITHUB_TOKEN
    private val repoOwner = BuildConfig.REPO_OWNER
    private val repoName = BuildConfig.REPO_NAME
    private val filePersonsPath = BuildConfig.FILE_PERSONS_PATH
    private val fileWorkPath = BuildConfig.FILE_WORK_PATH
    private val fileCacheWorkPath = BuildConfig.FILE_CACHEWORK_PATH

    private val client = OkHttpClient()
    private val gson = GsonBuilder()
        .registerTypeAdapter(LocalDate::class.java, LocalDateSerializer())
        .registerTypeAdapter(LocalDate::class.java, LocalDateDeserializer())
        .create()

    private val apiUrl = "https://api.github.com/repos/$repoOwner/$repoName/contents/"
    private var fileSha: String? = null  // Variable to store the SHA

    companion object {
        const val TAG = "GitHubJsonHandler"
    }

    // Fetch JSON file from GitHub
    private fun fetchJsonFromGitHub(filePath: String): String? {
        Log.i(TAG, "fetchJsonFromGitHub : ${filePath} : START")

        val request = Request.Builder()
            .url(apiUrl+filePath)
            .header("Authorization", githubToken)
            .build()

        Log.i(TAG, "calling Read API : ${filePath} : ${request.url}")

        try {
            val response = client.newCall(request).execute() // Synchronous call
            if (!response.isSuccessful) {
                println("Failed to fetch JSON : ${filePath} : ${response.message}")
                return null
            }

            val responseBody = response.body?.string() ?: return null
            val fileInfo = gson.fromJson(responseBody, GitHubFileResponse::class.java)
            fileSha = fileInfo.sha  // Save the SHA

            return String(Base64.decode(fileInfo.content, Base64.DEFAULT))
        } catch (e: IOException) {
            Log.i(TAG, "fetchJsonFromGitHub: Exception Occured : REQ=${request} : FILEPATH=${filePath}")
            e.printStackTrace()
            return null
        }
    }

    private fun fetchPersonsList() {
        val jsonContent = fetchJsonFromGitHub(filePersonsPath)?: return
        val objectType = object : TypeToken<List<Person>>() {}.type
        SharedData.personsList = gson.fromJson(jsonContent, objectType)
    }

    private fun fetchWorkList() {
        val jsonContent = fetchJsonFromGitHub(fileWorkPath)?: return
        val objectType = object : TypeToken<List<Work>>() {}.type
        SharedData.workList = gson.fromJson(jsonContent, objectType)
    }

    private fun fetchCacheWork() {
        val jsonContent = fetchJsonFromGitHub(fileCacheWorkPath)?: return
        val objectType = object : TypeToken<Work>() {}.type
        SharedData.cacheWork = gson.fromJson(jsonContent, objectType)
    }

    fun fetchAllDataFromDB() {
        fetchPersonsList()
        fetchWorkList()
        fetchCacheWork()
    }

    // Get File SHA
    private fun getFileSha(filePath: String): String? {
        Log.i(TAG, "getFileSha : ${filePath} : START")

        val request = Request.Builder()
            .url(apiUrl+filePath)
            .header("Authorization", githubToken)
            .build()

        Log.i(TAG, "calling Read API for SHA : ${filePath} : ${request.url}")

        try {
            val response = client.newCall(request).execute() // Synchronous call
            if (!response.isSuccessful) {
                Log.i(TAG,"Failed to fetch JSON : ${filePath}: ${response.message}")
                return null
            }

            val responseBody = response.body?.string() ?: return null
            val fileInfo = gson.fromJson(responseBody, GitHubFileResponse::class.java)
            return fileInfo.sha  // Save the SHA
        } catch (e: IOException) {
            Log.i(TAG, "getFileSha: Exception Occured : REQ=${request} : FILEPATH=${filePath}")
            e.printStackTrace()
            return null
        }
    }

    // Update JSON file on GitHub
    private fun updateJsonOnGitHub(updatedJson: String, filePath: String, commitMessage: String): Boolean {
        val encodedContent = Base64.encodeToString(updatedJson.toByteArray(), Base64.NO_WRAP)

        val sha = getFileSha(filePath) ?: run {
            Log.i(TAG, "Get SHA API Failed : ${filePath}. Unable to update the JSON file.")
            return false
        }

        val requestBody = """
            {
              "message": "$commitMessage",
              "content": "$encodedContent",
              "sha": "$sha"
            }
        """.trimIndent()

        Log.i(TAG, "request body: $requestBody")

        val request = Request.Builder()
            .url(apiUrl+filePath)
            .header("Authorization", githubToken)
            .put(requestBody.toRequestBody("application/json".toMediaType()))
            .build()

        Log.i(TAG, "calling Update API : ${filePath} : ${request}")

        try {
            val response = client.newCall(request).execute() // Synchronous call
            if (response.isSuccessful) {
                Log.i(TAG, "Successfully updated the JSON file on GitHub : ${filePath}.")
                return true
            } else {
                Log.i(TAG, "Failed to update the JSON file : ${filePath} : ${response.body?.string()}")
                return false
            }
        } catch (e: IOException) {
            Log.i(TAG, "updateJsonOnGitHub: Exception Occured : REQ=${request} : FILEPATH=${filePath}")
            e.printStackTrace()
            return false
        }
    }

    fun updatePersonsListInDB(person: Person) {
        Log.i(Calculate.TAG, "updatePersonListInDB()")
        val newPersonList = SharedData.personsList.toMutableList()
        newPersonList.add(person)
        val updatedJson = gson.toJson(newPersonList.toList())
        if(updateJsonOnGitHub(updatedJson, filePersonsPath, "Update Persons List")) {
            SharedData.personsList = newPersonList.toList()
        }
    }

    fun updateWorkListInDB(cacheWork: Work) {
        Log.i(Calculate.TAG, "updateWorkListInDB()")
        val newWorkList = SharedData.workList.toMutableList()
        newWorkList.add(cacheWork)
        val updatedJson = gson.toJson(newWorkList.toList())
        if(updateJsonOnGitHub(updatedJson, fileWorkPath, "Update Work List")) {
            SharedData.workList = newWorkList.toList()
        }
    }

    fun updateCacheWorkInDB() {

    }

    // Helper data class to store GitHub file metadata response
    private data class GitHubFileResponse(
        val content: String,
        val sha: String
    )
}
