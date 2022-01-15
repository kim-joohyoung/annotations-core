package com.jhkim.annotations_core

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.jhkim.annotations.Arg
import com.jhkim.annotations.FragmentBuilder
import com.jhkim.annotations.Result
import com.jhkim.annotations_core.databinding.FragmentSecondBinding

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
@FragmentBuilder(listener = true)
@Result("result1", String::class)
@Result("result2", String::class)
class SecondFragment : Fragment() {
    @Arg
    lateinit var arg1 : String
    @Arg
    lateinit var arg2 : String

    private var _binding: FragmentSecondBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        SecondFragmentBuilder.inject(this)

        _binding = FragmentSecondBinding.inflate(inflater, container, false)
        binding.textviewSecond.text = "$arg1\n$arg2"
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.buttonSecond.setOnClickListener {
            SecondFragmentBuilder.setResult(parentFragmentManager, "SecondFragment","Result")
            parentFragmentManager.popBackStack()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    companion object{

    }
}
