package com.spahiush.kursehu

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_add_transaction.*
import kotlinx.android.synthetic.main.transaction_layout.*
import androidx.core.widget.addTextChangedListener
import androidx.room.Room
import kotlinx.android.synthetic.main.activity_add_transaction.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class AddTransactionActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_transaction)

        label_input.addTextChangedListener {
            if(it!!.count() > 0)
                label_layout.error = null
        }

        amount_input.addTextChangedListener {
            if(it!!.count() > 0)
                amount_layout.error = null
        }

        addTransactionBtn.setOnClickListener{
            val label = label_input.text.toString()
            val description = description_input.text.toString()
            val amount = amount_input.text.toString().toDoubleOrNull()

           if (label.isEmpty())
               label_layout.error = "Caktoni nje titull per tranzaksionin tuaj!"

            else if (amount== null)
                amount_layout.error = "Caktoni nje shume per tranzaksionin tuaj!"
            else {
                val transaction= Transaction(0, label,amount, description)
               insert(transaction)
           }
        }

        closeBtn.setOnClickListener {
            finish()
        }
    }

    private fun insert(transaction: Transaction ){
       val db = Room.databaseBuilder(
            this,
            AppDatabase::class.java,
            "transactions").build()

        GlobalScope.launch {
            db.transactionDao().insertAll(transaction)
            finish()
        }
    }
}