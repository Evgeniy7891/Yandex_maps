package ru.netoloy.maps.screens.main

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.PointF
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKit
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.layers.GeoObjectTapListener
import com.yandex.mapkit.layers.ObjectEvent
import com.yandex.mapkit.map.*
import com.yandex.mapkit.map.Map
import com.yandex.mapkit.search.*
import com.yandex.mapkit.user_location.UserLocationLayer
import com.yandex.mapkit.user_location.UserLocationObjectListener
import com.yandex.mapkit.user_location.UserLocationView
import com.yandex.runtime.Error
import com.yandex.runtime.image.ImageProvider
import com.yandex.runtime.network.NetworkError
import com.yandex.runtime.network.RemoteError
import com.yandex.runtime.ui_view.ViewProvider
import ru.netoloy.maps.R
import ru.netoloy.maps.databinding.FragmentMainBinding
import ru.netoloy.maps.screens.viewmodel.MainViewModel

class MainFragment : Fragment(), UserLocationObjectListener, Session.SearchListener,
    CameraListener {
    private val viewModel: MainViewModel by viewModels(ownerProducer = ::requireParentFragment)

    private lateinit var geoObjectTapListener: GeoObjectTapListener
    lateinit var locationMapKit: UserLocationLayer
    lateinit var searchEdit: EditText
    lateinit var searchManager: SearchManager
    lateinit var searchSession: Session
    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    private fun sumbitQuery(query: String) {
        searchSession = searchManager.submit(
            query,
            VisibleRegionUtils.toPolygon(binding.mapView.map.visibleRegion),
            SearchOptions(),
            this
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        MapKitFactory.setApiKey("3b29c9f6-b8a8-451f-961c-bebe6041c295")
        MapKitFactory.initialize(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        val mapKit: MapKit = MapKitFactory.getInstance()
        binding.mapView.map.move(
            CameraPosition((viewModel.beginPosition), 15.0f, 0.0f, 0.0f),
            Animation(Animation.Type.SMOOTH, 10f), null
        )
        requestLocationPermission()
        locationMapKit = mapKit.createUserLocationLayer(binding.mapView.mapWindow)
        locationMapKit.isVisible = true
        locationMapKit.setObjectListener(this)
        SearchFactory.initialize(requireContext())
        searchManager = SearchFactory.getInstance().createSearchManager(SearchManagerType.COMBINED)
        binding.mapView.map.addCameraListener(this)
        binding.searchEdit.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                Log.d("TAG", "if sumbit")
                sumbitQuery(binding.searchEdit.text.toString())
            }
            false
        }
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
            Log.d("TAG", "geoObj1 - ${it.isSelected}")
            viewModel.pointPosition = null
            val current = viewModel.beginPosition
            viewModel.beginPosition = it.geoObject.geometry.getOrNull(0)?.point ?: current
            if (findNavController().currentDestination?.id != R.id.addFragment) {
                findNavController().navigate(R.id.action_mainFragment_to_addFragment)
                Log.d("TAG", "geoObj2")
            }
            true
        }
        binding.mapView.map.addTapListener(geoObjectTapListener)

        binding.bottomNavMenu.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.add -> findNavController().navigate(R.id.action_mainFragment_to_addFragment)
                R.id.list -> findNavController().navigate(R.id.action_mainFragment_to_listMarkFragment)
            }
            true
        }
        var probki = mapKit.createTrafficLayer(binding.mapView.mapWindow)
        var probkiState = true
        probki.isTrafficVisible = true
        binding.trafficButton.setOnClickListener {
            when (probkiState) {
                false -> {
                    probkiState = true;
                    probki.isTrafficVisible = true;
                    binding.trafficButton.setBackgroundResource(R.drawable.icon_car)
                    Log.d("TAG", "traffis false - $probkiState")
                }
                true -> {
                    probkiState = false;
                    probki.isTrafficVisible = false;
                   binding.trafficButton.setBackgroundResource(R.drawable.ic_off)
                    Log.d("TAG", "traffis true- $probkiState")
                }
            }

        }


        return binding.root
    }

    private fun requestLocationPermission() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Log.d("TAG", "requestLocation")
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                0
            )
            return
        }
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

    override fun onObjectAdded(userLocationView: UserLocationView) {
        Log.d("TAG", "object Addd")
        locationMapKit.setAnchor(
            PointF(
                (binding.mapView.width() * 0.5).toFloat(),
                (binding.mapView.height() * 0.5).toFloat()
            ),
            PointF(
                (binding.mapView.width() * 0.5).toFloat(),
                (binding.mapView.height() * 0.83).toFloat()
            ),
        )
        userLocationView.arrow.setIcon(
            ImageProvider.fromResource(
                requireContext(),
                R.drawable.navigation_arrow_com
            )
        )
        val picIcon = userLocationView.pin.useCompositeIcon()
        picIcon.setIcon(
            "icon",
            ImageProvider.fromResource(requireContext(), R.drawable.circle),
            IconStyle().setAnchor(PointF(0f, 0f))
                .setRotationType(RotationType.ROTATE).setZIndex(0f).setScale(1f)
        )
        picIcon.setIcon(
            "pin", ImageProvider.fromResource(requireContext(), R.drawable.nothing),
            IconStyle().setAnchor(PointF(0.5f, 05f)).setRotationType(RotationType.ROTATE)
                .setZIndex(1f).setScale(0.5f)
        )
        userLocationView.accuracyCircle.fillColor = Color.BLUE and -0x66000001
    }

    override fun onObjectRemoved(p0: UserLocationView) {
    }

    override fun onObjectUpdated(p0: UserLocationView, p1: ObjectEvent) {
    }

    override fun onSearchResponse(response: Response) {
        val mapObject: MapObjectCollection = binding.mapView.map.mapObjects
        mapObject.clear()
        for (searchResult in response.collection.children) {
            val resultLocation = searchResult.obj!!.geometry[0].point
            if (resultLocation != null) {
                mapObject.addPlacemark(
                    resultLocation,
                    ImageProvider.fromResource(requireContext(), R.drawable.circle)
                )
            }
        }
    }

    override fun onSearchError(p0: Error) {
        var errorMessage = "Неизвестная ошибка!"
        if (p0 is RemoteError) {
            errorMessage = "Беспроводная ошибка!"
        } else if (p0 is NetworkError) {
            errorMessage = "Проблема с интернетом"
        }
        Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
    }

    override fun onCameraPositionChanged(
        map: Map,
        cameraPosition: CameraPosition,
        cameraUpdateReson: CameraUpdateReason,
        finished: Boolean
    ) {
        if (finished) {
            sumbitQuery(binding.searchEdit.text.toString())
        }
    }
}
