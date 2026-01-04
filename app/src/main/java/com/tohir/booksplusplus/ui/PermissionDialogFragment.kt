package com.tohir.booksplusplus.ui

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.tohir.booksplusplus.R

class PermissionDialogFragment : DialogFragment() {

    var onPermissionRequest: (() -> Unit)? = null
    var onCancel: (() -> Unit)? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setView(R.layout.alarm_permission)
            .create()

        dialog.setCancelable(false)
        dialog.setCanceledOnTouchOutside(false)

        return dialog
    }

    override fun onResume() {
        super.onResume()
        dialog?.findViewById<MaterialButton>(R.id.button_allow)?.setOnClickListener {
            dismiss()
            onPermissionRequest?.invoke()
        }

        dialog?.findViewById<MaterialButton>(R.id.button_not_now)?.setOnClickListener {
            dismiss()
            onCancel?.invoke()
        }
    }
}