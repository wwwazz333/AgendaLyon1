package com.iutcalendar.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.iutcalendar.calendrier.InputStreamFileException
import com.iutcalendar.data.DataGlobal
import com.iutcalendar.data.FileGlobal
import com.iutcalendar.filedownload.FileDownload
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanIntentResult
import com.journeyapps.barcodescanner.ScanOptions
import com.univlyon1.tools.agenda.R
import com.univlyon1.tools.agenda.databinding.FragmentUrlSetterBinding
import java.io.IOException

class URLSetterFragment : Fragment() {
    private lateinit var binding: FragmentUrlSetterBinding

    private val optionsScanner = ScanOptions().apply {
        setOrientationLocked(true)
        setBeepEnabled(false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Calendrier classique
        binding.inputURL.let { editText: EditText ->
            editText.setText(DataGlobal.getSavedPath(requireContext()))
            scanQR(binding.scanBtn, editText)
        }
        val prevURL = binding.inputURL.text.toString()

        //Calendrier des salles
        binding.inputURLRooms.let { editText: EditText ->
            editText.setText(DataGlobal.getSavedRoomsPath(requireContext()))
            scanQR(binding.scanRoomsBtn, editText)
        }
        val prevURLRooms = binding.inputURLRooms.text.toString()



        binding.submitBtn.setOnClickListener {
            if (prevURL != binding.inputURL.text.toString()) {
                FileGlobal.getFileCalendar(requireContext()).delete()
                FileGlobal.getFile(requireContext(), FileGlobal.CHANGEMENT_EVENT).delete()
                DataGlobal.savePath(requireContext(), binding.inputURL.text.toString())
                Thread {
                    try {
                        FileDownload.updateFichier(FileGlobal.getFileCalendar(context).absolutePath, requireContext())
                        //TODO faire un system de validation d'url
                    } catch (_: InputStreamFileException) {
                    } catch (_: IOException) {
                    }
                }.start()
            }
            binding.inputURLRooms.text.toString().let { newPath ->
                if (prevURLRooms != newPath) {
                    DataGlobal.saveRoomsPath(requireContext(), newPath)
                }
            }
            parentFragmentManager.popBackStackImmediate()
        }

        binding.cancelBtn.setOnClickListener {
            parentFragmentManager.popBackStackImmediate()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentUrlSetterBinding.inflate(inflater, container, false)
        return binding.root
    }

    private fun scanQR(button: Button, editText: EditText) {
        val barcodeLauncher = registerForActivityResult(ScanContract()) { result: ScanIntentResult ->
            result.contents?.let {
                editText.setText(it)
            }
            if (result.contents == null) {
                Toast.makeText(requireContext(), getString(R.string.Error), Toast.LENGTH_SHORT).show()
            }
        }

        button.setOnClickListener {
            barcodeLauncher.launch(optionsScanner)
        }
    }
}


