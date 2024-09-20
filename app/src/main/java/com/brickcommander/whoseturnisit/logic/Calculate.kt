package com.brickcommander.whoseturnisit.logic

import com.brickcommander.whoseturnisit.model.Person
import GitHubJsonHandler
import android.util.Log
import com.brickcommander.whoseturnisit.BuildConfig

class Calculate {

    private val githubToken = BuildConfig.GITHUB_TOKEN
    private val repoOwner = BuildConfig.REPO_OWNER
    private val repoName = BuildConfig.REPO_NAME
    private val filePersonsPath = BuildConfig.FILE_PERSONS_PATH
    private val fileWorkPath = BuildConfig.FILE_WORK_PATH

    // Create an instance of GitHubJsonHandler
    private val handler =
        GitHubJsonHandler(githubToken, repoOwner, repoName, filePersonsPath, fileWorkPath)

    companion object {
        const val TAG = "Calculate"
    }

    fun updateToLatestDB(): List<Person>? {
        Log.i(TAG, "updateToLatestDB()")
        var persons: List<Person>? = null
        Thread {
            persons = handler.fetchJsonFromGitHub()

            if (persons != null) {
                println("Fetched persons:")
                persons!!.forEach { person ->
                    println(person.toString())
                }

//                // Example of modifying the list
//                persons.forEach { person ->
//                    person.increaseScore(9)
//                }
//
//                // Update the JSON file on GitHub
//                handler.updateJsonOnGitHub(persons, "Updated JSON with new person")
            } else {
                Log.i(TAG, "updateToLatestDB: Failed to fetch persons.")
            }
        }.start() // Start the thread

        return persons
    }


}
