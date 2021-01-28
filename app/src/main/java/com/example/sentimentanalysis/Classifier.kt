package com.example.sentimentanalysis

import android.content.Context
import android.os.AsyncTask
import android.util.Log
import org.json.JSONObject
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.gpu.GpuDelegate
import org.tensorflow.lite.support.common.FileUtil
import java.io.IOException
import java.util.*
import kotlin.collections.HashMap


class Classifier(context: Context, jsonFilename: String) {

    private var context: Context? = context
    private var filename: String? = jsonFilename
    private var callback: DataCallback? = null
    private var maxlen: Int? = 200
    private var vocabData: HashMap<String, Int>? = null
    private var interpreter: Interpreter? = null

    fun loadData() {
        val loadVocabularyTask = LoadVocabularyTask(callback)
        loadVocabularyTask.execute(loadJSONFromAsset(filename))
        loadModel()
    }

    fun unload() {
        interpreter?.close()
        vocabData?.clear()
    }

    private fun loadJSONFromAsset(filename: String?): String? {
        var json: String? = null
        try {
            val inputStream = context!!.assets.open(filename!!)
            val size = inputStream.available()
            val buffer = ByteArray(size)
            inputStream.read(buffer)
            inputStream.close()
            json = String(buffer)
        } catch (ex: IOException) {
            ex.printStackTrace()
            return null
        }
        return json
    }

    fun setCallback(callback: DataCallback) {
        this.callback = callback
    }

    fun tokenize(message: String): IntArray {
        var str = message
        for (c in message) {
            if (c.isLetter()) {
                str += c
            } else {
                str += " "
            }
        }
        str = str.toLowerCase()
        val parts: List<String> = str.split(" ")
        val tokenizedMessage = ArrayList<Int>()
        for (part in parts) {
            if (part.trim() != "") {
                var index: Int? = 0
                if (vocabData!![part] == null) {
                    index = 0
                } else {
                    index = vocabData!![part]
                }
                tokenizedMessage.add(index!!)
            }
        }
        return tokenizedMessage.toIntArray()
    }

    fun padSequence(sequence: IntArray): IntArray {
        val maxlen = this.maxlen
        if (sequence.size > maxlen!!) {
            return sequence.sliceArray(0 until maxlen)
        } else if (sequence.size < maxlen) {
            val array = ArrayList<Int>()
            for (i in array.size until maxlen - sequence.size) {
                array.add(0)
            }
            array.addAll(sequence.asList())
            return array.toIntArray()
        } else {
            return sequence
        }
    }

    fun classify(text: String): FloatArray {
        val tokenized = tokenize(text)
        val sequence = padSequence(tokenized)
        val inputs: Array<FloatArray> = arrayOf(sequence.map { it.toFloat() }.toFloatArray())
        val outputs: Array<FloatArray> = arrayOf(floatArrayOf(0.0f))
        if (interpreter == null) {
            Log.e("TAG", "classify: interpreter is null")
        }
        interpreter?.run(inputs, outputs)
        return outputs[0]
    }

    fun setVocab(data: HashMap<String, Int>?) {
        this.vocabData = data
    }

    fun setMaxLength(maxlen: Int) {
        this.maxlen = maxlen
    }

    fun destroy() {
        context = null
        filename = null
        maxlen = null
        callback = null
        vocabData?.clear()
        vocabData = null
    }

    interface DataCallback {
        fun onDataProcessed(result: HashMap<String, Int>?)
    }

    private inner class LoadVocabularyTask(callback: DataCallback?) :
        AsyncTask<String, Void, HashMap<String, Int>?>() {

        private var callback: DataCallback? = callback

        override fun doInBackground(vararg params: String?): HashMap<String, Int>? {
            val jsonObject = JSONObject(params[0])
            val iterator: Iterator<String> = jsonObject.keys()
            val data = HashMap<String, Int>()
            while (iterator.hasNext()) {
                val key = iterator.next()
                data.put(key, jsonObject.get(key) as Int)
            }
            return data
        }

        override fun onPostExecute(result: HashMap<String, Int>?) {
            super.onPostExecute(result)
            callback?.onDataProcessed(result)
        }

    }

    private fun loadModel() {
        try {
            interpreter = Interpreter(
                FileUtil.loadMappedFile(context!!, "model.tflite"),
                Interpreter.Options().apply { addDelegate(GpuDelegate()) })

        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }


}