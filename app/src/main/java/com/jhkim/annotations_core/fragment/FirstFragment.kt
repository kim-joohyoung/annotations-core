package com.jhkim.annotations_core.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.jhkim.annotations.Arg
import com.jhkim.annotations.FragmentBuilder
import com.jhkim.annotations_core.R
import com.jhkim.annotations_core.databinding.FragmentFirstBinding

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */

@SuppressLint("SetTextI18n")
@FragmentBuilder
class FirstFragment : Fragment() {
    private var _binding: FragmentFirstBinding? = null

    @Arg
    var page : Int=0

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        SecondFragmentBuilder.register(this) { result1, result2 ->
            _binding!!.textviewFirst.text = "page : $page\nresult1=$result1\nresult2=$result2"
        }
        FirstFragmentBuilder.inject(this)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        _binding!!.textviewFirst.text = "page : $page"
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonFirst.setOnClickListener {
            parentFragmentManager.commit {
                addToBackStack(null)
                replace(R.id.layout, SecondFragmentBuilder.build("arg1_data", "arg2_data"))
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}