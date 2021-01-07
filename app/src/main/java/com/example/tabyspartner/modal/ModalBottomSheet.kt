package com.example.tabyspartner.modal


import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tabyspartner.R
import com.example.tabyspartner.adapter.CreditCardAdapter
import com.example.tabyspartner.databinding.FragmentModalBottomSheetBinding
import com.example.tabyspartner.model.CreditCard
import com.example.tabyspartner.ui.ui.withdraw.CardFormActivity
import com.example.tabyspartner.utils.DatabaseHandler
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.android.synthetic.main.fragment_modal_bottom_sheet.*

class ModalBottomSheet : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentModalBottomSheetBinding
    private lateinit var myDb: DatabaseHandler
    private var creditCardListFromDB: MutableList<CreditCard> = mutableListOf()

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_modal_bottom_sheet, container, false
        )

        myDb = DatabaseHandler(requireContext())
        creditCardListFromDB = (myDb.getAllCreditCards() as MutableList<CreditCard>?)!!

        binding.creditCardListModal.adapter = CreditCardAdapter(
            creditCardListFromDB,
            onItemClick = {
//                Toast.makeText(requireContext(),it.creditCardNumber.toString(), Toast.LENGTH_SHORT).show()
//                binding.chooseCardBtn.text = it.creditCardNumber
//                bottomSheetDialog.dismiss()
            },
            onDelete = {
                MaterialAlertDialogBuilder(requireContext())
                    .setTitle(context?.resources?.getString(R.string.delete_card_title))
                    .setMessage(context?.resources?.getString(R.string.delete_card_message))
                    .setNegativeButton(context?.resources?.getString(R.string.decline)) { dialog, which ->
                        dialog.dismiss()
                    }
                    .setPositiveButton(context?.resources?.getString(R.string.accept)) { dialog, which ->
                        myDb.deleteCardItem(it.id)
                        getFragmentManager()
                            ?.beginTransaction()
                            ?.detach(this)
                            ?.attach(this)
                            ?.commit();
                    }
                    .show()
            },
            myDb,
            this.context,
        )
        binding.creditCardListModal.layoutManager = LinearLayoutManager(
            requireContext()
        )

        binding.addCardBtn.setOnClickListener {
            startActivityForResult(Intent(requireContext(), CardFormActivity::class.java), 1)
        }

        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            getFragmentManager()
                ?.beginTransaction()
                ?.detach(this)
                ?.attach(this)
                ?.commit();
        }
    }

}