package com.brickcommander.whoseturnisit.logic

import com.brickcommander.whoseturnisit.model.Person
import GitHubJsonHandler
import android.util.Log
import com.brickcommander.whoseturnisit.BuildConfig

class Calculate {

    private val githubToken = BuildConfig.GITHUB_TOKEN
    private val repoOwner = BuildConfig.REPO_OWNER
    private val repoName = BuildConfig.REPO_NAME
    private val filePath = BuildConfig.FILEPATH

    // Create an instance of GitHubJsonHandler
    private val handler = GitHubJsonHandler(githubToken, repoOwner, repoName, filePath)

    companion object {
        const val TAG = "Calculate"
    }

    fun updateToLatestDB() {
        handler.fetchJsonFromGitHub { persons ->
            if (persons != null) {
                Log.i(TAG, "Fetched persons:")
                persons.forEach { person ->
                    println(person.toString())
                }

                // Example of modifying the list
                val updatedList = persons.toMutableList()
                persons.forEach { person ->
                    person.increaseScore(9)
                }

                persons.forEach { person ->
                    println(person.toString())
                    Log.i(TAG, "person: $person")
                }

                // Update the JSON file on GitHub
                handler.updateJsonOnGitHub(updatedList, "Updated JSON with new person")
            } else {
                Log.i(TAG, "Failed to fetch persons.")
            }
        }
    }
}


