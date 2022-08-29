package com.iutcalendar.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.iutcalendar.data.DataGlobal
import com.iutcalendar.data.FileGlobal
import com.iutcalendar.dialog.DialogMessage
import com.iutcalendar.filedownload.FileDownload
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanIntentResult
import com.journeyapps.barcodescanner.ScanOptions
import com.univlyon1.tools.agenda.R
import com.univlyon1.tools.agenda.databinding.FragmentUrlSetterBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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
            lifecycleScope.launch(Dispatchers.IO) {
                withContext(Dispatchers.Main) { loading(true) }
                var succes = true
                binding.inputURL.text.toString().let { newPath ->
                    if (newPath.isNotEmpty() && !FileDownload.isValideURL(newPath)) {
                        succes = false
                        withContext(Dispatchers.Main) {
                            DialogMessage.showWarning(
                                requireContext(), getString(R.string.url_invalide), getString(R.string.url_invalide_msg)
                            ) {
                                loading(false)
                            }
                        }
                    } else if (prevURL != newPath) {
                        FileGlobal.getFileCalendar(requireContext()).delete()
                        FileGlobal.getFile(requireContext(), FileGlobal.CHANGEMENT_EVENT).delete()
                        DataGlobal.savePath(requireContext(), newPath)

                        try {
                            FileDownload.updateFichier(FileGlobal.getFileCalendar(requireContext()).absolutePath, requireContext())
                        } catch (exception: Exception) {
                        }
                    }
                }
                binding.inputURLRooms.text.toString().let { newPath ->

                    if (newPath.isNotEmpty() && !FileDownload.isValideURL(newPath)) {
                        succes = false
                        withContext(Dispatchers.Main) {
                            DialogMessage.showWarning(
                                requireContext(), getString(R.string.url_rooms_invalide), getString(R.string.url_rooms_invalide_msg)
                            ) {
                                loading(false)
                            }
                        }
                    } else if (prevURLRooms != newPath) {
                        DataGlobal.saveRoomsPath(requireContext(), newPath)
                    }
                }

                withContext(Dispatchers.Main) {
                    if (succes) {
                        parentFragmentManager.popBackStackImmediate()
                        loading(false)
                    }
                }
            }
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

    private fun loading(state: Boolean) {
        if (state) {
            binding.blockClickView.visibility = View.VISIBLE
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.blockClickView.visibility = View.INVISIBLE
            binding.progressBar.visibility = View.INVISIBLE
        }
    }
}


