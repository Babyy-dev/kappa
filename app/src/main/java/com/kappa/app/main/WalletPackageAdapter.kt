package com.kappa.app.main

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.kappa.app.R
import com.kappa.app.domain.economy.CoinPackage

class WalletPackageAdapter(
    private val onBuyClick: (CoinPackage) -> Unit
) : RecyclerView.Adapter<WalletPackageAdapter.PackageViewHolder>() {

    private val items = mutableListOf<CoinPackage>()
    private val priceOverrides = mutableMapOf<String, String>()

    fun submitList(list: List<CoinPackage>) {
        items.clear()
        items.addAll(list)
        notifyDataSetChanged()
    }

    fun updatePrices(prices: Map<String, String>) {
        priceOverrides.clear()
        priceOverrides.putAll(prices)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PackageViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_wallet_package, parent, false)
        return PackageViewHolder(view)
    }

    override fun onBindViewHolder(holder: PackageViewHolder, position: Int) {
        val item = items[position]
        holder.name.text = item.name
        holder.coins.text = "${item.coinAmount} coins"
        val override = item.storeProductId?.let { priceOverrides[it] }
        holder.price.text = override ?: "$${item.priceUsd}"
        holder.buyButton.setOnClickListener { onBuyClick(item) }
    }

    override fun getItemCount(): Int = items.size

    class PackageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.text_wallet_package_name)
        val coins: TextView = itemView.findViewById(R.id.text_wallet_package_coins)
        val price: TextView = itemView.findViewById(R.id.text_wallet_package_price)
        val buyButton: MaterialButton = itemView.findViewById(R.id.button_wallet_buy)
    }
}
