package com.example.vamosrachar

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.lang.Error
import java.text.NumberFormat
import java.util.*

class MainActivity : AppCompatActivity(), TextToSpeech.OnInitListener, TextWatcher {
    private lateinit var finalValueField: TextView
    private lateinit var groupField: TextView
    private lateinit var moneyField: TextView
    private lateinit var btnShare: FloatingActionButton
    private lateinit var btnSpeak: FloatingActionButton
    private lateinit var tts: TextToSpeech

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        this.tts = TextToSpeech(this, this)

        this.finalValueField = findViewById(R.id.valorFinal)
        this.groupField = findViewById(R.id.editTextGroup)
        this.moneyField = findViewById(R.id.editTextMoney)
        groupField.addTextChangedListener(this)
        moneyField.addTextChangedListener(this)
        this.setValorFinal(0.0)

        this.btnShare = findViewById(R.id.shareActionButton)
        this.btnSpeak = findViewById(R.id.speakActionButton)
        btnShare.setOnClickListener { onBtnShare() }
        btnSpeak.setOnClickListener { onBtnSpeak() }

        moneyField.text = savedInstanceState?.getCharSequence("moneyField") ?: ""
        groupField.text = savedInstanceState?.getCharSequence("groupField") ?: ""
        finalValueField.text = savedInstanceState?.getCharSequence("finalValueField") ?: ""
    }


    override fun onInit(p0: Int) {
//        if (p0 == TextToSpeech.SUCCESS) {
//            val result = tts.setLanguage(Locale.ROOT)
//            if(result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
//                callToast(getString(R.string.error_tts_not_supported))
//            }
//        }
    }

    override fun onDestroy() {
        tts.stop()
        tts.shutdown()
        super.onDestroy()
    }


    override fun onSaveInstanceState(outState: Bundle) {
        outState.putCharSequence("moneyField", moneyField.text)
        outState.putCharSequence("groupField", groupField.text)
        outState.putCharSequence("finalValueField", finalValueField.text)
        super.onSaveInstanceState(outState)
    }


    private fun onBtnShare() {
        if(checkValidInput()) {
            val intent = Intent(Intent.ACTION_SEND)
            intent.type = "text/plain"
            intent.putExtra(
                Intent.EXTRA_TEXT,
                getString(R.string.text_intent)
                    .format(finalValueField.text.toString()
                        .replace(getString(R.string.currency),"").trim(),
                        getString(R.string.currency_name)
                    )
            )
            startActivity(Intent.createChooser(intent, getString(R.string.intent_choose_text)))
        }
    }


    private fun onBtnSpeak() {
        val text = getString(R.string.text_to_speak).format(
            finalValueField.text.toString()
        )
        Log.v("PDM-log", text)
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, "")
    }


    private fun formatCurrency(): NumberFormat{
        val currencyFormat = NumberFormat.getCurrencyInstance()
        currencyFormat.currency = Currency.getInstance(getString(R.string.currency_format))
        currencyFormat.maximumFractionDigits = 2
        return currencyFormat
    }


    private fun setValorFinal(valor: Double) {
        finalValueField.text = formatCurrency().format(valor)
    }


    private fun checkValidInput(): Boolean {
        if (!groupField.text.isNullOrBlank() && !moneyField.text.isNullOrBlank()) {
            if (groupField.text.toString().toInt() > 0) {
                return true
            }
        }
        return false
    }


    private fun callToast(text: String?) {
        val context = applicationContext
        val duration = Toast.LENGTH_SHORT
        val toast = Toast.makeText(context, text, duration)
        toast.show()
    }

    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

    }

    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

    }

    override fun afterTextChanged(p0: Editable?) {
        if(checkValidInput()) {
            setValorFinal((moneyField.text.toString().toDouble() / groupField.text.toString().toInt()))
        } else {
            setValorFinal(0.0)
        }
    }
}


