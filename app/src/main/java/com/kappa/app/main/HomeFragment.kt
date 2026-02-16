package com.kappa.app.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import android.widget.LinearLayout
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.kappa.app.R
import com.kappa.app.core.network.ApiService
import com.kappa.app.core.network.model.InboxMessageRequest
import com.kappa.app.core.storage.PreferencesManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlinx.coroutines.launch

/**
 * Home screen fragment with inbox/friends/family.
 */
@AndroidEntryPoint
class HomeFragment : Fragment() {

    @Inject
    lateinit var apiService: ApiService

    @Inject
    lateinit var preferencesManager: PreferencesManager

    private lateinit var inboxAdapter: InboxAdapter
    private lateinit var friendsAdapter: InboxAdapter
    private val familyMembersAdapter = SimpleRowAdapter()
    private val familyRoomsAdapter = SimpleRowAdapter()
    private val viewModel: HomeViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val messagesRecycler = view.findViewById<RecyclerView>(R.id.recycler_inbox_messages)
        val friendsRecycler = view.findViewById<RecyclerView>(R.id.recycler_inbox_friends)
        inboxAdapter = InboxAdapter { item ->
            viewModel.markThreadRead(item.id)
            showThreadDialog(item)
        }
        friendsAdapter = InboxAdapter { item ->
            Toast.makeText(requireContext(), "Opened profile for ${item.name}", Toast.LENGTH_SHORT).show()
        }
        messagesRecycler.layoutManager = LinearLayoutManager(requireContext())
        messagesRecycler.adapter = inboxAdapter
        friendsRecycler.layoutManager = LinearLayoutManager(requireContext())
        friendsRecycler.adapter = friendsAdapter
        messagesRecycler.setHasFixedSize(true)
        friendsRecycler.setHasFixedSize(true)

