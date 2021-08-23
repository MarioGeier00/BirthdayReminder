package com.mgprogramms.birthdayreminder.ui.home

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.mgprogramms.birthdayreminder.R
import com.mgprogramms.birthdayreminder.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel
    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private var birthdayFound: Boolean = false

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        homeViewModel.nameOfNextBirthday.observe(viewLifecycleOwner, {
            binding.textBirthdayName.text =
                requireContext().resources.getString(R.string.until_birthday_of) + it
        })
        homeViewModel.daysUntilNextBirthday.observe(viewLifecycleOwner, {
            binding.textDaysUntilBirthday.text = it.toString()
            binding.textDays.text = requireContext().resources.getString(R.string.days)
            if (it == 1) {
                binding.textDays.text = requireContext().resources.getString(R.string.day)
            }

            birthdayFound = it != null && it >= 0

            binding.textDays.isVisible = birthdayFound
            binding.textDaysUntilBirthday.isVisible = birthdayFound

            if (!birthdayFound) {
                binding.textBirthdayName.text =
                    requireContext().resources.getString(R.string.birthday_not_found)
            }
        })
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}