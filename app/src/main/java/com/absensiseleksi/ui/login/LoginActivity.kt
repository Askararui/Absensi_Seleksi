package com.absensiseleksi.ui.login

import android.content.Intent
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import androidx.appcompat.app.AppCompatActivity
import com.absensiseleksi.MainActivity
import com.absensiseleksi.R
import com.absensiseleksi.data.local.PrefManager
import com.absensiseleksi.databinding.ActivityLoginBinding
import com.absensiseleksi.utils.ViewUtils.addClickAnimation
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private var isPasswordVisible = false
    
    @Inject
    lateinit var prefManager: PrefManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (prefManager.getToken() != null) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        setupListeners()
    }

    private fun setupListeners() {
        binding.ivPasswordVisibility.setOnClickListener {
            isPasswordVisible = !isPasswordVisible
            if (isPasswordVisible) {
                binding.etPassword.transformationMethod = HideReturnsTransformationMethod.getInstance()
                binding.ivPasswordVisibility.setImageResource(R.drawable.ic_eye_off)
            } else {
                binding.etPassword.transformationMethod = PasswordTransformationMethod.getInstance()
                binding.ivPasswordVisibility.setImageResource(R.drawable.ic_eye)
            }
            binding.etPassword.setSelection(binding.etPassword.text.length)
        }

        binding.btnLogin.addClickAnimation()
        binding.btnLogin.setOnClickListener {
            val username = binding.etUsername.text.toString()
            prefManager.saveName(if (username.isNotEmpty()) username else "Developer")
            prefManager.saveToken("dummy_token_jwt")

            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }
}