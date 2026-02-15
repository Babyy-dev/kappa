package com.kappa.app.agency.presentation

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.kappa.app.R
import com.kappa.app.agency.domain.model.AgencyApplication
import com.kappa.app.agency.domain.model.ResellerApplication

class AgencyApplicationAdapter(
    private val onApprove: (AgencyApplication) -> Unit,
    private val onReject: (AgencyApplication) -> Unit
) : RecyclerView.Adapter<AgencyApplicationAdapter.ApplicationViewHolder>() {

    private val items = mutableListOf<AgencyApplication>()
    private var canReview: Boolean = false

    fun submitItems(apps: List<AgencyApplication>, canReview: Boolean) {
        items.clear()
        items.addAll(apps)
        this.canReview = canReview
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ApplicationViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_agency_application, parent, false)
        return ApplicationViewHolder(view, onApprove, onReject)
    }

    override fun onBindViewHolder(holder: ApplicationViewHolder, position: Int) {
        holder.bind(items[position], canReview)
    }

    override fun getItemCount(): Int = items.size

    class ApplicationViewHolder(
        itemView: View,
        private val onApprove: (AgencyApplication) -> Unit,
        private val onReject: (AgencyApplication) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {
        private val nameText = itemView.findViewById<TextView>(R.id.text_agency_app_name)
        private val statusText = itemView.findViewById<TextView>(R.id.text_agency_app_status)
        private val actions = itemView.findViewById<View>(R.id.layout_agency_app_actions)
        private val approveButton = itemView.findViewById<MaterialButton>(R.id.button_agency_app_approve)
        private val rejectButton = itemView.findViewById<MaterialButton>(R.id.button_agency_app_reject)

        fun bind(application: AgencyApplication, canReview: Boolean) {
            nameText.text = application.agencyName
            statusText.text = "Status: ${application.status}"
            actions.visibility = if (canReview) View.VISIBLE else View.GONE
            val pending = application.status.equals("PENDING", ignoreCase = true)
            approveButton.isEnabled = canReview && pending
            rejectButton.isEnabled = canReview && pending
            approveButton.setOnClickListener { onApprove(application) }
            rejectButton.setOnClickListener { onReject(application) }
        }
    }
}

class ResellerApplicationAdapter(
    private val onApprove: (ResellerApplication) -> Unit,
    private val onReject: (ResellerApplication) -> Unit
) : RecyclerView.Adapter<ResellerApplicationAdapter.ApplicationViewHolder>() {

    private val items = mutableListOf<ResellerApplication>()
    private var canReview: Boolean = false

    fun submitItems(apps: List<ResellerApplication>, canReview: Boolean) {
        items.clear()
        items.addAll(apps)
        this.canReview = canReview
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ApplicationViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_agency_application, parent, false)
        return ApplicationViewHolder(view, onApprove, onReject)
    }

    override fun onBindViewHolder(holder: ApplicationViewHolder, position: Int) {
        holder.bind(items[position], canReview)
    }

    override fun getItemCount(): Int = items.size

    class ApplicationViewHolder(
        itemView: View,
        private val onApprove: (ResellerApplication) -> Unit,
        private val onReject: (ResellerApplication) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {
        private val nameText = itemView.findViewById<TextView>(R.id.text_agency_app_name)
        private val statusText = itemView.findViewById<TextView>(R.id.text_agency_app_status)
        private val actions = itemView.findViewById<View>(R.id.layout_agency_app_actions)
        private val approveButton = itemView.findViewById<MaterialButton>(R.id.button_agency_app_approve)
        private val rejectButton = itemView.findViewById<MaterialButton>(R.id.button_agency_app_reject)

        fun bind(application: ResellerApplication, canReview: Boolean) {
            nameText.text = "Reseller ${application.userId}"
            statusText.text = "Status: ${application.status}"
            actions.visibility = if (canReview) View.VISIBLE else View.GONE
            val pending = application.status.equals("PENDING", ignoreCase = true)
            approveButton.isEnabled = canReview && pending
            rejectButton.isEnabled = canReview && pending
            approveButton.setOnClickListener { onApprove(application) }
            rejectButton.setOnClickListener { onReject(application) }
        }
    }
}