        val familyMembersRecycler = view.findViewById<RecyclerView>(R.id.recycler_family_members)
        val familyRoomsRecycler = view.findViewById<RecyclerView>(R.id.recycler_family_rooms)
        val familyNameInput = view.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.input_family_name)
        val familyIdInput = view.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.input_family_id)
        val familyRoomNameInput = view.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.input_family_room_name)
        val familyCreateButton = view.findViewById<com.google.android.material.button.MaterialButton>(R.id.button_family_create)
        val familyJoinButton = view.findViewById<com.google.android.material.button.MaterialButton>(R.id.button_family_join)
        val familyRoomCreateButton = view.findViewById<com.google.android.material.button.MaterialButton>(R.id.button_family_room_create)
        val familyCurrentText = view.findViewById<TextView>(R.id.text_family_current)
        val familyCodeText = view.findViewById<TextView>(R.id.text_family_code)
        val familyStatusText = view.findViewById<TextView>(R.id.text_family_status)
        familyMembersRecycler.layoutManager = LinearLayoutManager(requireContext())
        familyMembersRecycler.adapter = familyMembersAdapter
        familyRoomsRecycler.layoutManager = LinearLayoutManager(requireContext())
        familyRoomsRecycler.adapter = familyRoomsAdapter
        familyMembersRecycler.setHasFixedSize(true)
        familyRoomsRecycler.setHasFixedSize(true)

        val tabMensagem = view.findViewById<TextView>(R.id.tab_inbox_mensagem)
        val tabAmigos = view.findViewById<TextView>(R.id.tab_inbox_amigos)
        val tabFamilia = view.findViewById<TextView>(R.id.tab_inbox_familia)
        val sectionMensagem = view.findViewById<View>(R.id.section_inbox_mensagem)
        val sectionAmigos = view.findViewById<View>(R.id.section_inbox_amigos)
        val sectionFamilia = view.findViewById<View>(R.id.section_inbox_familia)
        val friendsSearchInput = view.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.input_friends_search)

        fun setActiveTab(active: TextView) {
            val activeColor = resources.getColor(R.color.kappa_gold_300, null)
            val inactiveColor = resources.getColor(R.color.kappa_cream, null)
            tabMensagem.setTextColor(inactiveColor)
            tabAmigos.setTextColor(inactiveColor)
            tabFamilia.setTextColor(inactiveColor)
            active.setTextColor(activeColor)
        }

        fun showSection(mensagem: Boolean, amigos: Boolean, familia: Boolean) {
            sectionMensagem.visibility = if (mensagem) View.VISIBLE else View.GONE
            sectionAmigos.visibility = if (amigos) View.VISIBLE else View.GONE
            sectionFamilia.visibility = if (familia) View.VISIBLE else View.GONE
        }

        tabMensagem.setOnClickListener {
            setActiveTab(tabMensagem)
            showSection(mensagem = true, amigos = false, familia = false)
        }
        tabAmigos.setOnClickListener {
            setActiveTab(tabAmigos)
            showSection(mensagem = false, amigos = true, familia = false)
        }
        tabFamilia.setOnClickListener {
            setActiveTab(tabFamilia)
            showSection(mensagem = false, amigos = false, familia = true)
        }

        familyCreateButton.setOnClickListener {
            val name = familyNameInput.text?.toString()?.trim().orEmpty()
            viewModel.createFamily(name)
        }

        familyJoinButton.setOnClickListener {
            val code = familyIdInput.text?.toString()?.trim().orEmpty()
            viewModel.joinFamily(code)
        }

        familyRoomCreateButton.setOnClickListener {
            val name = familyRoomNameInput.text?.toString()?.trim().orEmpty()
            viewModel.createFamilyRoom(name)
        }

        friendsSearchInput.addTextChangedListener {
            viewModel.searchFriends(it?.toString().orEmpty())
        }

        viewModel.loadAll()

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.viewState.collect { state ->
                    inboxAdapter.submitList(state.inbox)
                    friendsAdapter.submitList(state.friendSearch)
                    familyMembersAdapter.submitRows(state.familyMembers)
                    familyRoomsAdapter.submitRows(state.familyRooms)
                    if (state.familyName.isNullOrBlank()) {
                        familyCurrentText.text = getString(R.string.home_family_none)
                        familyCodeText.text = getString(R.string.home_code_default)
                    } else {
                        familyCurrentText.text = getString(R.string.home_family_label_format, state.familyName)
                        familyCodeText.text = getString(R.string.home_code_format, state.familyCode ?: "-")
                    }
                    val hasFamily = state.family != null
                    familyRoomCreateButton.isEnabled = hasFamily
                    familyRoomNameInput.isEnabled = hasFamily
                    if (state.isLoading) {
                        familyStatusText.text = getString(R.string.home_updating)
                        familyStatusText.visibility = View.VISIBLE
                    } else if (state.message.isNullOrBlank()) {
                        familyStatusText.visibility = View.GONE
                    } else {
                        familyStatusText.text = state.message
                        familyStatusText.visibility = View.VISIBLE
                    }
                    if (state.message != null) {
                        Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
                        viewModel.clearMessage()
                    }
                }
            }
        }
    }

    private fun showThreadDialog(item: InboxItem) {
        val context = requireContext()
        val rowsAdapter = SimpleRowAdapter()
        val container = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(24, 12, 24, 12)
        }
        val messagesRecycler = RecyclerView(context).apply {
            layoutManager = LinearLayoutManager(context)
            adapter = rowsAdapter
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                600
            )
        }
        val composer = LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        }
        val messageInput = TextInputEditText(context).apply {
            hint = "Type message"
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
        }
        val sendButton = MaterialButton(context).apply {
            text = "Send"
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        }
        composer.addView(messageInput)
        composer.addView(sendButton)
        container.addView(messagesRecycler)
        container.addView(composer)

        val dialog = MaterialAlertDialogBuilder(context)
            .setTitle(item.name)
            .setView(container)
            .setNegativeButton("Close", null)
            .create()

        fun refreshMessages() {
            viewLifecycleOwner.lifecycleScope.launch {
                val currentUserId = preferencesManager.getUserIdOnce()
                val response = runCatching { apiService.getInboxThreadMessages(item.id, 100) }.getOrNull()
                val rows = response?.data.orEmpty().map { message ->
                    val sender = if (message.senderId == currentUserId) "You" else item.name
                    sender to message.message
                }
                rowsAdapter.submitRows(rows.ifEmpty { listOf("No messages yet" to "") })
                viewModel.markThreadRead(item.id)
            }
        }

        sendButton.setOnClickListener {
            val targetId = item.targetId
            val text = messageInput.text?.toString()?.trim().orEmpty()
            if (targetId.isNullOrBlank() || text.isBlank()) {
                return@setOnClickListener
            }
            viewLifecycleOwner.lifecycleScope.launch {
                val sent = runCatching {
                    apiService.sendInboxMessage(InboxMessageRequest(targetId, text))
                }.getOrNull()
                if (sent?.success == true) {
                    messageInput.setText("")
                    refreshMessages()
                    viewModel.loadAll()
                } else {
                    Toast.makeText(
                        requireContext(),
                        sent?.error ?: "Failed to send message",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        dialog.setOnShowListener { refreshMessages() }
        dialog.show()
    }
}
