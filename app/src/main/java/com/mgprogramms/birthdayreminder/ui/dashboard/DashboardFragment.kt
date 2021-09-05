package com.mgprogramms.birthdayreminder.ui.dashboard

import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.ContactsContract
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CursorAdapter
import android.widget.SimpleCursorAdapter
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.mgprogramms.birthdayreminder.R
import com.mgprogramms.birthdayreminder.birthday.Contacts.Companion.getContactIdByIndex
import com.mgprogramms.birthdayreminder.birthday.Contacts.Companion.getContacts
import com.mgprogramms.birthdayreminder.createFriendlyDate
import com.mgprogramms.birthdayreminder.databinding.FragmentDashboardBinding

class DashboardFragment : Fragment() {

    private lateinit var dashboardViewModel: DashboardViewModel
    private var _binding: FragmentDashboardBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dashboardViewModel =
            ViewModelProvider(this).get(DashboardViewModel::class.java)

        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.contactList.setOnItemClickListener { adapterView, view, i, l ->
            val contactId = getContactIdByIndex(requireContext(), i)
            if (contactId != null) {
                showContactDetail(contactId)
            }
        }
        binding.contactList.setOnItemLongClickListener { adapterView, view, i, l ->
            val contactId = getContactIdByIndex(requireContext(), i)
            if (contactId != null) {
                showEditContact(contactId)
            }
            true
        }

        loadContactList()

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun loadContactList() {

        val contactsCursor = getContacts(requireContext());

        val projectionFrom =
            arrayOf(ContactsContract.Data.DISPLAY_NAME, ContactsContract.CommonDataKinds.Event.START_DATE)
        val projectionTo = arrayOf(R.id.contact_name, R.id.birthday)

        if (contactsCursor !== null) {

            val adapter = SimpleCursorAdapter(
                requireContext(),
                R.layout.fragment_contact_list_item,
                contactsCursor,
                projectionFrom,
                projectionTo.toIntArray(),
                CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER
            )

            binding.contactList.adapter = adapter
            adapter.viewBinder = SimpleCursorAdapter.ViewBinder { a: View, b: Cursor, c: Int ->
                setViewValue(
                    a as TextView, b, c
                )
            }
        }
    }

    private fun showEditContact(contactId: Int) {
        val intent = Intent(Intent.ACTION_EDIT)
        val uri: Uri = Uri.withAppendedPath(
            ContactsContract.Contacts.CONTENT_URI,
            contactId.toString()
        )
        intent.data = uri
        this.startActivity(intent)
    }

    private fun showContactDetail(contactId: Int) {
        val intent = Intent(Intent.ACTION_VIEW)
        val uri: Uri = Uri.withAppendedPath(
            ContactsContract.Contacts.CONTENT_URI,
            contactId.toString()
        )
        intent.data = uri
        this.startActivity(intent)
    }


    @RequiresApi(Build.VERSION_CODES.O)
    fun setViewValue(
        aView: TextView,
        aCursor: Cursor,
        aColumnIndex: Int
    ): Boolean {
        if (aColumnIndex == 2) {
            val createDate: String = aCursor.getString(aColumnIndex)

            val friendlyDate = createFriendlyDate(createDate)
            aView.text = friendlyDate
            return true
        }
        return false
    }
}
