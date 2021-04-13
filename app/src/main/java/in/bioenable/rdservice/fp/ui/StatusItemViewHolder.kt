package `in`.bioenable.rdservice.fp.ui

import `in`.bioenable.rdservice.fp.R
import `in`.bioenable.rdservice.fp.contracts.StatusItemView
import android.content.DialogInterface
import androidx.core.content.ContextCompat
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView

open class StatusItemViewHolder (val view: View,title:String,icon:Int) : StatusItemView {

    private val tvTitle = view.findViewById<TextView>(R.id.status_title)
    private val ivIcon = view.findViewById<ImageView>(R.id.status_icon)
    private val progress = view.findViewById<ProgressBar>(R.id.status_progress)
    private val button = view.findViewById<Button>(R.id.status_btn)
    private val textView = view.findViewById<TextView>(R.id.status_info)
    private val textView2 = view.findViewById<TextView>(R.id.validity_info)

    init {
        tvTitle.text = title
        ivIcon.setImageResource(icon)
    }

    override fun setMessage(msg: String) {
        textView.text = msg
     //   textView2.text = validity
        tvTitle.setCompoundDrawables(null,null,null,null)
        tvTitle.error = null
    }

    override fun setValidity(validity: String) {
         textView2.text = validity
    }

    override fun setError(error: String) {
        textView.text = error
        tvTitle.error = ""
    }

    override fun setWarning(warning: String) {
        tvTitle.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.ic_warning_yellow_24dp,0)
        textView.text = warning
    }

    override fun showButton() {
        button.visibility = View.VISIBLE
    }

    override fun setButtonText(text: String) {
        button.setText(text)
    }

    override fun enableButton() {
        button.isEnabled = true
    }

    override fun disableButton() {
        button.isEnabled = false
    }

    override fun hideButton() {
        button.visibility = View.GONE
    }

    override fun showProgress() {
        progress.visibility = View.VISIBLE
    }

    override fun hideProgress() {
        progress.visibility = View.GONE
    }

    override fun hide() {
        view.visibility = View.GONE
    }

    override fun show() {
        view.visibility = View.VISIBLE
    }

    override fun setAction(runnable: Runnable) {
        button.setOnClickListener { runnable.run() }
    }
}