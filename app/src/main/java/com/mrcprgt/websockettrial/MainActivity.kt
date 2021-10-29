package com.mrcprgt.websockettrial

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import com.mrcprgt.websockettrial.databinding.ActivityMainBinding
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import eu.davidea.flexibleadapter.FlexibleAdapter
import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ServerHandshake
import java.lang.Exception
import java.net.URI
import java.text.DecimalFormat
import javax.net.ssl.SSLSocketFactory

class MainActivity : AppCompatActivity() {
    private lateinit var webSocketClient: WebSocketClient

    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private val adapter by lazy {
        FlexibleAdapter(emptyList())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setupRV()
    }

    override fun onResume() {
        super.onResume()
        initWebSocket()
    }

    override fun onPause() {
        super.onPause()
        webSocketClient.close()
    }

    private fun setupRV() {
        binding.rvSLp.adapter = adapter
        binding.rvSLp.layoutManager = LinearLayoutManager(this)
    }

    private fun initWebSocket() {
        val coinbaseUri: URI? = URI(WEB_SOCKET_URL)

        createWebSocketClient(coinbaseUri)

        val socketFactory: SSLSocketFactory = SSLSocketFactory.getDefault() as SSLSocketFactory

        webSocketClient.setSocketFactory(socketFactory)
        webSocketClient.connect()
    }

    private fun createWebSocketClient(coinbaseUri: URI?) {
        webSocketClient = object : WebSocketClient(coinbaseUri) {
            override fun onOpen(handshakedata: ServerHandshake?) {
                Log.d(TAG, "onOpen")
                subscribe()
            }

            override fun onMessage(message: String?) {
                Log.d(TAG, "onMessage: $message")
                setUpBtcPriceText(message)
            }

            override fun onClose(code: Int, reason: String?, remote: Boolean) {
                Log.d(TAG, "onClose")
                unsubscribe()
            }

            override fun onError(ex: Exception?) {
                Log.e(TAG, "onError: ${ex?.message}")
            }

        }
    }

    private fun subscribe() {
//        webSocketClient.send(
//            "{\n" +
//                    "    \"type\": \"subscribe\",\n" +
//                    "    \"channels\": [{ \"name\": \"ticker\", \"product_ids\": [\"BTC-EUR\"] }]\n" +
//                    "}"
//        )
    }

    private fun unsubscribe() {
        webSocketClient.close()
//        webSocketClient.send(
//            "{\n" +
//                    "    \"type\": \"unsubscribe\",\n" +
//                    "    \"channels\": [\"ticker\"]\n" +
//                    "}"
//        )
    }

    @SuppressLint("SetTextI18n")
    private fun setUpBtcPriceText(message: String?) {
        message?.let {
            val moshi = Moshi.Builder().build()
            val adapter: JsonAdapter<SlpTicker> = moshi.adapter(SlpTicker::class.java)
            val bitcoin = adapter.fromJson(message)
            runOnUiThread {
                binding.tvSlpPrice.text =
                    "SLP Price: $ ${bitcoin!!.price}"
                this.adapter.addItem(
                    this.adapter.itemCount,
                    SlpFlexiItem(
                        SlpTicker(
                            bitcoin!!.symbol,
                            bitcoin.price,
                            bitcoin.quantity,
                            bitcoin.tradeTime
                        )
                    )

                )
            }
        }
    }

    fun Double.toCurrencyFormat(): String {
        val currencyFormat by lazy { DecimalFormat("###,###,###,##0.00") }

        return "$ ${currencyFormat.format(this)}"
    }

    companion object {

        const val WEB_SOCKET_URL = "wss://stream.binance.com:9443/ws/slpusdt@aggTrade"

        //        const val WEB_SOCKET_URL = "wss://ws-feed.pro.coinbase.com"
//        const val TAG = "Coinbase"
        const val TAG = "Binance"
    }
}