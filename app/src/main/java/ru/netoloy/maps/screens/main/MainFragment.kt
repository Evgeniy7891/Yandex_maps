package ru.netoloy.maps.screens.main

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.layers.GeoObjectTapListener
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.MapObjectTapListener
import com.yandex.runtime.ui_view.ViewProvider
import ru.netoloy.maps.R
import ru.netoloy.maps.databinding.FragmentMainBinding
import ru.netoloy.maps.screens.viewmodel.MainViewModel

class MainFragment : Fragment() {
    private val viewModel: MainViewModel by viewModels(ownerProducer = ::requireParentFragment)

    private lateinit var geoObjectTapListener: GeoObjectTapListener

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        MapKitFactory.setApiKey("")
        MapKitFactory.initialize(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        binding.mapView.map.move(
            CameraPosition((viewModel.beginPosition), 15.0f, 0.0f, 0.0f),
            Animation(Animation.Type.SMOOTH, 10f), null
        )

        viewModel.data.observe(viewLifecycleOwner) {
            binding.mapView.map.mapObjects.clear()
            Log.d("TAG", "obser")

            it.forEach {
                val view: View = View.inflate(requireContext(), R.layout.like_mark_on_map, null)
                val name = view.findViewById<TextView>(R.id.pointName)
                name.text = it.name

                val mapObjectTapListener = MapObjectTapListener { _, _ ->
                    viewModel.pointPosition = it
                    viewModel.beginPosition = it.point
                    Log.d("TAG", "mapObj")
                    findNavController().navigate(R.id.action_mainFragment_to_addFragment)
                    true
                }

                binding.mapView.map.mapObjects.addPlacemark(it.point, ViewProvider(view)).also {
                    it.addTapListener(mapObjectTapListener)
                }
            }
        }
        geoObjectTapListener = GeoObjectTapListener {
            viewModel.pointPosition = null
            val current = viewModel.beginPosition
            viewModel.beginPosition = it.geoObject.geometry.getOrNull(0)?.point ?: current
            if(findNavController().currentDestination?.id != R.id.addFragment) {
                findNavController().navigate(R.id.action_mainFragment_to_addFragment)
                Log.d("TAG", "geoObj")
            }
            true
        }
        binding.mapView.map.addTapListener(geoObjectTapListener)

        binding.bottomNavMenu.setOnNavigationItemSelectedListener {
            when(it.itemId) {
                R.id.add -> findNavController().navigate(R.id.action_mainFragment_to_addFragment)
                R.id.list -> findNavController().navigate(R.id.action_mainFragment_to_listMarkFragment)
            }
            true
        }



        return binding.root
    }

    override fun onStop() {
        binding.mapView.onStop()
        Log.d("TAG", "Stop")
        MapKitFactory.getInstance().onStop()
        super.onStop()
    }

    override fun onStart() {
        binding.mapView.onStart()
        Log.d("TAG", "Start")
        MapKitFactory.getInstance().onStart()
        super.onStart()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
