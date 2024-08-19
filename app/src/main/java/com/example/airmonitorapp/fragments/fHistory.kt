package com.example.airmonitorapp.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.airmonitorapp.R
import com.example.airmonitorapp.data.DataItem
import com.example.airmonitorapp.data.ResponseItem
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartModel
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartType
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartView
import com.github.aachartmodel.aainfographics.aachartcreator.AASeriesElement
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

/**
 * A simple [Fragment] subclass.
 * Use the [fHistory.newInstance] factory method to
 * create an instance of this fragment.
 */
class fHistory : Fragment() {

    //region inisiasi variabel goal
    private lateinit var terpilih: String
    private lateinit var dataChart2: List<Double>
    private lateinit var chart:AAChartView
    private lateinit var txt_avgCO2: TextView
    private lateinit var txt_maxCO2: TextView
    private lateinit var txt_minCO2: TextView
    private lateinit var SpinnerAPI: Spinner
    private lateinit var SpinnerAPI2: Spinner
    private lateinit var SpinnerData: Spinner
    private lateinit var SpinnerTgl1: Spinner
    private lateinit var SpinnerTgl2: Spinner
    private lateinit var txt_TEMP: TextView
    private lateinit var txt_HUM: TextView
    private lateinit var txt_LUX: TextView
    private lateinit var txt_METANA: TextView
    private lateinit var txt_PM10: TextView
    private lateinit var txt_PM100: TextView
    private lateinit var txt_PM25: TextView
    private lateinit var txt_CO2: TextView
    private lateinit var txt_Status: TextView
    private lateinit var txt_Judul: TextView
    private lateinit var adapter: ArrayAdapter<String>

    private var pilihan1: Int = 0
    private var pilihan2: Int = 0
    private var pilihan3: Int = 6
    private var d_maxCO2: String = ""
    private var d_minCO2: String = ""
    private var d_avgCO2: String = ""
    private var w_maxCO2: String = ""
    private var w_minCO2: String = ""
    private var w_avgCO2: String = ""
    private var m_maxCO2: String = ""
    private var m_minCO2: String = ""
    private var m_avgCO2: String = ""
    private val data_air: MutableList<DataItem> = mutableListOf()
    private val week_temp: ArrayList<String> = ArrayList()
    private val week_hum: ArrayList<String> = ArrayList()
    private val week_lux: ArrayList<String> = ArrayList()
    private val week_metana: ArrayList<String> = ArrayList()
    private val week_co2: ArrayList<String> = ArrayList()
    private val week_pm10: ArrayList<String> = ArrayList()
    private val week_pm100: ArrayList<String> = ArrayList()
    private val week_pm25: ArrayList<String> = ArrayList()
    private val dateList: ArrayList<String> = ArrayList()

    private val BASE_URL = "https://air-quality-itenas.000webhostapp.com/"
    //endregion

    //region Interface API yang digunakan
    interface MyAPI {
        @GET("ariq/get_daily_co2.php")
        fun getDailyCO2(): Call<List<ResponseItem>>

        @GET("ariq/get_weekly_co2.php")
        fun getWeeklyCO2(): Call<List<ResponseItem>>

        @GET("ariq/get_monthly_co2.php")
        fun getMonthlyCO2(): Call<List<ResponseItem>>

