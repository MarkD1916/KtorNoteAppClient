package com.androiddevs.ktornoteapp.ui.auth

import android.content.pm.ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.androiddevs.ktornoteapp.R
import com.androiddevs.ktornoteapp.databinding.FragmentAuthBinding
import com.androiddevs.ktornoteapp.other.EventObserver
import com.androiddevs.ktornoteapp.ui.snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AuthFragment: Fragment() {
    private var _binding: FragmentAuthBinding? = null
    val mBinding get() = _binding!!

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
//        mBinding.btnLogin.setOnClickListener {
//            findNavController().navigate(R.id.action_authFragment_to_noteFragment)
//        }
        requireActivity().requestedOrientation = SCREEN_ORIENTATION_PORTRAIT

        mBinding.btnRegister.setOnClickListener{
            with(mBinding){
                val email = etRegisterEmail.text.toString()
                val password = etRegisterPassword.text.toString()
                val confirmPassword = etRegisterPasswordConfirm.text.toString()
                viewModel.register(email,password,confirmPassword)
            }

        }

    }

    override fun onStart() {
        super.onStart()
        subscribeToObservers()
    }

    private fun subscribeToObservers(){
        viewModel.registerStatus.observe(viewLifecycleOwner, EventObserver(
            onError = {
                snackbar(it)
            },
            onLoading = {
                mBinding.registerProgressBar.visibility = View.VISIBLE
            }
        ){
            mBinding.registerProgressBar.visibility = View.GONE
        }
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}