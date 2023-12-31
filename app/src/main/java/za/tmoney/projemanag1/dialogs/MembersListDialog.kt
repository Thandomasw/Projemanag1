package za.tmoney.projemanag1.dialogs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import za.tmoney.projemanag1.R
import za.tmoney.projemanag1.adapters.MembersListItemAdapters
import za.tmoney.projemanag1.models.User

abstract class MembersListDialog(
    context: Context,
    private var list: ArrayList<User>,
    private val title: String = ""
) : Dialog(context) {

    private var adapter: MembersListItemAdapters? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState ?: Bundle())

        val view = LayoutInflater.from(context).inflate(R.layout.dialog_list, null)

        setContentView(view)
        setCanceledOnTouchOutside(true)
        setCancelable(true)
        setUpRecyclerView(view)
    }

    private fun setUpRecyclerView(view: View) {
        view.findViewById<TextView>(R.id.tvTitle).text = title
        val recyclerView = view.findViewById<RecyclerView>(R.id.rvList)

        if (list.size > 0) {

            recyclerView.layoutManager = LinearLayoutManager(context)
            adapter = MembersListItemAdapters(context, list)
            recyclerView.adapter = adapter

            adapter!!.setOnClickListener(object :
                MembersListItemAdapters.OnClickListener {
                override fun onClick(position: Int, user: User, action:String) {
                    dismiss()
                    onItemSelected(user, action)
                }
            })
        }
    }

    protected abstract fun onItemSelected(user: User, action:String)
}