        @GET("arkan/get_7_days_avg.php")
        fun getWeekData(): Call<List<DataItem>>
    }
    //endregion

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_f_history, container, false)

        //region inisiasi findViewById
        SpinnerData = view.findViewById(R.id.api_spinner_data)
        SpinnerTgl1 = view.findViewById(R.id.api_spinner_tgl1)
        SpinnerTgl2 = view.findViewById(R.id.api_spinner_tgl2)

        txt_TEMP = view.findViewById(R.id.api_valueTEMP)
        txt_HUM = view.findViewById(R.id.api_valueHUM)
        txt_LUX = view.findViewById(R.id.api_valueLUX)
        txt_METANA = view.findViewById(R.id.api_valueMETANA)
        txt_CO2 = view.findViewById(R.id.api_valueCO2)
        txt_PM10 = view.findViewById(R.id.api_valuePM10)
        txt_PM100 = view.findViewById(R.id.api_valuePM100)
        txt_PM25 = view.findViewById(R.id.api_valuePM25)
        txt_Status = view.findViewById(R.id.api_valueStatus)
        txt_Judul = view.findViewById(R.id.api_statusJudul)

        SpinnerAPI = view.findViewById(R.id.api_spinner)
        SpinnerAPI2 = view.findViewById(R.id.api_spinner_7days)
        txt_avgCO2 = view.findViewById(R.id.api_avgco2)
        txt_maxCO2 = view.findViewById(R.id.api_maxco2)
        txt_minCO2 = view.findViewById(R.id.api_minco2)
        chart = view.findViewById(R.id.history_card3_chart)
        //endregion

        //region setting spinner
        val typeList: ArrayList<String> = arrayListOf("TEMP", "HUM", "LUX", "CH4", "PM1", "PM10", "PM2.5", "CO2")

        val adapter_spin = ArrayAdapter(
            requireActivity(),
            R.layout.spinner_list_content,
            typeList
        )

        adapter = ArrayAdapter<String>(
            requireActivity(),
            R.layout.spinner_list_content
        )

        adapter_spin.setDropDownViewResource(R.layout.spinner_dropdown_content)

        SpinnerData.adapter = adapter_spin

        SpinnerAPI.setOnItemSelectedListener(
            object : AdapterView.OnItemSelectedListener{
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    if(position == 0){
                        txt_avgCO2.text = d_avgCO2
                        txt_maxCO2.text = d_maxCO2
                        txt_minCO2.text = d_minCO2
                    } else if(position == 1) {
                        txt_avgCO2.text = w_avgCO2
                        txt_maxCO2.text = w_maxCO2
                        txt_minCO2.text = w_minCO2
                    } else if(position == 2) {
                        txt_avgCO2.text = m_avgCO2
                        txt_maxCO2.text = m_maxCO2
                        txt_minCO2.text = m_minCO2
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    TODO("Not yet implemented")
                }

            })

        SpinnerAPI2.setOnItemSelectedListener(
            object : AdapterView.OnItemSelectedListener{
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    if(position == 0){
                        updateData(data_air, 0)
                    } else if(position == 1) {
                        updateData(data_air, 1)
                    } else if(position == 2) {
                        updateData(data_air, 2)
                    } else if (position == 3) {
                        updateData(data_air, 3)
                    } else if (position == 4) {
                        updateData(data_air, 4)
                    } else if (position == 5) {
                        updateData(data_air, 5)
                    } else if (position == 6) {
                        updateData(data_air, 6)
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    TODO("Not yet implemented")
                }

            })

        var initialSelection = true

        SpinnerData.setOnItemSelectedListener(
            object : AdapterView.OnItemSelectedListener{
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    // Check if it's the initial selection
                    if (initialSelection) {
                        initialSelection = false
                    } else {
                        // Your existing code for handling item selection
                        pilihan1 = position
                        updateChart(dateList, pilihan1, pilihan2, pilihan3)
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    TODO("Not yet implemented")
                }

            })

        SpinnerTgl1.setOnItemSelectedListener(
            object : AdapterView.OnItemSelectedListener{
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    if(position <= pilihan3){
                        pilihan2 = position
                        updateChart(dateList, pilihan1, pilihan2, pilihan3)
                    } else {
                        Toast.makeText(requireActivity(), "The Start Date Cannot be After the" +
                                " End Date", Toast.LENGTH_SHORT).show()
                        SpinnerTgl1.setSelection(pilihan2)
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    TODO("Not yet implemented")
                }

            })

        SpinnerTgl2.setOnItemSelectedListener(
            object : AdapterView.OnItemSelectedListener{
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    if(position >= pilihan2){
                        pilihan3 = position
                        updateChart(dateList, pilihan1, pilihan2, pilihan3)
                    } else {
                        Toast.makeText(requireActivity(), "The End Date cannot be before the" +
                                " Start Date", Toast.LENGTH_SHORT).show()
                        SpinnerTgl2.setSelection(pilihan3)
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    TODO("Not yet implemented")
                }

            })
        //endregion

        getData()

        return view
    }

    //region update data sensor
    private fun updateData(data: MutableList<DataItem>, pos: Int){
        val data_bydate = data[pos]

        txt_TEMP.text = data_bydate.suhu
        txt_HUM.text = data_bydate.kelembaban
        txt_LUX.text = data_bydate.kecerahan
        txt_METANA.text = data_bydate.metana
        txt_CO2.text = data_bydate.co2
        txt_PM10.text = data_bydate.pm10
        txt_PM100.text = data_bydate.pm100
        txt_PM25.text = data_bydate.pm25

        txt_Judul.text = "CONCLUSION FOR ${data_bydate.tanggal} AQI (Air Quality Index):"

        val nilai_pm25 = data_bydate.pm25?.toInt()

        if (nilai_pm25 != null) {
            if (nilai_pm25 >= 0 && nilai_pm25 <= 12.0) {
                txt_Status.text = "Good"
            } else if (nilai_pm25 >= 12.1 && nilai_pm25 <= 35.4) {
                txt_Status.text = "Moderate"
            } else if (nilai_pm25 >= 35.5 && nilai_pm25 <= 55.4) {
                txt_Status.text = "Unhealthy for Sensitive Groups"
            } else if (nilai_pm25 >= 55.5 && nilai_pm25 <= 150.4) {
                txt_Status.text = "Unhealthy"
            } else if (nilai_pm25 >= 150.5 && nilai_pm25 <= 250.4) {
                txt_Status.text = "Very Unhealthy"
            } else if (nilai_pm25 >= 250.5) {
                txt_Status.text = "Hazardous"
            }
        }
    }
    //endregion

    //region chart card
    private fun updateChart(dateList : MutableList<String>, pil1 : Int,  pil2 : Int,  pil3 : Int) {
        var dataChart : List<Double> = listOf()
        var dateChart = dateList

        //region kondisi if pilihan1
        when (pil1) {
            0 -> {
                terpilih = "TEMP"
                val doubleList: List<Double> = week_temp.map { it.toDouble() }
                dataChart2 = doubleList
            }
            1 -> {
                terpilih = "HUM"
                val doubleList: List<Double> = week_hum.map { it.toDouble() }
                dataChart2 = doubleList
            }
            2 -> {
                terpilih = "LUX"
                val doubleList: List<Double> = week_lux.map { it.toDouble() }
                dataChart2 = doubleList
            }
            3 -> {
                terpilih = "CH4"
                val doubleList: List<Double> = week_metana.map { it.toDouble() }
                dataChart2 = doubleList
            }
            4 -> {
                terpilih = "PM1"
                val doubleList: List<Double> = week_pm10.map { it.toDouble() }
                dataChart2 = doubleList
            }
            5 -> {
                terpilih = "PM10"
                val doubleList: List<Double> = week_pm100.map { it.toDouble() }
                dataChart2 = doubleList
            }
            6 -> {
                terpilih = "PM2.5"
                val doubleList: List<Double> = week_pm25.map { it.toDouble() }
                dataChart2 = doubleList
            }
            7 -> {
                terpilih = "CO2"
                val doubleList: List<Double> = week_co2.map { it.toDouble() }
                dataChart2 = doubleList
            }
        }
        //endregion

        if (pil2 in 0 until 7 && pil3 in 0 until 7) {
            dataChart = dataChart2.subList(pil2, pil3+1)
            dateChart = dateList.subList(pil2, pil3+1)
        }

        val aaChartModel2 : AAChartModel = AAChartModel()
            .chartType(AAChartType.Bar)
            .title("SPECIFIC DAY $terpilih VALUE")
            .categories(arrayOf(*dateChart.toTypedArray()))
            .backgroundColor("#FFFFFF")
            .dataLabelsEnabled(true)
            .series(
                arrayOf(
                    AASeriesElement()
                        .name("$terpilih VALUE")
                        .data(dataChart.toTypedArray()),
                )
            )
            .colorsTheme(arrayOf("#6EB1D6"))
        chart.aa_drawChartWithChartModel(aaChartModel2)
    }
    //endregion

    //region fungsi untuk memasukkan data API ke recycle view
    private fun getData() {
        //region mengambil data API menggunakan retrofit
        val api = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(MyAPI::class.java)
        //endregion

        //region memasukkan data daily ke variabel dari json yang diambil dari API
        api.getDailyCO2().enqueue(object : Callback<List<ResponseItem>> {
            override fun onResponse(call: Call<List<ResponseItem>>, response: Response<List<ResponseItem>>) {
                if (response.isSuccessful) {
                    response.body()?.let { responseItemList ->
                        for (item in responseItemList) {
                            Log.d("API_RESPONSE", "min: ${item.min}, avg: ${item.avg}, max: ${item.max}")
                            d_avgCO2 = item.avg.toString()
                            d_maxCO2 = item.max.toString()
                            d_minCO2 = item.min.toString()
                            txt_avgCO2.text = d_avgCO2
                            txt_maxCO2.text = d_maxCO2
                            txt_minCO2.text = d_minCO2
                        }
                    }
                }
            }

            override fun onFailure(call: Call<List<ResponseItem>>, t: Throwable) {
                d_avgCO2 = "DATA IS NOT AVAILABLE"
                d_maxCO2 = "DATA IS NOT AVAILABLE"
                d_minCO2 = "DATA IS NOT AVAILABLE"
            }
        })

        api.getWeeklyCO2().enqueue(object : Callback<List<ResponseItem>> {
            override fun onResponse(call: Call<List<ResponseItem>>, response: Response<List<ResponseItem>>) {
                if (response.isSuccessful) {
                    response.body()?.let { responseItemList ->
                        for (item in responseItemList) {
                            Log.d("API_RESPONSE", "min: ${item.min}, avg: ${item.avg}, max: ${item.max}")
                            w_avgCO2 = item.avg.toString()
                            w_maxCO2 = item.max.toString()
                            w_minCO2 = item.min.toString()
                        }
                    }
                }
            }

            override fun onFailure(call: Call<List<ResponseItem>>, t: Throwable) {
                w_avgCO2 = "DATA IS NOT AVAILABLE"
                w_maxCO2 = "DATA IS NOT AVAILABLE"
                w_minCO2 = "DATA IS NOT AVAILABLE"
            }
        })

        api.getMonthlyCO2().enqueue(object : Callback<List<ResponseItem>> {
            override fun onResponse(call: Call<List<ResponseItem>>, response: Response<List<ResponseItem>>) {
                if (response.isSuccessful) {
                    response.body()?.let { responseItemList ->
                        for (item in responseItemList) {
                            Log.d("API_RESPONSE", "min: ${item.min}, avg: ${item.avg}, max: ${item.max}")
                            m_avgCO2 = item.avg.toString()
                            m_maxCO2 = item.max.toString()
                            m_minCO2 = item.min.toString()
                        }
                    }
                }
            }

            override fun onFailure(call: Call<List<ResponseItem>>, t: Throwable) {
                m_avgCO2 = "DATA IS NOT AVAILABLE"
                m_maxCO2 = "DATA IS NOT AVAILABLE"
                m_minCO2 = "DATA IS NOT AVAILABLE"
            }
        })

        api.getWeekData().enqueue(object : Callback<List<DataItem>> {
            override fun onResponse(call: Call<List<DataItem>>, response: Response<List<DataItem>>) {
                if (response.isSuccessful) {
                    response.body()?.let { responseItemList ->

                        data_air.addAll(responseItemList)

                        for (item in responseItemList) {
                            week_temp.add(item.suhu ?: "N/A")
                            week_hum.add(item.kelembaban ?: "N/A")
                            week_lux.add(item.kecerahan ?: "N/A")
                            week_metana.add(item.metana ?: "N/A")
                            week_co2.add(item.co2 ?: "N/A")
                            week_pm10.add(item.pm10 ?: "N/A")
                            week_pm100.add(item.pm100 ?: "N/A")
                            week_pm25.add(item.pm25 ?: "N/A")
                            dateList.add(item.tanggal ?: "N/A")
                        }

                        adapter.addAll(dateList)
                        adapter.setDropDownViewResource(R.layout.spinner_dropdown_content)

                        SpinnerAPI2.adapter = adapter
                        SpinnerTgl1.adapter = adapter
                        SpinnerTgl2.adapter = adapter

                        SpinnerData.setSelection(0)
                        SpinnerTgl1.setSelection(0)
                        SpinnerTgl2.setSelection(6)

                        updateChart(dateList, pilihan1, pilihan2, pilihan3)
                    }
                }
            }

            override fun onFailure(call: Call<List<DataItem>>, t: Throwable) {
                Log.e("API_RESPONSE", "Request failed: ${t.message}", t)
            }
        })
        //endregion
    }
    //endregion

}