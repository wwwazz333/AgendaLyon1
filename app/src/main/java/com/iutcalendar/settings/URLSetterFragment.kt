package com.iutcalendar.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_url_setter, container, false)
        binding = FragmentUrlSetterBinding.bind(view)

        val prevURL = binding.inputURL.text.toString()
        val prevURLRooms = binding.inputURLRooms.text.toString()

        //Calendrier classique
        binding.inputURL.let { editText: EditText ->
            editText.setText(DataGlobal.getSavedPath(context))

            binding.scanBtn.setOnClickListener {
                scanQR(editText)
            }
        }
        //Calendrier des salles
        binding.inputURLRooms.let { editText: EditText ->
            editText.setText(DataGlobal.getSavedRoomsPath(context))

            binding.scanBtn.setOnClickListener {
                scanQR(editText)
            }
        }



        binding.submitBtn.setOnClickListener {
            if (prevURL != binding.inputURL.text.toString()) {
                FileGlobal.getFileCalendar(context).delete()
                FileGlobal.getFile(context, FileGlobal.CHANGEMENT_EVENT).delete()
                DataGlobal.savePath(context, binding.inputURL.text.toString())
                Thread {
                    try {
                        FileDownload.updateFichier(FileGlobal.getFileCalendar(context).absolutePath, context)
                        //TODO faire un system de validation
                    } catch (_: InputStreamFileException) {
                    } catch (_: IOException) {
                    }
                }.start()
            }
            binding.inputURLRooms.text.toString().let { newPath ->
                if (prevURLRooms != newPath) {
                    DataGlobal.saveRoomsPath(context, newPath)
                }
            }
            parentFragmentManager.popBackStackImmediate()
        }

        binding.cancelBtn.setOnClickListener {
            parentFragmentManager.popBackStackImmediate()
        }
        return view
    }

    private fun scanQR(editText: EditText) {
        val barcodeLauncher = registerForActivityResult(ScanContract()) { result: ScanIntentResult ->
            result.contents?.let {
                editText.setText(it)
            }
            if (result.contents == null) {
                Toast.makeText(context, getString(R.string.Error), Toast.LENGTH_SHORT).show()
            }
        }


        val options = ScanOptions()
        options.setOrientationLocked(true)
        options.setBeepEnabled(false)
        barcodeLauncher.launch(options)
    }
}


