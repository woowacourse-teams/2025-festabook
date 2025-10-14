package com.daedan.festabook.presentation.splash

import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.suspendCancellableCoroutine

@OptIn(DelicateCoroutinesApi::class)
class AppVersionManager(
    private val appUpdateManager: AppUpdateManager,
    private val launcher: ActivityResultLauncher<IntentSenderRequest>,
) {
    private val appUpdateInfoTask = appUpdateManager.appUpdateInfo

    suspend fun getIsAppUpdateAvailable(): Result<Boolean> =
        suspendCancellableCoroutine { continuation ->
            appUpdateInfoTask
                .addOnSuccessListener { appUpdateInfo ->
                    val isUpdateAvailable =
                        appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE &&
                            appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)
                    continuation.resumeWith(
                        Result.success(
                            Result.success(isUpdateAvailable),
                        ),
                    )
                }.addOnFailureListener { e ->
                    continuation.resumeWith(
                        Result.success(
                            Result.failure(e),
                        ),
                    )
                }
        }

    fun updateApp() {
        appUpdateManager.startUpdateFlowForResult(
            appUpdateInfoTask.result,
            launcher,
            AppUpdateOptions.newBuilder(AppUpdateType.IMMEDIATE).build(),
        )
    }
}
