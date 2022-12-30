package ru.netoloy.maps.screens.list

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import ru.netoloy.maps.R
import ru.netoloy.maps.databinding.FragmentListMarkBinding
import ru.netoloy.maps.model.MarkMap
import ru.netoloy.maps.screens.viewmodel.MainViewModel

class ListMarkFragment : Fragment() {

    private var _binding: FragmentListMarkBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MainViewModel by viewModels(
        ownerProducer = ::requireParentFragment
    )


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentListMarkBinding.inflate(inflater, container, false)
        val adapter = MarkListAdapter(object : OnInteractionListener {
            override fun onClick(mapPoint: MarkMap) {
                viewModel.pointPosition = mapPoint
                viewModel.beginPosition = mapPoint.point
                findNavController().navigateUp()
            }

            override fun onEdit(markMap: MarkMap) {
                viewModel.pointPosition = markMap
                viewModel.beginPosition = markMap.point
                findNavController().navigate(R.id.action_listMarkFragment_to_addFragment)
            }

            override fun onRemove(markMap: MarkMap) {
                viewModel.removeByid(markMap.id)
            }
        })

        binding.list.adapter = adapter
        viewModel.data.observe(viewLifecycleOwner) { state ->
            adapter.submitList(state)
        }
        return binding.root
    }
    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}