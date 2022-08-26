package com.iutcalendar.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

        binding.inputURL.setText(DataGlobal.getSavedPath(context))
        val barcodeLauncher = registerForActivityResult(ScanContract()) { result: ScanIntentResult ->
            result.contents?.let {
                binding.inputURL.setText(it)
            }
            if (result.contents == null) {
                Toast.makeText(context, getString(R.string.Error), Toast.LENGTH_SHORT).show()
            }
        }
        binding.scanBtn.setOnClickListener {
            val options = ScanOptions()
            options.setOrientationLocked(true)
            options.setBeepEnabled(false)
            barcodeLauncher.launch(options)
        }
        val prevURL = binding.inputURL.text.toString()
        binding.submitBtn.setOnClickListener {
            if (prevURL != binding.inputURL.text.toString()) {
                FileGlobal.getFileDownload(context).delete()
                FileGlobal.getFile(context, FileGlobal.CHANGEMENT_EVENT).delete()
                DataGlobal.savePath(context, binding.inputURL.text.toString())
                Thread {
                    try {
                        FileDownload.updateFichier(FileGlobal.getFileDownload(context).absolutePath, context)
                    } catch (_: InputStreamFileException) {
                    } catch (_: IOException) {
                    }
                }.start()
            }
            parentFragmentManager.popBackStackImmediate()
        }
        binding.cancelBtn.setOnClickListener {
            parentFragmentManager.popBackStackImmediate()
        }
        return view
    }
}