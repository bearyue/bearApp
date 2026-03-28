package com.bear.asset.ui.auth

import androidx.biometric.BiometricManager as AndroidBiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity

object BiometricManager {

    fun canAuthenticate(activity: FragmentActivity): Boolean {
        val biometricManager = AndroidBiometricManager.from(activity)
        return biometricManager.canAuthenticate(
            AndroidBiometricManager.Authenticators.BIOMETRIC_STRONG or
                    AndroidBiometricManager.Authenticators.BIOMETRIC_WEAK
        ) == AndroidBiometricManager.BIOMETRIC_SUCCESS
    }

    fun authenticate(
        activity: FragmentActivity,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val executor = ContextCompat.getMainExecutor(activity)

        val callback = object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                onSuccess()
            }

            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errorCode, errString)
                if (errorCode != BiometricPrompt.ERROR_USER_CANCELED &&
                    errorCode != BiometricPrompt.ERROR_NEGATIVE_BUTTON
                ) {
                    onError(errString.toString())
                }
            }

            override fun onAuthenticationFailed() {
                super.onAuthenticationFailed()
                onError("身份验证失败，请重试")
            }
        }

        val biometricPrompt = BiometricPrompt(activity, executor, callback)

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("BearAsset 身份验证")
            .setSubtitle("使用指纹或面部识别解锁")
            .setNegativeButtonText("使用密码")
            .build()

        biometricPrompt.authenticate(promptInfo)
    }
}
