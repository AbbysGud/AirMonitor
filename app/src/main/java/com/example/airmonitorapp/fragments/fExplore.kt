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
import androidx.fragment.app.Fragment
import com.example.airmonitorapp.R
import com.example.airmonitorapp.data.DataItem
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartModel
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartType
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartView
import com.github.aachartmodel.aainfographics.aachartcreator.AASeriesElement
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.DecimalFormat

class fExplore : Fragment() {

    private lateinit var chart: AAChartView
    private lateinit var txt_conclusion: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_f_explore, container, false)

        //region inisiasi findViewById
        val Spinner = view.findViewById<Spinner>(R.id.explore_spin_card1)
        val txt1 = view.findViewById<TextView>(R.id.explore_card1_txt1)
        val txt2 = view.findViewById<TextView>(R.id.explore_card1_txt2)
        val txt3 = view.findViewById<TextView>(R.id.explore_card1_txt3)
        val txt4 = view.findViewById<TextView>(R.id.explore_card1_txtvalue)
        val txt5 = view.findViewById<TextView>(R.id.explore_card1_txtkey)
        chart = view.findViewById(R.id.aa_chart_explore)
        txt_conclusion = view.findViewById(R.id.explore_conclusion)
        //endregion

        //region atur spinner
        val adapter = ArrayAdapter.createFromResource(
            requireActivity(),
            R.array.explore_spinitem_card1,
            R.layout.spinner_list_content
        )

        adapter.setDropDownViewResource(R.layout.spinner_dropdown_content)
        Spinner.setAdapter(adapter)
        //endregion

        //region spinner card 1
        Spinner.setOnItemSelectedListener(
            object : AdapterView.OnItemSelectedListener{
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    when (position) {
                        0 -> {
                            txt1.text = "Temperature Role in Air Quality"
                            txt2.text =  getString(R.string.roletemp)
                            txt3.text = getString(R.string.whattemp)
                            txt4.text = "20"
                            txt5.text = "TEMP"
                        } 1 -> {
                            txt1.text = "Humidity Role in Air Quality"
                            txt2.text = getString(R.string.rolehum)
                            txt3.text = getString(R.string.whathum)
                            txt4.text = "55"
                            txt5.text = "HUM"
                        }  2 -> {
                            txt1.text = "Lux Role in Air Quality"
                            txt2.text = getString(R.string.rolelux)
                            txt3.text = getString(R.string.whatlux)
                            txt4.text = "0"
                            txt5.text = "LUX"
                        }  3 -> {
                            txt1.text = "Metana Role in Air Quality"
                            txt2.text = getString(R.string.rolelux)
                            txt3.text = getString(R.string.whatlux)
                            txt4.text = "800"
                            txt5.text = "CH4"
                        }  4 -> {
                            txt1.text = "PM10 Role in Air Quality"
                            txt2.text = getString(R.string.rolepm10)
                            txt3.text = getString(R.string.whatpm10)
                            txt4.text = "15"
                            txt5.text = "PM10"
                        }  5 -> {
                            txt1.text = "PM100 Role in Air Quality"
                            txt2.text = getString(R.string.rolepm100)
                            txt3.text = getString(R.string.whatpm100)
                            txt4.text = "15"
                            txt5.text = "PM100"
                        }  6 -> {
                            txt1.text = "PM2.5 Role in Air Quality"
                            txt2.text = getString(R.string.rolepm2_5)
                            txt3.text = getString(R.string.whatpm2_5)
                            txt4.text = "10"
                            txt5.text = "PM2.5"
                        }  7 -> {
                            txt1.text = "CO2 Role in Air Quality"
                            txt2.text = getString(R.string.roleco2)
                            txt3.text = getString(R.string.whatco2)
                            txt4.text = "600\nPMM"
                            txt5.text = "CO2"
                        }
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

    private fun getData() {
        //region mengambil data API menggunakan retrofit
        val api = Retrofit.Builder()
            .baseUrl("https://air-quality-itenas.000webhostapp.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(fHistory.MyAPI::class.java)
        //endregion

        //region memasukkan data daily ke variabel dari json yang diambil dari API
        api.getWeekData().enqueue(object : Callback<List<DataItem>> {
            override fun onResponse(call: Call<List<DataItem>>, response: Response<List<DataItem>>) {
                if (response.isSuccessful) {
                    response.body()?.let { responseItemList ->

                        var goodDays = 0
                        var moderateDays = 0
                        var unhealthySensitiveDays = 0
                        var unhealthyDays = 0
                        var veryUnhealthyDays = 0
                        var hazardousDays = 0

                        var aqi_list : ArrayList<Double> = arrayListOf()
                        var dateList: ArrayList<String> = arrayListOf()

                        for (item in responseItemList) {
                            var pm25 = item.pm25

                            if (pm25 != null) {
                                val pm25Value = pm25.toDouble()
                                var aqi_value = 0.0

                                if (pm25Value in 0.0..12.0) {
                                    aqi_value = (50 - 0) / (12.0 - 0) * (pm25Value - 0) + 0
                                    goodDays++
                                } else if (pm25Value in 12.1..35.4) {
                                    aqi_value = (100 - 51) / (35.4 - 12.1) * (pm25Value - 12.1) + 51
                                    moderateDays++
                                } else if (pm25Value in 35.5..55.4) {
                                    aqi_value = (150 - 101) / (55.4 - 35.5) * (pm25Value - 35.5) + 101
                                    unhealthySensitiveDays++
                                } else if (pm25Value in 55.5..150.4) {
                                    aqi_value = (200 - 151) / (150.4 - 55.5) * (pm25Value - 55.5) + 151
                                    unhealthyDays++
                                } else if (pm25Value in 150.5..250.4) {
                                    aqi_value = (300 - 201) / (250.4 - 150.5) * (pm25Value - 150.5) + 201
                                    veryUnhealthyDays++
                                } else if (pm25Value >= 250.5) {
                                    aqi_value = (500 - 301) / (500.4 - 250.5) * (pm25Value - 250.5) + 301
                                    hazardousDays++
                                }

                                val formattedAqi = DecimalFormat("#.##").format(aqi_value)
                                aqi_list.add(formattedAqi.toDouble())
                                dateList.add(item.tanggal ?: "N/A")
                            }
                        }

                        val resultMessage = buildString {
                            append("There are ")
                            if (goodDays > 0) append("$goodDays good days ")
                            if (moderateDays > 0) append("$moderateDays moderate days ")
                            if (unhealthySensitiveDays > 0) append("$unhealthySensitiveDays unhealthy days for sensitive groups ")
                            if (unhealthyDays > 0) append("$unhealthyDays unhealthy days ")
                            if (veryUnhealthyDays > 0) append("$veryUnhealthyDays very unhealthy days ")
                            if (hazardousDays > 0) append("$hazardousDays hazardous days. ")
                        }

                        txt_conclusion.text = resultMessage

                        updateChart(dateList, aqi_list)

                    }
                }
            }

            override fun onFailure(call: Call<List<DataItem>>, t: Throwable) {
                Log.e("API_RESPONSE", "Request failed: ${t.message}", t)
            }
        })
        //endregion
    }

    //region chart card
    private fun updateChart(dateList : MutableList<String>, aqiList : ArrayList<Double>) {

        val aaChartModel : AAChartModel = AAChartModel()
            .chartType(AAChartType.Bar)
            .title("LAST 7 DAY AIR QUALITY INDEX")
            .categories(arrayOf(*dateList.toTypedArray()))
            .backgroundColor("#FFFFFF")
            .dataLabelsEnabled(true)
            .series(
                arrayOf(
                    AASeriesElement()
                        .name("AQI")
                        .data(aqiList.toTypedArray()),
                )
            )
            .colorsTheme(arrayOf("#6EB1D6"))
        chart.aa_drawChartWithChartModel(aaChartModel)
    }
    //endregion

}