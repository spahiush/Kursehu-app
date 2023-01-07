package com.spahiush.kursehu

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.core.widget.addTextChangedListener
import androidx.room.Room
import kotlinx.android.synthetic.main.activity_add_transaction.*
import kotlinx.android.synthetic.main.activity_add_transaction.amount_input
import kotlinx.android.synthetic.main.activity_add_transaction.amount_layout
import kotlinx.android.synthetic.main.activity_add_transaction.closeBtn
import kotlinx.android.synthetic.main.activity_add_transaction.description_input
import kotlinx.android.synthetic.main.activity_add_transaction.label_input
import kotlinx.android.synthetic.main.activity_add_transaction.label_layout
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class DetailActivity : AppCompatActivity() {
    private lateinit var transaction: Transaction
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)


        val transaction = intent.getSerializableExtra("transaction") as Transaction

        label_input.setText(transaction.label)
        amount_input.setText(transaction.amount.toString())
        description_input.setText(transaction.description)

        // reset focus
        rootView.setOnClickListener{
            this.window.decorView.clearFocus()

            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(it.windowToken, 0)
        }

        label_input.addTextChangedListener {
            updateBtn.visibility = View.VISIBLE
            if(it!!.count() > 0)
                label_layout.error = null
        }

        amount_input.addTextChangedListener {
            updateBtn.visibility = View.VISIBLE
            if(it!!.count() > 0)
                amount_layout.error = null
        }

       description_input.addTextChangedListener {
            updateBtn.visibility = View.VISIBLE
        }

        updateBtn.setOnClickListener{
            val label = label_input.text.toString()
            val description = description_input.text.toString()
            val amount = amount_input.text.toString().toDoubleOrNull()

            if (label.isEmpty())
                label_layout.error = "Caktoni nje titull per tranzaksionin tuaj!"

            else if (amount== null)
                amount_layout.error = "Caktoni nje shume per tranzaksionin tuaj!"
            else {
                 val transaction= Transaction(transaction.id, label,amount, description)
                update(transaction)
            }
        }

        closeBtn.setOnClickListener {
            finish()
        }
    }

    private fun update(transaction: Transaction ){
        val db = Room.databaseBuilder(
            this,
            AppDatabase::class.java,
            "transactions").build()

        GlobalScope.launch {
            db.transactionDao().update(transaction)
            finish()
        }
    }
}