package com.example.achievemaster.utils

import android.util.Base64
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

object EncryptionHelper {

    private const val ALGORITHM = "AES"
    private const val TRANSFORMATION = "AES/GCM/NoPadding"
    private const val KEY_SIZE = 256
    private const val IV_SIZE = 12
    private const val TAG_LENGTH = 128

    fun generateKey(): SecretKey {
        val keyGenerator = KeyGenerator.getInstance(ALGORITHM)
        keyGenerator.init(KEY_SIZE, SecureRandom())
        return keyGenerator.generateKey()
    }

    fun encryptPassword(password: String, secretKey: SecretKey): String {
        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.ENCRYPT_MODE, secretKey)

        val iv = cipher.iv
        val encryptedData = cipher.doFinal(password.toByteArray(Charsets.UTF_8))

        val combined = ByteArray(iv.size + encryptedData.size)
        System.arraycopy(iv, 0, combined, 0, iv.size)
        System.arraycopy(encryptedData, 0, combined, iv.size, encryptedData.size)

        return Base64.encodeToString(combined, Base64.NO_WRAP)
    }

    fun decryptPassword(encryptedPassword: String, secretKey: SecretKey): String {
        val combined = Base64.decode(encryptedPassword, Base64.NO_WRAP)

        val iv = combined.copyOfRange(0, IV_SIZE)
        val encryptedData = combined.copyOfRange(IV_SIZE, combined.size)

        val cipher = Cipher.getInstance(TRANSFORMATION)
        val spec = GCMParameterSpec(TAG_LENGTH, iv)
        cipher.init(Cipher.DECRYPT_MODE, secretKey, spec)

        val decryptedData = cipher.doFinal(encryptedData)
        return String(decryptedData, Charsets.UTF_8)
    }
}