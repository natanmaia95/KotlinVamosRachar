package com.example.constraintlayout

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.util.*

class MainActivity : AppCompatActivity() , TextToSpeech.OnInitListener {
    private lateinit var tts: TextToSpeech
    private lateinit var edtConta: EditText
    private lateinit var edtNumberOfPersons: EditText
    private lateinit var resultText: TextView
    private lateinit var shareButton: FloatingActionButton
    private lateinit var speechButton: FloatingActionButton
    private var ttsSucess: Boolean = false;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        edtConta = findViewById<EditText>(R.id.edtConta)
        edtNumberOfPersons = findViewById<EditText>(R.id.edtPessoas)
        resultText = findViewById<TextView>(R.id.textResultado)
        shareButton = findViewById<FloatingActionButton>(R.id.shareButton)
        speechButton = findViewById<FloatingActionButton>(R.id.actBtnFalar)
        edtConta.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                onChangedTextPriceInput(s.toString())
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        edtNumberOfPersons.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                onChangedTextNumberOfPersonsInput(s.toString())
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        shareButton.setOnClickListener {
            shareMessageWithResult();
        }

        speechButton.setOnClickListener {
            val priceResult = resultText.text.toString();
            if(priceResult.isNotBlank()) {
                clickFalar(priceResult)
            }
        }

        // Initialize TTS engine
        tts = TextToSpeech(this, this)

    }


    fun clickFalar(stringToSpeech: String){
        if (tts.isSpeaking) {
            tts.stop()
        }
        if(ttsSucess) {
            tts.setLanguage(Locale("pt", "BR"))

            tts.speak("o valor final é $stringToSpeech", TextToSpeech.QUEUE_FLUSH, null, null)
        }

    }
    override fun onDestroy() {
            // Release TTS engine resources
            tts.stop()
            tts.shutdown()
            super.onDestroy()
        }

    override fun onInit(status: Int) {
            if (status == TextToSpeech.SUCCESS) {
                // TTS engine is initialized successfully
                tts.language = Locale.getDefault()
                ttsSucess=true
                Log.d("PDM23","Sucesso na Inicialização")
            } else {
                // TTS engine failed to initialize
                Log.e("PDM23", "Failed to initialize TTS engine.")
                ttsSucess=false
            }
        }

    fun onChangedTextPriceInput(value: String) {
        if (value.isNotBlank()) {
            val priceValue = value.toDouble();
            val numberOfPersonsText = edtNumberOfPersons.text.toString()
            if(numberOfPersonsText.isNotBlank()) {
                val numberOfPersonsValue = numberOfPersonsText.toInt();
                if(numberOfPersonsValue != 0) {
                    val divisionResult = priceValue / numberOfPersonsValue;
                    resultText.text = "R$ %.2f".format(divisionResult).replace('.', ',');
                }
            }

        }else {
            resultText.text = "R$ 0,00";
        }
    }

    fun onChangedTextNumberOfPersonsInput(value: String) {
        if(value.isNotBlank()) {
            val numberOfPersons = value.toInt();
            if(numberOfPersons != 0) {
                val priceValue = edtConta.text.toString().toDouble();
                val divisionResult = priceValue / numberOfPersons;
                resultText.text = "R$ %.2f".format(divisionResult).replace('.', ',');
            }
        }else {
            resultText.text = "R$ 0,00";
        }
    }

    fun shareMessageWithResult() {
        val resultTextString: String = resultText.text.toString();
        if(resultTextString != "R$ 0,00") {
            val numberOfPersons = edtNumberOfPersons.text.toString();
            val priceValue = edtConta.text.toString();
            val sendIntent: Intent = Intent().apply {
                action = Intent.ACTION_SEND;
                putExtra(
                    Intent.EXTRA_TEXT,
                    "O número de pessoas é $numberOfPersons, o valor é $priceValue e o resultado da divisão é $resultTextString"
                );
                type = "text/plain";
            }
            val shareIntent = Intent.createChooser(sendIntent, null);
            startActivity(shareIntent);
        }

    }


}

