package ru.netoloy.maps.screens.add

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.yandex.mapkit.geometry.Point
import ru.netoloy.maps.databinding.FragmentAddBinding
import ru.netoloy.maps.model.MarkMap
import ru.netoloy.maps.screens.viewmodel.MainViewModel

class AddFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentAddBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MainViewModel by viewModels(
        ownerProducer = ::requireParentFragment
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAddBinding.inflate(inflater, container, false)

        with(binding) {
            val id = if (viewModel.pointPosition is MarkMap) {
                header.text = "Редактирование места"
                val selectedPoint = viewModel.pointPosition as MarkMap
                latitudeValue.setText(selectedPoint.point.latitude.toString())
                longitudeValue.setText(selectedPoint.point.longitude.toString())
                descriptionValue.setText(selectedPoint.name)
                selectedPoint.id
            } else {
                header.text = "Добавление точки"
                latitudeValue.setText(viewModel.beginPosition.latitude.toString())
                longitudeValue.setText(viewModel.beginPosition.longitude.toString())
                delete.visibility = View.GONE
                (viewModel.data.value?.maxByOrNull { point -> point.id }?.id ?: 0) + 1
            }

            save.setOnClickListener {
                val newLatitudeValue = latitudeValue.text.toString().toDouble()
                val newLongitudeValue = longitudeValue.text.toString().toDouble()
                if (newLatitudeValue < -90 || newLatitudeValue > 90 ||
                    newLongitudeValue < -180 || newLongitudeValue > 180
                ) {
                    error.visibility = View.VISIBLE
                } else {
                    val mark = MarkMap(
                        id = id,
                        name = descriptionValue.text.toString(),
                        point = Point(
                            newLatitudeValue,
                            newLongitudeValue
                        )
                    )
                    viewModel.insert(mark)
                    viewModel.beginPosition = mark.point
                    findNavController().navigateUp()
                }
            }

            delete.setOnClickListener {
                viewModel.removeByid(id)
                findNavController().navigateUp()
            }

            cancel.setOnClickListener {
                findNavController().navigateUp()
            }
        }


        return binding.root
    }
    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}