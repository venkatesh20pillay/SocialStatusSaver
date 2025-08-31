package com.statuses.statussavers.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.statuses.statussavers.databinding.FragmentMainBinding

/**
 * A placeholder fragment containing a simple view.
 */
class PlaceholderFragment : Fragment() {
    private var pageViewModel: PageViewModel? = null
    private var binding: FragmentMainBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pageViewModel = ViewModelProvider(this).get(PageViewModel::class.java)
        var index = 1
        if (arguments != null) {
            index = requireArguments().getInt(ARG_SECTION_NUMBER)
        }
        pageViewModel!!.setIndex(index)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMainBinding.inflate(inflater, container, false)
        val root: View = binding!!.root

        val textView = binding!!.sectionLabel
        pageViewModel!!.text.observe(viewLifecycleOwner) { s -> textView.text = s }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    companion object {
        private const val ARG_SECTION_NUMBER = "section_number"
    }
}