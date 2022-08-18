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
import java.io.IOException

class URLSetterFragment : Fragment() {
    private lateinit var scan: Button
    private lateinit var valide: Button
    private lateinit var cancel: Button
    private lateinit var input: EditText

    private fun initVariable(view: View) {
        scan = view.findViewById(R.id.scanBtn)
        valide = view.findViewById(R.id.submitBtn)
        cancel = view.findViewById(R.id.cancelBtn)
        input = view.findViewById(R.id.inputURL)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_url_setter, container, false)
        initVariable(view)
        input.setText(DataGlobal.getSavedPath(context))
        val barcodeLauncher = registerForActivityResult(
            ScanContract()
        ) { result: ScanIntentResult ->
            if (result.contents != null) {
                Toast.makeText(context, "QR code scanné", Toast.LENGTH_SHORT).show()
                val input = view.findViewById<EditText>(R.id.inputURL)
                input.setText(result.contents)
            } else {
                Toast.makeText(context, "échec", Toast.LENGTH_SHORT).show()
            }
        }
        scan.setOnClickListener {
            val options = ScanOptions()
            options.setOrientationLocked(true)
            options.setBeepEnabled(false)
            barcodeLauncher.launch(options)
        }
        val prevURL = input.text.toString()
        valide.setOnClickListener {
            if (prevURL != input.text.toString()) {
                FileGlobal.getFileDownload(context).delete()
                FileGlobal.getFile(context, FileGlobal.CHANGEMENT_EVENT).delete()
                DataGlobal.savePath(context, input.text.toString())
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
        cancel.setOnClickListener {
            parentFragmentManager.popBackStackImmediate()
        }
        return view
    }
}