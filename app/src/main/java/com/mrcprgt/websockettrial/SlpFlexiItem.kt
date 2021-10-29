package com.mrcprgt.websockettrial

import android.annotation.SuppressLint
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.mrcprgt.websockettrial.databinding.TradeListItemBinding
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem
import eu.davidea.flexibleadapter.items.IFlexible
import eu.davidea.viewholders.FlexibleViewHolder
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

class SlpFlexiItem(val slp: SlpTicker) : BaseFlexibleItem() {
    override fun equals(other: Any?): Boolean {
        if (javaClass != other?.javaClass) return false
        other as SlpFlexiItem?
        if (slp != other.slp) return false
        return true
    }

    override fun hashCode(): Int {
        return slp.hashCode()
    }

    override fun getLayoutRes() = R.layout.trade_list_item

    override fun createViewHolder(
        view: View,
        adapter: FlexibleAdapter<IFlexible<RecyclerView.ViewHolder>>
    ): BaseFlexibleViewHolder {
        return SlpViewHolder(view, adapter)
    }

    @SuppressLint("SetTextI18n")
    override fun bindViewHolder(
        adapter: FlexibleAdapter<IFlexible<RecyclerView.ViewHolder>>?,
        holder: BaseFlexibleViewHolder?,
        position: Int,
        payloads: MutableList<Any>?
    ) {
        if (holder is SlpViewHolder) {
            val value = slp.price * slp.quantity
            holder.price.text = value.toCurrencyFormat()
            holder.quantity.text = "${slp.quantity.toInt()}"
            holder.date.text = Date(slp.tradeTime.toLong()).formatToHHMMA()
        }
    }

    class SlpViewHolder(
        view: View,
        adapter: FlexibleAdapter<IFlexible<RecyclerView.ViewHolder>>
    ) : BaseFlexibleViewHolder(view, adapter) {

        private val binding = TradeListItemBinding.bind(view)
        val quantity = binding.tvQuantity
        val price = binding.tvPrice
        val date = binding.tvTradeTime
    }
}

fun Date.formatToHHMMA(): String {
    val dateFormat = SimpleDateFormat("hh:mm:a")

    return dateFormat.format(this)
}

fun Double.toCurrencyFormat(): String {
    val currencyFormat by lazy { DecimalFormat("###,###,###,##0.00") }

    return "$ ${currencyFormat.format(this)}"
}

abstract class BaseFlexibleItem : AbstractFlexibleItem<BaseFlexibleItem.BaseFlexibleViewHolder>() {
    abstract class BaseFlexibleViewHolder(
        view: View,
        flexibleAdapter: FlexibleAdapter<*>,
        isSticky: Boolean? = false
    ) : FlexibleViewHolder(view, flexibleAdapter, isSticky ?: false)
}
