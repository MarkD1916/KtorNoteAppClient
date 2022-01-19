package com.androiddevs.ktornoteapp.ui.auth

import android.content.SharedPreferences
import android.content.pm.ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.androiddevs.ktornoteapp.R
import com.androiddevs.ktornoteapp.databinding.FragmentAuthBinding
import com.androiddevs.ktornoteapp.other.Constants.KEY_LOGGED_IN_EMAIL
import com.androiddevs.ktornoteapp.other.Constants.KEY_PASSWORD
import com.androiddevs.ktornoteapp.other.EventObserver
import com.androiddevs.ktornoteapp.preferences.BasicAuthPreferences
import com.androiddevs.ktornoteapp.ui.snackbar
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AuthFragment : Fragment() {
    private var _binding: FragmentAuthBinding? = null
    val mBinding get() = _binding!!

    @Inject
    lateinit var basicAuthSharedPreferences: BasicAuthPreferences

    private var curEmail: String? = null
    private var curPassword: String? = null

    private val viewModel: AuthViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAuthBinding.inflate(layoutInflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().requestedOrientation = SCREEN_ORIENTATION_PORTRAIT
        subscribeToObservers()
        with(mBinding) {
            btnRegister.setOnClickListener {
                val email = etRegisterEmail.text.toString()
                val password = etRegisterPassword.text.toString()
                val confirmPassword = etRegisterPasswordConfirm.text.toString()
                viewModel.register(email, password, confirmPassword)
            }
            btnLogin.setOnClickListener {
                val email = etLoginEmail.text.toString()
                val password = etLoginPassword.text.toString()
                curEmail = email
                curPassword = password
                viewModel.login(email, password)
            }

        }

    }

    private fun redirectLogin(){
        val navOptions = NavOptions.Builder()
            .setPopUpTo(R.id.authFragment, true)
            .build()
        findNavController().navigate(AuthFragmentDirections.actionAuthFragmentToNoteFragment(), navOptions)
    }

    private fun subscribeToObservers() {
        viewModel.registerStatus.observe(viewLifecycleOwner, EventObserver(
            onError = {
                mBinding.registerProgressBar.visibility = View.GONE
                snackbar(it)
            },
            onLoading = {
                mBinding.registerProgressBar.visibility = View.VISIBLE
            }
        ) {
            mBinding.registerProgressBar.visibility = View.GONE
        }
        )

        viewModel.loginStatus.observe(viewLifecycleOwner, EventObserver(
            onError = {
                mBinding.loginProgressBar.visibility = View.GONE
                snackbar(it)
            },
            onLoading = {
                mBinding.loginProgressBar.visibility = View.VISIBLE
            }
        ) {
            mBinding.loginProgressBar.visibility = View.GONE
            snackbar("Successfully logged in")
            basicAuthSharedPreferences.setStoredEmail(curEmail!!)
            basicAuthSharedPreferences.setStoredPassword(curPassword!!)
            redirectLogin()
        }
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}