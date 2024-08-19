package com.example.airmonitorapp.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.HorizontalScrollView
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import com.example.airmonitorapp.R
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartModel
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartType
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartView
import com.github.aachartmodel.aainfographics.aachartcreator.AASeriesElement
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.jakewharton.threetenabp.AndroidThreeTen
import info.mqtt.android.service.MqttAndroidClient
import org.eclipse.paho.client.mqttv3.IMqttActionListener
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken
import org.eclipse.paho.client.mqttv3.IMqttToken
import org.eclipse.paho.client.mqttv3.MqttCallback
import org.eclipse.paho.client.mqttv3.MqttException
import org.eclipse.paho.client.mqttv3.MqttMessage
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter
import java.io.InputStream

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [fHome.newInstance] factory method to
 * create an instance of this fragment.
 */
class fHome : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var mqttAndroidClient: MqttAndroidClient
    private lateinit var txt_TEMP: TextView
    private lateinit var txt_HUM: TextView
    private lateinit var txt_LUX: TextView
    private lateinit var txt_METANA: TextView
    private lateinit var txt_PM10: TextView
    private lateinit var txt_PM100: TextView
    private lateinit var txt_PM25: TextView
    private lateinit var txt_CO2: TextView
    private lateinit var txt_mqtt_status: TextView
    private lateinit var txt_aqi_status: TextView
    private lateinit var view_horizontal: HorizontalScrollView
    private lateinit var view_linear: LinearLayout
    private lateinit var txt_status: TextView
    private var status: Boolean = false
    private var messageArrived: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
        AndroidThreeTen.init(requireActivity())
    }

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_f_home, container, false)

        //region inisiasi findViewbyId
        txt_TEMP = view.findViewById(R.id.api_home_valueTEMP)
        txt_HUM = view.findViewById(R.id.api_home_valueHUM)
        txt_LUX = view.findViewById(R.id.api_home_valueLUX)
        txt_METANA = view.findViewById(R.id.api_home_valueMETANA)
        txt_CO2 = view.findViewById(R.id.api_home_valueCO2)
        txt_PM10 = view.findViewById(R.id.api_home_valuePM10)
        txt_PM100 = view.findViewById(R.id.api_home_valuePM100)
        txt_PM25 = view.findViewById(R.id.api_home_valuePM25)
        txt_mqtt_status = view.findViewById(R.id.mqtt_status)
        txt_aqi_status = view.findViewById(R.id.aqi_status)
        view_horizontal = view.findViewById(R.id.home_horizontal)
        view_linear = view.findViewById(R.id.home_layout)
        txt_status = view.findViewById(R.id.home_txt_status)

        view_horizontal.visibility = View.GONE
        //endregion

        val btn_connect = view.findViewById<Button>(R.id.btn_connect_mqtt)
        val btn_disconnect = view.findViewById<Button>(R.id.btn_disconnect_mqtt)
        val btn_publish = view.findViewById<Button>(R.id.btn_publish_mqtt)
        val input = view.findViewById<EditText>(R.id.txt_publish_mqtt)
        btn_connect.setOnClickListener {
            if(!status){
                connect(requireActivity())
            } else {
                Toast.makeText(requireActivity(), "MQTT Is Already Connected", Toast.LENGTH_SHORT).show()
            }

        }
        btn_disconnect.setOnClickListener {
            if(!status){
                Toast.makeText(requireActivity(), "Can't Disconnect, because MQTT Is Not Connected", Toast.LENGTH_SHORT).show()
            } else {
                disconnect()
            }

        }

        btn_publish.setOnClickListener {
            if(!status){
                Toast.makeText(requireActivity(), "Please Connect To MQTT First", Toast.LENGTH_SHORT).show()
            } else {
                publish("ahan", input.text.toString())
                Toast.makeText(requireActivity(), "Data Successfully Published", Toast.LENGTH_SHORT).show()
            }
        }

        return view
    }

    fun connect(applicationContext : Context) {

        mqttAndroidClient = MqttAndroidClient ( applicationContext,
            "tcp://broker.hivemq.com",
            "1883" )

        mqttAndroidClient.setCallback(object : MqttCallback{
            override fun connectionLost(cause: Throwable?) {
                Log.d("Ariq BS", "connection lost: " + cause?.message.toString())
            }

            override fun messageArrived(topic: String?, message: MqttMessage?) {
                activity?.runOnUiThread {
                    Log.d("Ariq BS", "message: " + message.toString())

                    messageArrived = true
                    view_horizontal.visibility = View.VISIBLE
                    view_linear.visibility = View.GONE

                    val dataValues = message?.toString()?.split(" ")

                    if (dataValues != null && dataValues.size == 8) {
                        val temperature = dataValues[0]
                        val humidity = dataValues[1]
                        val brightness = dataValues[2]
                        val methane = dataValues[3]
                        val pm10 = dataValues[7]
                        val pm25 = dataValues[5]
                        val pm100 = dataValues[6]
                        val co2 = dataValues[4]

                        txt_TEMP.text = "$temperature"
                        txt_HUM.text = "$humidity"
                        txt_LUX.text = "$brightness"
                        txt_METANA.text = "$methane"
                        txt_PM10.text = "$pm10"
                        txt_PM25.text = "$pm25"
                        txt_PM100.text = "$pm100"
                        txt_CO2.text = "$co2"

                        if (pm25.toDouble() in 0.0..12.0) {
                            txt_aqi_status.text = "Good"
                        } else if (pm25.toDouble() in 12.1..35.4) {
                            txt_aqi_status.text = "Moderate"
                        } else if (pm25.toDouble() in 35.5..55.4) {
                            txt_aqi_status.text = "Unhealthy for Sensitive Groups"
                        } else if (pm25.toDouble() in 55.5..150.4) {
                            txt_aqi_status.text = "Unhealthy"
                        } else if (pm25.toDouble() in 150.5..250.4) {
                            txt_aqi_status.text = "Very Unhealthy"
                        } else if (pm25.toDouble() >= 250.5) {
                            txt_aqi_status.text = "Hazardous"
                        }

                        Log.d("Ariq BS", "Temperature: $temperature, Humidity: $humidity, Brightness: $brightness, Methane: $methane, PM10: $pm10, PM25: $pm25, PM100: $pm100, CO2: $co2")
                    } else {
                        Log.e("Ariq BS", "Invalid data format")
                    }
                }
            }

            override fun deliveryComplete(token: IMqttDeliveryToken?) {
                Log.d("Ariq BS", "complete: " + token.toString())
            }

        })

        try {
            val token = mqttAndroidClient.connect()
            token.actionCallback = object : IMqttActionListener {
                override fun onSuccess(asyncActionToken: IMqttToken){
                    Log.d("Ariq BS", "Koneksi broker berhasil")
                    subscribe("ramzi")
                }
                override fun onFailure(asyncActionToken: IMqttToken, exception: Throwable) {
                    Log.i("Ariq BS", "Koneksi broker gagal")
                    exception.printStackTrace()
                }
            }
        } catch (e: MqttException) {
            // Give your callback on connection failure here
            e.printStackTrace()
        }
    }

    fun subscribe(topic: String, qos: Int = 1) {
        try {
            mqttAndroidClient.subscribe(topic, qos, null, object : IMqttActionListener {
                override fun onSuccess(asyncActionToken: IMqttToken?) {
                    activity?.runOnUiThread {
                        Log.d("Ariq BS", "Subscribed to $topic")
                        if (!messageArrived) {
                            view_horizontal.visibility = View.GONE
                            view_linear.visibility = View.VISIBLE
                            txt_mqtt_status.text = "DEVICE IS CONNECTED TO MQTT"
                            status = true
                            txt_status.text = "The Air Quality Sensor is Not Online Right Now"
                        }
                    }
                }

                override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                    Log.d("Ariq BS", "Failed to subscribe $topic")
                }
            })
        } catch (e: MqttException) {
            e.printStackTrace()
        }
    }

    fun disconnect() {
        try {
            mqttAndroidClient.disconnect(null, object : IMqttActionListener {
                override fun onSuccess(asyncActionToken: IMqttToken?) {
                    activity?.runOnUiThread {
                        Log.d("Ariq BS", "Disconnected")

                        txt_mqtt_status.text = "DEVICE IS NOT CONNECTED TO MQTT"
                        status = false
                        txt_status.text = "Please Connect To MQTT First"
                        view_horizontal.visibility = View.GONE
                        view_linear.visibility = View.VISIBLE
                        txt_aqi_status.text = ""

                        messageArrived = false

                        txt_TEMP.text = ""
                        txt_HUM.text = ""
                        txt_LUX.text = ""
                        txt_METANA.text = ""
                        txt_PM10.text = ""
                        txt_PM25.text = ""
                        txt_PM100.text = ""
                        txt_CO2.text = ""
                    }

                }

                override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                    Log.d("Ariq BS", "Failed to disconnect")
                }
            })
        } catch (e: MqttException) {
            e.printStackTrace()
        }
    }

    fun publish(topic: String, msg: String, qos: Int = 1, retained: Boolean = false) {
        try {
            val message = MqttMessage()
            message.payload = msg.toByteArray()
            message.qos = qos
            message.isRetained = retained
            mqttAndroidClient.publish(topic, message, null, object : IMqttActionListener {
                override fun onSuccess(asyncActionToken: IMqttToken?) {
                    Log.d("Ariq BS", "$msg published to $topic")
                }

                override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                    Log.d("Ariq BS", "Failed to publish $msg to $topic")
                }
            })
        } catch (e: MqttException) {
            e.printStackTrace()
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment fHome.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            fHome().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}