package com.spahiush.kursehu

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings.Global
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var deletedTransaction: Transaction
    private lateinit var transactions: List<Transaction>
    private lateinit var oldTransactions: List<Transaction>
    private lateinit var transactionAdapter: TransactionAdapter
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var db: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        transactions = arrayListOf()

        transactionAdapter = TransactionAdapter(transactions)
        linearLayoutManager = LinearLayoutManager(this)

        db = Room.databaseBuilder(
            this,
            AppDatabase::class.java,
            "transactions").build()

        recyclerview.apply {
            adapter = transactionAdapter
            layoutManager = linearLayoutManager
        }
        //swipe to remove
        val itemTouchHelper = object: ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT){
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
               return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                deleteTransaction(transactions[viewHolder.adapterPosition])
            }

        }

        val swipeHelper = ItemTouchHelper(itemTouchHelper)
        swipeHelper.attachToRecyclerView(recyclerview)

        addBtn.setOnClickListener {
            val intent = Intent(this, AddTransactionActivity::class.java)
            startActivity(intent)
        }
    }

    private fun fetchAll() {
        // background thread to call db methods
        GlobalScope.launch {
            transactions = db.transactionDao().getAll()

            runOnUiThread {
                updateDashBoard()
                transactionAdapter.setData(transactions)
            }
        }
    }

    private fun updateDashBoard() {
        val totalAmount = transactions.map { it.amount }.sum()
        val budgetAmount = transactions.filter { it.amount > 0 }.map { it.amount }.sum()
        val expenseAmount = totalAmount - budgetAmount

        balance.text = "%.2f".format(totalAmount) + "€"
        budget.text = "%.2f".format(budgetAmount) + "€"
        expenses.text = "%.2f".format(expenseAmount) + "€"
    }

    private fun undoDelete(){
        GlobalScope.launch {
            db.transactionDao().insertAll(deletedTransaction)
            transactions = oldTransactions

            runOnUiThread {
                transactionAdapter.setData(transactions)
                updateDashBoard()
            }
        }
    }

    // show deteltion msg
    private fun showSnackBar() {
    val view = findViewById<View>(R.id.coordinator )
        val snackbar=  Snackbar.make(view, "Tranzaksioni u largua nga lista.", Snackbar.LENGTH_LONG)
        snackbar.setAction("C'beje"){
            undoDelete()
        }

            .setActionTextColor(ContextCompat.getColor(this, R.color.red))
            .setActionTextColor(ContextCompat.getColor(this, R.color.white))
            .show()
    }

    // undo
    private fun deleteTransaction(transaction: Transaction){
         deletedTransaction = transaction
        oldTransactions = transactions

        GlobalScope.launch {
            // delete transaction
            db.transactionDao().delete(transaction)

            // update transactio list
            transactions = transactions.filter {it.id != transaction.id }
            runOnUiThread {
                updateDashBoard()
                transactionAdapter.setData(transactions)
                showSnackBar()
            }
        }
    }


    override fun onResume() {
        super.onResume()
        fetchAll()
    }
}